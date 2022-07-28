package slimeknights.tconstruct.tables.block.entity.table;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.SoundUtils;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.library.events.TinkerEventFactory;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.shared.inventory.ConfigurableInvWrapperCapability;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.block.TinkerStationBlock;
import slimeknights.tconstruct.tables.block.entity.inventory.LazyResultContainer;
import slimeknights.tconstruct.tables.block.entity.inventory.LazyResultContainer.ILazyCrafter;
import slimeknights.tconstruct.tables.block.entity.inventory.TinkerStationContainerWrapper;
import slimeknights.tconstruct.tables.menu.TinkerStationContainerMenu;
import slimeknights.tconstruct.tables.network.UpdateTinkerStationRecipePacket;

import javax.annotation.Nullable;

public class TinkerStationBlockEntity extends RetexturedTableBlockEntity implements ILazyCrafter {
  /** Slot index of the tool slot */
  public static final int TINKER_SLOT = 0;
  /** Slot index of the first input slot */
  public static final int INPUT_SLOT = 1;
  /** Name of the TE */
  private static final Component NAME = TConstruct.makeTranslation("gui", "tinker_station");

  /** Last crafted crafting recipe */
  @Nullable
  private ITinkerStationRecipe lastRecipe;
  /** Result inventory, lazy loads results */
  @Getter
  private final LazyResultContainer craftingResult;
  /** Crafting inventory for the recipe calls */
  private final TinkerStationContainerWrapper inventoryWrapper;

  @Getter
  private ValidatedResult currentError = ValidatedResult.PASS;

  public TinkerStationBlockEntity(BlockPos pos, BlockState state) {
    // if the block is the right type, use it for slot count
    this(pos, state, (state.getBlock() instanceof TinkerStationBlock station) ? station.getSlotCount() : 6);
  }

  public TinkerStationBlockEntity(BlockPos pos, BlockState state, int slots) {
    super(TinkerTables.tinkerStationTile.get(), pos, state, NAME, slots);
    this.itemHandler = new ConfigurableInvWrapperCapability(this, false, false);
    this.itemHandlerCap = LazyOptional.of(() -> this.itemHandler);
    this.inventoryWrapper = new TinkerStationContainerWrapper(this);
    this.craftingResult = new LazyResultContainer(this);
  }

  @Override
  public Component getDefaultName() {
    if (this.level == null) {
      return super.getDefaultName();
    }
    return new TranslatableComponent(this.getBlockState().getBlock().getDescriptionId());
  }

  /**
   * Gets the number of item input slots, ignoring the tool
   * @return  Input count
   */
  public int getInputCount() {
    return getContainerSize() - 1;
  }

  @Override
  public void resize(int size) {
    super.resize(size);
    inventoryWrapper.resize();
  }

  @Nullable
  @Override
  public AbstractContainerMenu createMenu(int menuId, Inventory playerInventory, Player playerEntity) {
    return new TinkerStationContainerMenu(menuId, playerInventory, this);
  }

  /* Crafting */

  @Override
  public ItemStack calcResult(@Nullable Player player) {
    if (this.level == null) {
      return ItemStack.EMPTY;
    }

    // assume empty unless we learn otherwise
    ItemStack result = ItemStack.EMPTY;
    this.currentError = ValidatedResult.PASS;

    if (!this.level.isClientSide && this.level.getServer() != null) {
      RecipeManager manager = this.level.getServer().getRecipeManager();

      // first, try the cached recipe
      ITinkerStationRecipe recipe = lastRecipe;
      // if it does not match, find a new recipe
      if (recipe == null || !recipe.matches(this.inventoryWrapper, this.level)) {
        recipe = manager.getRecipeFor(TinkerRecipeTypes.TINKER_STATION.get(), this.inventoryWrapper, this.level).orElse(null);
      }

      // if we have a recipe, fetch its result
      boolean needsSync = true;
      if (recipe != null) {
        // sync if the recipe is different
        if (lastRecipe != recipe) {
          this.lastRecipe = recipe;
          this.syncToRelevantPlayers(this::syncRecipe);
          needsSync = false;
        }

        // try for UI errors
        ValidatedResult validatedResult = recipe.getValidatedResult(this.inventoryWrapper);
        if (validatedResult.isSuccess()) {
          result = TinkerEventFactory.onTinkerToolCrafting(player, validatedResult.getResult(), false);
        } else if (validatedResult.hasError()) {
          this.currentError = validatedResult;
        }
      }
      // recipe will sync screen, so only need to call it when not syncing the recipe
      if (needsSync) {
        this.syncToRelevantPlayers(this::syncScreen);
      }
    }
    // client side only needs to update result, server syncs message elsewhere
    else if (this.lastRecipe != null && this.lastRecipe.matches(this.inventoryWrapper, level)) {
      ValidatedResult validatedResult = this.lastRecipe.getValidatedResult(this.inventoryWrapper);
      if (validatedResult.isSuccess()) {
        result = TinkerEventFactory.onTinkerToolCrafting(player, validatedResult.getResult(), false);
      } else if (validatedResult.hasError()) {
        this.currentError = validatedResult;
      }
    }

    return result;
  }

  @Override
  public void onCraft(Player player, ItemStack result, int amount) {
    if (amount == 0 || this.lastRecipe == null || this.level == null) {
      return;
    }

    // fire crafting events
    result.onCraftedBy(this.level, player, amount);
    ForgeEventFactory.firePlayerCraftingEvent(player, result, this.inventoryWrapper);
    this.playCraftSound(player);

    // run the recipe, will shrink inputs
    // run both sides for the sake of shift clicking
    this.inventoryWrapper.setPlayer(player);
    this.lastRecipe.updateInputs(result, inventoryWrapper, !level.isClientSide);
    this.inventoryWrapper.setPlayer(null);

    // remove the center slot item, just clear it entirely (if you want shrinking you should use the outer slots or ask nicely for a shrink amount hook)
    ItemStack tinkerable = this.getItem(TINKER_SLOT);
    if (!tinkerable.isEmpty()) {
      int shrinkToolSlot = lastRecipe.shrinkToolSlotBy();
      if (tinkerable.getCount() <= shrinkToolSlot) {
        this.setItem(TINKER_SLOT, ItemStack.EMPTY);
      } else {
        this.setItem(TINKER_SLOT, ItemHandlerHelper.copyStackWithSize(tinkerable, tinkerable.getCount() - shrinkToolSlot));
      }
    }
  }

  @Override
  public void setItem(int slot, ItemStack itemstack) {
    super.setItem(slot, itemstack);
    // clear the crafting result when the matrix changes so we recalculate the result
    this.craftingResult.clearContent();
    this.inventoryWrapper.refreshInput(slot);
  }
  
  @Override
  protected void playCraftSound(Player player) {
    SoundUtils.playSoundForAll(player, this.getInputCount() > 4 ? SoundEvents.ANVIL_USE : Sounds.SAW.getSound(), 0.8f, 0.8f + 0.4f * player.level.random.nextFloat());
  }

  /* Syncing */

  /**
   * Sends the current recipe to the given player
   * @param player  Player to send an update to
   */
  public void syncRecipe(Player player) {
    // must have a last recipe and a server level
    if (this.lastRecipe != null && this.level != null && !this.level.isClientSide && player instanceof ServerPlayer server) {
      TinkerNetwork.getInstance().sendTo(new UpdateTinkerStationRecipePacket(this.worldPosition, this.lastRecipe), server);
    }
  }

  /**
   * Updates the recipe from the server
   * @param recipe  New recipe
   */
  public void updateRecipe(ITinkerStationRecipe recipe) {
    this.lastRecipe = recipe;
    this.craftingResult.clearContent();
  }
}

package slimeknights.tconstruct.tables.tileentity.table;

import lombok.Getter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.hooks.BasicEventHooks;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.shared.inventory.ConfigurableInvWrapperCapability;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.inventory.table.CraftingStationContainer;
import slimeknights.tconstruct.tables.network.UpdateCraftingRecipePacket;
import slimeknights.tconstruct.tables.tileentity.crafting.CraftingInventoryWrapper;
import slimeknights.tconstruct.tables.tileentity.crafting.LazyResultInventory;

import javax.annotation.Nullable;
import java.util.Collections;

public class CraftingStationTileEntity extends RetexturedTableTileEntity implements LazyResultInventory.ILazyCrafter {
  public static final ITextComponent UNCRAFTABLE = Util.makeTranslation("gui", "crafting_station.uncraftable");

  /** Last crafted crafting recipe */
  @Nullable
  private ICraftingRecipe lastRecipe;
  /** Result inventory, lazy loads results */
  @Getter
  private final LazyResultInventory craftingResult;
  /** Crafting inventory for the recipe calls */
  private final CraftingInventoryWrapper craftingInventory;

  public CraftingStationTileEntity() {
    super(TinkerTables.craftingStationTile.get(), "gui.tconstruct.crafting_station", 9);
    this.itemHandler = new ConfigurableInvWrapperCapability(this, false, false);
    this.itemHandlerCap = LazyOptional.of(() -> this.itemHandler);
    this.craftingInventory = new CraftingInventoryWrapper(this, 3, 3);
    this.craftingResult = new LazyResultInventory(this);
  }

  @Nullable
  @Override
  public Container createMenu(int menuId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
    return new CraftingStationContainer(menuId, playerInventory, this);
  }

  @Override
  public AxisAlignedBB getRenderBoundingBox() {
    return new AxisAlignedBB(pos, pos.add(1, 2, 1));
  }

  /* Crafting */

  @Override
  public ItemStack calcResult() {
    if (this.world == null || isEmpty()) {
      return ItemStack.EMPTY;
    }
    // assume empty unless we learn otherwise
    ItemStack result = ItemStack.EMPTY;
    if (!this.world.isRemote && this.world.getServer() != null) {
      RecipeManager manager = this.world.getServer().getRecipeManager();

      // first, try the cached recipe
      ICraftingRecipe recipe = lastRecipe;
      // if it does not match, find a new recipe
      if (recipe == null || !recipe.matches(this.craftingInventory, this.world)) {
        recipe = manager.getRecipe(IRecipeType.CRAFTING, this.craftingInventory, this.world).orElse(null);
      }

      // if we have a recipe, fetch its result
      if (recipe != null) {
        result = recipe.getCraftingResult(this.craftingInventory);
        // sync if the recipe is different
        if (recipe != lastRecipe) {
          this.lastRecipe = recipe;
          this.syncToRelevantPlayers(this::syncRecipe);
        }
      }
    }
    else if (this.lastRecipe != null && this.lastRecipe.matches(this.craftingInventory, this.world)) {
      result = this.lastRecipe.getCraftingResult(this.craftingInventory);
    }
    return result;
  }

  /**
   * Gets the player sensitive crafting result, also validating the player has access to this recule
   * @param player  Player
   * @return  Player sensitive result
   */
  public ItemStack getResultForPlayer(PlayerEntity player) {
    ForgeHooks.setCraftingPlayer(player);
    ICraftingRecipe recipe = this.lastRecipe; // local variable just to prevent race conditions if the field changes, though that is unlikely

    // try matches again now that we have player access
    if (recipe == null || this.world == null || !recipe.matches(craftingInventory, world)) {
      ForgeHooks.setCraftingPlayer(null);
      return ItemStack.EMPTY;
    }

    // check if the player has access to the recipe, if not give up
    // TODO: gamerules are not synced to the client, so there is no easy way to do the is unlocked check without desyncs. As a result, offically not supporting limited crafting
//    if (!recipe.isDynamic() && world.getGameRules().getBoolean(GameRules.DO_LIMITED_CRAFTING)) {
//      // mojang, why can't PlayerEntity just have a RecipeBook getter, why must I go through the sided classes? grr
//      boolean locked = DistExecutor.unsafeRunForDist(
//        () -> () -> player instanceof ClientPlayerEntity && !((ClientPlayerEntity) player).getRecipeBook().isUnlocked(recipe),
//        () -> () -> player instanceof ServerPlayerEntity && !((ServerPlayerEntity) player).getRecipeBook().isUnlocked(recipe));
//      // if the player cannot craft this, block crafting
//      if (locked) {
//        ForgeHooks.setCraftingPlayer(null);
//        return ItemStack.EMPTY;
//      }
//    }

    ItemStack result = recipe.getCraftingResult(craftingInventory);
    ForgeHooks.setCraftingPlayer(null);
    return result;
  }

  /**
   * Removes the result from this inventory, updating inputs and triggering recipe hooks
   * @param player  Player taking result
   * @param result  Result removed
   * @param amount  Number of times crafted
   */
  public void takeResult(PlayerEntity player, ItemStack result, int amount) {
    ICraftingRecipe recipe = this.lastRecipe; // local variable just to prevent race conditions if the field changes, though that is unlikely
    if (recipe == null || this.world == null) {
      return;
    }

    // fire crafting events
    if (!recipe.isDynamic()) {
      // unlock the recipe if it was not unlocked
      player.unlockRecipes(Collections.singleton(recipe));
    }
    result.onCrafting(this.world, player, amount);
    BasicEventHooks.firePlayerCraftingEvent(player, result, this.craftingInventory);

    // update all slots in the inventory
    // remove remaining items
    ForgeHooks.setCraftingPlayer(player);
    NonNullList<ItemStack> remaining = recipe.getRemainingItems(craftingInventory);
    ForgeHooks.setCraftingPlayer(null);
    for (int i = 0; i < remaining.size(); ++i) {
      ItemStack original = this.getStackInSlot(i);
      ItemStack newStack = remaining.get(i);

      // if the slot contains a stack, decrease by 1
      if (!original.isEmpty()) {
        original.shrink(1);
      }

      // if we have a new item, try merging it in
      if (!newStack.isEmpty()) {
        // if empty, set directly
        if (original.isEmpty()) {
          this.setInventorySlotContents(i, newStack);
        }
        else if (ItemStack.areItemsEqual(original, newStack) && ItemStack.areItemStackTagsEqual(original, newStack)) {
          // if matching, merge
          newStack.grow(original.getCount());
          this.setInventorySlotContents(i, newStack);
        }
        else {
          // otherwise, drop the item as the player
          if (!player.inventory.addItemStackToInventory(newStack)) {
            player.dropItem(newStack, false);
          }
        }
      }
    }
  }

  /** Sends a message alerting the player this item is currently uncraftable, typically due to gamerules */
  public void notifyUncraftable(PlayerEntity player) {
    // if empty, send a message so the player is more aware of why they cannot craft it, sent to chat as status bar is not visible
    // TODO: consider moving into the UI somewhere
    if (world != null && !world.isRemote) {
      player.sendStatusMessage(CraftingStationTileEntity.UNCRAFTABLE, false);
    }
  }

  @Override
  public ItemStack onCraft(PlayerEntity player, ItemStack result, int amount) {
    // going to refetch result, so just start at empty
    result = ItemStack.EMPTY;

    if (amount > 0) {
      // get the player sensitive result
      result = getResultForPlayer(player);
      if (!result.isEmpty()) {
        // update the inputs and trigger recipe hooks
        takeResult(player, result, amount);
      }
    }
    // the return value ultimately does nothing, so manually set the result into the player
    player.inventory.setItemStack(result);
    if (result.isEmpty()) {
      notifyUncraftable(player);
    }
    return result;
  }

  @Override
  public void setInventorySlotContents(int slot, ItemStack itemstack) {
    super.setInventorySlotContents(slot, itemstack);
    // clear the crafting result when the matrix changes so we recalculate the result
    this.craftingResult.clear();
  }


  /* Syncing */

  /**
   * Sends the current recipe to the given player
   * @param player  Player to send an update to
   */
  public void syncRecipe(PlayerEntity player) {
    // must have a last recipe and a server world
    if (this.lastRecipe != null && this.world != null && !this.world.isRemote && player instanceof ServerPlayerEntity) {
      TinkerNetwork.getInstance().sendTo(new UpdateCraftingRecipePacket(this.pos, this.lastRecipe), (ServerPlayerEntity) player);
    }
  }

  /**
   * Updates the recipe from the server
   * @param recipe  New recipe
   */
  public void updateRecipe(ICraftingRecipe recipe) {
    this.lastRecipe = recipe;
    this.craftingResult.clear();
  }
}

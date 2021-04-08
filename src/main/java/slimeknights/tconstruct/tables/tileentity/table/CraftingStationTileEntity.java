package slimeknights.tconstruct.tables.tileentity.table;

import lombok.Getter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Box;
import net.minecraft.world.GameRules;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.hooks.BasicEventHooks;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.shared.inventory.ConfigurableInvWrapperCapability;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.inventory.table.CraftingStationContainer;
import slimeknights.tconstruct.tables.network.UpdateCraftingRecipePacket;
import slimeknights.tconstruct.tables.tileentity.crafting.CraftingInventoryWrapper;
import slimeknights.tconstruct.tables.tileentity.crafting.LazyResultInventory;

import org.jetbrains.annotations.Nullable;
import java.util.Collections;

public class CraftingStationTileEntity extends RetexturedTableTileEntity implements LazyResultInventory.ILazyCrafter {

  /** Last crafted crafting recipe */
  @Nullable
  private CraftingRecipe lastRecipe;
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
  public ScreenHandler createMenu(int menuId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
    return new CraftingStationContainer(menuId, playerInventory, this);
  }

  @Override
  public Box getRenderBoundingBox() {
    return new Box(pos, pos.add(1, 2, 1));
  }

  /* Crafting */

  @Override
  public ItemStack calcResult() {
    if (this.world == null || isEmpty()) {
      return ItemStack.EMPTY;
    }
    // assume empty unless we learn otherwise
    ItemStack result = ItemStack.EMPTY;
    if (!this.world.isClient && this.world.getServer() != null) {
      RecipeManager manager = this.world.getServer().getRecipeManager();

      // first, try the cached recipe
      CraftingRecipe recipe = lastRecipe;
      // if it does not match, find a new recipe
      if (recipe == null || !recipe.matches(this.craftingInventory, this.world)) {
        recipe = manager.getFirstMatch(RecipeType.CRAFTING, this.craftingInventory, this.world).orElse(null);
      }

      // if we have a recipe, fetch its result
      if (recipe != null) {
        result = recipe.craft(this.craftingInventory);
        // sync if the recipe is different
        if (recipe != lastRecipe) {
          this.lastRecipe = recipe;
          this.syncToRelevantPlayers(this::syncRecipe);
        }
      }
    }
    else if (this.lastRecipe != null && this.lastRecipe.matches(this.craftingInventory, this.world)) {
      result = this.lastRecipe.craft(this.craftingInventory);
    }
    return result;
  }

  @Override
  public ItemStack onCraft(PlayerEntity player, ItemStack result, int amount) {
    if (this.world == null || amount == 0 || this.lastRecipe == null || !this.lastRecipe.matches(this.craftingInventory, this.world)) {
      return ItemStack.EMPTY;
    }

    // check if the player has access to the result
    if (player instanceof ServerPlayerEntity) {
      if (this.lastRecipe != null) {
        // if the player cannot craft this, block crafting
        if (!this.lastRecipe.isIgnoredInRecipeBook() && world.getGameRules().getBoolean(GameRules.DO_LIMITED_CRAFTING) && !((ServerPlayerEntity) player).getRecipeBook().contains(this.lastRecipe)) {
          return ItemStack.EMPTY;
        }
        // unlock the recipe if it was not unlocked
        if (this.lastRecipe != null && !this.lastRecipe.isIgnoredInRecipeBook()) {
          player.unlockRecipes(Collections.singleton(this.lastRecipe));
        }
      }

      // fire crafting events
      result.onCraft(this.world, player, amount);
      BasicEventHooks.firePlayerCraftingEvent(player, result, this.craftingInventory);
    }

    // update all slots in the inventory
    // remove remaining items
    ForgeHooks.setCraftingPlayer(player);
    DefaultedList<ItemStack> remaining = this.lastRecipe.getRemainingStacks(craftingInventory);
    ForgeHooks.setCraftingPlayer(null);
    for (int i = 0; i < remaining.size(); ++i) {
      ItemStack original = this.getStack(i);
      ItemStack newStack = remaining.get(i);

      // if the slot contains a stack, decrease by 1
      if (!original.isEmpty()) {
        original.decrement(1);
      }

      // if we have a new item, try merging it in
      if (!newStack.isEmpty()) {
        // if empty, set directly
        if (original.isEmpty()) {
          this.setStack(i, newStack);
        }
        else if (ItemStack.areItemsEqualIgnoreDamage(original, newStack) && ItemStack.areTagsEqual(original, newStack)) {
          // if matching, merge
          newStack.increment(original.getCount());
          this.setStack(i, newStack);
        }
        else {
          // otherwise, drop the item as the player
          if (!player.inventory.insertStack(newStack)) {
            player.dropItem(newStack, false);
          }
        }
      }
    }

    return result;
  }

  @Override
  public void setStack(int slot, ItemStack itemstack) {
    super.setStack(slot, itemstack);
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
    if (this.lastRecipe != null && this.world != null && !this.world.isClient && player instanceof ServerPlayerEntity) {
      TinkerNetwork.getInstance().sendTo(new UpdateCraftingRecipePacket(this.pos, this.lastRecipe), (ServerPlayerEntity) player);
    }
  }

  /**
   * Updates the recipe from the server
   * @param recipe  New recipe
   */
  public void updateRecipe(CraftingRecipe recipe) {
    this.lastRecipe = recipe;
    this.craftingResult.clear();
  }
}

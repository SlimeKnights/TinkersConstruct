package slimeknights.tconstruct.tables.tileentity.crafting;

import com.google.common.base.Preconditions;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeFinder;

/**
 * Extension of {@link CraftingInventory} to use instead wrap an existing {@link Inventory}
 */
public class CraftingInventoryWrapper extends CraftingInventory {
  private final Inventory crafter;
  public CraftingInventoryWrapper(Inventory crafter, int width, int height) {
    //noinspection ConstantConditions
    super(null, width, height);
    Preconditions.checkArgument(crafter.size() == width * height, "Invalid width and height for inventroy size");
    this.crafter = crafter;
  }

  /** Inventory redirection */

  @Override
  public ItemStack getStack(int index) {
    return crafter.getStack(index);
  }

  @Override
  public int size() {
    return crafter.size();
  }

  @Override
  public boolean isEmpty() {
    return crafter.isEmpty();
  }

  @Override
  public ItemStack removeStack(int index) {
    return crafter.removeStack(index);
  }

  @Override
  public ItemStack removeStack(int index, int count) {
    return crafter.removeStack(index, count);
  }

  @Override
  public void setStack(int index, ItemStack stack) {
    crafter.setStack(index, stack);
  }

  @Override
  public void markDirty() {
    crafter.markDirty();
  }

  @Override
  public void clear() {
    crafter.clear();
  }

  @Override
  public void provideRecipeInputs(RecipeFinder helper) {
    for (int i = 0; i < crafter.size(); i++) {
      helper.addNormalItem(crafter.getStack(i));
    }
  }
}

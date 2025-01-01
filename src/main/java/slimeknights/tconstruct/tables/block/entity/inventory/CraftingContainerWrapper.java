package slimeknights.tconstruct.tables.block.entity.inventory;

import com.google.common.base.Preconditions;
import lombok.Getter;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link CraftingContainer} to use instead wrap an existing {@link Container}.
 * TODO: consider using an item handler instead?
 */
public class CraftingContainerWrapper implements CraftingContainer {
  private final Container crafter;
  @Getter
  private final int width;
  @Getter
  private final int height;
  public CraftingContainerWrapper(Container crafter, int width, int height) {
    Preconditions.checkArgument(crafter.getContainerSize() == width * height, "Invalid width and height for inventroy size");
    this.crafter = crafter;
    this.width = width;
    this.height = height;
  }

  /** Inventory redirection */

  @Override
  public ItemStack getItem(int index) {
    return crafter.getItem(index);
  }

  @Override
  public int getContainerSize() {
    return crafter.getContainerSize();
  }

  @Override
  public boolean isEmpty() {
    return crafter.isEmpty();
  }

  @Override
  public ItemStack removeItemNoUpdate(int index) {
    return crafter.removeItemNoUpdate(index);
  }

  @Override
  public ItemStack removeItem(int index, int count) {
    return crafter.removeItem(index, count);
  }

  @Override
  public void setItem(int index, ItemStack stack) {
    crafter.setItem(index, stack);
  }

  @Override
  public void setChanged() {
    crafter.setChanged();
  }

  @Override
  public boolean stillValid(Player pPlayer) {
    return true;
  }

  @Override
  public void clearContent() {
    crafter.clearContent();
  }

  @Override
  public void fillStackedContents(StackedContents helper) {
    for (int i = 0; i < crafter.getContainerSize(); i++) {
      helper.accountSimpleStack(crafter.getItem(i));
    }
  }

  @Override
  public List<ItemStack> getItems() {
    // TODO: would rather use the internal list for the container, should make a custom item handler perhaps?
    List<ItemStack> stacks = new ArrayList<>(getContainerSize());
    for (int i = 0; i < getContainerSize(); i++) {
      stacks.add(getItem(i));
    }
    return stacks;
  }
}

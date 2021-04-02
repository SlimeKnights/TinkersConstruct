package slimeknights.tconstruct.tables.tileentity.crafting;

import lombok.RequiredArgsConstructor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * This class represents an output slot inventory for a crafting inventory.
 * It will calculate the result when requested based on the methods in {@link ILazyCrafter}, and update other slots on recipe take
 */
@RequiredArgsConstructor
public class LazyResultInventory implements IInventory {
  private final ILazyCrafter crafter;

  /** Cache of the last result */
  @Nullable
  private ItemStack result = null;

  /**
   * Gets the result of this inventory, lazy loading it if not yet calculated
   * @return  Item stack result
   */
  public ItemStack getResult() {
    if (result == null) {
      result = Objects.requireNonNull(crafter.calcResult(), "Result cannot be null");
    }
    return result;
  }

  /* Inventory logic */

  @Override
  public ItemStack getStackInSlot(int index) {
    return getResult();
  }

  @Override
  public int getSizeInventory() {
    return 1;
  }

  @Override
  public boolean isEmpty() {
    return getResult().isEmpty();
  }

  /**
   * Gets the result of crafting, and consumes required items
   * @param amount  Number to craft
   * @return  Crafting result
   */
  public ItemStack craftResult(PlayerEntity player, int amount) {
    // get result and consume items
    ItemStack output = crafter.onCraft(player, getResult().copy(), amount);
    // clear result cache, items changed
    clear();
    // return result
    return output;
  }

  /**
   * Returns the result stack from the inventory. This will not consume inputs
   * @param index  Unused
   * @return  Result stack
   * @deprecated use {@link #craftResult(PlayerEntity, int)} or {@link #getResult()}
   */
  @Deprecated
  @Override
  public ItemStack removeStackFromSlot(int index) {
    return getResult().copy();
  }

  /**
   * Returns the result stack from the inventory. This will not consume inputs OR edit size
   * @param index  Unused
   * @param count  Unused as output sizes should never change
   * @return  Result stack
   * @deprecated use {@link #craftResult(PlayerEntity, int)} or {@link #getResult()}
   */
  @Deprecated
  @Override
  public ItemStack decrStackSize(int index, int count) {
    return getResult().copy();
  }

  /**
   * Clears the result cache, causing the result to be recalculated
   */
  @Override
  public void clear() {
    this.result = null;
  }

  /* Required methods */

  /** @deprecated Unsupported method */
  @Deprecated
  @Override
  public void setInventorySlotContents(int index, ItemStack stack) {}

  /** @deprecated Unused method */
  @Deprecated
  @Override
  public void markDirty() {}

  @Override
  public boolean isUsableByPlayer(PlayerEntity player) {
    return true;
  }

  /**
   * Logic to get results for the lazy results inventory
   */
  public interface ILazyCrafter {
    /**
     * Calculates the recipe result
     * @return  Item stack result
     */
    ItemStack calcResult();

    /**
     * Called when an item is crafted to consume requirements
     * @param player  Player doing the crafting
     * @param result  Crafting result
     * @param amount  Amount to craft
     */
    ItemStack onCraft(PlayerEntity player, ItemStack result, int amount);
  }
}

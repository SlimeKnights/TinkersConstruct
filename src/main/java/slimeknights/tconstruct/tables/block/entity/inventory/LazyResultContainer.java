package slimeknights.tconstruct.tables.block.entity.inventory;

import lombok.RequiredArgsConstructor;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * This class represents an output slot inventory for a crafting inventory.
 * It will calculate the result when requested based on the methods in {@link ILazyCrafter}, and update other slots on recipe take
 */
@RequiredArgsConstructor
public class LazyResultContainer implements Container {
  private final ILazyCrafter crafter;

  /** Cache of the last result */
  @Nullable
  private ItemStack result = null;

  /**
   * Gets the result of this inventory, lazy loading it if not yet calculated. This result is not player sensitive.
   * @return  Item stack result
   */
  public final ItemStack getResult() {
    return getResult(null);
  }

  /** Calculates the result for the given player. Used by the crafting station since we want player sensitive results. */
  public ItemStack calcResult(Player player) {
    return crafter.calcResult(player);
  }

  /** @deprecated use {@link #calcResult(Player)} or {@link #getResult()} */
  @Deprecated(forRemoval = true)
  public ItemStack getResult(@Nullable Player player) {
    if (result == null) {
      result = Objects.requireNonNull(crafter.calcResult(player), "Result cannot be null");
    }
    return result;
  }

  /* Inventory logic */

  @Override
  public ItemStack getItem(int index) {
    return getResult();
  }

  @Override
  public int getContainerSize() {
    return 1;
  }

  @Override
  public boolean isEmpty() {
    return getResult().isEmpty();
  }

  /**
   * Gets the result of crafting, and consumes required items
   * @param player  Player access
   * @param result  Item that was crafted
   * @param amount  Number to craft
   */
  public void craftResult(Player player, ItemStack result, int amount) {
    // get result and consume items
    crafter.onCraft(player, result, amount);
    // clear result cache, items changed
    clearContent();
  }

  /** @deprecated use {@link #craftResult(Player, ItemStack, int)} */
  @Deprecated(forRemoval = true)
  public void craftResult(Player player, int amount) {
    craftResult(player, calcResult(player).copy(), amount);
  }

  /**
   * Returns the result stack from the inventory. This will not consume inputs
   * @param index  Unused
   * @return  Result stack
   * @deprecated use {@link #craftResult(Player, ItemStack, int)} or {@link #getResult()}
   */
  @Deprecated
  @Override
  public ItemStack removeItemNoUpdate(int index) {
    return getResult().copy();
  }

  /**
   * Returns the result stack from the inventory. This will not consume inputs OR edit size
   * @param index  Unused
   * @param count  Unused as output sizes should never change
   * @return  Result stack
   * @deprecated use {@link #craftResult(Player, ItemStack, int)} or {@link #getResult()}
   */
  @Deprecated
  @Override
  public ItemStack removeItem(int index, int count) {
    return getResult().copy();
  }

  /**
   * Clears the result cache, causing the result to be recalculated
   */
  @Override
  public void clearContent() {
    this.result = null;
  }

  /* Required methods */

  /** @deprecated Unsupported method */
  @Deprecated
  @Override
  public void setItem(int index, ItemStack stack) {}

  /** @deprecated Unused method */
  @Deprecated
  @Override
  public void setChanged() {}

  @Override
  public boolean stillValid(Player player) {
    return true;
  }

  /**
   * Logic to get results for the lazy results inventory
   */
  public interface ILazyCrafter {
    /**
     * Calculates the recipe result
     * @param  player  Player entity. May be null if not supported.
     *                 May not match the player used in {@link #onCraft(Player, ItemStack, int)} as this result is cached
     * @return  Item stack result
     */
    ItemStack calcResult(@Nullable Player player);

    /**
     * Called when an item is crafted to consume requirements
     * @param player  Player doing the crafting
     * @param result  Crafting result
     * @param amount  Amount to craft
     */
    void onCraft(Player player, ItemStack result, int amount);
  }
}

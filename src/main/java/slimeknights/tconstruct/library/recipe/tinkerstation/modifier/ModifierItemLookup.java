package slimeknights.tconstruct.library.recipe.tinkerstation.modifier;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import slimeknights.mantle.recipe.SizedIngredient;
import slimeknights.tconstruct.common.RecipeCacheInvalidator;
import slimeknights.tconstruct.common.RecipeCacheInvalidator.DuelSidedListener;

import java.util.HashSet;
import java.util.Set;

/** Logic to check if an item is a modifier */
public class ModifierItemLookup {
  /** List of all modifier inputs */
  private static final Set<Item> MODIFIERS = new HashSet<>();

  /** Listener for clearing the recipe cache on recipe reload */
  private static final DuelSidedListener LISTENER = RecipeCacheInvalidator.addDuelSidedListener(MODIFIERS::clear);

  /**
   * Adds an item as a modifier
   * @param item  Item
   */
  public static void addItem(Item item) {
    LISTENER.checkClear();
    MODIFIERS.add(item);
  }

  /**
   * Adds an ingredient to the list
   */
  public static void addIngredient(Ingredient ingredient) {
    LISTENER.checkClear();
    // this should work on both client and server
    // server just pulls from the tag, client does not use tags directly at this stage
    for (ItemStack stack : ingredient.getMatchingStacks()) {
      MODIFIERS.add(stack.getItem());
    }
  }

  /**
   * Adds an ingredient to the list
   */
  public static void addIngredient(SizedIngredient ingredient) {
    LISTENER.checkClear();
    // this should work on both client and server
    // server just pulls from the tag, client does not use tags directly at this stage
    for (ItemStack stack : ingredient.getMatchingStacks()) {
      MODIFIERS.add(stack.getItem());
    }
  }

  /**
   * Checks if an item is a modifier
   * @param item  Item to check
   * @return  True if its a modifier
   */
  public static boolean isModifier(Item item) {
    return MODIFIERS.contains(item);
  }
}

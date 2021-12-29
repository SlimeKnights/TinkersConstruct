package slimeknights.tconstruct.library.recipe.partbuilder;

import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.recipe.inventory.ISingleItemInventory;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;

import javax.annotation.Nullable;

/**
 * Inventory interface for the part builder
 */
public interface IPartBuilderInventory extends ISingleItemInventory {
  /**
   * Gets the material recipe based on the current slot contents
   * @return  Material recipe, or null if the slot contents are not a valid material
   */
  @Nullable
  MaterialRecipe getMaterial();

  /**
   * Gets the stack in the pattern slot
   * @return  Pattern slot stack
   */
  ItemStack getPatternStack();

  /*
   * Gets the currently selected pattern item
   * @return
   */
  //ResourceLocation getPattern();
}

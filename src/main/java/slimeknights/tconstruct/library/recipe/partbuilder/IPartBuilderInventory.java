package slimeknights.tconstruct.library.recipe.partbuilder;

import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.recipe.inventory.ISingleItemInventory;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;

import javax.annotation.Nullable;

public interface IPartBuilderInventory extends ISingleItemInventory {
  /**
   * Gets the material recipe based on the current slot contents
   * @return  Material recipe
   */
  @Nullable
  MaterialRecipe getMaterial();

  /**
   * Gets the stack in the pattern slot
   * @return  Pattern slot stack
   */
  ItemStack getPatternStack();

  /**
   * Gets the currently selected pattern item
   * @return
   */
  //ResourceLocation getPattern();
}

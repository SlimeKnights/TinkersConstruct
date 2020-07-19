package slimeknights.tconstruct.library.recipe.toolstation;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import slimeknights.tconstruct.library.recipe.RecipeTypes;

public interface IToolStationRecipe extends IRecipe<IToolStationInventory> {
  /* Recipe data */

  @Override
  default IRecipeType<?> getType() {
    return RecipeTypes.TOOL_STATION;
  }

  @Override
  default boolean isDynamic() {
    return true;
  }

  /* Required methods */

  /** @deprecated unused */
  @Deprecated
  @Override
  default boolean canFit(int width, int height) {
    return true;
  }

}

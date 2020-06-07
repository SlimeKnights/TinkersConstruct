package slimeknights.tconstruct.library.recipe;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.casting.AbstractCastingRecipe;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.recipe.partbuilder.PartRecipe;

/**
 * Class containing all of Tinkers Construct recipe types
 */
public interface RecipeTypes {
  IRecipeType<PartRecipe> PART_BUILDER = register("part_builder");
  IRecipeType<MaterialRecipe> MATERIAL = register("material");

  IRecipeType<AbstractCastingRecipe> CASTING_BASIN = register("casting_basin");
  IRecipeType<AbstractCastingRecipe> CASTING_TABLE = register("casting_table");

  /**
   * Registers a new recipe type, prefixing with the mod ID
   * @param name  Recipe type name
   * @param <T>   Recipe type
   * @return  Registered recipe type
   */
  static <T extends IRecipe<?>> IRecipeType<T> register(String name) {
    return IRecipeType.register(TConstruct.modID + ":" + name);
  }
}

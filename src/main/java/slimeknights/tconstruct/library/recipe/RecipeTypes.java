package slimeknights.tconstruct.library.recipe;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe;

/**
 * Class containing all of Tinkers Construct recipe types
 */
public interface RecipeTypes {
  IRecipeType<IMeltingRecipe> MELTING = register("melting");

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

package slimeknights.tconstruct.compat.neoforged.neoforge.common.crafting;

/** Compatibility shim for code that only needs shaped recipe dimensions. */
public interface IShapedRecipe<T> {
  int getRecipeWidth();

  int getRecipeHeight();
}

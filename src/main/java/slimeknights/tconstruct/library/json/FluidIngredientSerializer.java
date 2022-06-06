package slimeknights.tconstruct.library.json;

import slimeknights.mantle.recipe.ingredient.FluidIngredient;

/** @deprecated use {@link FluidIngredient#SERIALIZER} */
@Deprecated
public class FluidIngredientSerializer {
  /** @deprecated use {@link FluidIngredient#SERIALIZER} */
  @Deprecated
  public static FluidIngredient.Serializer INSTANCE = FluidIngredient.SERIALIZER;

  private FluidIngredientSerializer() {}
}

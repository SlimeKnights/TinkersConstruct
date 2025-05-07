package slimeknights.tconstruct.plugin.jei.util;

import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IClickableIngredient;
import net.minecraft.client.renderer.Rect2i;

/**
 * JEI does not realize you can put non-interfaces (such as records) in the API, so we are stuck recreating this just to adhere to JEI's new API.
 */
public record ClickableIngredient<T>(IIngredientType<T> getType, T getIngredient, Rect2i getArea) implements IClickableIngredient<T>, ITypedIngredient<T>  {
  @SuppressWarnings("removal")
  @Override
  public ITypedIngredient<T> getTypedIngredient() {
    return this;
  }
}

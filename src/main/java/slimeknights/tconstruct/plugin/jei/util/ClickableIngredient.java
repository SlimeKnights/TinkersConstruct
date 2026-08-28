package slimeknights.tconstruct.plugin.jei.util;

import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IClickableIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.renderer.Rect2i;
import org.jetbrains.annotations.Nullable;

/** @deprecated {@link IIngredientManager#createClickableIngredient(IIngredientType, Object, Rect2i, boolean)} or {@link mezz.jei.api.gui.builder.IClickableIngredientFactory} */
@Deprecated(forRemoval = true)
public record ClickableIngredient<T>(IIngredientType<T> getType, T getIngredient, Rect2i getArea) implements IClickableIngredient<T>, ITypedIngredient<T>  {
  @Override
  public ITypedIngredient<T> getTypedIngredient() {
    return this;
  }

  @SuppressWarnings("unchecked")
  @Override
  public @Nullable <V> ClickableIngredient<V> cast(IIngredientType<V> ingredientType) {
    if (getType.equals(ingredientType)) {
      return (ClickableIngredient<V>) this;
    }
    return null;
  }
}

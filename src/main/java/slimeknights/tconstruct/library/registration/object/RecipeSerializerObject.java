package slimeknights.tconstruct.library.registration.object;

import net.minecraft.item.crafting.IRecipeSerializer;

import java.util.function.Supplier;

public class RecipeSerializerObject<RECIPE extends IRecipeSerializer<?>> implements Supplier<RECIPE> {

  protected final Supplier<RECIPE> recipe;

  public RecipeSerializerObject(Supplier<RECIPE> recipe) {
    this.recipe = recipe;
  }

  @Override
  public RECIPE get() {
    return recipe.get();
  }
}

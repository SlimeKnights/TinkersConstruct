package slimeknights.tconstruct.plugin.jei.material;

import slimeknights.tconstruct.library.recipe.material.ShapelessMaterialsRecipe;

/** @deprecated use {@link MaterialsCraftingExtension#shapeless(ShapelessMaterialsRecipe)} */
@Deprecated(forRemoval = true)
public class ShapelessMaterialsExtension extends MaterialsCraftingExtension<ShapelessMaterialsRecipe> {
  private ShapelessMaterialsExtension(ShapelessMaterialsRecipe recipe) {
    super(recipe);
  }
}

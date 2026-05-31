package slimeknights.tconstruct.plugin.jei.material;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import slimeknights.tconstruct.library.recipe.material.ShapedMaterialsRecipe;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/** Logic to show {@link ShapedMaterialsRecipe} in JEI */
public class ShapedMaterialsExtension extends MaterialsCraftingExtension<ShapedMaterialsRecipe> {
  public static final ICraftingCategoryExtension<ShapedMaterialsRecipe> INSTANCE = new ICraftingCategoryExtension<>() {
    @Override
    public void setRecipe(RecipeHolder<ShapedMaterialsRecipe> holder, IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
      ShapedMaterialsExtension extension = ShapedMaterialsExtension.create(holder.value());
      if (extension != null) {
        extension.setRecipe(builder, craftingGridHelper, focuses);
      }
    }

    @Override
    public Optional<ResourceLocation> getRegistryName(RecipeHolder<ShapedMaterialsRecipe> holder) {
      return Optional.of(holder.id());
    }

    @Override
    public int getWidth(RecipeHolder<ShapedMaterialsRecipe> holder) {
      return holder.value().getWidth();
    }

    @Override
    public int getHeight(RecipeHolder<ShapedMaterialsRecipe> holder) {
      return holder.value().getHeight();
    }
  };

  private ShapedMaterialsExtension(ShapedMaterialsRecipe recipe) {
    super(recipe);
  }

  /** {@return Instance of the shaped extension, or null if the recipe is invalid for display} */
  @Nullable
  public static ShapedMaterialsExtension create(ShapedMaterialsRecipe recipe) {
    for (Ingredient ingredient : recipe.getParts()) {
      if (ingredient.getItems().length == 0) {
        return null;
      }
    }
    return new ShapedMaterialsExtension(recipe);
  }

  @Override
  protected int[] getMaterialSlots(ShapedMaterialsRecipe recipe, Ingredient firstPart) {
    List<Ingredient> inputs = recipe.getIngredients();
    return IntStream.range(0, inputs.size()).filter(i -> inputs.get(i) == firstPart).toArray();
  }

  @Override
  public int getWidth() {
    return recipe.getWidth();
  }

  @Override
  public int getHeight() {
    return recipe.getHeight();
  }
}

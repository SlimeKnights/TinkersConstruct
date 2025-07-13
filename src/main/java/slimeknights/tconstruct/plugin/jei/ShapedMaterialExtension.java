package slimeknights.tconstruct.plugin.jei;

import com.google.common.collect.Streams;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.tconstruct.library.recipe.ingredient.MaterialValueIngredient;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipeCache;
import slimeknights.tconstruct.library.recipe.material.ShapedMaterialRecipe;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/** Logic to show {@link ShapedMaterialRecipe} in JEI */
public class ShapedMaterialExtension implements ICraftingCategoryExtension {
  private final ShapedMaterialRecipe recipe;
  private final ItemStack plainResult;
  private final List<ItemStack> result;
  private final int[] materialSlots;
  public ShapedMaterialExtension(ShapedMaterialRecipe recipe) {
    this.recipe = recipe;
    MaterialValueIngredient materials = recipe.getMaterial();
    plainResult = recipe.getResultItem(Objects.requireNonNull(SafeClientAccess.getRegistryAccess()));
    if (materials != null) {
      this.result = MaterialRecipeCache.getAllRecipes().stream().filter(materials::test).flatMap(mat -> {
        ItemStack stack = plainResult.copy();
        recipe.setMaterial(stack, mat.getMaterial().getVariant());
        // add one copy of the stack per item in the nested ingredient, so the lengths match up
        return IntStream.range(0, mat.getIngredient().getItems().length).mapToObj(i -> stack);
      }).toList();
    } else {
      this.result = List.of(plainResult);
    }
    List<Ingredient> inputs = recipe.getIngredients();
    this.materialSlots = IntStream.range(0, inputs.size()).filter(i -> inputs.get(i) instanceof MaterialValueIngredient).toArray();
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focusGroup) {
    builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT).addItemStack(this.plainResult);

    // apply ingredient stacks
    List<List<ItemStack>> inputStacks = this.recipe.getIngredients().stream().map(ingredient -> List.of(ingredient.getItems())).toList();
    int width = this.recipe.getWidth();
    int height = this.recipe.getHeight();
    List<IRecipeSlotBuilder> inputs = craftingGridHelper.createAndSetInputs(builder, VanillaTypes.ITEM_STACK, inputStacks, width, height);
    IRecipeSlotBuilder output = craftingGridHelper.createAndSetOutputs(builder, this.result);
    if (inputs.size() != 9) {
      Mantle.logger.error("Failed to create focus link for {} as the layout {} is not 3x3", this.recipe.getId(), builder.getClass().getName());
    } else {
      // apply focus links
      builder.createFocusLink(Streams.concat(
        Stream.of(output),
        Arrays.stream(this.materialSlots).mapToObj(i -> inputs.get(getCraftingIndex(i, width, height)))
      ).toArray(IRecipeSlotBuilder[]::new));
    }
  }

  /** Borrowed from {@link ICraftingGridHelper} implementation. TODO: make mantle variant public */
  private static int getCraftingIndex(int i, int width, int height) {
    int index;
    if (width == 1) {
      if (height == 3) {
        index = (i * 3) + 1;
      } else if (height == 2) {
        index = (i * 3) + 1;
      } else {
        index = 4;
      }
    } else if (height == 1) {
      index = i + 3;
    } else if (width == 2) {
      index = i;
      if (i > 1) {
        index++;
        if (i > 3) {
          index++;
        }
      }
    } else if (height == 2) {
      index = i + 3;
    } else {
      index = i;
    }
    return index;
  }
}

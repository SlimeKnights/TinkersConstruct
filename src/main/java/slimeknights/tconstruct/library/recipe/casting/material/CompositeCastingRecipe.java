package slimeknights.tconstruct.library.recipe.casting.material;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.helper.LoadableRecipeSerializer;
import slimeknights.mantle.recipe.helper.TypeAwareRecipeSerializer;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.recipe.casting.DisplayCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.ICastingContainer;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Casting recipe taking a part of a material and a fluid and outputting the part with a new material
 */
public class CompositeCastingRecipe extends MaterialCastingRecipe {
  public static final RecordLoadable<CompositeCastingRecipe> LOADER = RecordLoadable.create(
    LoadableRecipeSerializer.TYPED_SERIALIZER.requiredField(), ContextKey.ID.requiredField(),
    LoadableRecipeSerializer.RECIPE_GROUP, RESULT_FIELD, ITEM_COST_FIELD,
    CompositeCastingRecipe::new);

  private final IMaterialItem result;
  public CompositeCastingRecipe(TypeAwareRecipeSerializer<?> serializer, ResourceLocation id, String group, IMaterialItem result, int itemCost) {
    super(serializer, id, group, Ingredient.of(result), itemCost, result, true, false);
    this.result = result;
  }

  @Override
  protected Optional<MaterialFluidRecipe> getMaterialFluid(ICastingContainer inv) {
    return MaterialCastingLookup.getCompositeFluid(inv);
  }

  /* JEI display */
  @Override
  public List<IDisplayableCastingRecipe> getRecipes() {
    if (multiRecipes == null) {
      RecipeType<?> type = getType();
      multiRecipes = MaterialCastingLookup
        .getAllCompositeFluids().stream()
        .filter(recipe -> {
          MaterialVariant output = recipe.getOutput();
          MaterialVariant input = recipe.getInput();
          return !output.isUnknown() && input != null && !input.isUnknown()
            && !output.get().isHidden() && !input.get().isHidden() && result.canUseMaterial(output.getId()) && result.canUseMaterial(input.getId());
        })
        .map(recipe -> {
          List<FluidStack> fluids = resizeFluids(recipe.getFluids());
          int fluidAmount = fluids.stream().mapToInt(FluidStack::getAmount).max().orElse(0);
          return new DisplayCastingRecipe(type, Collections.singletonList(result.withMaterial(Objects.requireNonNull(recipe.getInput()).getVariant())), fluids, result.withMaterial(recipe.getOutput().getVariant()),
                                          ICastingRecipe.calcCoolingTime(recipe.getTemperature(), itemCost * fluidAmount), isConsumed());
        })
        .collect(Collectors.toList());
    }
    return multiRecipes;
  }
}

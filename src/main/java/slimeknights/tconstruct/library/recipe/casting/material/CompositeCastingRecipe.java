package slimeknights.tconstruct.library.recipe.casting.material;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.recipe.helper.LoadableRecipeSerializer;
import slimeknights.mantle.recipe.helper.TypeAwareRecipeSerializer;
import slimeknights.tconstruct.library.json.predicate.material.MaterialPredicate;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.recipe.casting.DisplayCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.ICastingContainer;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipeCache;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Casting recipe taking a part of a material and a fluid and outputting the part with a new material
 */
public class CompositeCastingRecipe extends MaterialCastingRecipe {
  public static final RecordLoadable<CompositeCastingRecipe> LOADER = RecordLoadable.create(
    LoadableRecipeSerializer.TYPED_SERIALIZER.requiredField(), ContextKey.ID.requiredField(),
    LoadableRecipeSerializer.RECIPE_GROUP, ITEM_COST_FIELD, RESULT_FIELD, MATERIALS_FIELD,
    MaterialStatsId.PARSER.nullableField("casting_stat_conflict", r -> r.castingStatConflict),
    CompositeCastingRecipe::new);

  @Nullable
  private final MaterialStatsId castingStatConflict;

  public CompositeCastingRecipe(TypeAwareRecipeSerializer<?> serializer, ResourceLocation id, String group, int itemCost, IMaterialItem result, IJsonPredicate<MaterialVariantId> materials, @Nullable MaterialStatsId castingStatConflict) {
    super(serializer, id, group, Ingredient.of(result), itemCost, result, materials, true, false);
    this.castingStatConflict = castingStatConflict;
  }

  /** @deprecated use {@link #CompositeCastingRecipe(TypeAwareRecipeSerializer, ResourceLocation, String, int, IMaterialItem, IJsonPredicate, MaterialStatsId)} */
  @Deprecated(forRemoval = true)
  public CompositeCastingRecipe(TypeAwareRecipeSerializer<?> serializer, ResourceLocation id, String group, IMaterialItem result, int itemCost, @Nullable MaterialStatsId castingStatConflict) {
    this(serializer, id, group, itemCost, result, MaterialPredicate.ANY, castingStatConflict);
  }

  @Override
  protected MaterialFluidRecipe getFluidRecipe(ICastingContainer inv) {
    Fluid fluid = inv.getFluid();
    if (castingStatConflict != null) {
      // if we have casting recipe that matches our fluid and is valid for the result, return no match
      // used to prevent conflicts between tool casting and composite part casting
      MaterialFluidRecipe recipe = MaterialCastingLookup.getCastingFluid(fluid); // TODO: does this need a filter?
      if (recipe != MaterialFluidRecipe.EMPTY && castingStatConflict.canUseMaterial(recipe.getOutput().getId())) {
        return MaterialFluidRecipe.EMPTY;
      }
    }
    // find a composite match, requires fetching the material ID but not a huge deal as we already validated the cast (won't be calling this for multiple fluids)
    return MaterialCastingLookup.getCompositeFluid(fluid, IMaterialItem.getMaterialFromStack(inv.getStack()), materials);
  }

  /* JEI display */
  @Override
  public List<IDisplayableCastingRecipe> getRecipes(RegistryAccess access) {
    if (multiRecipes == null) {
      RecipeType<?> type = getType();
      ImmutableList.Builder<IDisplayableCastingRecipe> recipes = ImmutableList.builder();
      for (MaterialFluidRecipe recipe : MaterialCastingLookup.getAllCompositeFluids()) {
        MaterialVariant output = recipe.getOutput();
        MaterialVariant input = recipe.getInput();
        if (recipe.isVisible() && input != null
            && result.canUseMaterial(output.getId()) && result.canUseMaterial(input.getId())
            && this.materials.matches(output.getVariant())) {
          List<FluidStack> fluids = recipe.getFluids();
          if (castingStatConflict != null) {
            // if we require non-casting, filter out all fluids that match a casting recipe
            fluids = fluids.stream()
                           .filter(fluid -> {
                             MaterialFluidRecipe fluidRecipe = MaterialCastingLookup.getCastingFluid(fluid.getFluid());
                             // its fine if we have a recipe as long as the material is not usable by this part
                             return fluidRecipe == MaterialFluidRecipe.EMPTY || !castingStatConflict.canUseMaterial(fluidRecipe.getOutput().getId());
                           })
                           .toList();
          }
          if (!fluids.isEmpty()) {
            fluids = resizeFluids(recipe.getFluids());
            MaterialVariantId inputId = input.getVariant();
            List<ItemStack> inputs;
            if (inputId.getVariant().isEmpty()) {
              inputs = MaterialRecipeCache.getVariants(input.getId()).stream().map(result::withMaterial).toList();
            } else {
              inputs = List.of(result.withMaterial(inputId));
            }
            recipes.add(new DisplayCastingRecipe(getId(), type, inputs, fluids, result.withMaterial(output.getVariant()),
                                                 ICastingRecipe.calcCoolingTime(recipe.getTemperature(), itemCost * fluids.stream().mapToInt(FluidStack::getAmount).max().orElse(0)),
                                                 isConsumed()));
          }
        }
      }
      multiRecipes = recipes.build();
    }
    return multiRecipes;
  }
}

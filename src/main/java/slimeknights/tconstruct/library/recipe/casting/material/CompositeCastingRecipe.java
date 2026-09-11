package slimeknights.tconstruct.library.recipe.casting.material;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.helper.LoadableRecipeSerializer;
import slimeknights.mantle.recipe.helper.TypeAwareRecipeSerializer;
import slimeknights.tconstruct.library.json.predicate.material.MaterialPredicate;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.recipe.casting.ICastingContainer;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.material.DisplayMaterialCastingRecipe.CompositeFluid;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipeCache;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Casting recipe taking a part of a material and a fluid and outputting the part with a new material
 */
public class CompositeCastingRecipe extends MaterialCastingRecipe implements IMultiRecipe<IDisplayableCastingRecipe> {
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

  /* JEI */

  /** Grows the given list to the new size by repeating elements modulo */
  private static <T> List<T> growList(List<T> list, int newSize) {
    List<T> newList = new ArrayList<>(newSize);
    newList.addAll(list);
    int oldSize = list.size();
    for (int i = oldSize; i < newSize; i++) {
      newList.add(list.get(i % oldSize));
    }
    return newList;
  }

  @Override
  public List<IDisplayableCastingRecipe> getRecipes(RegistryAccess access) {
    if (multiRecipes == null) {
      List<MaterialFluidRecipe> recipes = MaterialCastingLookup.getSortedCompositeFluids();
      List<FluidStack> displayFluids = new ArrayList<>(recipes.size());
      List<ItemStack> displayInputs = new ArrayList<>(recipes.size());
      List<ItemStack> displayResults = new ArrayList<>(recipes.size());
      Object2IntMap<CompositeFluid> coolingTimes = new Object2IntOpenHashMap<>();
      int maxTime = 0;
      for (MaterialFluidRecipe recipe : MaterialCastingLookup.getAllCompositeFluids()) {
        MaterialVariant output = recipe.getOutput();
        MaterialVariantId outputId = output.getVariant();
        MaterialVariant input = recipe.getInput();
        if (input == null || !result.canUseMaterial(output.getId()) || !result.canUseMaterial(input.getId()) || !materials.matches(outputId)) {
          continue;
        }
        // filter fluids for the casting stat conflict
        List<FluidStack> fluids = recipe.getFluids();
        if (castingStatConflict != null) {
          // if we require non-casting, filter out all fluids that match a casting recipe
          fluids = fluids.stream()
            .filter(fluid -> {
              MaterialFluidRecipe fluidRecipe = MaterialCastingLookup.getCastingFluid(fluid.getFluid());
              // its fine if we have a recipe as long as the material is not usable by this part
              return fluidRecipe == MaterialFluidRecipe.EMPTY || !castingStatConflict.canUseMaterial(fluidRecipe.getOutput().getId());
            })
            .map(this::resizeFluid)
            .toList();
        } else {
          fluids = resizeFluids(fluids);
        }
        if (fluids.isEmpty()) continue;

        // expand input into a list of variants if no variant is matched
        MaterialVariantId inputId = input.getVariant();
        List<ItemStack> inputs;
        if (inputId.getVariant().isEmpty()) {
          // skip outputs that are the same variant as the input; those are not valid recipes
          inputs = MaterialRecipeCache.getVariants(input.getId()).stream().filter(material -> !output.sameVariant(material)).map(result::withMaterial).toList();
        } else {
          inputs = List.of(result.withMaterial(inputId.normalizeVariant()));
        }
        if (inputs.isEmpty()) continue;

        // store all cooling times now, before we duplicate fluids
        for (FluidStack fluid : fluids) {
          int time = ICastingRecipe.calcCoolingTime(recipe.getTemperature(), fluid.getAmount());
          coolingTimes.put(new CompositeFluid(fluid), time);
          if (time > maxTime) {
            maxTime = time;
          }
        }

        // its important that input and fluids are the same size. If not, pad the smaller one by cycling elements
        int inputSize = inputs.size();
        int fluidSize = fluids.size();
        if (inputSize != fluidSize) {
          if (inputSize < fluidSize) {
            inputs = growList(inputs, fluidSize);
          } else {
            fluids = growList(fluids, inputSize);
          }
        }
        // add items to the lists
        ItemStack result = this.result.withMaterial(outputId);
        for (int i = 0; i < inputs.size(); i++) {
          displayResults.add(result);
        }
        displayFluids.addAll(fluids);
        displayInputs.addAll(inputs);
      }

      // if no fluids, nothing to display
      if (displayFluids.isEmpty()) {
        multiRecipes = List.of();
      } else {
        multiRecipes = List.of(DisplayMaterialCastingRecipe.from(this)
          .casts(List.copyOf(displayInputs)).consumed()
          .fluids(List.copyOf(displayFluids))
          .results(List.copyOf(displayResults))
          .maxCoolingTime(maxTime)
          .composite(coolingTimes));
      }
    }
    return multiRecipes;
  }
}

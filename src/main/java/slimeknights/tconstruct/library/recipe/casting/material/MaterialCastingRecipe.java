package slimeknights.tconstruct.library.recipe.casting.material;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.helper.LoadableRecipeSerializer;
import slimeknights.mantle.recipe.helper.TypeAwareRecipeSerializer;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.json.predicate.material.MaterialPredicate;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.recipe.casting.CastingRecipeLookup;
import slimeknights.tconstruct.library.recipe.casting.ICastingContainer;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Casting recipe that takes an arbitrary fluid of a given amount and set the material on the output based on that fluid
 */
public class MaterialCastingRecipe extends AbstractMaterialCastingRecipe implements IMultiRecipe<IDisplayableCastingRecipe> {
  protected static final LoadableField<IMaterialItem,MaterialCastingRecipe> RESULT_FIELD = TinkerLoadables.MATERIAL_ITEM.requiredField("result", r -> r.result);
  public static final RecordLoadable<MaterialCastingRecipe> LOADER = RecordLoadable.create(
    LoadableRecipeSerializer.TYPED_SERIALIZER.requiredField(),
    ContextKey.ID.requiredField(), LoadableRecipeSerializer.RECIPE_GROUP, CAST_FIELD,
    ITEM_COST_FIELD, RESULT_FIELD, MATERIALS_FIELD, CAST_CONSUMED_FIELD, SWITCH_SLOTS_FIELD,
    MaterialCastingRecipe::new);

  protected final IMaterialItem result;

  public MaterialCastingRecipe(TypeAwareRecipeSerializer<?> serializer, ResourceLocation id, String group, Ingredient cast, int itemCost, IMaterialItem result, IJsonPredicate<MaterialVariantId> materials, boolean consumed, boolean switchSlots) {
    super(serializer, id, group, cast, itemCost, consumed, switchSlots, materials);
    this.result = result;
    CastingRecipeLookup.registerCastable(result);
    MaterialCastingLookup.registerItemCost(result, itemCost);
  }

  /** @deprecated use {@link #MaterialCastingRecipe(TypeAwareRecipeSerializer, ResourceLocation, String, Ingredient, int, IMaterialItem, IJsonPredicate, boolean, boolean)} */
  @Deprecated(forRemoval = true)
  public MaterialCastingRecipe(TypeAwareRecipeSerializer<?> serializer, ResourceLocation id, String group, Ingredient cast, int itemCost, IMaterialItem result, boolean consumed, boolean switchSlots) {
    this(serializer, id, group, cast, itemCost, result, MaterialPredicate.ANY, consumed, switchSlots);
  }

  @Override
  public boolean matches(ICastingContainer inv, Level worldIn) {
    if (!this.getCast().test(inv.getStack())) {
      return false;
    }
    MaterialFluidRecipe fluid = getFluidRecipe(inv);
    return fluid != MaterialFluidRecipe.EMPTY && result.canUseMaterial(fluid.getOutput().getId());
  }

  @Override
  public ItemStack getResultItem(RegistryAccess access) {
    return new ItemStack(result);
  }

  @Override
  public ItemStack assemble(ICastingContainer inv, RegistryAccess access) {
    return result.withMaterial(getFluidRecipe(inv).getOutput().getVariant());
  }


  /* JEI */
  protected List<IDisplayableCastingRecipe> multiRecipes;

  @Override
  public List<IDisplayableCastingRecipe> getRecipes(RegistryAccess access) {
    if (multiRecipes == null) {
      List<MaterialFluidRecipe> recipes = MaterialCastingLookup.getSortedCastingFluids();
      List<FluidStack> fluids = new ArrayList<>(recipes.size());
      List<ItemStack> results = new ArrayList<>(recipes.size());
      Object2IntMap<Fluid> coolingTimes = new Object2IntOpenHashMap<>();
      int maxTime = 0;
      for (MaterialFluidRecipe recipe : recipes) {
        // must support this material
        MaterialVariant output = recipe.getOutput();
        MaterialVariantId outputId = output.getVariant();
        if (!result.canUseMaterial(output.getId()) || !this.materials.matches(outputId)) {
          continue;
        }

        // add all fluids to our builders
        List<FluidStack> newFluids = resizeFluids(recipe.getFluids());
        fluids.addAll(newFluids);
        ItemStack result = this.result.withMaterial(outputId);
        for (FluidStack fluid : newFluids) {
          // add one copy of result per fluid
          results.add(result);
          // cache the time so we don't need to compute it again
          int time = ICastingRecipe.calcCoolingTime(recipe.getTemperature(), fluid.getAmount());
          coolingTimes.put(fluid.getFluid(), time);
          if (time > maxTime) {
            maxTime = time;
          }
        }
      }
      if (fluids.isEmpty()) {
        multiRecipes = List.of();
      } else {
        multiRecipes = List.of(DisplayMaterialCastingRecipe.from(this)
          .cast(getCast()).consumed(isConsumed())
          .fluids(List.copyOf(fluids))
          .results(List.copyOf(results))
          .coolingTimes(coolingTimes).maxCoolingTime(maxTime)
          .build());
      }
    }
    return multiRecipes;
  }
}

package slimeknights.tconstruct.library.recipe.casting.material;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.primitive.EnumLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.helper.LoadableRecipeSerializer;
import slimeknights.mantle.recipe.helper.TypeAwareRecipeSerializer;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.json.predicate.material.MaterialPredicate;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.recipe.RecipeSlot;
import slimeknights.tconstruct.library.recipe.casting.CastingRecipeLookup;
import slimeknights.tconstruct.library.recipe.casting.ICastingContainer;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.material.DisplayMaterialCastingRecipe.CompositeFluid;
import slimeknights.tconstruct.library.recipe.tinkerstation.building.MaterialSwappingRecipe;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/** Recipe for casting a tool using molten metal on either a tool part or a non-tool part (2 materials or 1) */
public class ToolCastingRecipe extends PartSwapCastingRecipe implements IMultiRecipe<IDisplayableCastingRecipe> {
  public static final RecordLoadable<ToolCastingRecipe> LOADER = RecordLoadable.create(
    LoadableRecipeSerializer.TYPED_SERIALIZER.requiredField(),
    ContextKey.ID.requiredField(), LoadableRecipeSerializer.RECIPE_GROUP, CAST_FIELD, ITEM_COST_FIELD,
    new EnumLoadable<>(CastPurpose.class).defaultField("cast_purpose", CastPurpose.MAYBE_MATERIAL, true, r -> r.castPurpose),
    TinkerLoadables.MODIFIABLE_ITEM.requiredField("result", r -> r.result),
    MATERIALS_FIELD,
    MaterialVariantId.LOADABLE.list(0).defaultField("extra_materials", List.of(), false, r -> r.extraMaterials),
    BooleanLoadable.INSTANCE.defaultField("fluid_swapping", true, false, r -> r.fluidSwapping),
    ToolCastingRecipe::new);

  private final IModifiable result;
  private final CastPurpose castPurpose;
  /** List of materials to add after the cast and fluid */
  private final List<MaterialVariantId> extraMaterials;
  /** If true, this recipe's information will be used to also add a fluid part swapping recipe. Mainly useful to disable if there are multiple copies of this recipe (like slimeskulls) */
  private final boolean fluidSwapping;

  protected ToolCastingRecipe(TypeAwareRecipeSerializer<?> serializer, ResourceLocation id, String group, Ingredient cast, int itemCost, CastPurpose castPurpose, IModifiable result, IJsonPredicate<MaterialVariantId> allowedMaterials, List<MaterialVariantId> extraMaterials, boolean fluidSwapping) {
    super(serializer, id, group, cast, itemCost, castPurpose.fluidIndex, allowedMaterials);
    this.result = result;
    this.extraMaterials = extraMaterials;
    CastingRecipeLookup.registerCastable(result);
    if (castPurpose == CastPurpose.CONSUMED_OFFSET && extraMaterials.isEmpty()) {
      TConstruct.LOG.error("Error creating recipe {}: Cannot use cast purpose of consume offset for a tool casting recipe with no extra materials, subbing in consumed.", id);
      this.castPurpose = CastPurpose.CONSUMED;
    } else {
      this.castPurpose = castPurpose;
    }
    this.fluidSwapping = fluidSwapping;
  }

  /** @deprecated use {@link #ToolCastingRecipe(TypeAwareRecipeSerializer, ResourceLocation, String, Ingredient, int, CastPurpose, IModifiable, IJsonPredicate, List, boolean)} */
  @Deprecated(forRemoval = true)
  protected ToolCastingRecipe(TypeAwareRecipeSerializer<?> serializer, ResourceLocation id, String group, Ingredient cast, int itemCost, CastPurpose castPurpose, IModifiable result, IJsonPredicate<MaterialVariantId> allowedMaterials, List<MaterialVariantId> extraMaterials) {
    this(serializer, id, group, cast, itemCost, castPurpose, result, allowedMaterials, extraMaterials, true);
  }

  /** @deprecated use {@link #ToolCastingRecipe(TypeAwareRecipeSerializer, ResourceLocation, String, Ingredient, int, CastPurpose, IModifiable, IJsonPredicate, List, boolean)} */
  @Deprecated(forRemoval = true)
  public ToolCastingRecipe(TypeAwareRecipeSerializer<?> serializer, ResourceLocation id, String group, Ingredient cast, int itemCost, IModifiable result) {
    this(serializer, id, group, cast, itemCost, CastPurpose.MAYBE_MATERIAL, result, MaterialPredicate.ANY, List.of());
  }

  @Override
  protected MaterialFluidRecipe getFluidRecipe(ICastingContainer inv) {
    // if its not part swapping, original lookup is best
    if (inv.getStack().getItem() != result.asItem()) {
      return MaterialCastingLookup.getCastingFluid(inv.getFluid(), materials);
    }
    return super.getFluidRecipe(inv);
  }

  @Override
  public boolean matches(ICastingContainer inv, Level level) {
    ItemStack cast = inv.getStack();
    // if the tool matches, perform part swapping
    if (fluidSwapping && cast.getItem() == result.asItem()) {
      return canPartSwap(inv);
    }
    // no tool match? need to check cast and fluid
    if (!this.getCast().test(cast)) {
      return false;
    }
    // if we have a material item input, must have exactly 2 materials, else exactly 1
    List<MaterialStatsId> requirements = ToolMaterialHook.stats(result.getToolDefinition());
    // last material is the part, may be index 0 or 1
    MaterialFluidRecipe recipe = getFluidRecipe(inv);
    return recipe != MaterialFluidRecipe.EMPTY && requirements.get(castPurpose == CastPurpose.MAYBE_MATERIAL ? requirements.size() - 1 : castPurpose.fluidIndex).canUseMaterial(recipe.getOutput().getId());
  }

  @Override
  public ItemStack getResultItem(RegistryAccess access) {
    return new ItemStack(result);
  }

  /** Creates the tool for the given cast and fluid material */
  private ItemStack assemble(ItemStack cast, MaterialVariant fluidMaterial) {
    // figure out how to apply our materials
    MaterialNBT.Builder materials = MaterialNBT.builder();
    // in offset mode, the first extra material goes before the fluid material
    boolean offset = castPurpose == CastPurpose.CONSUMED_OFFSET && !extraMaterials.isEmpty();
    if (offset) {
      materials.add(extraMaterials.get(0));
    }

    // if the cast material goes second, need our material now
    if (castPurpose == CastPurpose.SECOND_MATERIAL) {
      materials.add(fluidMaterial);
    }
    // add cast material if relevant
    if (castPurpose == CastPurpose.FIRST_MATERIAL || castPurpose == CastPurpose.SECOND_MATERIAL
      || castPurpose == CastPurpose.MAYBE_MATERIAL && ToolMaterialHook.stats(result.getToolDefinition()).size() > 1) {
      materials.add(IMaterialItem.getMaterialFromStack(cast));
    }
    // add fluid material
    if (castPurpose != CastPurpose.SECOND_MATERIAL) {
      materials.add(fluidMaterial);
    }
    // add extra materials
    if (offset) {
      for (int i = 1; i < extraMaterials.size(); i++) {
        materials.add(extraMaterials.get(i));
      }
    } else {
      materials.add(extraMaterials);
    }
    return ToolBuildHandler.buildItemFromMaterials(result, materials.build());
  }

  @Override
  public ItemStack assemble(ICastingContainer inv, RegistryAccess access) {
    // if the cast is the result, we are part swapping, replace the last material
    ItemStack cast = inv.getStack();
    if (fluidSwapping && cast.getItem() == result) {
      return super.assemble(inv, access);
    } else {
      return assemble(cast, getFluidRecipe(inv).getOutput());
    }
  }

  @Override
  public boolean isConsumed() {
    return castPurpose != CastPurpose.CATALYST;
  }

  @Override
  public boolean isConsumed(ICastingContainer inv) {
    // if part swapping, always consume the input
    return isConsumed() || inv.getStack().getItem() == result.asItem();
  }


  /* JEI display */

  @Override
  public List<IDisplayableCastingRecipe> getRecipes(RegistryAccess access) {
    if (multiRecipes == null) {
      List<MaterialStatsId> requirements = ToolMaterialHook.stats(result.getToolDefinition());
      if (requirements.size() < castPurpose.minMaterials) {
        TConstruct.LOG.error("Failed to create display recipes for Tool Casting {}: tool {} has too few materials.", getId(), result.getToolDefinition().getId());
        multiRecipes = List.of();
      } else {
        // tool is valid, create setup to call part swapping methods
        int fluidIndex = getIndex(requirements);
        ToolRequirement tool = new ToolRequirement(ToolStack.from(result.asItem(), result.getToolDefinition(), new CompoundTag()), fluidIndex, requirements.get(fluidIndex));
        MaterialSwappingRecipe.setMaterials(tool.tool(), fluidIndex, MaterialVariant.of(ToolBuildHandler.getRenderMaterial(0)));
        List<IDisplayableCastingRecipe> displayRecipes = new ArrayList<>(fluidSwapping ? 3 : 1);

        // casting recipes
        List<FluidRecipe> fluidRecipes = new ArrayList<>();
        Object2IntMap<Fluid> castingTimes = new Object2IntOpenHashMap<>();
        MaterialStatsId requirement = tool.requirement();
        int maxCoolingTime = prepareCastingRecipes(fluidRecipes, castingTimes, requirement);
        // this should never happen or the recipe would not exist, but better to not make broken JEI
        if (fluidRecipes.isEmpty()) {
          TConstruct.LOG.error("Failed to create display recipes for Tool Casting {}: no fluid casting matches conditions.", getId());
        } else {
          // build the cast swapping recipe, it has our fluids
          IDisplayableCastingRecipe castSwap = makeRecipe(tool, fluidRecipes, false, maxCoolingTime).casting(castingTimes);

          // if we have a second material, create the dynamic display recipe
          List<ItemStack> casts = List.of(getCast().getItems());
          if (castPurpose == CastPurpose.FIRST_MATERIAL || castPurpose == CastPurpose.SECOND_MATERIAL
            || castPurpose == CastPurpose.MAYBE_MATERIAL && requirements.size() > 1) {
            displayRecipes.add(new DisplayRecipe(fluidIndex, requirement, casts, castSwap.getFluids(), maxCoolingTime, castingTimes));
          } else {
            // standard display recipe, animates 1 material
            List<ItemStack> tools;
            if (!extraMaterials.isEmpty()) {
              // if we have extra materials, need to use them to compute the cast tools
              tools = fluidRecipes.stream().flatMap(recipe -> {
                // we already checked, cast is not a material item so doesn't matter what we pass in
                ItemStack withMaterial = assemble(ItemStack.EMPTY, recipe.output());
                return IntStream.range(0, recipe.fluids().size()).mapToObj(i -> withMaterial);
              }).toList();
            } else {
              tools = castSwap.getOutputs();
            }
            displayRecipes.add(DisplayMaterialCastingRecipe.from(this)
              .casts(casts).consumed(isConsumed())
              .fluids(castSwap.getFluids()).results(tools)
              .maxCoolingTime(maxCoolingTime).casting(castingTimes));
          }
          // want the cast swap to be second
          // still go through the trouble of making the recipe regardless as we wish to reuse a lot of its code
          if (fluidSwapping) {
            displayRecipes.add(castSwap);
          }
        }

        // composite recipe third
        if (fluidSwapping) {
          fluidRecipes.clear();
          Object2IntMap<CompositeFluid> compositeTimes = new Object2IntOpenHashMap<>();
          maxCoolingTime = prepareCompositeRecipes(fluidRecipes, compositeTimes, requirement);
          if (!fluidRecipes.isEmpty()) {
            displayRecipes.add(makeRecipe(tool, fluidRecipes, true, maxCoolingTime).composite(compositeTimes));
          }
        }

        // build final list
        this.multiRecipes = List.copyOf(displayRecipes);
      }
    }
    return multiRecipes;
  }

  /** Enum describing the function of the cast in this recipe */
  @RequiredArgsConstructor
  public enum CastPurpose {
    /**
     * Based on the material definition stat count, cast is either the first material or has no material purpose.
     * @deprecated use {@link #CONSUMED} or {@link #FIRST_MATERIAL}.
     */
    @Deprecated
    MAYBE_MATERIAL(-1, 1),
    /** Cast is not consumed by the recipe */
    CATALYST(0, 1),
    /** Cast is consumed, but has no material purpose */
    CONSUMED(0, 1),
    /** Cast is consumed, but has no material purpose. However, an extra material will set material 1 on the tool */
    CONSUMED_OFFSET(1, 2),
    /** Cast is consumed, and becomes the first material with the fluid the second. */
    FIRST_MATERIAL(1, 2),
    /** Cast is consumed, and becomes the second material with the fluid the first. */
    SECOND_MATERIAL(0, 2);

    /** Index for the fluid in the recipe. If -1, means the fluid is the last material. */
    private final int fluidIndex;
    /** Minimum number of materials needed for this cast */
    private final int minMaterials;
  }

  /**
   * Recipe to display tool casting with multiple materials.
   * Only used if {@link #castPurpose} is {@link CastPurpose#FIRST_MATERIAL}, {@link CastPurpose#SECOND_MATERIAL}, or {@link CastPurpose#MAYBE_MATERIAL} with the materrial confirmed.
   */
  private class DisplayRecipe implements IDisplayableCastingRecipe {
    @Getter
    private final List<ItemStack> castItems;
    @Getter
    private final List<FluidStack> fluids;
    @Getter
    private final List<ItemStack> outputs;
    @Getter
    private final int coolingTime;
    private final Object2IntMap<Fluid> coolingTimes;

    /** Index on the tool holding the fluid material. */
    private final int fluidIndex;
    /** Index on the tool holding the cast material. */
    private final int castIndex;
    /** Material item representing the cast, used when a tool is the output focus. */
    private final IMaterialItem castItem;
    /** Filter to find fluid recipes given the material on the tool */
    private final Predicate<MaterialFluidRecipe> fluidFilter;

    private DisplayRecipe(int fluidIndex, MaterialStatsId statType, List<ItemStack> castItems, List<FluidStack> fluids, int coolingTime, Object2IntMap<Fluid> coolingTimes) {
      this.castItems = castItems;
      this.fluids = fluids;
      this.outputs = List.of(IModifiableDisplay.getDisplayStack(result.asItem()));
      this.coolingTime = coolingTime;
      this.coolingTimes = coolingTimes;
      this.castIndex = castPurpose == CastPurpose.SECOND_MATERIAL ? 1 : 0;
      this.fluidIndex = fluidIndex;
      // while we should always have a cast item, might as well be safe
      this.castItem = !castItems.isEmpty() && castItems.get(0).getItem() instanceof IMaterialItem item ? item : IMaterialItem.EMPTY;
      // create the filter for the fluid recipes, no reason to compute this every display update
      // create a filter for fluid recipes
      this.fluidFilter = recipe -> {
        MaterialVariantId output = recipe.getOutput().getVariant();
        return materials.matches(output) && statType.canUseMaterial(output.getId());
      };
    }

    @Override
    public boolean hasCast() {
      // always have a cast, it provides our material
      return true;
    }

    @Override
    public boolean isConsumed() {
      return true;
    }

    @Override
    public boolean linkCastToOutput() {
      // we are using display update to handle output materials instead of a focus link
      return false;
    }


    /* Dynamic */

    @Override
    public boolean isCoolingTimeDynamic() {
      return true;
    }

    @Override
    public int getCoolingTime(FluidStack fluid) {
      return coolingTimes.getOrDefault(fluid, coolingTime);
    }

    @Override
    public List<ItemStack> getCastItems(ItemStack focus, boolean focusOutput) {
      if (focusOutput && !focus.isEmpty()) {
        MaterialVariantId castMaterial = MaterialIdNBT.from(focus).getMaterial(castIndex);
        if (castItem.canUseMaterial(castMaterial.getId())) {
          return List.of(castItem.withMaterialForDisplay(castMaterial));
        }
      }
      return castItems;
    }

    @Override
    public List<FluidStack> getFluids(ItemStack focus, boolean focusOutput) {
      if (focusOutput && !focus.isEmpty()) {
        List<FluidStack> fluids = MaterialCastingLookup.getCastingFluids(MaterialIdNBT.from(focus).getMaterial(fluidIndex)).stream()
          .filter(fluidFilter)
          .flatMap(recipe -> recipe.getFluids().stream().map(ToolCastingRecipe.this::resizeFluid))
          .toList();
        if (!fluids.isEmpty()) {
          return fluids;
        }
      }
      return this.fluids;
    }

    @Override
    public boolean isSlotsDynamic() {
      return true;
    }

    @Override
    public void onDisplayUpdate(RecipeSlot<ItemStack> cast, RecipeSlot<FluidStack> fluid, RecipeSlot<ItemStack> output) {
      // set the output based on the current cast and the current fluid, however the recipe would regularly do that
      output.set(assemble(cast.get(), MaterialCastingLookup.getCastingFluid(fluid.get().getFluid()).getOutput()));
    }

    /** @deprecated use {@link #getOutputs()} */
    @Deprecated
    @Override
    public ItemStack getOutput() {
      return new ItemStack(result);
    }
  }
}

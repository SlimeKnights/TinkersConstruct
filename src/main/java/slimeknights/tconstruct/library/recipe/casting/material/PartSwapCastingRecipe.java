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
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.helper.LoadableRecipeSerializer;
import slimeknights.mantle.recipe.helper.TypeAwareRecipeSerializer;
import slimeknights.tconstruct.library.json.predicate.material.MaterialPredicate;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.recipe.casting.AbstractCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.ICastingContainer;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.material.DisplayMaterialCastingRecipe.CompositeFluid;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.building.MaterialSwappingRecipe;
import slimeknights.tconstruct.library.tools.definition.module.material.MaterialRepairModule;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Recipe for allowing part swapping on casting, without making the tool craftable on casting.
 * @see ToolCastingRecipe
 */
public class PartSwapCastingRecipe extends AbstractMaterialCastingRecipe implements IMultiRecipe<IDisplayableCastingRecipe> {
  public static final RecordLoadable<PartSwapCastingRecipe> LOADER = RecordLoadable.create(
    LoadableRecipeSerializer.TYPED_SERIALIZER.requiredField(),
    ContextKey.ID.requiredField(), LoadableRecipeSerializer.RECIPE_GROUP,
    IngredientLoadable.ALLOW_EMPTY.requiredField("tools", AbstractCastingRecipe::getCast),
    ITEM_COST_FIELD,
    IntLoadable.FROM_ZERO.requiredField("index", r -> r.index),
    MATERIALS_FIELD,
    PartSwapCastingRecipe::new);

  private final int index;
  /** Last composite casting recipe to match, speeds up recipe lookup for cooling time and fluid amount */
  @Nullable
  private MaterialFluidRecipe cachedPartSwapping = null;

  protected PartSwapCastingRecipe(TypeAwareRecipeSerializer<?> serializer, ResourceLocation id, String group, Ingredient cast, int itemCost, int index, IJsonPredicate<MaterialVariantId> materials) {
    super(serializer, id, group, cast, itemCost, true, false, materials);
    this.index = index;
  }

  /** @deprecated use {@link #PartSwapCastingRecipe(TypeAwareRecipeSerializer, ResourceLocation, String, Ingredient, int, int, IJsonPredicate)} */
  @Deprecated(forRemoval = true)
  protected PartSwapCastingRecipe(TypeAwareRecipeSerializer<?> serializer, ResourceLocation id, String group, Ingredient cast, int itemCost, int index) {
    this(serializer, id, group, cast, itemCost, index, MaterialPredicate.ANY);
  }

  /** Maps negative indices to the end of the parts list */
  protected int getIndex(List<MaterialStatsId> requirements) {
    if (index < 0) {
      return requirements.size() + index;
    }
    return index;
  }

  @Override
  protected MaterialFluidRecipe getFluidRecipe(ICastingContainer inv) {
    return inv.getStack().getItem() instanceof IModifiable modifiable ? getFluidRecipe(inv, modifiable) : MaterialFluidRecipe.EMPTY;
  }

  protected MaterialFluidRecipe getFluidRecipe(ICastingContainer inv, IModifiable modifiable) {
    ItemStack stack = inv.getStack();
    // so we are part swapping, we might have a casting or a composite recipe. We only do composite if the fluid does not match casting
    // start with the cached part swapping, can be either type. No need to check casting stat type here as it would never get cached if invalid
    Fluid fluid = inv.getFluid();
    List<MaterialStatsId> requirements = ToolMaterialHook.stats(modifiable.getToolDefinition());
    int index = getIndex(requirements);
    MaterialVariantId currentMaterial = MaterialIdNBT.from(stack).getMaterial(index);
    if (cachedPartSwapping != null && cachedPartSwapping.matches(fluid, currentMaterial)) {
      return cachedPartSwapping;
    }
    // cache did not match? try a casting recipe.
    // note its possible we have a valid casting material that is just not valid for this tool, hence the extra check
    // the casting recipe needs to match our stat type to be valid
    MaterialFluidRecipe casting = MaterialCastingLookup.getCastingFluid(fluid, materials);
    // need to validate the stat type, since the super call will not check stat type
    if (casting != MaterialFluidRecipe.EMPTY && !casting.getOutput().sameVariant(currentMaterial) && requirements.get(index).canUseMaterial(casting.getOutput().getId())) {
      cachedPartSwapping = casting;
      return casting;
    }
    // no casting? try composite.
    MaterialFluidRecipe composite = MaterialCastingLookup.getCompositeFluid(fluid, currentMaterial, materials);
    if (composite != MaterialFluidRecipe.EMPTY) {
      cachedPartSwapping = composite;
      return composite;
    }
    return MaterialFluidRecipe.EMPTY;
  }

  /** Checks if part swapping is possible on this tool */
  protected boolean canPartSwap(ICastingContainer inv) {
    ItemStack cast = inv.getStack();
    if (!(cast.getItem() instanceof IModifiable modifiable)) {
      return false;
    }
    // if we have a material item input, must have exactly 2 materials, else exactly 1
    List<MaterialStatsId> requirements = ToolMaterialHook.stats(modifiable.getToolDefinition());
    int index = getIndex(requirements);
    // must have enough parts
    if (index >= requirements.size()) {
      return false;
    }
    // must have a valid material
    MaterialFluidRecipe recipe = getFluidRecipe(inv, modifiable);
    MaterialVariant output = recipe.getOutput();
    if (recipe == MaterialFluidRecipe.EMPTY || !requirements.get(index).canUseMaterial(output.getId())) {
      return false;
    }
    // ensure the tool is still valid after replacing
    ToolStack original = ToolStack.from(cast);
    ToolStack tool = original.copy();
    tool.replaceMaterial(index, output);
    return tool.tryValidate() == null && ModifierRemovalHook.onRemoved(original, tool) == null;
  }

  @Override
  public boolean matches(ICastingContainer inv, Level level) {
    return getCast().test(inv.getStack()) && canPartSwap(inv);
  }

  @Override
  public ItemStack getResultItem(RegistryAccess registryAccess) {
    return getCast().getItems()[0].copy();
  }

  @Override
  public ItemStack assemble(ICastingContainer inv, RegistryAccess access) {
    MaterialFluidRecipe fluidRecipe = getFluidRecipe(inv);
    MaterialVariant material = fluidRecipe.getOutput();
    ItemStack cast = inv.getStack();
    ToolStack original = ToolStack.from(cast);
    ToolStack tool = original.copy();
    List<MaterialStatsId> stats = ToolMaterialHook.stats(tool.getDefinition());
    int index = getIndex(stats);
    tool.replaceMaterial(index, material);
    // don't repair if its a composite recipe, since those are not paying the proper repair cost
    if (fluidRecipe.getInput() == null) {
      // if its a new material, repair with the head stat
      // with the tools we have this will always be a full repair, but addon usage of this recipe may vary
      float repairDurability = MaterialRepairModule.getDurability(null, material.getId(), stats.get(index));
      if (repairDurability > 0 && tool.getDamage() > 0) {
        repairDurability *= itemCost / MaterialRecipe.INGOTS_PER_REPAIR;
        for (ModifierEntry entry : tool.getModifierList()) {
          repairDurability = entry.getHook(ModifierHooks.REPAIR_FACTOR).getRepairFactor(tool, entry, repairDurability);
          if (repairDurability <= 0) {
            break;
          }
        }
        if (repairDurability > 0) {
          ToolDamageUtil.repair(tool, (int)repairDurability);
        }
      }
    }
    // validate and run removal hooks, but don't give up if either failed (hopefully matches dealt with that)
    tool.tryValidate();
    ModifierRemovalHook.onRemoved(original, tool);
    return tool.copyStack(cast, 1);
  }


  /* JEI display */
  protected List<IDisplayableCastingRecipe> multiRecipes;

  /** Gets the max fluid amount from a list of fluids */
  protected static int getFluidAmount(List<FluidStack> fluids) {
    return fluids.stream().mapToInt(FluidStack::getAmount).max().orElse(0);
  }

  /** Helper class for storing a tool ready to swap. Used in JEI displays for part swapping recipes. */
  protected record ToolRequirement(ToolStack tool, int index, MaterialStatsId requirement) {}

  /** Gets tool requirement objects from the casts ingredient */
  protected List<ToolRequirement> getTools(ItemStack... casts) {
    // always use 0 as it has good contract
    MaterialVariant renderMaterial = MaterialVariant.of(ToolBuildHandler.getRenderMaterial(0));
    List<ToolRequirement> tools = new ArrayList<>(casts.length);
    for (ItemStack cast : casts) {
      ToolStack tool = ToolStack.from(cast);
      List<MaterialStatsId> requirements = ToolMaterialHook.stats(tool.getDefinition());
      int index = getIndex(requirements);
      if (index < requirements.size()) {
        tool = tool.copy();
        MaterialSwappingRecipe.setMaterials(tool, index, renderMaterial);
        tools.add(new ToolRequirement(tool, index, requirements.get(index)));
      }
    }
    return tools;
  }

  /** Compacted form of {@link MaterialFluidRecipe}, used for holding fluids rescaled to the tool. */
  protected record FluidRecipe(List<FluidStack> fluids, MaterialVariant input, MaterialVariant output) {
    public FluidRecipe(List<FluidStack> fluids, MaterialVariant material) {
      // to avoid nullable, just use the same object for both output and input
      this(fluids, material, material);
    }
  }

  /** Sets up the data structures for {@link #makeRecipes(List, List, List, boolean, int, Function)} */
  protected int prepareCastingRecipes(List<FluidRecipe> castingRecipes, Object2IntMap<Fluid> coolingTimes, @Nullable MaterialStatsId statType) {
    int maxCoolingTime = 0;
    // start by filtering using the predicate and making the cooling time map
    // will share the same map for all created recipes, even if they ended up filtering it further
    for (MaterialFluidRecipe recipe : MaterialCastingLookup.getSortedCastingFluids()) {
      MaterialVariant output = recipe.getOutput();
      // ensure the recipe can use the material
      if (!materials.matches(output.getVariant())) continue;
      if (statType != null && !statType.canUseMaterial(output.getId())) continue;
      // prepare the fluids
      List<FluidStack> fluids = resizeFluids(recipe.getFluids());
      for (FluidStack fluid : fluids) {
        int time = ICastingRecipe.calcCoolingTime(recipe.getTemperature(), fluid.getAmount());
        coolingTimes.put(fluid.getFluid(), time);
        if (time > maxCoolingTime) {
          maxCoolingTime = time;
        }
      }
      castingRecipes.add(new FluidRecipe(fluids, output));
    }
    return maxCoolingTime;
  }

  /** Creates the composite recipes for each tool and adds them to {@code displayRecipes} */
  protected int prepareCompositeRecipes(List<FluidRecipe> compositeRecipes, Object2IntMap<CompositeFluid> coolingTimes, @Nullable MaterialStatsId statType) {
    // mostly same deal as casting before, but now we care about the input material
    int maxCoolingTime = 0;
    // again, filtering using the predicate and make cooling time map
    for (MaterialFluidRecipe recipe : MaterialCastingLookup.getSortedCompositeFluids()) {
      MaterialVariantId output = recipe.getOutput().getVariant();
      // ensure the recipe can use the materials
      if (!materials.matches(output)) continue;
      if (statType != null && !statType.canUseMaterial(output.getId())) continue;
      // scale the fluids to the recipe size
      List<FluidStack> fluids = resizeFluids(recipe.getFluids());
      for (FluidStack fluid : fluids) {
        int time = ICastingRecipe.calcCoolingTime(recipe.getTemperature(), fluid.getAmount());
        coolingTimes.put(new CompositeFluid(fluid), time);
        if (time > maxCoolingTime) {
          maxCoolingTime = time;
        }
      }
      compositeRecipes.add(new FluidRecipe(fluids, Objects.requireNonNull(recipe.getInput()), recipe.getOutput()));
    }
    return maxCoolingTime;
  }

  /** Creates the recipes for each tool and adds them to {@code displayRecipes} */
  protected void makeRecipes(List<IDisplayableCastingRecipe> displayRecipes, List<ToolRequirement> tools, List<FluidRecipe> fluidRecipes, boolean uniqueInput, int maxCoolingTime, Function<DisplayMaterialCastingRecipe.Builder,IDisplayableCastingRecipe> constructor) {
    // if we have any, add recipes for casting
    if (!fluidRecipes.isEmpty()) {
      // start building a recipe per tool type
      for (ToolRequirement tool : tools) {
        // filter down materials to just those applicable to the tool
        List<FluidRecipe> filtered = fluidRecipes.stream().filter(recipe -> tool.requirement.canUseMaterial(recipe.output.getId())).toList();
        if (!filtered.isEmpty()) {
          displayRecipes.add(constructor.apply(makeRecipe(tool, filtered, uniqueInput, maxCoolingTime)));
        }
      }
    }
  }

  /** Prepares the builder for a recipe given the fluid recipes. */
  protected DisplayMaterialCastingRecipe.Builder makeRecipe(ToolRequirement tool, List<FluidRecipe> fluidRecipes, boolean uniqueInput, int maxCoolingTime) {
    // create lists of fluids and results of the same size
    List<ItemStack> inputs;
    if (uniqueInput) {
      inputs = new ArrayList<>();
    } else {
      inputs = List.of(tool.tool.createStack());
    }
    List<ItemStack> results = new ArrayList<>();
    List<FluidStack> fluids = new ArrayList<>();
    for (FluidRecipe recipe : fluidRecipes) {
      List<FluidStack> newFluids = recipe.fluids();
      fluids.addAll(newFluids);
      ToolStack copy = tool.tool.copy();
      // add inputs if requested
      if (uniqueInput) {
        copy.replaceMaterial(tool.index, recipe.input);
        // copy to unlink from the tool instance
        ItemStack input = copy.createStack().copy();
        for (int i = 0; i < newFluids.size(); i++) {
          inputs.add(input);
        }
      }
      // add result tool regardless
      copy.replaceMaterial(tool.index, recipe.output);
      ItemStack result = copy.createStack();
      for (int i = 0; i < newFluids.size(); i++) {
        results.add(result);
      }
    }
    return DisplayMaterialCastingRecipe.from(this)
      .casts(inputs).consumed()
      .results(List.copyOf(results))
      .fluids(List.copyOf(fluids))
      .maxCoolingTime(maxCoolingTime);
  }

  @Override
  public List<IDisplayableCastingRecipe> getRecipes(RegistryAccess access) {
    if (multiRecipes == null) {
      List<ToolRequirement> tools = getTools(getCast().getItems());
      if (tools.isEmpty()) {
        multiRecipes = List.of();
      } else {
        // we have tools, start building recipes
        List<IDisplayableCastingRecipe> displayRecipes = new ArrayList<>(tools.size() * 2);

        // casting recipes
        List<FluidRecipe> fluidRecipes = new ArrayList<>();
        Object2IntMap<Fluid> castingTimes = new Object2IntOpenHashMap<>();
        int maxCoolingTime = prepareCastingRecipes(fluidRecipes, castingTimes, null);
        makeRecipes(displayRecipes, tools, fluidRecipes, false, maxCoolingTime, builder -> builder.casting(castingTimes));

        // composite recipes
        fluidRecipes.clear();
        Object2IntMap<CompositeFluid> compositeTimes = new Object2IntOpenHashMap<>();
        maxCoolingTime = prepareCompositeRecipes(fluidRecipes, compositeTimes, null);
        makeRecipes(displayRecipes, tools, fluidRecipes, true, maxCoolingTime, builder -> builder.composite(compositeTimes));

        // make final recipe list
        this.multiRecipes = List.copyOf(displayRecipes);
      }
    }
    return multiRecipes;
  }

  // TODO: can use custom display recipe so we show the materials of your tool on the input/output
}

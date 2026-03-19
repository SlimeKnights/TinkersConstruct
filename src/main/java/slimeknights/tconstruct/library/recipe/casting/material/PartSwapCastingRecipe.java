package slimeknights.tconstruct.library.recipe.casting.material;

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
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.recipe.casting.AbstractCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.DisplayCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.ICastingContainer;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.tools.definition.module.material.MaterialRepairModule;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.helper.TooltipUtil;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
  private int getIndex(List<MaterialStatsId> requirements) {
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

  /** Creates a new item stack with the given material. Will modify {@code tool}. */
  private ItemStack withMaterial(ToolStack tool, MaterialVariant material) {
    if (tool.getMaterials().isEmpty()) {
      MaterialNBT.Builder builder = MaterialNBT.builder();
      List<MaterialStatsId> requirements = ToolMaterialHook.stats(tool.getDefinition());
      for (int i = 0; i < requirements.size(); i++) {
        if (i == index) {
          builder.add(material);
        } else {
          builder.add(MaterialRegistry.firstWithStatType(requirements.get(i)));
        }
      }
      tool.setMaterials(builder.build());
    } else {
      // if it has materials already just swap the one to update
      tool.replaceMaterial(index, material);
    }
    return tool.createStack();
  }

  @Override
  public List<IDisplayableCastingRecipe> getRecipes(RegistryAccess access) {
    if (multiRecipes == null) {
      List<ItemStack> casts = List.of(getCast().getItems());
      Predicate<MaterialFluidRecipe> validRecipe = recipe -> recipe.isVisible() && materials.matches(recipe.getOutput().getVariant());
      multiRecipes = Stream.concat(
          // show recipes for creating the tool from all castable fluids
          MaterialCastingLookup.getAllCastingFluids().stream()
            .filter(validRecipe)
            .flatMap(recipe -> {
              // map each cast item to contain the new material
              MaterialVariant output = recipe.getOutput();
              List<ItemStack> inputs = new ArrayList<>(casts.size());
              List<ItemStack> results = new ArrayList<>(casts.size());
              for (ItemStack cast : casts) {
                ToolStack tool = ToolStack.copyFrom(cast);
                // must support the stat type to consider
                List<MaterialStatsId> requirements = ToolMaterialHook.stats(tool.getDefinition());
                if (index < requirements.size() && requirements.get(index).canUseMaterial(output.getId())) {
                  results.add(withMaterial(tool, output).copy());
                  // mark input as display so tooltip does not show useless stats
                  ItemStack input = withMaterial(tool, MaterialVariant.of(ToolBuildHandler.getRenderMaterial(0)));
                  input.getOrCreateTag().putBoolean(TooltipUtil.KEY_DISPLAY, true);
                  inputs.add(input);
                }
              }
              // if nothing is supported, skip this fluid
              if (results.isEmpty()) {
                return Stream.empty();
              }
              List<FluidStack> fluids = resizeFluids(recipe.getFluids());
              return Stream.of(new DisplayCastingRecipe(getId(), getType(), List.copyOf(inputs), fluids, List.copyOf(results),
                ICastingRecipe.calcCoolingTime(recipe.getTemperature(), itemCost * getFluidAmount(fluids)), isConsumed()));
            }),
          // all composite fluids become special composite swapping recipes
          MaterialCastingLookup.getAllCompositeFluids().stream()
            .filter(validRecipe)
            .flatMap(recipe -> {
              // start creating our list of tools to display
              MaterialVariant output = recipe.getOutput();
              MaterialVariant input = recipe.getInput();
              assert input != null;
              List<ItemStack> inputs = new ArrayList<>(casts.size());
              List<ItemStack> outputs = new ArrayList<>(casts.size());
              for (ItemStack cast : casts) {
                ToolStack tool = ToolStack.copyFrom(cast);
                // tool must support the material at the given index
                List<MaterialStatsId> requirements = ToolMaterialHook.stats(tool.getDefinition());
                if (index < requirements.size()) {
                  MaterialStatsId requirement = requirements.get(index);
                  if (requirement.canUseMaterial(output.getId()) && requirement.canUseMaterial(input.getId())) {
                    // these are both using the same tool stack, but since we copy input immediately that delinks them
                    // as a bonus, saves us doing the first lookup twice if there were no materials present before
                    inputs.add(withMaterial(tool, input).copy());
                    outputs.add(withMaterial(tool, output));
                  }
                }
              }
              // if we found no valid tools, skip this fluid
              if (inputs.isEmpty() || outputs.isEmpty()) {
                return Stream.empty();
              }
              // build the recipe
              List<FluidStack> fluids = resizeFluids(recipe.getFluids());
              return Stream.of(new DisplayCastingRecipe(getId(), getType(), List.copyOf(inputs), fluids, List.copyOf(outputs),
                ICastingRecipe.calcCoolingTime(recipe.getTemperature(), itemCost * getFluidAmount(fluids)), isConsumed()));
            })
        )
        .collect(Collectors.toList());
    }
    return multiRecipes;
  }
}

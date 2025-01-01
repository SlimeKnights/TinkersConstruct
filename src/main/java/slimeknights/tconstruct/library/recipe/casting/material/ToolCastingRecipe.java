package slimeknights.tconstruct.library.recipe.casting.material;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.helper.LoadableRecipeSerializer;
import slimeknights.mantle.recipe.helper.TypeAwareRecipeSerializer;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.recipe.casting.DisplayCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.ICastingContainer;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.tools.definition.module.material.MaterialRepairModule;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Recipe for casting a tool using molten metal on either a tool part or a non-tool part (2 materials or 1) */
public class ToolCastingRecipe extends AbstractMaterialCastingRecipe implements IMultiRecipe<IDisplayableCastingRecipe> {
  public static final RecordLoadable<ToolCastingRecipe> LOADER = RecordLoadable.create(
    LoadableRecipeSerializer.TYPED_SERIALIZER.requiredField(),
    ContextKey.ID.requiredField(), LoadableRecipeSerializer.RECIPE_GROUP, CAST_FIELD, ITEM_COST_FIELD,
    TinkerLoadables.MODIFIABLE_ITEM.requiredField("result", r -> r.result),
    ToolCastingRecipe::new);

  private final IModifiable result;
  /** Last composite casting recipe to match, speeds up recipe lookup for cooling time and fluid amount */
  @Nullable
  private MaterialFluidRecipe cachedPartSwapping = null;
  public ToolCastingRecipe(TypeAwareRecipeSerializer<?> serializer, ResourceLocation id, String group, Ingredient cast, int itemCost, IModifiable result) {
    super(serializer, id, group, cast, itemCost, true, false);
    this.result = result;
  }

  @Override
  protected MaterialFluidRecipe getFluidRecipe(ICastingContainer inv) {
    ItemStack stack = inv.getStack();
    // if its not part swapping, super is sufficient
    if (stack.getItem() != result.asItem()) {
      return super.getFluidRecipe(inv);
    }
    // so we are part swapping, we might have a casting or a composite recipe. We only do composite if the fluid does not match casting
    // start with the cached part swapping, can be either type. No need to check casting stat type here as it would never get cached if invalid
    Fluid fluid = inv.getFluid();
    List<MaterialStatsId> requirements = ToolMaterialHook.stats(result.getToolDefinition());
    int indexToCheck = requirements.size() - 1;
    MaterialVariantId currentMaterial = MaterialIdNBT.from(stack).getMaterial(indexToCheck);
    if (cachedPartSwapping != null && cachedPartSwapping.matches(fluid, currentMaterial)) {
      return cachedPartSwapping;
    }
    // cache did not match? try a casting recipe.
    // note its possible we have a valid casting material that is just not valid for this tool, hence the extra check
    // the casting recipe needs to match our stat type to be valid
    MaterialFluidRecipe casting = super.getFluidRecipe(inv);
    // need to validate the stat type, since the super call will not check stat type
    if (casting != MaterialFluidRecipe.EMPTY && !casting.getOutput().sameVariant(currentMaterial) && requirements.get(indexToCheck).canUseMaterial(casting.getOutput().getId())) {
      cachedPartSwapping = casting;
      return casting;
    }
    // no casting? try composite.
    MaterialFluidRecipe composite = MaterialCastingLookup.getCompositeFluid(fluid, currentMaterial);
    if (composite != MaterialFluidRecipe.EMPTY) {
      cachedPartSwapping = composite;
      return composite;
    }
    return MaterialFluidRecipe.EMPTY;
  }

  @Override
  public boolean matches(ICastingContainer inv, Level level) {
    ItemStack cast = inv.getStack();
    // tool match is used for part swapping
    boolean partSwapping = cast.getItem() == result.asItem();
    if (!partSwapping && !this.getCast().test(cast)) {
      return false;
    }
    // if we have a material item input, must have exactly 2 materials, else exactly 1
    List<MaterialStatsId> requirements = ToolMaterialHook.stats(result.getToolDefinition());
    // must have 1 or 2 tool parts
    int numRequirements = requirements.size();
    if (numRequirements < 1 || numRequirements > 2) {
      return false;
    }
    // last material is the part, may be index 0 or 1
    MaterialFluidRecipe recipe = getFluidRecipe(inv);
    return recipe != MaterialFluidRecipe.EMPTY && requirements.get(numRequirements - 1).canUseMaterial(recipe.getOutput().getId());
  }

  @Override
  public ItemStack getResultItem(RegistryAccess access) {
    return new ItemStack(result);
  }

  @Override
  public ItemStack assemble(ICastingContainer inv, RegistryAccess access) {
    MaterialFluidRecipe fluidRecipe = getFluidRecipe(inv);
    MaterialVariant material = fluidRecipe.getOutput();
    ItemStack cast = inv.getStack();
    List<MaterialStatsId> stats = ToolMaterialHook.stats(result.getToolDefinition());
    // if the cast is the result, we are part swapping, replace the last material
    if (cast.getItem() == result) {
      ToolStack tool = ToolStack.from(cast);
      int replaceIndex = stats.size() - 1;
      tool.replaceMaterial(replaceIndex, material.getVariant());
      // don't repair if its a composite recipe, since those are not paying the proper repair cost
      if (fluidRecipe.getInput() == null) {
        // if its a new material, repair with the head stat
        // with the tools we have this will always be a full repair, but addon usage of this recipe may vary
        float repairDurability = MaterialRepairModule.getDurability(null, material.getId(), stats.get(replaceIndex));
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
      return tool.createStack();
    } else {
      MaterialNBT materials;
      // if we have 2 materials, we assume the cast has a material. 1 means the cast is a random item
      if (stats.size() == 2) {
        materials = new MaterialNBT(List.of(MaterialVariant.of(IMaterialItem.getMaterialFromStack(cast)), material));
      } else {
        materials = new MaterialNBT(List.of(material));
      }
      return ToolBuildHandler.buildItemFromMaterials(result, materials);
    }
  }


  /* JEI display */
  protected List<IDisplayableCastingRecipe> multiRecipes;

  @Override
  public List<IDisplayableCastingRecipe> getRecipes(RegistryAccess access) {
    if (multiRecipes == null) {
      List<MaterialStatsId> requirements = ToolMaterialHook.stats(result.getToolDefinition());
      if (requirements.isEmpty()) {
        multiRecipes = List.of();
      } else {
        RecipeType<?> type = getType();
        List<ItemStack> castItems = Arrays.asList(getCast().getItems());
        MaterialStatsId requirement = requirements.get(requirements.size() - 1);
        // if we have two item requirement, fill in the part in display
        Function<MaterialVariant,MaterialNBT> materials;
        if (requirements.size() > 1) {
          MaterialVariant firstMaterial = MaterialVariant.of(MaterialRegistry.firstWithStatType(requirements.get(0)));
          materials = mat -> new MaterialNBT(List.of(firstMaterial, mat));
        } else {
          materials = mat -> new MaterialNBT(List.of(mat));
        }
        multiRecipes = MaterialCastingLookup
          .getAllCastingFluids().stream()
          .filter(recipe -> {
            MaterialVariant output = recipe.getOutput();
            return !output.isUnknown() && !output.get().isHidden() && requirement.canUseMaterial(output.getId());
          })
          .map(recipe -> {
            List<FluidStack> fluids = resizeFluids(recipe.getFluids());
            int fluidAmount = fluids.stream().mapToInt(FluidStack::getAmount).max().orElse(0);
            // TODO: would be nice to have a list of outputs based on the different inputs
            return new DisplayCastingRecipe(type, castItems, fluids,
                                            ToolBuildHandler.buildItemFromMaterials(result, materials.apply(recipe.getOutput())),
                                            ICastingRecipe.calcCoolingTime(recipe.getTemperature(), itemCost * fluidAmount), isConsumed());
          })
          .collect(Collectors.toList());
      }
    }
    return multiRecipes;
  }
}

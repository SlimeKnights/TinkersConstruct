package slimeknights.tconstruct.library.recipe.casting.material;

import com.google.common.collect.Streams;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.helper.LoadableRecipeSerializer;
import slimeknights.mantle.recipe.helper.TypeAwareRecipeSerializer;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.recipe.casting.CastingRecipeLookup;
import slimeknights.tconstruct.library.recipe.casting.DisplayCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.ICastingContainer;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.helper.TooltipUtil;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Recipe for casting a tool using molten metal on either a tool part or a non-tool part (2 materials or 1) */
public class ToolCastingRecipe extends PartSwapCastingRecipe implements IMultiRecipe<IDisplayableCastingRecipe> {
  public static final RecordLoadable<ToolCastingRecipe> LOADER = RecordLoadable.create(
    LoadableRecipeSerializer.TYPED_SERIALIZER.requiredField(),
    ContextKey.ID.requiredField(), LoadableRecipeSerializer.RECIPE_GROUP, CAST_FIELD, ITEM_COST_FIELD,
    TinkerLoadables.MODIFIABLE_ITEM.requiredField("result", r -> r.result),
    ToolCastingRecipe::new);

  private final IModifiable result;
  public ToolCastingRecipe(TypeAwareRecipeSerializer<?> serializer, ResourceLocation id, String group, Ingredient cast, int itemCost, IModifiable result) {
    super(serializer, id, group, cast, itemCost, -1);
    this.result = result;
    CastingRecipeLookup.registerCastable(result);
  }

  @Override
  protected MaterialFluidRecipe getFluidRecipe(ICastingContainer inv) {
    // if its not part swapping, original lookup is best
    if (inv.getStack().getItem() != result.asItem()) {
      return MaterialCastingLookup.getCastingFluid(inv.getFluid());
    }
    return super.getFluidRecipe(inv);
  }

  @Override
  public boolean matches(ICastingContainer inv, Level level) {
    ItemStack cast = inv.getStack();
    // if the tool matches, perform part swapping
    if (cast.getItem() == result.asItem()) {
      return canPartSwap(inv);
    }
    // no tool match? need to check cast and fluid
    if (!this.getCast().test(cast)) {
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
    // if the cast is the result, we are part swapping, replace the last material
    ItemStack cast = inv.getStack();
    if (cast.getItem() == result) {
      return super.assemble(inv, access);
    } else {
      MaterialNBT materials;
      // if we have 2 materials, we assume the cast has a material. 1 means the cast is a random item
      List<MaterialStatsId> stats = ToolMaterialHook.stats(result.getToolDefinition());
      MaterialVariant material = getFluidRecipe(inv).getOutput();
      if (stats.size() > 1) {
        materials = new MaterialNBT(List.of(MaterialVariant.of(IMaterialItem.getMaterialFromStack(cast)), material));
      } else {
        materials = new MaterialNBT(List.of(material));
      }
      return ToolBuildHandler.buildItemFromMaterials(result, materials);
    }
  }


  /* JEI display */

  @Override
  public List<IDisplayableCastingRecipe> getRecipes(RegistryAccess access) {
    if (multiRecipes == null) {
      List<MaterialStatsId> requirements = ToolMaterialHook.stats(result.getToolDefinition());
      if (requirements.isEmpty()) {
        multiRecipes = List.of();
      } else {
        MaterialStatsId requirement = requirements.get(requirements.size() - 1);
        MaterialVariant dummyRequirement = MaterialVariant.of(ToolBuildHandler.getRenderMaterial(0));
        // if we have two item requirements, fill in the part in display
        BiFunction<MaterialVariant,List<ItemStack>,List<ItemStack>> materials;
        ItemStack partSwapDisplay;
        if (requirements.size() > 1) {
          MaterialVariant firstMaterial = MaterialVariant.of(MaterialRegistry.firstWithStatType(requirements.get(0)));
          materials = (mat, casts) -> casts.stream().map(cast -> {
            // if the material is unknown, just use the first; deals with the fact the tool is an extra cast for showing part swapping
            MaterialVariantId id = IMaterialItem.getMaterialFromStack(cast);
            MaterialVariant variant = id == IMaterial.UNKNOWN_ID ? firstMaterial : MaterialVariant.of(id);
            return ToolBuildHandler.buildItemFromMaterials(result, MaterialNBT.of(variant, mat));
          }).toList();
          partSwapDisplay = ToolBuildHandler.buildItemFromMaterials(result, MaterialNBT.of(firstMaterial, dummyRequirement));
        } else {
          materials = (mat, casts) -> List.of(ToolBuildHandler.buildItemFromMaterials(result, MaterialNBT.of(mat)));
          partSwapDisplay = ToolBuildHandler.buildItemFromMaterials(result, MaterialNBT.of(dummyRequirement));
        }
        // mark tool as display so tooltip does not show useless stats
        partSwapDisplay.getOrCreateTag().putBoolean(TooltipUtil.KEY_DISPLAY, true);
        // add the tool to the list of cast items to show that part swapping is an option
        List<ItemStack> casts = List.of(getCast().getItems());
        List<ItemStack> castsWithTool = Streams.concat(casts.stream(), Stream.of(partSwapDisplay)).toList();

        // start building recipes
        multiRecipes = Stream.concat(
          // show recipes for creating the tool from all castable fluids
          MaterialCastingLookup.getAllCastingFluids().stream()
            .filter(recipe -> recipe.isVisible() && requirement.canUseMaterial(recipe.getOutput().getId()))
            .map(recipe -> {
              List<FluidStack> fluids = resizeFluids(recipe.getFluids());
              return new DisplayCastingRecipe(getId(), getType(), castsWithTool, fluids, materials.apply(recipe.getOutput(), castsWithTool),
                ICastingRecipe.calcCoolingTime(recipe.getTemperature(), itemCost * getFluidAmount(fluids)), isConsumed());
            }),
            // all composite fluids become special composite swapping recipes
            MaterialCastingLookup.getAllCompositeFluids().stream()
              .filter(recipe -> recipe.isVisible() && requirement.canUseMaterial(recipe.getOutput().getId()))
              .map(recipe -> {
                List<FluidStack> fluids = resizeFluids(recipe.getFluids());
                return new DisplayCastingRecipe(getId(), getType(), materials.apply(recipe.getInput(), casts), fluids, materials.apply(recipe.getOutput(), casts),
                  ICastingRecipe.calcCoolingTime(recipe.getTemperature(), itemCost * getFluidAmount(fluids)), isConsumed());
              })
          )
          .collect(Collectors.toList());
      }
    }
    return multiRecipes;
  }
}

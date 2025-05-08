package slimeknights.tconstruct.library.recipe.casting.material;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.helper.LoadableRecipeSerializer;
import slimeknights.mantle.recipe.helper.TypeAwareRecipeSerializer;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.recipe.casting.DisplayCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.ICastingContainer;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    // tool match is used for part swapping
    return cast.getItem() == result.asItem() ? super.matches(inv, level) : this.getCast().test(cast);
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

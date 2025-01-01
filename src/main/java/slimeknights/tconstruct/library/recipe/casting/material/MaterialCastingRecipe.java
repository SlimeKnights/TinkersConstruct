package slimeknights.tconstruct.library.recipe.casting.material;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.helper.LoadableRecipeSerializer;
import slimeknights.mantle.recipe.helper.TypeAwareRecipeSerializer;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.recipe.casting.DisplayCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.ICastingContainer;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Casting recipe that takes an arbitrary fluid of a given amount and set the material on the output based on that fluid
 */
public class MaterialCastingRecipe extends AbstractMaterialCastingRecipe implements IMultiRecipe<IDisplayableCastingRecipe> {
  protected static final LoadableField<IMaterialItem,MaterialCastingRecipe> RESULT_FIELD = TinkerLoadables.MATERIAL_ITEM.requiredField("result", r -> r.result);
  public static final RecordLoadable<MaterialCastingRecipe> LOADER = RecordLoadable.create(
    LoadableRecipeSerializer.TYPED_SERIALIZER.requiredField(),
    ContextKey.ID.requiredField(), LoadableRecipeSerializer.RECIPE_GROUP, CAST_FIELD,
    ITEM_COST_FIELD, RESULT_FIELD, CAST_CONSUMED_FIELD, SWITCH_SLOTS_FIELD,
    MaterialCastingRecipe::new);

  protected final IMaterialItem result;

  public MaterialCastingRecipe(TypeAwareRecipeSerializer<?> serializer, ResourceLocation id, String group, Ingredient cast, int itemCost, IMaterialItem result, boolean consumed, boolean switchSlots) {
    super(serializer, id, group, cast, itemCost, consumed, switchSlots);
    this.result = result;
    MaterialCastingLookup.registerItemCost(result, itemCost);
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

  /* JEI display */
  protected List<IDisplayableCastingRecipe> multiRecipes;

  @Override
  public List<IDisplayableCastingRecipe> getRecipes(RegistryAccess access) {
    if (multiRecipes == null) {
      RecipeType<?> type = getType();
      List<ItemStack> castItems = Arrays.asList(getCast().getItems());
      multiRecipes = MaterialCastingLookup
        .getAllCastingFluids().stream()
        .filter(recipe -> {
          MaterialVariant output = recipe.getOutput();
          return !output.isUnknown() && !output.get().isHidden() && result.canUseMaterial(output.getId());
        })
        .map(recipe -> {
          List<FluidStack> fluids = resizeFluids(recipe.getFluids());
          int fluidAmount = fluids.stream().mapToInt(FluidStack::getAmount).max().orElse(0);
          return new DisplayCastingRecipe(type, castItems, fluids, result.withMaterial(recipe.getOutput().getVariant()),
                                          ICastingRecipe.calcCoolingTime(recipe.getTemperature(), itemCost * fluidAmount), isConsumed());
        })
        .collect(Collectors.toList());
    }
    return multiRecipes;
  }
}

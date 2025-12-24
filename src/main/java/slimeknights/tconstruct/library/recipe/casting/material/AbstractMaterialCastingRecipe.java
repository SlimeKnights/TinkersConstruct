package slimeknights.tconstruct.library.recipe.casting.material;

import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.recipe.helper.TypeAwareRecipeSerializer;
import slimeknights.tconstruct.library.json.predicate.material.MaterialPredicate;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.recipe.casting.AbstractCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.ICastingContainer;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Casting recipe that takes an arbitrary fluid of a given amount and set the material on the output based on that fluid
 */
public abstract class AbstractMaterialCastingRecipe extends AbstractCastingRecipe {
  protected static final LoadableField<Integer,AbstractMaterialCastingRecipe> ITEM_COST_FIELD = IntLoadable.FROM_ONE.requiredField("item_cost", r -> r.itemCost);
  protected static final LoadableField<IJsonPredicate<MaterialVariantId>,AbstractMaterialCastingRecipe> MATERIALS_FIELD = MaterialPredicate.LOADER.defaultField("allowed_materials", r -> r.materials);

  @Getter
  private final RecipeSerializer<?> serializer;
  protected final int itemCost;
  protected final IJsonPredicate<MaterialVariantId> materials;

  public AbstractMaterialCastingRecipe(TypeAwareRecipeSerializer<?> serializer, ResourceLocation id, String group, Ingredient cast, int itemCost, boolean consumed, boolean switchSlots, IJsonPredicate<MaterialVariantId> materials) {
    super(serializer.getType(), id, group, cast, consumed, switchSlots);
    this.serializer = serializer;
    this.itemCost = itemCost;
    this.materials = materials;
  }

  /** @deprecated use {@link #AbstractMaterialCastingRecipe(TypeAwareRecipeSerializer, ResourceLocation, String, Ingredient, int, boolean, boolean, IJsonPredicate)} */
  @Deprecated(forRemoval = true)
  public AbstractMaterialCastingRecipe(TypeAwareRecipeSerializer<?> serializer, ResourceLocation id, String group, Ingredient cast, int itemCost, boolean consumed, boolean switchSlots) {
    this(serializer, id, group, cast, itemCost, consumed, switchSlots, MaterialPredicate.ANY);
  }

  /** Gets the material fluid recipe for the given recipe */
  protected MaterialFluidRecipe getFluidRecipe(ICastingContainer inv) {
    return MaterialCastingLookup.getCastingFluid(inv.getFluid(), materials);
  }

  @Override
  public int getCoolingTime(ICastingContainer inv) {
    MaterialFluidRecipe recipe = getFluidRecipe(inv);
    if (recipe != MaterialFluidRecipe.EMPTY) {
      return ICastingRecipe.calcCoolingTime(recipe.getTemperature(), recipe.getFluidAmount(inv.getFluid()) * itemCost);
    }
    return 1;
  }

  @Override
  public int getFluidAmount(ICastingContainer inv) {
    return getFluidRecipe(inv).getFluidAmount(inv.getFluid()) * itemCost;
  }

  /** Resizes the list of the fluids with respect to the item cost */
  protected List<FluidStack> resizeFluids(List<FluidStack> fluids) {
    if (itemCost != 1) {
      return fluids.stream()
                   .map(fluid -> new FluidStack(fluid, fluid.getAmount() * itemCost))
                   .collect(Collectors.toList());
    }
    return fluids;
  }
}

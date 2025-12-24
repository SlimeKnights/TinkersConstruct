package slimeknights.tconstruct.library.recipe.melting;

import lombok.Getter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.helper.FluidOutput;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingLookup;
import slimeknights.tconstruct.library.recipe.ingredient.MaterialIngredient;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Recipe to melt all castable tool parts of a given material
 */
public class MaterialMeltingRecipe implements IMeltingRecipe, IMultiRecipe<MeltingRecipe> {
  public static final RecordLoadable<MaterialMeltingRecipe> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(),
    MaterialVariantId.LOADABLE.requiredField("input", r -> r.input.getVariant()),
    IntLoadable.FROM_ONE.requiredField("temperature", r -> r.temperature),
    FluidOutput.Loadable.REQUIRED.requiredField("result", r -> r.result),
    FluidOutput.Loadable.REQUIRED.list(0).defaultField("byproducts", List.of(), false, r -> r.byproducts),
    MaterialMeltingRecipe::new);

  @Getter
  private final ResourceLocation id;
  private final MaterialVariant input;
  private final int temperature;
  private final FluidOutput result;
  private final List<FluidOutput> byproducts;

  public MaterialMeltingRecipe(ResourceLocation id, MaterialVariantId input, int temperature, FluidOutput result, List<FluidOutput> byproducts) {
    this.id = id;
    this.input = MaterialVariant.of(input);
    this.temperature = temperature;
    this.result = result;
    this.byproducts = byproducts;
  }

  /** @deprecated use {@link #MaterialMeltingRecipe(ResourceLocation,MaterialVariantId,int,FluidOutput,List)} */
  @Deprecated(forRemoval = true)
  public MaterialMeltingRecipe(ResourceLocation id, MaterialVariantId input, int temperature, FluidOutput result) {
    this(id, input, temperature, result, List.of());
  }

  @Override
  public boolean matches(IMeltingContainer inv, Level worldIn) {
    if (input.isUnknown()) {
      return false;
    }
    ItemStack stack = inv.getStack();
    if (stack.isEmpty() || MaterialCastingLookup.getItemCost(stack.getItem()) == 0) {
      return false;
    }
    return input.matchesVariant(stack);
  }

  @Override
  public int getTemperature(IMeltingContainer inv) {
    return temperature;
  }

  @Override
  public int getTime(IMeltingContainer inv) {
    int cost = MaterialCastingLookup.getItemCost(inv.getStack().getItem());
    return IMeltingRecipe.calcTimeForAmount(temperature, result.getAmount() * cost);
  }

  @Override
  public FluidStack getOutput(IMeltingContainer inv) {
    int cost = MaterialCastingLookup.getItemCost(inv.getStack().getItem());
    return new FluidStack(result.get(), result.getAmount() * cost);
  }

  @Override
  public void handleByproducts(IMeltingContainer inv, IFluidHandler handler) {
    if (!byproducts.isEmpty()) {
      int cost = MaterialCastingLookup.getItemCost(inv.getStack().getItem());
      for (FluidOutput byproduct : byproducts) {
        handler.fill(new FluidStack(byproduct.get(), byproduct.getAmount() * cost), FluidAction.EXECUTE);
      }
    }
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerSmeltery.materialMeltingSerializer.get();
  }


  /* JEI display */
  private List<MeltingRecipe> multiRecipes = null;

  @Override
  public List<MeltingRecipe> getRecipes(RegistryAccess access) {
    if (multiRecipes == null) {
      if (input.get().isHidden()) {
        multiRecipes = Collections.emptyList();
      } else {
        // 1 recipe for each part
        MaterialVariantId inputId = input.getVariant();
        multiRecipes = MaterialCastingLookup
          .getAllItemCosts().stream()
          .filter(entry -> entry.getKey().canUseMaterial(inputId.getId()))
          .map(entry -> {
            FluidOutput output = this.result;
            List<FluidOutput> byproducts = this.byproducts;
            int cost = entry.getIntValue();
            // if the part cost is 1, can skip messing with the output size
            if (cost != 1) {
              output = FluidOutput.fromStack(new FluidStack(output.get(), output.getAmount() * cost));
              // skip streaming the byproducts if empty
              if (!byproducts.isEmpty()) {
                byproducts = byproducts.stream().map(fluid -> FluidOutput.fromStack(new FluidStack(fluid.get(), fluid.getAmount() * cost))).toList();
              }
            }
            return new MeltingRecipe(id, "", MaterialIngredient.of(entry.getKey(), inputId), output, temperature,
                                     IMeltingRecipe.calcTimeForAmount(temperature, output.getAmount()), byproducts, false);
          }).collect(Collectors.toList());
      }
    }
    return multiRecipes;
  }
}

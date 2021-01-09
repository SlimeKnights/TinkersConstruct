package slimeknights.tconstruct.library.recipe.alloying;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.recipe.FluidIngredient;
import slimeknights.mantle.recipe.ICustomOutputRecipe;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.mantle.recipe.RecipeSerializer;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Base class for alloying recipes
 */
@RequiredArgsConstructor
public class AlloyRecipe implements ICustomOutputRecipe<IAlloyTank> {
  @Getter
  private final ResourceLocation id;
  /**
   * List of input ingredients.
   * Order matters, as if a fluid matches multiple ingredients it may produce unexpected behavior.
   * Making the most strict first will produce the best behavior
   */
  private final List<FluidIngredient> inputs;
  /** Recipe output */
  @Getter
  private final FluidStack output;
  /** Cache of recipe input list */
  private List<List<FluidStack>> displayInputs;

  /**
   * Gets the list of inputs for display in JEI
   * @return  List of input list for each "slot"
   */
  public List<List<FluidStack>> getDisplayInputs() {
    if (displayInputs == null) {
      displayInputs = inputs.stream().map(FluidIngredient::getFluids).collect(Collectors.toList());
    }
    return displayInputs;
  }

  @Override
  public boolean matches(IAlloyTank inv, World worldIn) {
    int tanks = inv.getTanks();
    // bit corresponding to fluids that are already used
    BitSet used = new BitSet(tanks);

    // mark empty as used to save a bit of effort
    for (int i = 0; i < tanks; i++) {
      if (inv.getFluidInTank(i).isEmpty()) {
        used.set(i);
      }
    }

    // amount of fluid that will be removed from the tank
    int toRemove = 0;
    FluidStack fluid;
    ingredientLoop:
    for (FluidIngredient ingredient : inputs) {
      for (int i = 0; i < tanks; i++) {
        // must not have used that fluid yet
        if (!used.get(i)) {
          fluid = inv.getFluidInTank(i);
          if (ingredient.test(fluid)) {
            // mark how much space will be freed and mark this fluid used
            toRemove += ingredient.getAmount(fluid.getFluid());
            used.set(i);
            continue ingredientLoop;
          }
        }
      }
      // no fluid matched this ingredient, match failed
      return false;
    }

    // ensure we have enough space for this match
    return output.getAmount() - toRemove <= inv.getRemainingSpace();
  }

  /**
   * Subtracts inputs from the fluid handler and adds in outputs
   * @param handler  Fluid handler representing the alloy tank
   */
  public void handleRecipe(IFluidHandler handler) {
    int tanks = handler.getTanks();

    // remove fluid for each ingredient
    FluidStack fluid, drained;
    ingredientLoop:
    for (FluidIngredient ingredient : inputs) {
      for (int i = 0; i < tanks; i++) {
        // find a fluid that matches the ingredient
        fluid = handler.getFluidInTank(i);
        if (ingredient.test(fluid)) {
          drained = handler.drain(fluid, FluidAction.EXECUTE);
          // ensure the right amount of fluid was drained and skip to next ingredient
          if (drained.getAmount() != fluid.getAmount()) {
            TConstruct.log.error("Wrong amount of fluid {} drained for recipe {}", drained, id);
          }
          continue ingredientLoop;
        }
      }
      // no fluid matched this ingredient, match failed
      TConstruct.log.error("Ingredient failed to match for recipe {}", id);
    }

    // add the output
    int filled = handler.fill(output.copy(), FluidAction.EXECUTE);
    if (filled != output.getAmount()) {
      TConstruct.log.error("Filled only {} for recipe {}", filled, id);
    }
  }

  @Override
  public IRecipeType<?> getType() {
    return RecipeTypes.ALLOYING;
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerSmeltery.alloyingSerializer.get();
  }

  public static class Serializer extends RecipeSerializer<AlloyRecipe> {
    @Override
    public AlloyRecipe read(ResourceLocation id, JsonObject json) {
      FluidStack output = RecipeHelper.deserializeFluidStack(JSONUtils.getJsonObject(json, "output"));
      List<FluidIngredient> inputs = JsonHelper.parseList(json, "inputs", FluidIngredient::deserialize);

      // ensure output is not part of any inputs, that would be bad and not clear to the user whats happening
      if (inputs.size() < 2) {
        throw new JsonSyntaxException("Too few inputs to alloy recipe " + id);
      }
      for (FluidIngredient input : inputs) {
        if (input.test(output)) {
          throw new JsonSyntaxException("Output fluid contained in input in alloy recipe " + id);
        }
      }
      return new AlloyRecipe(id, inputs, output);
    }

    @Override
    public void write(PacketBuffer buffer, AlloyRecipe recipe) {
      buffer.writeFluidStack(recipe.output);
      buffer.writeVarInt(recipe.inputs.size());
      for (FluidIngredient input : recipe.inputs) {
        input.write(buffer);
      }
    }

    @Nullable
    @Override
    public AlloyRecipe read(ResourceLocation id, PacketBuffer buffer) {
      FluidStack output = buffer.readFluidStack();
      int inputCount = buffer.readVarInt();
      ImmutableList.Builder<FluidIngredient> builder = ImmutableList.builder();
      for (int i = 0; i < inputCount; i++) {
        builder.add(FluidIngredient.read(buffer));
      }
      return new AlloyRecipe(id, builder.build(), output);
    }
  }
}

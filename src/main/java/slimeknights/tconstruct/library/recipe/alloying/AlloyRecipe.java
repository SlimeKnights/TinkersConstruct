package slimeknights.tconstruct.library.recipe.alloying;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.ICustomOutputRecipe;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.mantle.recipe.helper.RecipeHelper;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
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
  /** Required temperature to craft this */
  @Getter
  private final int temperature;


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

  /**
   * Creates the bitset used for marking fluids we do not care about
   * @param inv  Alloy tank
   * @return  Bitset
   */
  private static BitSet makeBitset(IAlloyTank inv) {
    int tanks = inv.getTanks();
    BitSet used = new BitSet(tanks);
    // mark empty as used to save a bit of effort
    for (int i = 0; i < tanks; i++) {
      if (inv.getFluidInTank(i).isEmpty()) {
        used.set(i);
      }
    }
    return used;
  }

  /**
   * Finds a match for the given ingredient
   * @param ingredient  Ingredient to check
   * @param inv         Alloy tank to search
   * @param used        Bitset for already used matches, will be modified
   * @return  Index of found match, or -1 if match not found
   */
  private static int findMatch(FluidIngredient ingredient, IAlloyTank inv, BitSet used, boolean checkSize) {
    FluidStack fluid;
    for (int i = 0; i < inv.getTanks(); i++) {
      // must not have used that fluid yet
      if (!used.get(i)) {
        fluid = inv.getFluidInTank(i);
        if (checkSize ? ingredient.test(fluid) : ingredient.test(fluid.getFluid())) {
          used.set(i);
          return i;
        }
      }
    }
    return -1;
  }

  @Override
  public boolean matches(IAlloyTank inv, Level worldIn) {
    BitSet used = makeBitset(inv);
    for (FluidIngredient ingredient : inputs) {
      // do not care about size for matches, just want a recipe with the right fluids
      int index = findMatch(ingredient, inv, used, false);
      if (index == -1) {
        return false;
      }
    }

    // goal of matches is to see if this works for any of those fluids, so ignore current space
    return true;
  }

  /**
   * Checks if this recipe can be performed.
   * Note that {@link #performRecipe(IMutableAlloyTank)} runs similar logic, so calling both is uneccessary.
   * @param inv  Alloy tank inventory
   * @return  True if this recipe can be performed
   */
  public boolean canPerform(IAlloyTank inv) {
    // skip if temperature is too low
    if (inv.getTemperature() < temperature) return false;

    // bit corresponding to fluids that are already used
    BitSet used = makeBitset(inv);
    int drainAmount = 0;
    FluidStack fluid;
    for (FluidIngredient ingredient : inputs) {
      // care about size, if too small just skip the recipe
      int index = findMatch(ingredient, inv, used, true);
      if (index != -1) {
        fluid = inv.getFluidInTank(index);
        drainAmount += ingredient.getAmount(fluid.getFluid());
      } else {
        // no fluid matched this ingredient, match failed
        return false;
      }
    }

    // ensure there is space for the recipe
    return inv.canFit(output, drainAmount);
  }

  /**
   * Attempts to perform the recipe. Will do nothing if either there is not enough input, or if there is not enough space for the output
   * @param inv      Fluid inventory that can be read and modified
   */
  public void performRecipe(IMutableAlloyTank inv) {
    // skip if temperature is too low
    if (inv.getTemperature() < temperature) return;

    // figure out how much fluid we need to remove
    FluidStack[] drainFluids = new FluidStack[inv.getTanks()];
    int drainAmount = 0;

    // bit corresponding to fluids that are already used
    BitSet used = makeBitset(inv);

    FluidStack fluid;
    for (FluidIngredient ingredient : inputs) {
      // care about size, if too small just skip the recipe
      int index = findMatch(ingredient, inv, used, true);
      if (index != -1 && drainFluids[index] == null) {
        fluid = inv.getFluidInTank(index);
        int amount = ingredient.getAmount(fluid.getFluid());
        drainAmount += amount;
        drainFluids[index] = new FluidStack(fluid, amount);
      } else {
        // no fluid matched this ingredient, match failed
        return;
      }
    }

    // ensure there is space for the recipe
    FluidStack drained;
    if (inv.canFit(output, drainAmount)) {
      // drain each marked fluid
      for (int i = 0; i < drainFluids.length; i++) {
        FluidStack toDrain = drainFluids[i];
        if (toDrain != null) {
          drained = inv.drain(i, toDrain);
          // ensure the right amount of fluid was drained and skip to next ingredient
          if (drained.getAmount() != toDrain.getAmount()) {
            TConstruct.LOG.error("Wrong amount of fluid {} drained for recipe {}", drained.getFluid(), id);
          }
        }
      }

      // add the output
      int filled = inv.fill(output.copy());
      if (filled != output.getAmount()) {
        TConstruct.LOG.error("Filled only {} for recipe {}", filled, id);
      }
    }
  }

  @Override
  public RecipeType<?> getType() {
    return TinkerRecipeTypes.ALLOYING.get();
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerSmeltery.alloyingSerializer.get();
  }

  public static class Serializer extends LoggingRecipeSerializer<AlloyRecipe> {
    @Override
    public AlloyRecipe fromJson(ResourceLocation id, JsonObject json) {
      FluidStack result = RecipeHelper.deserializeFluidStack(GsonHelper.getAsJsonObject(json, "result"));
      List<FluidIngredient> inputs = JsonHelper.parseList(json, "inputs", FluidIngredient::deserialize);

      // ensure result is not part of any inputs, that would be bad and not clear to the user whats happening
      if (inputs.size() < 2) {
        throw new JsonSyntaxException("Too few inputs to alloy recipe " + id);
      }
      for (FluidIngredient input : inputs) {
        if (input.test(result)) {
          throw new JsonSyntaxException("Result fluid contained in input in alloy recipe " + id);
        }
      }
      int temperature = GsonHelper.getAsInt(json, "temperature");
      return new AlloyRecipe(id, inputs, result, temperature);
    }

    @Override
    protected void toNetworkSafe(FriendlyByteBuf buffer, AlloyRecipe recipe) {
      buffer.writeFluidStack(recipe.output);
      buffer.writeVarInt(recipe.inputs.size());
      for (FluidIngredient input : recipe.inputs) {
        input.write(buffer);
      }
      buffer.writeVarInt(recipe.temperature);
    }

    @Nullable
    @Override
    protected AlloyRecipe fromNetworkSafe(ResourceLocation id, FriendlyByteBuf buffer) {
      FluidStack output = buffer.readFluidStack();
      int inputCount = buffer.readVarInt();
      ImmutableList.Builder<FluidIngredient> builder = ImmutableList.builder();
      for (int i = 0; i < inputCount; i++) {
        builder.add(FluidIngredient.read(buffer));
      }
      int temperature = buffer.readVarInt();
      return new AlloyRecipe(id, builder.build(), output, temperature);
    }
  }
}

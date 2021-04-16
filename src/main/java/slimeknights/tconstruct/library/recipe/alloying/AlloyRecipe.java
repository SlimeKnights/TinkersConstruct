package slimeknights.tconstruct.library.recipe.alloying;

import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.recipe.FluidIngredient;
import slimeknights.mantle.recipe.ICustomOutputRecipe;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.fluids.IFluidHandler;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Base class for alloying recipes
 */
public class AlloyRecipe implements ICustomOutputRecipe<IAlloyTank> {
  private final Identifier id;
  /**
   * List of input ingredients.
   * Order matters, as if a fluid matches multiple ingredients it may produce unexpected behavior.
   * Making the most strict first will produce the best behavior
   */
  private final List<FluidIngredient> inputs;
  /** Recipe output */
  private final FluidVolume output;
  /** Required temperature to craft this */
  private final int temperature;


  /** Cache of recipe input list */
  private List<List<FluidVolume>> displayInputs;

  public AlloyRecipe(Identifier id, List<FluidIngredient> inputs, FluidVolume output, int temperature) {
    this.id = id;
    this.inputs = inputs;
    this.output = output;
    this.temperature = temperature;
  }

  /**
   * Gets the list of inputs for display in JEI
   * @return  List of input list for each "slot"
   */
  public List<List<FluidVolume>> getDisplayInputs() {
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
    FluidVolume fluid;
    for (int i = 0; i < inv.getTanks(); i++) {
      // must not have used that fluid yet
      if (!used.get(i)) {
        fluid = inv.getFluidInTank(i);
        if (checkSize ? ingredient.test(fluid) : ingredient.test(fluid.getFluidKey())) {
          used.set(i);
          return i;
        }
      }
    }
    return -1;
  }

  @Override
  public boolean matches(IAlloyTank inv, World worldIn) {
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
   * Note that {@link #performRecipe(IAlloyTank, IFluidHandler)} runs similar logic, so calling both is uneccessary.
   * @param inv  Alloy tank inventory
   * @return  True if this recipe can be performed
   */
  public boolean canPerform(IAlloyTank inv) {
    // skip if temperature is too low
    if (inv.getTemperature() < temperature) return false;

    // bit corresponding to fluids that are already used
    BitSet used = makeBitset(inv);
    int drainAmount = 0;
    FluidVolume fluid;
    for (FluidIngredient ingredient : inputs) {
      // care about size, if too small just skip the recipe
      int index = findMatch(ingredient, inv, used, true);
      if (index != -1) {
        fluid = inv.getFluidInTank(index);
        drainAmount += ingredient.getAmount(fluid.getFluidKey()).asInt(1000);
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
   * @param inv      Fluid inventory for inputs
   * @param handler  Fluid handler representing the output
   */
  public void performRecipe(IAlloyTank inv, IFluidHandler handler) {
    // skip if temperature is too low
    if (inv.getTemperature() < temperature) return;

    // figure out how much fluid we need to remove
    List<FluidVolume> drainFluids = new ArrayList<>();
    int drainAmount = 0;

    // bit corresponding to fluids that are already used
    BitSet used = makeBitset(inv);

    FluidVolume fluid;
    for (FluidIngredient ingredient : inputs) {
      // care about size, if too small just skip the recipe
      int index = findMatch(ingredient, inv, used, true);
      if (index != -1) {
        fluid = inv.getFluidInTank(index);
        int amount = ingredient.getAmount(fluid.getFluidKey()).asInt(1000);
        drainAmount += amount;
        drainFluids.add(fluid.getFluidKey().withAmount(amount));
      } else {
        // no fluid matched this ingredient, match failed
        return;
      }
    }
    throw new RuntimeException("Crabs and recipes dont mix");
//    // ensure there is space for the recipe
//    FluidVolume drained;
//    if (inv.canFit(output, drainAmount)) {
//      // drain each marked fluid
//      for (FluidVolume toDrain : drainFluids) {
//        drained = handler.drain(toDrain, Simulation.EXECUTE);
//        // ensure the right amount of fluid was drained and skip to next ingredient
//        if (drained.getAmount() != toDrain.getAmount()) {
//          TConstruct.log.error("Wrong amount of fluid {} drained for recipe {}", drained, id);
//        }
//      }
//
//      // add the output
//      int filled = handler.fill(output.copy(), Simulation.EXECUTE);
//      if (filled != output.getAmount()) {
//        TConstruct.log.error("Filled only {} for recipe {}", filled, id);
//      }
//    }
  }

  @Override
  public RecipeType<?> getType() {
    return RecipeTypes.ALLOYING;
  }

  @Override
  public net.minecraft.recipe.RecipeSerializer<?> getSerializer() {
    return TinkerSmeltery.alloyingSerializer;
  }

  public Identifier getId() {
    return this.id;
  }

  public int getTemperature() {
    return this.temperature;
  }

  public static class Serializer implements RecipeSerializer<AlloyRecipe> {
    @Override
    public AlloyRecipe read(Identifier id, JsonObject json) {
      FluidVolume result = FluidVolume.fromJson(net.minecraft.util.JsonHelper.getObject(json, "result"));
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
      int temperature = net.minecraft.util.JsonHelper.getInt(json, "temperature");
      return new AlloyRecipe(id, inputs, result, temperature);
    }

    @Override
    public void write(PacketByteBuf buffer, AlloyRecipe recipe) {
      recipe.output.toMcBuffer(buffer);
      buffer.writeVarInt(recipe.inputs.size());
      for (FluidIngredient input : recipe.inputs) {
        input.write(buffer);
      }
      buffer.writeVarInt(recipe.temperature);
    }

    @Nullable
    @Override
    public AlloyRecipe read(Identifier id, PacketByteBuf buffer) {
      try {
        FluidVolume output = FluidVolume.fromMcBuffer(buffer);

        int inputCount = buffer.readVarInt();
        ImmutableList.Builder<FluidIngredient> builder = ImmutableList.builder();
        for (int i = 0; i < inputCount; i++) {
          builder.add(FluidIngredient.read(buffer));
        }
        int temperature = buffer.readVarInt();
        return new AlloyRecipe(id, builder.build(), output, temperature);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}

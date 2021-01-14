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
import java.util.ArrayList;
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
   * Attempts to perform the recipe. Will do nothing if either there is not enough input, or if there is not enough space for the output
   * @param inv      Fluid inventory for inputs
   * @param handler  Fluid handler representing the output
   */
  public void handleRecipe(IAlloyTank inv, IFluidHandler handler) {
    // figure out how much fluid we need to remove
    List<FluidStack> drainFluids = new ArrayList<>();
    int drainAmount = 0;

    // bit corresponding to fluids that are already used
    BitSet used = makeBitset(inv);

    FluidStack fluid;
    for (FluidIngredient ingredient : inputs) {
      // care about size, if too small just skip the recipe
      int index = findMatch(ingredient, inv, used, true);
      if (index != -1) {
        fluid = inv.getFluidInTank(index);
        int amount = ingredient.getAmount(fluid.getFluid());
        drainAmount += amount;
        drainFluids.add(new FluidStack(fluid, amount));
      } else {
        return;
      }
      // no fluid matched this ingredient, match failed
    }

    // ensure there is space for the recipe
    FluidStack drained;
    if (inv.canFit(output, drainAmount)) {
      // drain each marked fluid
      for (FluidStack toDrain : drainFluids) {
        drained = handler.drain(toDrain, FluidAction.EXECUTE);
        // ensure the right amount of fluid was drained and skip to next ingredient
        if (drained.getAmount() != toDrain.getAmount()) {
          TConstruct.log.error("Wrong amount of fluid {} drained for recipe {}", drained, id);
        }
      }

      // add the output
      int filled = handler.fill(output.copy(), FluidAction.EXECUTE);
      if (filled != output.getAmount()) {
        TConstruct.log.error("Filled only {} for recipe {}", filled, id);
      }
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

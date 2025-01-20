package slimeknights.tconstruct.library.recipe.alloying;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.ICustomOutputRecipe;
import slimeknights.mantle.recipe.helper.FluidOutput;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.util.BitSet;
import java.util.List;

/**
 * Base class for alloying recipes
 */
@RequiredArgsConstructor
public class AlloyRecipe implements ICustomOutputRecipe<IAlloyTank> {
  public static final RecordLoadable<AlloyRecipe> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(),
    AlloyIngredient.LOADABLE.list(2).requiredField("inputs", r -> r.inputs),
    FluidOutput.Loadable.REQUIRED.requiredField("result", r -> r.output),
    IntLoadable.FROM_ONE.requiredField("temperature", r -> r.temperature),
    AlloyRecipe::new);

  @Getter
  private final ResourceLocation id;
  /**
   * List of input ingredients.
   * Order matters, as if a fluid matches multiple ingredients it may produce unexpected behavior.
   * Making the most strict first will produce the best behavior
   */
  @Getter
  private final List<AlloyIngredient> inputs;
  /** Recipe output */
  private final FluidOutput output;
  /** Required temperature to craft this */
  @Getter
  private final int temperature;

  /** Gets the result of this recipe */
  public FluidStack getOutput() {
    return output.get();
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
    for (AlloyIngredient ingredient : inputs) {
      // do not care about size for matches, just want a recipe with the right fluids
      int index = findMatch(ingredient.fluid, inv, used, false);
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
    for (AlloyIngredient ingredient : inputs) {
      // care about size, if too small just skip the recipe
      int index = findMatch(ingredient.fluid, inv, used, true);
      if (index == -1) {
        // no fluid matched this ingredient, match failed
        return false;
      } else if (!ingredient.catalyst()) {
        // increase amount to drain only for non-catalysts
        fluid = inv.getFluidInTank(index);
        drainAmount += ingredient.fluid.getAmount(fluid.getFluid());
      }
    }

    // ensure there is space for the recipe
    return inv.canFit(output.get(), drainAmount);
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
    for (AlloyIngredient ingredient : inputs) {
      // care about size, if too small just skip the recipe
      int index = findMatch(ingredient.fluid, inv, used, true);
      if (index == -1) {
        // no fluid matched this ingredient, match failed
        return;
      } else if (!ingredient.catalyst) {
        // practically the drained fluid at the index should always be null as we don't reuse indexes
        assert drainFluids[index] == null;
        fluid = inv.getFluidInTank(index);
        int amount = ingredient.fluid.getAmount(fluid.getFluid());
        drainAmount += amount;
        drainFluids[index] = new FluidStack(fluid, amount);
      }
    }

    // ensure there is space for the recipe
    FluidStack drained;
    if (inv.canFit(output.get(), drainAmount)) {
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

  public record AlloyIngredient(FluidIngredient fluid, boolean catalyst) {
    public static final RecordLoadable<AlloyIngredient> LOADABLE = RecordLoadable.create(
      FluidIngredient.LOADABLE.tryDirectField("match", AlloyIngredient::fluid),
      BooleanLoadable.INSTANCE.defaultField("catalyst", false, false, AlloyIngredient::catalyst),
      AlloyIngredient::new
    ).compact(FluidIngredient.LOADABLE.flatXmap(fluid -> new AlloyIngredient(fluid, false), AlloyIngredient::fluid), alloy -> !alloy.catalyst());
  }
}

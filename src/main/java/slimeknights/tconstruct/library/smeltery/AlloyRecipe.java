package slimeknights.tconstruct.library.smeltery;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.ListIterator;

import slimeknights.tconstruct.library.TinkerAPIException;

/**
 * Represents an alloy mixing recipe.
 * The input Fluidstacks determine what liquids and how much of them are needed to mix.
 * Basically just put the relations as fluidstack amounts.
 * So 1:1 water:lava would simply be (1,water), (1,lava)
 */
public class AlloyRecipe {

  protected final List<FluidStack> fluids;
  protected final FluidStack result;

  public AlloyRecipe(FluidStack result, FluidStack... input) {
    this.result = result;

    ImmutableList.Builder<FluidStack> builder = ImmutableList.builder();
    for(FluidStack liquid : input) {
      if(liquid.amount < 1) {
        throw new TinkerAPIException("Invalid Alloy recipe: Fluid amount can't be less than 1");
      }
      builder.add(liquid);
    }

    fluids = builder.build();
  }

  public int matches(List<FluidStack> input) {
    // how often we can apply the alloy
    int times = Integer.MAX_VALUE;
    List<FluidStack> needed = Lists.newLinkedList(fluids);
    // for each liquid in the input
    for(FluidStack fluid : input) {
      // check if it's needed
      ListIterator<FluidStack> iter = needed.listIterator();
      while(iter.hasNext()) {
        FluidStack need = iter.next();
        if(fluid.containsFluid(need)) {
          // and if it matches, remove
          iter.remove();

          // check how often we can apply the recipe with this
          if(fluid.amount / need.amount < times) {
            times = fluid.amount / need.amount;
          }
          break;
        }
      }
    }

    // if the needed array is empty we found everything we needed
    return needed.isEmpty() ? times : 0;
  }

  public List<FluidStack> getFluids() {
    return fluids;
  }

  public FluidStack getResult() {
    return result;
  }
}

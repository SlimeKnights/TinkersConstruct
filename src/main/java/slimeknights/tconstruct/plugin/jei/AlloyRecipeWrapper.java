package slimeknights.tconstruct.plugin.jei;

import com.google.common.collect.ImmutableList;

import net.minecraftforge.fluids.FluidStack;

import java.util.List;

import javax.annotation.Nonnull;

import mezz.jei.api.recipe.BlankRecipeWrapper;
import slimeknights.tconstruct.library.smeltery.AlloyRecipe;

public class AlloyRecipeWrapper extends BlankRecipeWrapper {

  protected final List<FluidStack> inputs;
  protected final List<FluidStack> outputs;

  public AlloyRecipeWrapper(AlloyRecipe recipe) {
    this.inputs = recipe.getFluids();
    this.outputs = ImmutableList.of(recipe.getResult());
  }

  @Nonnull
  @Override
  public List<FluidStack> getFluidInputs() {
    return inputs;
  }

  @Nonnull
  @Override
  public List<FluidStack> getFluidOutputs() {
    return outputs;
  }
}

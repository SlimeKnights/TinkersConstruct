package slimeknights.tconstruct.plugin.jei;

import com.google.common.collect.ImmutableList;

import net.minecraftforge.fluids.FluidStack;

import java.util.List;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import slimeknights.tconstruct.library.smeltery.AlloyRecipe;

public class AlloyRecipeWrapper implements IRecipeWrapper {

  protected final List<FluidStack> inputs;
  protected final List<FluidStack> outputs;

  public AlloyRecipeWrapper(AlloyRecipe recipe) {
    this.inputs = recipe.getFluids();
    this.outputs = ImmutableList.of(recipe.getResult());
  }

  @Override
  public void getIngredients(IIngredients ingredients) {
    ingredients.setInputs(FluidStack.class, inputs);
    ingredients.setOutputs(FluidStack.class, outputs);
  }
}

package slimeknights.tconstruct.library.smeltery;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import slimeknights.mantle.util.RecipeMatch;

public class MeltingRecipe {
  // speed and temperature are inferred automatically through the output
  public final RecipeMatch input;
  public final FluidStack output;

  public MeltingRecipe(RecipeMatch input, Fluid output) {
    this.input = input;
    this.output = new FluidStack(output, input.amountMatched);
  }

  /**
   * FLUIDSTACK AMOUNT IS IGNORED! Only use this if your fluidstack has to carry data the regular fluid doesn't have.
   */
  public MeltingRecipe(RecipeMatch input, FluidStack output) {
    this.output = output;
    this.input = input;

    output.amount = input.amountMatched;
  }

  public int getRequiredTemperature() {
    return output.getFluid().getTemperature(output);
  }

  public boolean matches(ItemStack stack) {
    return input.matches(new ItemStack[]{stack}) != null;
  }

  public FluidStack getResult() {
    return output.copy();
  }

  public static class CustomTemperature extends MeltingRecipe {
    public final int temperature; // = speed and required temperature

    public CustomTemperature(RecipeMatch input, Fluid output, int temperature) {
      super(input, output);
      this.temperature = temperature;
    }

    public CustomTemperature(RecipeMatch input, FluidStack output, int temperature) {
      super(input, output);
      this.temperature = temperature;
    }

    @Override
    public int getRequiredTemperature() {
      return temperature;
    }
  }
}

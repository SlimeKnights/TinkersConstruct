package slimeknights.tconstruct.library.smeltery;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;

public class MeltingRecipe {
  // speed and temperature are inferred automatically through the output
  public final RecipeMatch input;
  public final FluidStack output;
  public final int temperature;

  public MeltingRecipe(RecipeMatch input, Fluid output) {
    this(input,  new FluidStack(output, input.amountMatched), 300, Material.VALUE_Ingot);
  }

  public MeltingRecipe(RecipeMatch input, Fluid output, int minTemperature, int baseline) {
    this(input,  new FluidStack(output, input.amountMatched), minTemperature, baseline);
  }

  /**
   * FLUIDSTACK AMOUNT IS IGNORED! Only use this if your fluidstack has to carry data the regular fluid doesn't have.
   */
  public MeltingRecipe(RecipeMatch input, FluidStack output, int minTemperature, int baseline) {
    this.output = output;
    this.input = input;

    output.amount = input.amountMatched;

    // We take an amount as the baseline. We then calculate required temperature depending on that baseline
    // The temperature can not go below minTemperature
    // If the result is less than the baseline we linearly reduce the temperature and then penalize it a bit because it's less efficient
    // If the result is more than the baseline we linearly increase the temperature and then reduce it a bit because it's more efficient
    // increased temperature cannot go past the fluids temperature

//    int max = Math.max(minTemperature, output.getFluid().getTemperature(output));
//    int tmp = minTemperature + (max - minTemperature)/2; // tmp for temperature of course, what else?
    int tmp = output.getFluid().getTemperature(output);

    // calculate how far we're off the baseline
    float f = (float)output.amount / (float)baseline;

    // penalize
    if(f < 0) {
      f += 1/(f*9);
    }
    // reward
    else {
      f -= f/9;
    }

    // we take a full block as the baseline for the melting temperature
    this.temperature = (int)Math.max(minTemperature, f * tmp);
  }

  public MeltingRecipe(RecipeMatch input, Fluid output, int customTemperature) {
    this(input, new FluidStack(output, input.amountMatched), customTemperature);
  }

  public MeltingRecipe(RecipeMatch input, FluidStack output, int customTemperature) {
    this.input = input;
    this.output =  new FluidStack(output, input.amountMatched);
    this.temperature = customTemperature;
  }

  /** Required time to execute the recpipe, expressed as "temperature", and also the minimum required temp. for this recipe */
  public int getTemperature() {
    return temperature;
  }

  // seriously, who thought kelvin is a good unit for this?
  public int getUsableTemperature() {
    return Math.max(1, temperature-300);
  }

  public boolean matches(ItemStack stack) {
    return input.matches(new ItemStack[]{stack}) != null;
  }

  public FluidStack getResult() {
    return output.copy();
  }

  public MeltingRecipe register() {
    TinkerRegistry.registerMelting(this);
    return this;
  }
}

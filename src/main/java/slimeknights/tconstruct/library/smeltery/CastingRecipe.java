package slimeknights.tconstruct.library.smeltery;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerAPIException;

public class CastingRecipe {

  public final RecipeMatch cast;
  protected final FluidStack fluid;
  protected final ItemStack output;
  protected final int time; // ticks to cool down
  protected final boolean consumesCast;
  protected final boolean switchOutputs; // switches cast and output. Mostly used for cast creation

  public CastingRecipe(ItemStack output, RecipeMatch cast, Fluid fluid, int amount) {
    this(output, cast, fluid, amount, calcCooldownTime(fluid, amount));
  }

  public CastingRecipe(ItemStack output, RecipeMatch cast, Fluid fluid, int amount, int time) {
    this(output, cast, new FluidStack(fluid, amount), time, false, false);
  }

  public CastingRecipe(ItemStack output, Fluid fluid, int amount, int time) {
    this(output, null, new FluidStack(fluid, amount), time, false, false);
  }

  public CastingRecipe(ItemStack output, RecipeMatch cast, FluidStack fluid, boolean consumesCast, boolean switchOutputs) {
    this(output, cast, fluid, calcCooldownTime(fluid.getFluid(), fluid.amount), consumesCast, switchOutputs);
  }

  public CastingRecipe(ItemStack output, RecipeMatch cast, FluidStack fluid, int time, boolean consumesCast, boolean switchOutputs) {
    if(output == null) {
      throw new TinkerAPIException("Casting Recipe is missing an output!");
    }
    else if(fluid == null) {
      throw new TinkerAPIException(String.format("Casting Recipe for %s has no fluid!", output.getDisplayName()));
    }

    this.output = output;
    this.cast = cast;
    this.fluid = fluid;
    this.time = time;
    this.consumesCast = consumesCast;
    this.switchOutputs = switchOutputs;
  }

  public boolean matches(ItemStack cast, Fluid fluid) {
    if((cast == null && this.cast == null) || (this.cast != null && this.cast.matches(new ItemStack[]{cast}) != null)) {
      return this.fluid.getFluid() == fluid;
    }
    return false;
  }

  public ItemStack getResult() {
    return output.copy();
  }

  public FluidStack getFluid() {
    return fluid;
  }

  public int getTime() {
    return time;
  }

  public boolean consumesCast() {
    return consumesCast;
  }

  public boolean switchOutputs() {
    return switchOutputs;
  }

  public static int calcCooldownTime(Fluid fluid, int amount) {
    // minimum time = faucet animation time :I
    int time = 24;
    int temperature = fluid.getTemperature() - 300;

    return time + (temperature * amount) / 1600;
  }
}

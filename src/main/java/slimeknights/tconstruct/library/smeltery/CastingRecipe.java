package slimeknights.tconstruct.library.smeltery;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import slimeknights.mantle.util.RecipeMatch;

public class CastingRecipe {

  public final RecipeMatch cast;
  public final FluidStack fluid;
  public final ItemStack output;
  public final int time; // ticks to cool down
  public final boolean consumesCast;
  public final boolean switchOutputs; // switches cast and output. Mostly used for cast creation

  public CastingRecipe(ItemStack output, RecipeMatch cast, Fluid fluid, int amount) {
    this(output, cast,  fluid, amount, 20); // todo: automagic time
  }

  public CastingRecipe(ItemStack output, RecipeMatch cast, Fluid fluid, int amount, int time) {
    this(output, cast,  new FluidStack(fluid, amount), time, false, false);
  }

  public CastingRecipe(ItemStack output, Fluid fluid, int amount, int time) {
    this(output, null,  new FluidStack(fluid, amount), time, false, false);
  }

  public CastingRecipe(ItemStack output, RecipeMatch cast, FluidStack fluid, int time, boolean consumesCast, boolean switchOutputs) {
    this.output = output;
    this.cast = cast;
    this.fluid = fluid;
    this.time = time;
    this.consumesCast = consumesCast;
    this.switchOutputs = switchOutputs;
  }

  public boolean matches(ItemStack cast, Fluid fluid) {
    if((cast == null && this.cast == null) || this.cast.matches(new ItemStack[]{cast}) != null) {
      return this.fluid.getFluid() == fluid;
    }
    return false;
  }

  public ItemStack getResult() {
    return output.copy();
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
}

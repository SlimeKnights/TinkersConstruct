package slimeknights.tconstruct.library.smeltery;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public interface ICastingRecipe {

  ItemStack getResult(@Nonnull ItemStack cast, Fluid fluid);

  default FluidStack getFluid(@Nonnull ItemStack cast, Fluid fluid) {
    return new FluidStack(fluid, getFluidAmount());
  }

  boolean matches(@Nonnull ItemStack cast, Fluid fluid);

  // all the things that have to be the same for each recipe

  boolean switchOutputs();

  boolean consumesCast();

  int getTime();

  /** Amount of fluid needed for the recipe */
  int getFluidAmount();
}

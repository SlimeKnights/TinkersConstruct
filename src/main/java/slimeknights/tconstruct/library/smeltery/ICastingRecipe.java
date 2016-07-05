package slimeknights.tconstruct.library.smeltery;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public interface ICastingRecipe {

  ItemStack getResult(ItemStack cast, Fluid fluid);
  FluidStack getFluid(ItemStack cast, Fluid fluid);

  boolean matches(ItemStack cast, Fluid fluid);

  // all the things that have to be the same for each recipe

  boolean switchOutputs();
  boolean consumesCast();
  int getTime();

  /** Amount of fluid needed for the recipe */
  int getFluidAmount();
}

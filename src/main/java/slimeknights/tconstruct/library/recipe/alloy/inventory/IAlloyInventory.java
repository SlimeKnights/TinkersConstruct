package slimeknights.tconstruct.library.recipe.alloy.inventory;

import net.minecraft.fluid.Fluid;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.recipe.fuel.IFluidInventory;

import java.util.List;

/**
 * Inventory representing the contents of an alloy tank and surrounding tanks
 */
public interface IAlloyInventory extends IFluidInventory {
  /**
   * Gets the contained fluids in the inventory
   * @return List of contained fluids
   */
  List<FluidStack> getFluidStacks();
}

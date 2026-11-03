package slimeknights.tconstruct.fluids.util;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

/** @deprecated use {@link slimeknights.mantle.item.ConstantFluidContainerWrapper} */
@Deprecated(forRemoval = true)
public class ConstantFluidContainerWrapper extends slimeknights.mantle.item.ConstantFluidContainerWrapper {
  public ConstantFluidContainerWrapper(FluidStack fluid, ItemStack container) {
    super(fluid, container);
  }

  public ConstantFluidContainerWrapper(FluidStack fluid, ItemStack container, ItemStack emptyStack) {
    super(fluid, container, emptyStack);
  }
}

package slimeknights.tconstruct.library.fluid.transfer;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.helper.ItemOutput;

/** @deprecated use {@link slimeknights.mantle.fluid.transfer.EmptyFluidContainerTransfer} */
@Deprecated
public class EmptyFluidContainerTransfer extends slimeknights.mantle.fluid.transfer.EmptyFluidContainerTransfer implements IFluidContainerTransfer {
  public EmptyFluidContainerTransfer(Ingredient input, ItemOutput filled, FluidStack fluid) {
    super(input, filled, fluid);
  }
}

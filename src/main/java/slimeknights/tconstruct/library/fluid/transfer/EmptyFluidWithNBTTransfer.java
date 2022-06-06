package slimeknights.tconstruct.library.fluid.transfer;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.helper.ItemOutput;

/** @deprecated use {@link slimeknights.mantle.fluid.transfer.EmptyFluidWithNBTTransfer} */
@Deprecated
public class EmptyFluidWithNBTTransfer extends slimeknights.mantle.fluid.transfer.EmptyFluidWithNBTTransfer implements IFluidContainerTransfer {
  public EmptyFluidWithNBTTransfer(Ingredient input, ItemOutput filled, FluidStack fluid) {
    super(input, filled, fluid);
  }
}

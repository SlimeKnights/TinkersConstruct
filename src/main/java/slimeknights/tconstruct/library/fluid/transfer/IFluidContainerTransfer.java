package slimeknights.tconstruct.library.fluid.transfer;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import slimeknights.mantle.data.GenericLoaderRegistry.IHaveLoader;

import javax.annotation.Nullable;

/** Interface for transferring fluid either to or from an item */
public interface IFluidContainerTransfer extends IHaveLoader<IFluidContainerTransfer> {
  /**
   * Checks if this recipe matches the given item
   * @param stack  Stack to match
   * @param fluid  Current fluid the handler allows draining. Does not mean the handler may not accept other fluids
   * @return  True if this handler can transfer
   */
  boolean matches(ItemStack stack, FluidStack fluid);

  /**
   * Performs the actual transfer into or out of the handler
   * @param stack    Stack to transfer
   * @param handler  Handler either receiving or giving fluid
   * @param fluid    Current fluid the handler allows draining. Does not mean the handler may not accept other fluids
   * @return  container after the transfer and the fluid transferred, null if the transfer failed
   */
  @Nullable
  TransferResult transfer(ItemStack stack, FluidStack fluid, IFluidHandler handler);

  /**
   * Result after transferring a fluid
   * @param stack    Item stack result, may be modified
   * @param fluid    Fluid, generally should not be modified
   * @param didFill  If true, the item stack was filled. If false, it was draine
   */
  record TransferResult(ItemStack stack, FluidStack fluid, boolean didFill) {}
}

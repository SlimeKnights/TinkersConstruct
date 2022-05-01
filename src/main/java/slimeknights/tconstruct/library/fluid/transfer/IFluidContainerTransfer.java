package slimeknights.tconstruct.library.fluid.transfer;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import slimeknights.mantle.data.GenericRegisteredSerializer.IJsonSerializable;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/** Interface for transferring fluid either to or from an item */
public interface IFluidContainerTransfer extends IJsonSerializable {
  /** Adds any items matched by this recipe for the sake of enabling transfer client side */
  void addRepresentativeItems(Consumer<Item> consumer);

  /**
   * Checks if this recipe uniquely matches the given item
   * @param stack  Stack to match
   * @param fluid  Current fluid the handler allows draining. Does not mean the handler may not accept other fluids
   *               On client side, this will always be empty. Return true if this stack
   * @return  True if this handler can transfer
   */
  boolean matches(ItemStack stack, FluidStack fluid);

  /**
   * Performs the actual transfer into or out of the handler
   * @param stack    Stack to transfer
   * @param fluid    Current fluid the handler allows draining. Does not mean the handler may not accept other fluids
   * @param handler  Handler either receiving or giving fluid
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

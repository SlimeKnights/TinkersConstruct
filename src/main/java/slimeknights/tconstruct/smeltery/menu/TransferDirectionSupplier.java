package slimeknights.tconstruct.smeltery.menu;

import slimeknights.mantle.fluid.transfer.IFluidContainerTransfer.TransferDirection;

/** Helper to abstract out the transfer direction getter from our menu type */
public interface TransferDirectionSupplier {
  /** Gets the current transfer direction */
  TransferDirection getTransferDirection();
}

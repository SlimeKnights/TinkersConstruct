package slimeknights.tconstruct.library.fluid.transfer;

import com.google.gson.Gson;
import slimeknights.mantle.data.GenericRegisteredSerializer;
import slimeknights.mantle.fluid.transfer.IFluidContainerTransfer;

/** @deprecated use {@link slimeknights.mantle.fluid.transfer.FluidContainerTransferManager} */
@Deprecated
public class FluidContainerTransferManager  {
  /** @deprecated use {@link slimeknights.mantle.fluid.transfer.FluidContainerTransferManager#TRANSFER_LOADERS} */
  @Deprecated
  public static final GenericRegisteredSerializer<IFluidContainerTransfer> TRANSFER_LOADERS = slimeknights.mantle.fluid.transfer.FluidContainerTransferManager.TRANSFER_LOADERS;
  /** @deprecated use {@link slimeknights.mantle.fluid.transfer.FluidContainerTransferManager#FOLDER} */
  @Deprecated
  public static final String FOLDER = slimeknights.mantle.fluid.transfer.FluidContainerTransferManager.FOLDER;
  /** @deprecated use {@link slimeknights.mantle.fluid.transfer.FluidContainerTransferManager#GSON} */
  @Deprecated
  public static final Gson GSON = slimeknights.mantle.fluid.transfer.FluidContainerTransferManager.GSON;
  /** @deprecated use {@link slimeknights.mantle.fluid.transfer.FluidContainerTransferManager#INSTANCE} */
  @Deprecated
  public static final slimeknights.mantle.fluid.transfer.FluidContainerTransferManager INSTANCE = slimeknights.mantle.fluid.transfer.FluidContainerTransferManager.INSTANCE;
}

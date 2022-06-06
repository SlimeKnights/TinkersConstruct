package slimeknights.tconstruct.library.data.tinkering;

import net.minecraft.data.DataGenerator;

/** @deprecated use {@link slimeknights.mantle.fluid.transfer.AbstractFluidContainerTransferProvider} */
@Deprecated
public abstract class AbstractFluidContainerTransferProvider extends slimeknights.mantle.fluid.transfer.AbstractFluidContainerTransferProvider {
  public AbstractFluidContainerTransferProvider(DataGenerator generator, String modId) {
    super(generator, modId);
  }
}

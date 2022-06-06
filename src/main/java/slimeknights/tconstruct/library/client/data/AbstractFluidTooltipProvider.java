package slimeknights.tconstruct.library.client.data;

import net.minecraft.data.DataGenerator;

/** @deprecated use {@link slimeknights.tconstruct.smeltery.block.AbstractCastingBlock} */
@Deprecated
public abstract class AbstractFluidTooltipProvider extends slimeknights.mantle.fluid.tooltip.AbstractFluidTooltipProvider {
  public AbstractFluidTooltipProvider(DataGenerator generator, String modId) {
    super(generator, modId);
  }
}

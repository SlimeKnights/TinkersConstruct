package slimeknights.tconstruct.library.tools.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;

/** Capability provider for tool stacks, returns the proper cap for  */
public class ToolCapabilityProvider implements ICapabilityProvider {
  private final Lazy<ToolStack> tool;
  private final LazyOptional<IFluidHandlerItem> fluidCap;
  public ToolCapabilityProvider(ItemStack stack) {
    // NBt is not yet initialized when capabilities are created, so delay tool stack creation
    this.tool = Lazy.of(() -> ToolStack.from(stack));
    this.fluidCap = LazyOptional.of(() -> new ToolFluidCapability(stack, tool.get()));
  }

  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
    // clear the tool cache, as it may have changed since the last time a cap was fetched
    ToolStack toolStack = tool.get();
    toolStack.clearCache();
    // we use marker tags to indicate that caps should be used
    IModDataReadOnly volatileData = toolStack.getVolatileData();
    if (cap == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY && volatileData.getBoolean(ToolFluidCapability.HAS_CAPABILITY)) {
      return fluidCap.cast();
    }
    return LazyOptional.empty();
  }
}

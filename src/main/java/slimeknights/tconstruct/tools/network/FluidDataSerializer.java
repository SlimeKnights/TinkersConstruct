package slimeknights.tconstruct.tools.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraftforge.fluids.FluidStack;

/** Serializer for fluid stack data in entities */
public class FluidDataSerializer implements EntityDataSerializer<FluidStack> {
  @Override
  public void write(FriendlyByteBuf buffer, FluidStack stack) {
    buffer.writeFluidStack(stack);
  }

  @Override
  public FluidStack read(FriendlyByteBuf buffer) {
    return buffer.readFluidStack();
  }

  @Override
  public FluidStack copy(FluidStack stack) {
    return stack.copy();
  }
}

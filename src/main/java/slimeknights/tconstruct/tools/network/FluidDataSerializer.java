package slimeknights.tconstruct.tools.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.neoforge.fluids.FluidStack;

/** Serializer for fluid stack data in entities */
public class FluidDataSerializer implements EntityDataSerializer<FluidStack> {
  @Override
  public StreamCodec<? super RegistryFriendlyByteBuf, FluidStack> codec() {
    return FluidStack.OPTIONAL_STREAM_CODEC;
  }

  @Override
  public FluidStack copy(FluidStack stack) {
    return stack.copy();
  }
}

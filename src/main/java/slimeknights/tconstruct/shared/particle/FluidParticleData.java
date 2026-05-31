package slimeknights.tconstruct.shared.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;

/** Particle data for a fluid particle */
public class FluidParticleData implements ParticleOptions {
  private final ParticleType<FluidParticleData> type;
  private final FluidStack fluid;

  public FluidParticleData(ParticleType<FluidParticleData> type, FluidStack fluid) {
    this.type = type;
    this.fluid = fluid;
  }

  @Override
  public ParticleType<FluidParticleData> getType() {
    return type;
  }

  public FluidStack getFluid() {
    return fluid;
  }

  /** Particle type for a fluid particle */
  public static class Type extends ParticleType<FluidParticleData> {
    public Type() {
      super(false);
    }

    @Override
    public MapCodec<FluidParticleData> codec() {
      return FluidStack.CODEC.fieldOf("fluid").xmap(fluid -> new FluidParticleData(this, fluid), FluidParticleData::getFluid);
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, FluidParticleData> streamCodec() {
      return FluidStack.STREAM_CODEC.map(fluid -> new FluidParticleData(this, fluid), FluidParticleData::getFluid);
    }
  }
}

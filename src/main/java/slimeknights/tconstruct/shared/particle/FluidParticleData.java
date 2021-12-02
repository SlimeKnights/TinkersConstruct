package slimeknights.tconstruct.shared.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.serialization.Codec;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

/** Particle data for a fluid particle */
@RequiredArgsConstructor
public class FluidParticleData implements IParticleData {
  private static final DynamicCommandExceptionType UNKNOWN_FLUID = new DynamicCommandExceptionType(arg -> new TranslationTextComponent("command.tconstruct.fluid.not_found", arg));
  private static final IParticleData.IDeserializer<FluidParticleData> DESERIALIZER = new IParticleData.IDeserializer<FluidParticleData>() {
    @Override
    public FluidParticleData deserialize(ParticleType<FluidParticleData> type, StringReader reader) throws CommandSyntaxException {
      reader.expect(' ');
      int i = reader.getCursor();
      ResourceLocation id = ResourceLocation.read(reader);
      Fluid fluid = Registry.FLUID.getOptional(id).orElseThrow(() -> {
        reader.setCursor(i);
        return UNKNOWN_FLUID.createWithContext(reader, id.toString());
      });
      CompoundNBT nbt = null;
      if (reader.canRead() && reader.peek() == '{') {
        nbt = new JsonToNBT(reader).readStruct();
      }
      return new FluidParticleData(type, new FluidStack(fluid, FluidAttributes.BUCKET_VOLUME, nbt));
    }

    @Override
    public FluidParticleData read(ParticleType<FluidParticleData> type, PacketBuffer buffer) {
      return new FluidParticleData(type, FluidStack.readFromPacket(buffer));
    }
  };

  @Getter
  private final ParticleType<FluidParticleData> type;
  @Getter
  private final FluidStack fluid;

  @Override
  public void write(PacketBuffer buffer) {
    fluid.writeToPacket(buffer);
  }

  @Override
  public String getParameters() {
    StringBuilder builder = new StringBuilder();
    builder.append(getType().getRegistryName());
    builder.append(" ");
    builder.append(fluid.getFluid().getRegistryName());
    CompoundNBT nbt = fluid.getTag();
    if (nbt != null) {
      builder.append(nbt);
    }
    return builder.toString();
  }

  /** Particle type for a fluid particle */
  public static class Type extends ParticleType<FluidParticleData> {
    public Type() {
      super(false, DESERIALIZER);
    }

    @Override
    public Codec<FluidParticleData> func_230522_e_() {
      return FluidStack.CODEC.xmap(fluid -> new FluidParticleData(this, fluid), data -> data.fluid);
    }
  }
}

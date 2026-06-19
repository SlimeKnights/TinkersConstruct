package slimeknights.tconstruct.library.modifiers.fluid;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent.Context;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.TConstruct;

import java.util.ArrayList;
import java.util.List;

/** Packet to sync fluid predicates to the client */
@Internal
public record UpdateFluidEffectsPacket(List<FluidEffects.Entry> fluids) implements IThreadsafePacket {
  /** Clientside constructor, reading from the buffer */
  public static UpdateFluidEffectsPacket decode(FriendlyByteBuf buffer) {
    int size = buffer.readVarInt();
    List<FluidEffects.Entry> entries = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      ResourceLocation key = buffer.readResourceLocation();
      try {
        FluidEffects effects = FluidEffects.LOADABLE.decode(buffer, FluidEffectManager.contextBuilder(key).build());
        entries.add(new FluidEffects.Entry(key, effects));
      } catch (RuntimeException e) {
        // put exception in the log with a bit more info
        TConstruct.LOG.error("Failed to decode fluid effects with ID {}", key, e);
        throw e;
      }
    }
    return new UpdateFluidEffectsPacket(List.copyOf(entries));
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeVarInt(fluids.size());
    for (FluidEffects.Entry entry : fluids) {
      ResourceLocation key = entry.name();
      buffer.writeResourceLocation(key);
      try {
        FluidEffects.LOADABLE.encode(buffer, entry.effects());
      } catch (RuntimeException e) {
        TConstruct.LOG.error("Failed to encode fluid effects with ID {}", key, e);
        throw e;
      }
    }
  }

  @Override
  public void handleThreadsafe(Context context) {
    FluidEffectManager.INSTANCE.updateFromServer(fluids);
  }
}

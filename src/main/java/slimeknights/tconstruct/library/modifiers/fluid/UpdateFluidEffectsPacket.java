package slimeknights.tconstruct.library.modifiers.fluid;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.data.loadable.Streamable;
import slimeknights.mantle.network.packet.IThreadsafePacket;

import java.util.List;

/** Packet to sync fluid predicates to the client */
@Internal
public record UpdateFluidEffectsPacket(List<FluidEffects.Entry> fluids) implements IThreadsafePacket {
  /** Network syncing logic */
  private static final Streamable<List<FluidEffects.Entry>> NETWORK = FluidEffects.Entry.LOADABLE.list(0);

  /** Clientside constructor, reading from the buffer */
  public UpdateFluidEffectsPacket(FriendlyByteBuf buffer) {
    this(NETWORK.decode(buffer));
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    NETWORK.encode(buffer, fluids);
  }

  @Override
  public void handleThreadsafe(Context context) {
    FluidEffectManager.INSTANCE.updateFromServer(fluids);
  }
}

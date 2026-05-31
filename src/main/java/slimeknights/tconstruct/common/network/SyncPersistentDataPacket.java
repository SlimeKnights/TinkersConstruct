package slimeknights.tconstruct.common.network;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import slimeknights.mantle.compat.neoforged.neoforge.network.NetworkEvent.Context;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;

/** Packet to sync player persistent data to the client */
@RequiredArgsConstructor
public class SyncPersistentDataPacket implements IThreadsafePacket {
  private final CompoundTag data;

  public SyncPersistentDataPacket(FriendlyByteBuf buffer) {
    data = buffer.readNbt();
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeNbt(data);
  }

  @Override
  public void handleThreadsafe(Context context) {
    HandleClient.handle(this);
  }

  /** Handles client side only code safely */
  private static class HandleClient {
    private static void handle(SyncPersistentDataPacket packet) {
      Player player = Minecraft.getInstance().player;
      if (player != null) {
        PersistentDataCapability.getCapability(player).ifPresent(data -> data.copyFrom(packet.data));
      }
    }
  }
}

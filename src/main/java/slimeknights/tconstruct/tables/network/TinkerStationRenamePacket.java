package slimeknights.tconstruct.tables.network;

import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.tables.block.entity.table.TinkerStationBlockEntity;
import slimeknights.tconstruct.tables.menu.TinkerStationContainerMenu;

/** Packet to send to the server to update the name in the UI */
@RequiredArgsConstructor
public class TinkerStationRenamePacket implements IThreadsafePacket {
  private final String name;

  public TinkerStationRenamePacket(FriendlyByteBuf buf) {
    this.name = buf.readUtf(Short.MAX_VALUE);
  }

  @Override
  public void encode(FriendlyByteBuf buf) {
    buf.writeUtf(name);
  }

  @Override
  public void handleThreadsafe(Context context) {
    ServerPlayer sender = context.getSender();
    if (sender != null && sender.containerMenu instanceof TinkerStationContainerMenu station) {
      TinkerStationBlockEntity tile = station.getTile();
      if (tile != null) {
        station.getTile().setItemName(name);
      }
    }
  }
}

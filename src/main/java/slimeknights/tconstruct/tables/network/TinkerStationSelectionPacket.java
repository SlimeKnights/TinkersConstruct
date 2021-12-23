package slimeknights.tconstruct.tables.network;

import lombok.RequiredArgsConstructor;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayoutLoader;
import slimeknights.tconstruct.tables.inventory.table.tinkerstation.TinkerStationContainer;

@RequiredArgsConstructor
public class TinkerStationSelectionPacket implements IThreadsafePacket {
  private final ResourceLocation layoutName;
  public TinkerStationSelectionPacket(PacketBuffer buffer) {
    this.layoutName = buffer.readResourceLocation();
  }

  @Override
  public void encode(PacketBuffer buffer) {
    buffer.writeResourceLocation(this.layoutName);
  }

  @Override
  public void handleThreadsafe(Context context) {
    ServerPlayerEntity sender = context.getSender();
    if (sender != null) {
      Container container = sender.openContainer;
      if (container instanceof TinkerStationContainer) {
        ((TinkerStationContainer) container).setToolSelection(StationSlotLayoutLoader.getInstance().get(layoutName));
      }
    }
  }
}

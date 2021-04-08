package slimeknights.tconstruct.tables.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.tables.inventory.table.tinkerstation.TinkerStationContainer;

public class TinkerStationSelectionPacket implements IThreadsafePacket {

  private final int activeSlots;
  private final boolean tinkerSlotHidden;

  public TinkerStationSelectionPacket(int activeSlots, boolean tinkerSlotHidden) {
    this.activeSlots = activeSlots;
    this.tinkerSlotHidden = tinkerSlotHidden;
  }

  public TinkerStationSelectionPacket(PacketByteBuf buffer) {
    this.activeSlots = buffer.readInt();
    this.tinkerSlotHidden = buffer.readBoolean();
  }

  @Override
  public void encode(PacketByteBuf buffer) {
    buffer.writeInt(this.activeSlots);
    buffer.writeBoolean(this.tinkerSlotHidden);
  }

  @Override
  public void handleThreadsafe(Context context) {
    ServerPlayerEntity sender = context.getSender();
    if (sender != null) {
      ScreenHandler container = sender.currentScreenHandler;

      if (container instanceof TinkerStationContainer) {
        ((TinkerStationContainer) container).setToolSelection(this.activeSlots, this.tinkerSlotHidden);
      }
    }
  }
}

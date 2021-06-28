package slimeknights.tconstruct.tables.network;

import lombok.RequiredArgsConstructor;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.tables.inventory.table.tinkerstation.TinkerStationContainer;

@RequiredArgsConstructor
public class TinkerStationSelectionPacket implements IThreadsafePacket {

  private final int activeSlots;
  private final boolean tinkerSlotHidden;
  private final Item toolFilter;

  public TinkerStationSelectionPacket(PacketBuffer buffer) {
    this.activeSlots = buffer.readInt();
    this.tinkerSlotHidden = buffer.readBoolean();
    this.toolFilter = buffer.readRegistryIdUnsafe(ForgeRegistries.ITEMS);
  }

  @Override
  public void encode(PacketBuffer buffer) {
    buffer.writeInt(this.activeSlots);
    buffer.writeBoolean(this.tinkerSlotHidden);
    buffer.writeRegistryIdUnsafe(ForgeRegistries.ITEMS, toolFilter);
  }

  @Override
  public void handleThreadsafe(Context context) {
    ServerPlayerEntity sender = context.getSender();
    if (sender != null) {
      Container container = sender.openContainer;
      if (container instanceof TinkerStationContainer) {
        ToolDefinition filter = null;
        if (toolFilter instanceof ToolCore) {
          filter = ((ToolCore) toolFilter).getToolDefinition();
        }
        ((TinkerStationContainer) container).setToolSelection(this.activeSlots, this.tinkerSlotHidden, filter);
      }
    }
  }
}

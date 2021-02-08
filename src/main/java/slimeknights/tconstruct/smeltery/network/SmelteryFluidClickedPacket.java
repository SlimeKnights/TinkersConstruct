package slimeknights.tconstruct.smeltery.network;

import lombok.AllArgsConstructor;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import slimeknights.mantle.inventory.BaseContainer;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.smeltery.tileentity.tank.ISmelteryTankHandler;

/**
 * Packet sent when a fluid is clicked in the smeltery UI
 */
@AllArgsConstructor
public class SmelteryFluidClickedPacket implements IThreadsafePacket {
  private final int index;

  public SmelteryFluidClickedPacket(PacketBuffer buffer) {
    index = buffer.readVarInt();
  }

  @Override
  public void encode(PacketBuffer buffer) {
    buffer.writeVarInt(index);
  }

  @Override
  public void handleThreadsafe(Context context) {
    ServerPlayerEntity sender = context.getSender();
    if (sender != null) {
      Container container = sender.openContainer;
      if (container instanceof BaseContainer<?>) {
        TileEntity te = ((BaseContainer<?>)container).getTile();
        if (te instanceof ISmelteryTankHandler) {
          ((ISmelteryTankHandler) te).getTank().moveFluidToBottom(index);
        }
      }
    }
  }
}

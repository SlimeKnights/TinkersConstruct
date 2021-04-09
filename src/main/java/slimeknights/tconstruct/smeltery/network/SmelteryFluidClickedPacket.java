package slimeknights.tconstruct.smeltery.network;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import slimeknights.mantle.inventory.BaseContainer;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.smeltery.tileentity.tank.ISmelteryTankHandler;

/**
 * Packet sent when a fluid is clicked in the smeltery UI
 */
public class SmelteryFluidClickedPacket implements IThreadsafePacket {
  private final int index;

  public SmelteryFluidClickedPacket(int index) {
    this.index = index;
  }

  public SmelteryFluidClickedPacket(PacketByteBuf buffer) {
    index = buffer.readVarInt();
  }

  @Override
  public void encode(PacketByteBuf buffer) {
    buffer.writeVarInt(index);
  }

  @Override
  public void handleThreadsafe(PlayerEntity player, PacketSender context) {
    ServerPlayerEntity sender = (ServerPlayerEntity) player;
    if (sender != null) {
      ScreenHandler container = sender.currentScreenHandler;
      if (container instanceof BaseContainer<?>) {
        BlockEntity te = ((BaseContainer<?>)container).getTile();
        if (te instanceof ISmelteryTankHandler) {
          ((ISmelteryTankHandler) te).getTank().moveFluidToBottom(index);
        }
      }
    }
  }
}

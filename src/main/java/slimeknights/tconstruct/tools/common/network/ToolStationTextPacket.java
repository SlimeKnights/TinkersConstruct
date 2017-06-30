package slimeknights.tconstruct.tools.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import io.netty.buffer.ByteBuf;
import slimeknights.mantle.network.AbstractPacketThreadsafe;
import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.tools.common.inventory.ContainerToolStation;

public class ToolStationTextPacket extends AbstractPacketThreadsafe {

  public String text;

  public ToolStationTextPacket() {
  }

  public ToolStationTextPacket(String text) {
    this.text = text;
  }

  @Override
  public void handleClientSafe(NetHandlerPlayClient netHandler) {
    Container container = Minecraft.getMinecraft().player.openContainer;
    if(container instanceof ContainerToolStation) {
      ((ContainerToolStation) container).setToolName(text);
    }
  }

  @Override
  public void handleServerSafe(NetHandlerPlayServer netHandler) {
    Container container = netHandler.player.openContainer;
    if(container instanceof ContainerToolStation) {
      ((ContainerToolStation) container).setToolName(text);

      // find all people who also have the same gui open and update them too
      WorldServer server = netHandler.player.getServerWorld();
      for(EntityPlayer player : server.playerEntities) {
        if(player.openContainer instanceof ContainerToolStation) {
          if(((ContainerToolStation) container).sameGui((ContainerToolStation) player.openContainer)) {
            // same gui, send him an update
            TinkerNetwork.sendTo(this, (EntityPlayerMP) player);
          }
        }
      }
    }
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    text = ByteBufUtils.readUTF8String(buf);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    ByteBufUtils.writeUTF8String(buf, text);
  }
}

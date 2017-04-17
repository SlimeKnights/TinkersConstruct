package slimeknights.tconstruct.tools.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.WorldServer;

import io.netty.buffer.ByteBuf;
import slimeknights.mantle.inventory.BaseContainer;
import slimeknights.mantle.network.AbstractPacketThreadsafe;
import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.tools.common.client.GuiToolStation;
import slimeknights.tconstruct.tools.common.inventory.ContainerToolStation;

public class ToolStationSelectionPacket extends AbstractPacketThreadsafe {

  public ToolCore tool;
  public int activeSlots;

  public ToolStationSelectionPacket() {
  }

  public ToolStationSelectionPacket(ToolCore tool, int activeSlots) {
    this.tool = tool;
    this.activeSlots = activeSlots;
  }

  @Override
  public void handleClientSafe(NetHandlerPlayClient netHandler) {
    Container container = Minecraft.getMinecraft().player.openContainer;
    if(container instanceof ContainerToolStation) {
      ((ContainerToolStation) container).setToolSelection(tool, activeSlots);
      if(Minecraft.getMinecraft().currentScreen instanceof GuiToolStation) {
        ((GuiToolStation) Minecraft.getMinecraft().currentScreen).onToolSelectionPacket(this);
      }
    }
  }

  @Override
  public void handleServerSafe(NetHandlerPlayServer netHandler) {
    Container container = netHandler.player.openContainer;
    if(container instanceof ContainerToolStation) {
      ((ContainerToolStation) container).setToolSelection(tool, activeSlots);

      // find all people who also have the same gui open and update them too
      WorldServer server = netHandler.player.getServerWorld();
      for(EntityPlayer player : server.playerEntities) {
        if(player == netHandler.player) {
          continue;
        }
        if(player.openContainer instanceof ContainerToolStation) {
          if(((BaseContainer) container).sameGui((BaseContainer) player.openContainer)) {
            ((ContainerToolStation) player.openContainer).setToolSelection(tool, activeSlots);
            // same gui, send him an update
            TinkerNetwork.sendTo(this, (EntityPlayerMP) player);
          }
        }
      }
    }
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    int id = buf.readShort();
    if(id > -1) {
      Item item = Item.getItemById(id);
      if(item instanceof ToolCore) {
        tool = (ToolCore) item;
      }
    }

    activeSlots = buf.readInt();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    if(tool == null) {
      buf.writeShort(-1);
    }
    else {
      buf.writeShort(Item.getIdFromItem(tool));
    }

    buf.writeInt(activeSlots);
  }
}

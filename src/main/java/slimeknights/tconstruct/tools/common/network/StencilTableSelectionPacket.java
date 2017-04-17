package slimeknights.tconstruct.tools.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import io.netty.buffer.ByteBuf;
import slimeknights.mantle.inventory.BaseContainer;
import slimeknights.mantle.network.AbstractPacketThreadsafe;
import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.tools.common.client.GuiStencilTable;
import slimeknights.tconstruct.tools.common.inventory.ContainerStencilTable;

public class StencilTableSelectionPacket extends AbstractPacketThreadsafe {

  public ItemStack output;

  public StencilTableSelectionPacket() {
  }

  public StencilTableSelectionPacket(ItemStack output) {
    this.output = output;
  }

  @Override
  public void handleClientSafe(NetHandlerPlayClient netHandler) {
    Container container = Minecraft.getMinecraft().player.openContainer;
    if(container instanceof ContainerStencilTable) {
      ((ContainerStencilTable) container).setOutput(output);
      if(Minecraft.getMinecraft().currentScreen instanceof GuiStencilTable) {
        ((GuiStencilTable) Minecraft.getMinecraft().currentScreen).onSelectionPacket(this);
      }
    }
  }

  @Override
  public void handleServerSafe(NetHandlerPlayServer netHandler) {
    Container container = netHandler.player.openContainer;
    if(container instanceof ContainerStencilTable) {
      ((ContainerStencilTable) container).setOutput(output);

      // find all people who also have the same gui open and update them too
      WorldServer server = netHandler.player.getServerWorld();
      for(EntityPlayer player : server.playerEntities) {
        if(player == netHandler.player) {
          continue;
        }
        if(player.openContainer instanceof ContainerStencilTable) {
          if(((BaseContainer) container).sameGui((BaseContainer) player.openContainer)) {
            ((ContainerStencilTable) player.openContainer).setOutput(output);
            // same gui, send him an update
            TinkerNetwork.sendTo(this, (EntityPlayerMP) player);
          }
        }
      }
    }
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    output = ByteBufUtils.readItemStack(buf);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    ByteBufUtils.writeItemStack(buf, output);
  }
}

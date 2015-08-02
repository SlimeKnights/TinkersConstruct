package tconstruct.tools.network;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import io.netty.buffer.ByteBuf;
import tconstruct.TConstruct;
import tconstruct.common.network.AbstractPacketThreadsafe;

/**
 * Sent to the server when the user clicks on a tab in the TinkerStation GUI
 */
public class TinkerStationTabPacket extends AbstractPacketThreadsafe {

  public int blockX;
  public int blockY;
  public int blockZ;

  public TinkerStationTabPacket() {
  }

  @SideOnly(Side.CLIENT)
  public TinkerStationTabPacket(BlockPos pos) {
    this.blockX = pos.getX();
    this.blockY = pos.getY();
    this.blockZ = pos.getZ();
  }

  @Override
  public void handleClientSafe(NetHandlerPlayClient netHandler) {
    // never
  }

  @Override
  public void handleServerSafe(NetHandlerPlayServer netHandler) {
    EntityPlayerMP player = netHandler.playerEntity;

    ItemStack heldStack = null;
    if(player.inventory.getItemStack() != null) {
      heldStack = player.inventory.getItemStack();
      // set it to null so it's not getting dropped
      player.inventory.setItemStack(null);
    }

    player.openGui(TConstruct.instance, 0, player.worldObj, blockX, blockY, blockZ);

    // set hedl item again for the new container
    if(heldStack != null) {
      player.inventory.setItemStack(heldStack);
      // also send it to the client
      netHandler.sendPacket(new S2FPacketSetSlot(-1, -1, heldStack));
    }
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    blockX = buf.readInt();
    blockY = buf.readInt();
    blockZ = buf.readInt();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(blockX);
    buf.writeInt(blockY);
    buf.writeInt(blockZ);
  }
}

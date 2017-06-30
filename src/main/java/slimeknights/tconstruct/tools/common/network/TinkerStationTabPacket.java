package slimeknights.tconstruct.tools.common.network;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import io.netty.buffer.ByteBuf;
import slimeknights.mantle.network.AbstractPacketThreadsafe;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.tools.common.block.ITinkerStationBlock;

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
    // Serverside only
    throw new UnsupportedOperationException("Serverside only");
  }

  @Override
  public void handleServerSafe(NetHandlerPlayServer netHandler) {
    EntityPlayerMP player = netHandler.player;

    ItemStack heldStack = null;
    if(!player.inventory.getItemStack().isEmpty()) {
      heldStack = player.inventory.getItemStack();
      // set it to null so it's not getting dropped
      player.inventory.setItemStack(ItemStack.EMPTY);
    }

    BlockPos pos = new BlockPos(blockX, blockY, blockZ);
    IBlockState state = player.getEntityWorld().getBlockState(pos);
    if(state.getBlock() instanceof ITinkerStationBlock) {
      ((ITinkerStationBlock) state.getBlock()).openGui(player, player.getEntityWorld(), pos);
    }
    else {
      player.openGui(TConstruct.instance, 0, player.getEntityWorld(), blockX, blockY, blockZ);
    }

    // set held item again for the new container
    if(heldStack != null) {
      player.inventory.setItemStack(heldStack);
      // also send it to the client
      netHandler.sendPacket(new SPacketSetSlot(-1, -1, heldStack));
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

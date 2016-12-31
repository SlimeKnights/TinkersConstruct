package slimeknights.tconstruct.smeltery.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import io.netty.buffer.ByteBuf;
import slimeknights.mantle.network.AbstractPacketThreadsafe;

// Sent to the client when smeltery contents get updated on the server
// Needed to display items without open GUI
public class SmelteryInventoryUpdatePacket extends AbstractPacketThreadsafe {

  public int slot;
  public ItemStack stack;
  public BlockPos pos;

  public SmelteryInventoryUpdatePacket() {
  }

  public SmelteryInventoryUpdatePacket(ItemStack stack, int slot, BlockPos pos) {
    this.slot = slot;
    this.stack = stack;
    this.pos = pos;
  }

  @Override
  public void handleClientSafe(NetHandlerPlayClient netHandler) {
    TileEntity te = Minecraft.getMinecraft().world.getTileEntity(pos);
    if(te instanceof IInventory) {
      ((IInventory) te).setInventorySlotContents(slot, stack);
    }
  }

  @Override
  public void handleServerSafe(NetHandlerPlayServer netHandler) {
    // Clientside only
    throw new UnsupportedOperationException("Clientside only");
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    slot = buf.readInt();
    stack = ByteBufUtils.readItemStack(buf);
    pos = readPos(buf);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(slot);
    ByteBufUtils.writeItemStack(buf, stack);
    writePos(pos, buf);
  }
}

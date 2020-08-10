package slimeknights.tconstruct.tools.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.mantle.tileentity.InventoryTileEntity;
import slimeknights.mantle.util.TileEntityHelper;

public class InventorySlotSyncPacket implements IThreadsafePacket {

  public ItemStack itemStack;
  public int slot;
  public BlockPos pos;

  public InventorySlotSyncPacket(ItemStack itemStack, int slot, BlockPos pos) {
    this.itemStack = itemStack;
    this.slot = slot;
    this.pos = pos;
  }

  public InventorySlotSyncPacket(PacketBuffer buffer) {
    this.itemStack = buffer.readItemStack();
    this.slot = buffer.readShort();
    this.pos = buffer.readBlockPos();
  }

  @Override
  public void encode(PacketBuffer packetBuffer) {
    packetBuffer.writeItemStack(this.itemStack);
    packetBuffer.writeShort(this.slot);
    packetBuffer.writeBlockPos(this.pos);
  }

  @Override
  public void handleThreadsafe(Context context) {
    HandleClient.handle(this);
  }

  /** Safely runs client side only code in a method only called on client */
  private static class HandleClient {
    private static void handle(InventorySlotSyncPacket packet) {
      TileEntityHelper.getTile(InventoryTileEntity.class, Minecraft.getInstance().world, packet.pos).ifPresent(te -> {
        te.setInventorySlotContents(packet.slot, packet.itemStack);
        Minecraft.getInstance().worldRenderer.notifyBlockUpdate(null, packet.pos, null, null, 0);
      });
    }
  }
}

package slimeknights.tconstruct.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import slimeknights.mantle.network.packet.IThreadsafePacket;

public class InventorySlotSyncPacket implements IThreadsafePacket {

  public final ItemStack itemStack;
  public final int slot;
  public final BlockPos pos;

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
      World world = Minecraft.getInstance().world;
      if (world != null) {
        TileEntity te = world.getTileEntity(packet.pos);
        if (te != null) {
          te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            .filter(cap -> cap instanceof IItemHandlerModifiable)
            .ifPresent(cap -> {
              ((IItemHandlerModifiable)cap).setStackInSlot(packet.slot, packet.itemStack);
              //noinspection ConstantConditions
              Minecraft.getInstance().worldRenderer.notifyBlockUpdate(null, packet.pos, null, null, 0);
            });
        }
      }
    }
  }
}

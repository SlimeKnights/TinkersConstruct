package slimeknights.tconstruct.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import slimeknights.mantle.block.entity.InventoryBlockEntity;
import slimeknights.mantle.compat.neoforged.neoforge.network.NetworkEvent.Context;
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

  public InventorySlotSyncPacket(FriendlyByteBuf buffer) {
    this.itemStack = ItemStack.OPTIONAL_STREAM_CODEC.decode((RegistryFriendlyByteBuf)buffer);
    this.slot = buffer.readShort();
    this.pos = buffer.readBlockPos();
  }

  @Override
  public void encode(FriendlyByteBuf packetBuffer) {
    ItemStack.OPTIONAL_STREAM_CODEC.encode((RegistryFriendlyByteBuf)packetBuffer, this.itemStack);
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
      Level world = Minecraft.getInstance().level;
      if (world != null) {
        BlockEntity te = world.getBlockEntity(packet.pos);
        if (te != null) {
          if (te instanceof InventoryBlockEntity inventory) {
            inventory.getItemHandler().setStackInSlot(packet.slot, packet.itemStack);
            Minecraft.getInstance().levelRenderer.blockChanged(world, packet.pos, te.getBlockState(), te.getBlockState(), 0);
            return;
          }
          var cap = world.getCapability(Capabilities.ItemHandler.BLOCK, packet.pos, te.getBlockState(), te, null);
          if (cap instanceof IItemHandlerModifiable itemHandler) {
            itemHandler.setStackInSlot(packet.slot, packet.itemStack);
            Minecraft.getInstance().levelRenderer.blockChanged(world, packet.pos, te.getBlockState(), te.getBlockState(), 0);
          }
        }
      }
    }
  }
}

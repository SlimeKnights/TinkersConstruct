package slimeknights.tconstruct.tools.common.network;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.smeltery.tileentity.CastingTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryTileEntity;
import slimeknights.tconstruct.tables.tileentity.table.CraftingStationTileEntity;

public class InventorySlotSyncPacket implements IThreadsafePacket {

  public final ItemStack itemStack;
  public final int slot;
  public final BlockPos pos;

  public InventorySlotSyncPacket(ItemStack itemStack, int slot, BlockPos pos) {
    this.itemStack = itemStack;
    this.slot = slot;
    this.pos = pos;
  }

  public InventorySlotSyncPacket(PacketByteBuf buffer) {
    this.itemStack = buffer.readItemStack();
    this.slot = buffer.readShort();
    this.pos = buffer.readBlockPos();
  }

  @Override
  public void encode(PacketByteBuf packetBuffer) {
    packetBuffer.writeItemStack(this.itemStack);
    packetBuffer.writeShort(this.slot);
    packetBuffer.writeBlockPos(this.pos);
  }

  @Override
  public void handleThreadsafe(PlayerEntity player, PacketSender context) {
    HandleClient.handle(this);
  }

  /**
   * Safely runs client side only code in a method only called on client
   */
  private static class HandleClient {

    private static void handle(InventorySlotSyncPacket packet) {
      World world = MinecraftClient.getInstance().world;
      if (world != null) {
        BlockEntity te = world.getBlockEntity(packet.pos);
        if (te != null) {
          if (te instanceof SmelteryTileEntity) {
            SmelteryTileEntity smeltery = (SmelteryTileEntity) te;
            smeltery.meltingInventory.setStack(packet.slot, packet.itemStack);
            MinecraftClient.getInstance().worldRenderer.updateBlock(null, packet.pos, null, null, 0);
          }else if (te instanceof CastingTileEntity) {
            CastingTileEntity table = (CastingTileEntity) te;
            table.moldingInventory.setPattern(packet.itemStack);
            MinecraftClient.getInstance().worldRenderer.updateBlock(null, packet.pos, null, null, 0);
          } else if(te instanceof CraftingStationTileEntity) {
            CraftingStationTileEntity craftingStation = (CraftingStationTileEntity) te;
            craftingStation.setStack(packet.slot, packet.itemStack);
            MinecraftClient.getInstance().worldRenderer.updateBlock(null, packet.pos, null, null, 0);
          } else {
            throw new RuntimeException("Packet not fully implemented for " + te.getClass().getSimpleName());
          }
        }
      }
    }
  }
}

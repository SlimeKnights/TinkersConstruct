package slimeknights.tconstruct.tables.network;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;
import slimeknights.mantle.network.AbstractPacket;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.tables.block.ITinkerStationBlock;

import java.util.function.Supplier;

public class TinkerStationTabPacket extends AbstractPacket {

  public int blockX;
  public int blockY;
  public int blockZ;

  public TinkerStationTabPacket(BlockPos blockPos) {
    this.blockX = blockPos.getX();
    this.blockY = blockPos.getY();
    this.blockZ = blockPos.getZ();
  }

  public TinkerStationTabPacket(PacketBuffer buffer) {
    this.blockX = buffer.readInt();
    this.blockY = buffer.readInt();
    this.blockZ = buffer.readInt();
  }

  @Override
  public void encode(PacketBuffer packetBuffer) {
    packetBuffer.writeInt(this.blockX);
    packetBuffer.writeInt(this.blockY);
    packetBuffer.writeInt(this.blockZ);
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    supplier.get().enqueueWork(() -> {
      if (supplier.get().getDirection().getReceptionSide() == LogicalSide.SERVER) {
        if (supplier.get().getSender() != null) {
          ServerPlayerEntity playerEntity = supplier.get().getSender();
          ItemStack heldStack = null;

          if (playerEntity != null && !playerEntity.inventory.getItemStack().isEmpty()) {
            heldStack = playerEntity.inventory.getItemStack();
            // set it to empty, so it's doesn't get dropped
            playerEntity.inventory.setItemStack(ItemStack.EMPTY);
          }

          BlockPos pos = new BlockPos(blockX, blockY, blockZ);
          BlockState state = playerEntity.getEntityWorld().getBlockState(pos);

          if (state.getBlock() instanceof ITinkerStationBlock) {
            ((ITinkerStationBlock) state.getBlock()).openGui(playerEntity, playerEntity.getEntityWorld(), pos);
          } else {
            INamedContainerProvider provider = state.getContainer(playerEntity.getEntityWorld(), pos);

            if (provider != null) {
              NetworkHooks.openGui(playerEntity, provider, pos);
            }
          }

          if (heldStack != null) {
            playerEntity.inventory.setItemStack(heldStack);
            TinkerNetwork.getInstance().sendVanillaPacket(playerEntity, new SSetSlotPacket(-1, -1, heldStack));
          }
        }
      }
    });
    supplier.get().setPacketHandled(true);
  }
}

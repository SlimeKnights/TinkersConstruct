package slimeknights.tconstruct.tables.network;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.NetworkHooks;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.tables.block.ITinkerStationBlock;

public class StationTabPacket implements IThreadsafePacket {

  private BlockPos pos;
  public StationTabPacket(BlockPos blockPos) {
    this.pos = blockPos;
  }

  public StationTabPacket(PacketBuffer buffer) {
    this.pos = buffer.readBlockPos();
  }

  @Override
  public void encode(PacketBuffer buffer) {
    buffer.writeBlockPos(pos);
  }

  @Override
  public void handleThreadsafe(Context context) {
    ServerPlayerEntity sender = context.getSender();
    if (sender != null) {
      ItemStack heldStack = sender.inventory.getCarried();
      if (!heldStack.isEmpty()) {
        // set it to empty, so it's doesn't get dropped
        sender.inventory.setCarried(ItemStack.EMPTY);
      }

      World world = sender.getCommandSenderWorld();
      if (!world.hasChunkAt(pos)) {
        return;
      }
      BlockState state = world.getBlockState(pos);
      if (state.getBlock() instanceof ITinkerStationBlock) {
        ((ITinkerStationBlock) state.getBlock()).openGui(sender, sender.getCommandSenderWorld(), pos);
      } else {
        INamedContainerProvider provider = state.getMenuProvider(sender.getCommandSenderWorld(), pos);
        if (provider != null) {
          NetworkHooks.openGui(sender, provider, pos);
        }
      }

      if (!heldStack.isEmpty()) {
        sender.inventory.setCarried(heldStack);
        TinkerNetwork.getInstance().sendVanillaPacket(sender, new SSetSlotPacket(-1, -1, heldStack));
      }
    }
  }
}

package slimeknights.tconstruct.common.network;

import lombok.RequiredArgsConstructor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.BlockFlags;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.registries.GameData;
import slimeknights.mantle.network.packet.IThreadsafePacket;

/**
 * Packet to notify neighbors that a block changed, used when breaking blocks in weird contexts that vanilla suppresses updates in for some reason
 */
@RequiredArgsConstructor
public class UpdateNeighborsPacket implements IThreadsafePacket {
  private final BlockState state;
  private final BlockPos pos;

  public UpdateNeighborsPacket(PacketBuffer buffer) {
    this.state = GameData.getBlockStateIDMap().getByValue(buffer.readVarInt());
    this.pos = buffer.readBlockPos();
  }

  @Override
  public void encode(PacketBuffer buffer) {
    buffer.writeVarInt(Block.getStateId(state));
    buffer.writeBlockPos(pos);
  }

  @Override
  public void handleThreadsafe(Context context) {
    HandleClient.handle(this);
  }

  private static class HandleClient {
    private static void handle(UpdateNeighborsPacket packet) {
      World world = Minecraft.getInstance().world;
      if (world != null) {
        packet.state.updateNeighbours(world, packet.pos, BlockFlags.BLOCK_UPDATE, 511);
        packet.state.updateDiagonalNeighbors(world, packet.pos, BlockFlags.BLOCK_UPDATE, 511);
      }
    }
  }
}

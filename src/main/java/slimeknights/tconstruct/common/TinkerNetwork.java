package slimeknights.tconstruct.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;
import slimeknights.mantle.network.NetworkWrapper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.tools.common.network.BouncedPacket;
import slimeknights.tconstruct.tools.common.network.EntityMovementChangePacket;
import slimeknights.tconstruct.tools.common.network.InventorySlotSyncPacket;

public class TinkerNetwork extends NetworkWrapper {

  public static TinkerNetwork instance = new TinkerNetwork();

  public TinkerNetwork() {
    super(TConstruct.modID);
  }

  public void setup() {
    this.registerPacket(EntityMovementChangePacket.class, EntityMovementChangePacket::encode, EntityMovementChangePacket::new, EntityMovementChangePacket::handle);
    this.registerPacket(BouncedPacket.class, BouncedPacket::encode, BouncedPacket::new, BouncedPacket::handle);
    this.registerPacket(InventorySlotSyncPacket.class, InventorySlotSyncPacket::encode, InventorySlotSyncPacket::new, InventorySlotSyncPacket::handle);
  }

  public void sendVanillaPacket(Entity player, IPacket<?> packet) {
    if (player instanceof ServerPlayerEntity && ((ServerPlayerEntity) player).connection != null) {
      ((ServerPlayerEntity) player).connection.sendPacket(packet);
    }
  }

  public void sendToClientsAround(Object msg, ServerWorld serverWorld, BlockPos position) {
    Chunk chunk = serverWorld.getChunkAt(position);

    this.network.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), msg);
  }
}

package slimeknights.tconstruct.library.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.PacketDistributor;
import slimeknights.mantle.network.NetworkWrapper;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.smeltery.network.FaucetActivationPacket;
import slimeknights.tconstruct.smeltery.network.FluidUpdatePacket;
import slimeknights.tconstruct.smeltery.network.SmelteryFluidUpdatePacket;
import slimeknights.tconstruct.tables.network.TinkerStationTabPacket;
import slimeknights.tconstruct.tables.network.UpdateCraftingRecipePacket;
import slimeknights.tconstruct.tools.common.network.BouncedPacket;
import slimeknights.tconstruct.tools.common.network.EntityMovementChangePacket;
import slimeknights.tconstruct.tools.common.network.InventorySlotSyncPacket;

public class TinkerNetwork extends NetworkWrapper {

  private static TinkerNetwork instance;

  private TinkerNetwork() {
    super(Util.getResource("network"));
  }

  public static synchronized TinkerNetwork getInstance() {
    if(instance == null) {
      setup();
    }
    return instance;
  }

  public static void setup() {
    instance = new TinkerNetwork();
    instance.registerPacket(EntityMovementChangePacket.class, EntityMovementChangePacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(BouncedPacket.class, BouncedPacket::new, NetworkDirection.PLAY_TO_SERVER);
    instance.registerPacket(InventorySlotSyncPacket.class, InventorySlotSyncPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(TinkerStationTabPacket.class, TinkerStationTabPacket::new, NetworkDirection.PLAY_TO_SERVER);
    instance.registerPacket(FluidUpdatePacket.class, FluidUpdatePacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(SmelteryFluidUpdatePacket.class, SmelteryFluidUpdatePacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(FaucetActivationPacket.class, FaucetActivationPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(UpdateMaterialsPacket.class, UpdateMaterialsPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(UpdateMaterialStatsPacket.class, UpdateMaterialStatsPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(UpdateCraftingRecipePacket.class, UpdateCraftingRecipePacket::new, NetworkDirection.PLAY_TO_CLIENT);
  }

  public void sendVanillaPacket(Entity player, IPacket<?> packet) {
    if (player instanceof ServerPlayerEntity && ((ServerPlayerEntity) player).connection != null) {
      ((ServerPlayerEntity) player).connection.sendPacket(packet);
    }
  }

  @Override
  public void sendToClientsAround(Object msg, ServerWorld serverWorld, BlockPos position) {
    Chunk chunk = serverWorld.getChunkAt(position);

    this.network.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), msg);
  }
}

package slimeknights.tconstruct.library.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.PacketDistributor;
import slimeknights.mantle.network.NetworkWrapper;
import slimeknights.tconstruct.common.network.InventorySlotSyncPacket;
import slimeknights.tconstruct.common.network.UpdateNeighborsPacket;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.UpdateMaterialsPacket;
import slimeknights.tconstruct.library.materials.stats.UpdateMaterialStatsPacket;
import slimeknights.tconstruct.library.materials.traits.UpdateMaterialTraitsPacket;
import slimeknights.tconstruct.smeltery.network.ChannelFlowPacket;
import slimeknights.tconstruct.smeltery.network.FaucetActivationPacket;
import slimeknights.tconstruct.smeltery.network.FluidUpdatePacket;
import slimeknights.tconstruct.smeltery.network.SmelteryFluidClickedPacket;
import slimeknights.tconstruct.smeltery.network.SmelteryTankUpdatePacket;
import slimeknights.tconstruct.smeltery.network.StructureUpdatePacket;
import slimeknights.tconstruct.tables.network.StationTabPacket;
import slimeknights.tconstruct.tables.network.TinkerStationSelectionPacket;
import slimeknights.tconstruct.tables.network.UpdateCraftingRecipePacket;
import slimeknights.tconstruct.tables.network.UpdateStationScreenPacket;
import slimeknights.tconstruct.tables.network.UpdateTinkerStationRecipePacket;
import slimeknights.tconstruct.tools.network.EntityMovementChangePacket;
import slimeknights.tconstruct.tools.network.SwingArmPacket;

import javax.annotation.Nullable;

// TODO: move to common
public class TinkerNetwork extends NetworkWrapper {

  private static TinkerNetwork instance;

  private TinkerNetwork() {
    super(Util.getResource("network"));
  }

  public static synchronized TinkerNetwork getInstance() {
    if (instance == null) {
      setup();
    }
    return instance;
  }

  public static void setup() {
    instance = new TinkerNetwork();
    instance.registerPacket(InventorySlotSyncPacket.class, InventorySlotSyncPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(UpdateNeighborsPacket.class, UpdateNeighborsPacket::new, NetworkDirection.PLAY_TO_CLIENT);

    // gadgets
    instance.registerPacket(EntityMovementChangePacket.class, EntityMovementChangePacket::new, NetworkDirection.PLAY_TO_CLIENT);

    // tools
    instance.registerPacket(StationTabPacket.class, StationTabPacket::new, NetworkDirection.PLAY_TO_SERVER);
    instance.registerPacket(UpdateMaterialsPacket.class, UpdateMaterialsPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(UpdateMaterialStatsPacket.class, UpdateMaterialStatsPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(UpdateMaterialTraitsPacket.class, UpdateMaterialTraitsPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(UpdateCraftingRecipePacket.class, UpdateCraftingRecipePacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(TinkerStationSelectionPacket.class, TinkerStationSelectionPacket::new, NetworkDirection.PLAY_TO_SERVER);
    instance.registerPacket(UpdateTinkerStationRecipePacket.class, UpdateTinkerStationRecipePacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(UpdateStationScreenPacket.class, UpdateStationScreenPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(SwingArmPacket.class, SwingArmPacket::new, NetworkDirection.PLAY_TO_CLIENT);

    // smeltery
    instance.registerPacket(FluidUpdatePacket.class, FluidUpdatePacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(FaucetActivationPacket.class, FaucetActivationPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(ChannelFlowPacket.class, ChannelFlowPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(SmelteryTankUpdatePacket.class, SmelteryTankUpdatePacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(StructureUpdatePacket.class, StructureUpdatePacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(SmelteryFluidClickedPacket.class, SmelteryFluidClickedPacket::new, NetworkDirection.PLAY_TO_SERVER);
  }

  public void sendVanillaPacket(Entity player, IPacket<?> packet) {
    if (player instanceof ServerPlayerEntity && ((ServerPlayerEntity) player).connection != null) {
      ((ServerPlayerEntity) player).connection.sendPacket(packet);
    }
  }


  /**
   * Same as {@link #sendToClientsAround(Object, ServerWorld, BlockPos)}, but checks that the world is a serverworld
   * @param msg       Packet to send
   * @param world     World instance
   * @param position  Target position
   */
  public void sendToClientsAround(Object msg, @Nullable IWorld world, BlockPos position) {
    if (world instanceof ServerWorld) {
      sendToClientsAround(msg, (ServerWorld)world, position);
    }
  }

  /**
   * Sends a packet to all entities tracking the given entity
   * @param msg     Packet
   * @param world   World instance
   * @param entity  Entity to check
   */
  public void sendToTrackingAndSelf(Object msg, Entity entity) {
    this.network.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), msg);
  }

  /**
   * Sends a packet to all entities tracking the given entity
   * @param msg     Packet
   * @param world   World instance
   * @param entity  Entity to check
   */
  public void sendToTracking(Object msg, Entity entity) {
    this.network.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), msg);
  }
}

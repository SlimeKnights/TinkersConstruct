package slimeknights.tconstruct.library.network;

import net.minecraft.entity.Entity;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.network.NetworkWrapper;
import slimeknights.tconstruct.smeltery.network.ChannelFlowPacket;
import slimeknights.tconstruct.smeltery.network.FaucetActivationPacket;
import slimeknights.tconstruct.smeltery.network.FluidUpdatePacket;
import slimeknights.tconstruct.smeltery.network.SmelteryFluidClickedPacket;
import slimeknights.tconstruct.smeltery.network.SmelteryStructureUpdatedPacket;
import slimeknights.tconstruct.smeltery.network.SmelteryTankUpdatePacket;
import slimeknights.tconstruct.tables.network.StationTabPacket;
import slimeknights.tconstruct.tables.network.TinkerStationSelectionPacket;
import slimeknights.tconstruct.tables.network.UpdateCraftingRecipePacket;
import slimeknights.tconstruct.tables.network.UpdateStationScreenPacket;
import slimeknights.tconstruct.tables.network.UpdateTinkerStationRecipePacket;
import slimeknights.tconstruct.tools.common.network.BouncedPacket;
import slimeknights.tconstruct.tools.common.network.EntityMovementChangePacket;
import slimeknights.tconstruct.tools.common.network.InventorySlotSyncPacket;

// TODO: move to common
public class TinkerNetwork extends NetworkWrapper {

  private static TinkerNetwork instance;

  public static synchronized TinkerNetwork getInstance() {
    if (instance == null) {
      setup();
    }
    return instance;
  }

  public static void setup() {
    instance = new TinkerNetwork();
    /*instance.registerPacket(InventorySlotSyncPacket.class, InventorySlotSyncPacket::new, NetworkSide.CLIENTBOUND);

    // gadgets
    instance.registerPacket(EntityMovementChangePacket.class, EntityMovementChangePacket::new, NetworkSide.CLIENTBOUND);
    instance.registerPacket(BouncedPacket.class, BouncedPacket::new, NetworkSide.SERVERBOUND);

    // tools
    instance.registerPacket(StationTabPacket.class, StationTabPacket::new, NetworkSide.SERVERBOUND);
    instance.registerPacket(UpdateMaterialsPacket.class, UpdateMaterialsPacket::new, NetworkSide.CLIENTBOUND);
    instance.registerPacket(UpdateMaterialStatsPacket.class, UpdateMaterialStatsPacket::new, NetworkSide.CLIENTBOUND);
    instance.registerPacket(UpdateCraftingRecipePacket.class, UpdateCraftingRecipePacket::new, NetworkSide.CLIENTBOUND);
    instance.registerPacket(TinkerStationSelectionPacket.class, TinkerStationSelectionPacket::new, NetworkSide.SERVERBOUND);
    instance.registerPacket(UpdateTinkerStationRecipePacket.class, UpdateTinkerStationRecipePacket::new, NetworkSide.CLIENTBOUND);
    instance.registerPacket(UpdateStationScreenPacket.class, UpdateStationScreenPacket::new, NetworkSide.CLIENTBOUND);

    // smeltery
    instance.registerPacket(FluidUpdatePacket.class, FluidUpdatePacket::new, NetworkSide.CLIENTBOUND);
    instance.registerPacket(FaucetActivationPacket.class, FaucetActivationPacket::new, NetworkSide.CLIENTBOUND);
    instance.registerPacket(ChannelFlowPacket.class, ChannelFlowPacket::new, NetworkSide.CLIENTBOUND);
    instance.registerPacket(SmelteryTankUpdatePacket.class, SmelteryTankUpdatePacket::new, NetworkSide.CLIENTBOUND);
    instance.registerPacket(SmelteryStructureUpdatedPacket.class, SmelteryStructureUpdatedPacket::new, NetworkSide.CLIENTBOUND);
    instance.registerPacket(SmelteryFluidClickedPacket.class, SmelteryFluidClickedPacket::new, NetworkSide.SERVERBOUND);*/
  }

  public void sendVanillaPacket(Entity player, Packet<?> packet) {
    if (player instanceof ServerPlayerEntity && ((ServerPlayerEntity) player).networkHandler != null) {
      ((ServerPlayerEntity) player).networkHandler.sendPacket(packet);
    }
  }


  /**
   * Same as {@link #sendToClientsAround(Object, ServerWorld, BlockPos)}, but checks that the world is a serverworld
   * @param msg       Packet to send
   * @param world     World instance
   * @param position  Target position
   */
  public void sendToClientsAround(Object msg, @Nullable WorldAccess world, BlockPos position) {
    if (world instanceof ServerWorld) {
      sendToClientsAround(msg, (ServerWorld)world, position);
    }
  }
}

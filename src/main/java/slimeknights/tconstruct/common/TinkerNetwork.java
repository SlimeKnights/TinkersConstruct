package slimeknights.tconstruct.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.List;

import slimeknights.mantle.network.AbstractPacket;
import slimeknights.mantle.network.NetworkWrapper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.smeltery.network.FaucetActivationPacket;
import slimeknights.tconstruct.smeltery.network.SmelteryFluidClicked;
import slimeknights.tconstruct.smeltery.network.SmelteryFluidUpdatePacket;
import slimeknights.tconstruct.smeltery.network.SmelteryFuelUpdatePacket;
import slimeknights.tconstruct.smeltery.network.SmelteryInventoryUpdatePacket;
import slimeknights.tconstruct.smeltery.network.TankFluidUpdatePacket;
import slimeknights.tconstruct.tools.network.EntityMovementChangePacket;
import slimeknights.tconstruct.tools.network.InventoryCraftingSyncPacket;
import slimeknights.tconstruct.tools.network.InventorySlotSyncPacket;
import slimeknights.tconstruct.tools.network.PartCrafterSelectionPacket;
import slimeknights.tconstruct.tools.network.StencilTableSelectionPacket;
import slimeknights.tconstruct.tools.network.TinkerStationTabPacket;
import slimeknights.tconstruct.tools.network.ToolBreakAnimationPacket;
import slimeknights.tconstruct.tools.network.ToolStationSelectionPacket;
import slimeknights.tconstruct.tools.network.ToolStationTextPacket;

public class TinkerNetwork extends NetworkWrapper {
  public static TinkerNetwork instance = new TinkerNetwork();

  public TinkerNetwork() {
    super(TConstruct.modID);
  }

  public void setup() {
    // register all the packets

    // TOOLS
    registerPacket(StencilTableSelectionPacket.class);
    registerPacket(PartCrafterSelectionPacket.class);
    registerPacket(ToolStationSelectionPacket.class);
    registerPacket(ToolStationTextPacket.class);
    registerPacketServer(TinkerStationTabPacket.class);
    registerPacketServer(InventoryCraftingSyncPacket.class);
    registerPacketClient(InventorySlotSyncPacket.class);
    registerPacketClient(EntityMovementChangePacket.class);
    registerPacketClient(ToolBreakAnimationPacket.class);

    // SMELTERY
    registerPacketClient(SmelteryFluidUpdatePacket.class);
    registerPacketClient(SmelteryFuelUpdatePacket.class);
    registerPacketClient(SmelteryInventoryUpdatePacket.class);
    registerPacketServer(SmelteryFluidClicked.class);
    registerPacketClient(TankFluidUpdatePacket.class);
    registerPacketClient(FaucetActivationPacket.class);
  }

  public static void sendToAll(AbstractPacket packet)
  {
    instance.network.sendToAll(packet);
  }

  public static void sendTo(AbstractPacket packet, EntityPlayerMP player)
  {
    instance.network.sendTo(packet, player);
  }


  public static void sendToAllAround(AbstractPacket packet, NetworkRegistry.TargetPoint point)
  {
    instance.network.sendToAllAround(packet, point);
  }

  public static void sendToDimension(AbstractPacket packet, int dimensionId)
  {
    instance.network.sendToDimension(packet, dimensionId);
  }

  public static void sendToServer(AbstractPacket packet)
  {
    instance.network.sendToServer(packet);
  }

  public static void sendToClients(WorldServer world, BlockPos pos, AbstractPacket packet) {
      Chunk chunk = world.getChunkFromBlockCoords(pos);
      for(EntityPlayer player : world.playerEntities) {
        // only send to relevant players
        if(!(player instanceof EntityPlayerMP)) {
          continue;
        }
        EntityPlayerMP playerMP = (EntityPlayerMP) player;
        if(world.getPlayerManager().isPlayerWatchingChunk(playerMP, chunk.xPosition, chunk.zPosition)) {
          TinkerNetwork.sendTo(packet, playerMP);
        }
      }
  }
}

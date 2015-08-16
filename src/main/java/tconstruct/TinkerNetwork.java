package tconstruct;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import tconstruct.common.network.AbstractPacket;
import tconstruct.common.network.NetworkWrapper;
import tconstruct.tools.network.PartCrafterSelectionPacket;
import tconstruct.tools.network.StencilTableSelectionPacket;
import tconstruct.tools.network.TinkerStationTabPacket;
import tconstruct.tools.network.ToolStationSelectionPacket;
import tconstruct.tools.network.ToolStationTextPacket;

public class TinkerNetwork extends NetworkWrapper {
  static TinkerNetwork instance = new TinkerNetwork();

  public TinkerNetwork() {
    super(TConstruct.modID);
  }

  public void setup() {
    // register all the packets
    registerPacket(StencilTableSelectionPacket.class);
    registerPacket(PartCrafterSelectionPacket.class);
    registerPacket(ToolStationSelectionPacket.class);
    registerPacket(ToolStationTextPacket.class);
    registerPacketServer(TinkerStationTabPacket.class);
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
}

package tconstruct;

import tconstruct.common.network.NetworkWrapper;
import tconstruct.tools.network.TinkerStationTabPacket;

public class TinkerNetwork extends NetworkWrapper {
  public TinkerNetwork() {
    super(TConstruct.modID);

    // register all the packets
    registerPacketServer(TinkerStationTabPacket.class);
  }
}

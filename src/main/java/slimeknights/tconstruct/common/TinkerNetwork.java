package slimeknights.tconstruct.common;

import slimeknights.mantle.network.NetworkWrapper;
import slimeknights.mantle.network.book.PacketUpdateSavedPage;
import slimeknights.tconstruct.TConstruct;

public class TinkerNetwork extends NetworkWrapper {

  public static TinkerNetwork instance = new TinkerNetwork();

  public TinkerNetwork() {
    super(TConstruct.modID);
  }

  public void setup() {
    this.registerPacket(PacketUpdateSavedPage.class, PacketUpdateSavedPage::encode, PacketUpdateSavedPage::new, PacketUpdateSavedPage::handle);
  }
}

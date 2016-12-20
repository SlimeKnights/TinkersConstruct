package slimeknights.tconstruct.smeltery.inventory;

import slimeknights.mantle.inventory.BaseContainer;
import slimeknights.tconstruct.smeltery.tileentity.TileTinkerTank;

public class ContainerTinkerTank extends BaseContainer<TileTinkerTank> {

  public ContainerTinkerTank(TileTinkerTank tile) {
    super(tile);

    // no player inventory as we don't actually use slots
  }

}

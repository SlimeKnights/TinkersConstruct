package slimeknights.tconstruct.tools.item;

import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.tinkering.MaterialItem;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

public class RepairKitItem extends MaterialItem {
  public RepairKitItem(Properties properties) {
    super(properties);
  }

  @Override
  public boolean canUseMaterial(IMaterial material) {
    return MaterialRegistry.getInstance().getMaterialStats(material.getIdentifier(), HeadMaterialStats.ID).isPresent();
  }
}

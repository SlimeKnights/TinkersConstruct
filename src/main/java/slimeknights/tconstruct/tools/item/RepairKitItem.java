package slimeknights.tconstruct.tools.item;

import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.stats.IRepairableMaterialStats;
import slimeknights.tconstruct.library.tinkering.MaterialItem;

public class RepairKitItem extends MaterialItem {
  public RepairKitItem(Properties properties) {
    super(properties);
  }

  @Override
  public boolean canUseMaterial(IMaterial material) {
    return MaterialRegistry.getInstance()
                           .getAllStats(material.getIdentifier())
                           .stream()
                           .anyMatch(stats -> stats instanceof IRepairableMaterialStats);
  }
}

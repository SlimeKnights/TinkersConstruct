package slimeknights.tconstruct.tools.item;

import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.stats.IRepairableMaterialStats;
import slimeknights.tconstruct.library.tools.part.MaterialItem;

public class RepairKitItem extends MaterialItem {
  public RepairKitItem(Properties properties) {
    super(properties);
  }

  @Override
  public boolean canUseMaterial(MaterialId material) {
    return MaterialRegistry.getInstance()
                           .getAllStats(material)
                           .stream()
                           .anyMatch(stats -> stats instanceof IRepairableMaterialStats);
  }
}

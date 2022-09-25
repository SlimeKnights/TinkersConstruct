package slimeknights.tconstruct.library.client.book.sectiontransformer.materials;

import com.google.common.collect.ImmutableSet;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import java.util.Set;

/** @deprecated use {@link TierRangeMaterialSectionTransformer} */
@Deprecated
public class TieredMaterialSectionTransformer extends AbstractMaterialSectionTransformer {
  private static final Set<MaterialStatsId> VISIBLE_STATS = ImmutableSet.of(HeadMaterialStats.ID, HandleMaterialStats.ID, ExtraMaterialStats.ID);

  private final int materialTier;
  public TieredMaterialSectionTransformer(String sectionName, int materialTier, boolean detailed) {
    super(sectionName, detailed);
    this.materialTier = materialTier;
  }

  @Override
  protected boolean isValidMaterial(IMaterial material) {
    if (material.getTier() != this.materialTier) {
      return false;
    }
    // only show material stats for types with the proper stat types, as otherwise the page will be empty
    // if you want to show another stat type, just override this method/implement the parent class
    for (IMaterialStats stats : MaterialRegistry.getInstance().getAllStats(material.getIdentifier())) {
      if (VISIBLE_STATS.contains(stats.getIdentifier())) {
        return true;
      }
    }
    return false;
  }
}

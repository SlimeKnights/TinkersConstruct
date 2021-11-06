package slimeknights.tconstruct.library.book.sectiontransformer.materials;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.tconstruct.library.materials.IMaterialRegistry;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import java.util.List;

/** Material section transformer for a given tier and material set */
@OnlyIn(Dist.CLIENT)
public class TieredMaterialSectionTransformer extends AbstractMaterialSectionTransformer {
  private static final List<MaterialStatsId> VISIBLE_STATS = ImmutableList.of(HeadMaterialStats.ID, HandleMaterialStats.ID, ExtraMaterialStats.ID);

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
    MaterialId id = material.getIdentifier();
    IMaterialRegistry registry = MaterialRegistry.getInstance();
    for (MaterialStatsId stats : VISIBLE_STATS) {
      if (registry.getMaterialStats(id, stats).isPresent()) {
        return true;
      }
    }
    return false;
  }
}

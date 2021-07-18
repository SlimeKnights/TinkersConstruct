package slimeknights.tconstruct.library.book.sectiontransformer.materials;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.tconstruct.library.materials.definition.IMaterial;

@OnlyIn(Dist.CLIENT)
public class TieredMaterialSectionTransformer extends AbstractMaterialSectionTransformer {
  private final int materialTier;
  public TieredMaterialSectionTransformer(String sectionName, int materialTier, boolean detailed) {
    super(sectionName, detailed);
    this.materialTier = materialTier;
  }

  @Override
  protected boolean isValidMaterial(IMaterial material) {
    return material.getTier() == this.materialTier;
  }
}

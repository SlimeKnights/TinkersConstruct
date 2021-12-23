package slimeknights.tconstruct.library.book.sectiontransformer.materials;

import slimeknights.tconstruct.library.book.content.ContentMaterial;
import slimeknights.tconstruct.library.book.content.ContentMaterialSkull;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.tools.stats.SkullStats;

/** Section transformer for skull material pages */
public class SkullMaterialSectionTransformer extends AbstractMaterialSectionTransformer {
  public SkullMaterialSectionTransformer(String sectionName, boolean detailed) {
    super(sectionName, detailed);
  }

  @Override
  protected boolean isValidMaterial(IMaterial material) {
    return MaterialRegistry.getInstance().getMaterialStats(material.getIdentifier(), SkullStats.ID).isPresent();
  }

  @Override
  protected ContentMaterial getPageContent(IMaterial material) {
    return new ContentMaterialSkull(material, detailed);
  }
}

package slimeknights.tconstruct.library.book.sectiontransformer;

import slimeknights.mantle.client.book.data.content.PageContent;
import slimeknights.tconstruct.library.book.ContentMaterial;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;

/** Populates the materials section for tool materials with content */
public class MaterialSectionTransformer extends AbstractMaterialSectionTransformer {

  public MaterialSectionTransformer() {
    super("materials");
  }

  @Override
  protected boolean isValidMaterial(Material material) {
    return material.hasStats(MaterialTypes.HEAD) || material.hasStats(MaterialTypes.HEAD) || material.hasStats(MaterialTypes.HEAD);
  }

  @Override
  protected PageContent getPageContent(Material material) {
    return new ContentMaterial(material);
  }
}

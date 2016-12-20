package slimeknights.tconstruct.library.book.sectiontransformer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.mantle.client.book.data.content.PageContent;
import slimeknights.tconstruct.library.book.content.ContentMaterial;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;

/** Populates the materials section for tool materials with content */
@SideOnly(Side.CLIENT)
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

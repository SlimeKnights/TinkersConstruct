package slimeknights.tconstruct.library.book.sectiontransformer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.mantle.client.book.data.content.PageContent;
import slimeknights.tconstruct.library.book.content.ContentBowMaterial;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;

/** Populates the materials section for tool materials with content */
@SideOnly(Side.CLIENT)
public class BowMaterialSectionTransformer extends AbstractMaterialSectionTransformer {

  public BowMaterialSectionTransformer() {
    super("bowmaterials");
  }

  @Override
  protected boolean isValidMaterial(Material material) {
    return material.hasStats(MaterialTypes.BOW) || material.hasStats(MaterialTypes.SHAFT);
  }

  @Override
  protected PageContent getPageContent(Material material) {
    return new ContentBowMaterial(material);
  }
}

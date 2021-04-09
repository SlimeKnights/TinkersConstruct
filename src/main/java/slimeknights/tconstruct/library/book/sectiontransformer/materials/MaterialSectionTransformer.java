package slimeknights.tconstruct.library.book.sectiontransformer.materials;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.mantle.client.book.data.content.PageContent;
import slimeknights.tconstruct.library.book.content.ContentMaterial;
import slimeknights.tconstruct.library.materials.IMaterial;

@OnlyIn(Dist.CLIENT)
public class MaterialSectionTransformer extends AbstractMaterialSectionTransformer {

  public MaterialSectionTransformer() {
    super("materials");
  }

  @Override
  protected boolean isValidMaterial(IMaterial material) {
    return true;
  }

  @Override
  protected PageContent getPageContent(IMaterial material) {
    return new ContentMaterial(material);
  }
}

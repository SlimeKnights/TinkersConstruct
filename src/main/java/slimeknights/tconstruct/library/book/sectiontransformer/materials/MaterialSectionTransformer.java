package slimeknights.tconstruct.library.book.sectiontransformer.materials;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import slimeknights.mantle.client.book.data.content.PageContent;
import slimeknights.tconstruct.library.book.content.ContentMaterial;
import slimeknights.tconstruct.library.materials.IMaterial;
import java.util.List;

import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class MaterialSectionTransformer extends AbstractMaterialSectionTransformer {

  public MaterialSectionTransformer() {
    super("materials");
  }

  @Override
  protected boolean isValidMaterial(IMaterial material) {
    return true;
  }

  @Override
  protected PageContent getPageContent(IMaterial material, List<ItemStack> displayStacks) {
    return new ContentMaterial(material, displayStacks);
  }
}

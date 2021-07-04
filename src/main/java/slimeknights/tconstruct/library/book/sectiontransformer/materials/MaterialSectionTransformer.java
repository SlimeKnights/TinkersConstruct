package slimeknights.tconstruct.library.book.sectiontransformer.materials;

import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.mantle.client.book.data.content.PageContent;
import slimeknights.tconstruct.library.book.content.ContentMaterial;
import slimeknights.tconstruct.library.materials.definition.IMaterial;

import java.util.List;

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
  protected PageContent getPageContent(IMaterial material, List<ItemStack> displayStacks) {
    return new ContentMaterial(material, displayStacks);
  }
}

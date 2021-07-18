package slimeknights.tconstruct.library.book.sectiontransformer.materials;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.tconstruct.library.materials.definition.IMaterial;

// TODO: still needed? Not used in the mod currently
@OnlyIn(Dist.CLIENT)
public class MaterialSectionTransformer extends AbstractMaterialSectionTransformer {
  public static final MaterialSectionTransformer INSTANCE = new MaterialSectionTransformer("materials", false);

  public MaterialSectionTransformer(String name, boolean detailed) {
    super(name, detailed);
  }

  @Override
  protected boolean isValidMaterial(IMaterial material) {
    return true;
  }
}

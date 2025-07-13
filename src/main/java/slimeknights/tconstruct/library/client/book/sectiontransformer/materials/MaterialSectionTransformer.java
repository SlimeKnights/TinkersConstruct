package slimeknights.tconstruct.library.client.book.sectiontransformer.materials;

import slimeknights.tconstruct.library.materials.definition.IMaterial;

/** @deprecated use {@link TierRangeMaterialSectionTransformer} */
@SuppressWarnings("removal")
@Deprecated(forRemoval = true)
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

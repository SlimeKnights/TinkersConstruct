package tconstruct.library.tools.materials;

public class AbstractMaterialStats implements IMaterialStats {

  protected final String materialType;

  public AbstractMaterialStats(String materialType) {
    this.materialType = materialType;
  }

  @Override
  public String getMaterialType() {
    return materialType;
  }
}

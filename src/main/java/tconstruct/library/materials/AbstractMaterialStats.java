package tconstruct.library.materials;

public abstract class AbstractMaterialStats implements IMaterialStats {

  protected final String materialType;

  public AbstractMaterialStats(String materialType) {
    this.materialType = materialType;
  }

  @Override
  public String getIdentifier() {
    return materialType;
  }
}

package slimeknights.tconstruct.library.materials;

import java.text.DecimalFormat;

public abstract class AbstractMaterialStats implements IMaterialStats {

  protected static final DecimalFormat df = new DecimalFormat("#,###,###.##");
  protected static final DecimalFormat dfPercent = new DecimalFormat("#%");

  protected final String materialType;

  public AbstractMaterialStats(String materialType) {
    this.materialType = materialType;
  }

  @Override
  public String getIdentifier() {
    return materialType;
  }
}

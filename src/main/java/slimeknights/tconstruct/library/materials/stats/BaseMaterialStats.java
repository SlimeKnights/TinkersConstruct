package slimeknights.tconstruct.library.materials.stats;

import java.util.List;

/**
 * A simple material class without stats.
 * This class is meant to be extended with custom stats added to it for your use.
 */
public class BaseMaterialStats implements IMaterialStats {

  private final MaterialStatType identifier;

  public BaseMaterialStats(MaterialStatType identifier) {
    this.identifier = identifier;
  }

  @Override
  public MaterialStatType getIdentifier() {
    return identifier;
  }

  @Override
  public String getLocalizedName() {
    // todo
    return null;
  }

  @Override
  public List<String> getLocalizedInfo() {
    // todo
    return null;
  }

  @Override
  public List<String> getLocalizedDesc() {
    // todo
    return null;
  }
}

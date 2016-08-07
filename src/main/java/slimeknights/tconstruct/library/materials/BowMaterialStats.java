package slimeknights.tconstruct.library.materials;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class BowMaterialStats extends AbstractMaterialStats {

  public BowMaterialStats() {
    super(MaterialTypes.BOW);
  }

  @Override
  public List<String> getLocalizedInfo() {
    return ImmutableList.of("");
  }

  @Override
  public List<String> getLocalizedDesc() {
    return ImmutableList.of("");
  }
}

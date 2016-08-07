package slimeknights.tconstruct.library.materials;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class BowStringMaterialStats extends AbstractMaterialStats {

  public BowStringMaterialStats() {
    super(MaterialTypes.BOWSTRING);
  }

  @Override
  public List<String> getLocalizedInfo() {
    return ImmutableList.of();
  }

  @Override
  public List<String> getLocalizedDesc() {
    return ImmutableList.of();
  }
}

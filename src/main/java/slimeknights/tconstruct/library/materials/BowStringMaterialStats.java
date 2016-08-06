package slimeknights.tconstruct.library.materials;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class BowStringMaterialStats extends AbstractMaterialStats {

  public final static String TYPE = "bowstring";

  public BowStringMaterialStats() {
    super(TYPE);
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

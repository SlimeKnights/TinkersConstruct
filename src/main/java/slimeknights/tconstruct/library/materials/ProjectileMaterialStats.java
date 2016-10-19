package slimeknights.tconstruct.library.materials;

import com.google.common.collect.ImmutableList;

import java.util.List;

/** Basically a dummy stat type to differentiate projectile parts from regular parts */
public class ProjectileMaterialStats extends AbstractMaterialStats {

  public ProjectileMaterialStats() {
    super(MaterialTypes.PROJECTILE);
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

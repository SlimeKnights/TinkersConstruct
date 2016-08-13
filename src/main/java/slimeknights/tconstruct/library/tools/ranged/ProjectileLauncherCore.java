package slimeknights.tconstruct.library.tools.ranged;

import java.util.List;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ProjectileLauncherNBT;
import slimeknights.tconstruct.library.tools.TinkerToolCore;
import slimeknights.tconstruct.library.tools.ToolNBT;

public abstract class ProjectileLauncherCore extends TinkerToolCore {

  public ProjectileLauncherCore(PartMaterialType... requiredComponents) {
    super(requiredComponents);
  }


  @Override
  public final ToolNBT buildTagData(List<Material> materials) {
    return buildProjectileLauncherTagData(materials);
  }

  public abstract ProjectileLauncherNBT buildProjectileLauncherTagData(List<Material> materials);
}

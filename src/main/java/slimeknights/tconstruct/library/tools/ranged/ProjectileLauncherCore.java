package slimeknights.tconstruct.library.tools.ranged;

import net.minecraft.item.ItemStack;

import java.util.List;

import slimeknights.mantle.util.TagHelper;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ProjectileLauncherNBT;
import slimeknights.tconstruct.library.tools.TinkerToolCore;
import slimeknights.tconstruct.library.tools.ToolNBT;

public abstract class ProjectileLauncherCore extends TinkerToolCore {

  public ProjectileLauncherCore(PartMaterialType... requiredComponents) {
    super(requiredComponents);
  }

  protected ProjectileLauncherNBT getData(ItemStack stack) {
    return ProjectileLauncherNBT.from(stack);
  }

  @Override
  public abstract ProjectileLauncherNBT buildTagData(List<Material> materials);
}

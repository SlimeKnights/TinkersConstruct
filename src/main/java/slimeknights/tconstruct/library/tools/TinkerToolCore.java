package slimeknights.tconstruct.library.tools;

import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;

public abstract class TinkerToolCore extends ToolCore {

  public TinkerToolCore(PartMaterialType... requiredComponents) {
    super(requiredComponents);
  }

  @Override
  public final NBTTagCompound buildTag(List<Material> materials) {
    return buildTagData(materials).get();
  }

  protected abstract ToolNBT buildTagData(List<Material> materials);
}

package tconstruct.library.tools.partbehaviors;

import net.minecraft.nbt.NBTTagCompound;

import tconstruct.library.tools.Material;
import tconstruct.library.tools.materials.ToolMaterialStats;
import tconstruct.library.utils.Tags;

public class HandlePartBehavior extends PartBehavior {

  public HandlePartBehavior() {
    super(ToolMaterialStats.TYPE);
  }

  @Override
  public void applyPartBehavior(NBTTagCompound tag, Material material) {
    ToolMaterialStats stats = material.getStats(ToolMaterialStats.TYPE, ToolMaterialStats.class);

    tag.setInteger(Tags.DURABILITY,
                   Math.round(tag.getInteger(Tags.DURABILITY) * stats.durabilityModifier));
  }
}

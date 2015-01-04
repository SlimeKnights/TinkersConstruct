package tconstruct.library.tools.partbehaviors;

import net.minecraft.nbt.NBTTagCompound;

import tconstruct.library.tools.Material;
import tconstruct.library.tools.materials.ToolMaterialStats;
import tconstruct.library.utils.TagUtil;
import tconstruct.library.utils.Tags;

public class HeadPartBehavior extends PartBehavior {

  public HeadPartBehavior() {
    super(ToolMaterialStats.TYPE);
  }

  @Override
  public void applyPartBehavior(NBTTagCompound tag, Material material) {
    ToolMaterialStats stats = material.getStats(ToolMaterialStats.TYPE, ToolMaterialStats.class);

    TagUtil.addInteger(tag, TagUtil.TAG_DURABILITY, stats.durability);

  }
}

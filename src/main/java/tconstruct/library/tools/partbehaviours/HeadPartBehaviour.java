package tconstruct.library.tools.partbehaviours;

import net.minecraft.nbt.NBTTagCompound;

import tconstruct.library.tools.Material;
import tconstruct.library.tools.materials.ToolMaterialStats;
import tconstruct.library.utils.TagUtil;

public class HeadPartBehaviour extends PartBehaviour {

  public HeadPartBehaviour() {
    super(ToolMaterialStats.TYPE);
  }

  @Override
  public void applyPartBehaviour(NBTTagCompound tag, Material material) {
    ToolMaterialStats stats = material.getStats(ToolMaterialStats.TYPE, ToolMaterialStats.class);

    TagUtil.addInteger(tag, TagUtil.TAG_DURABILITY, stats.durability);

  }
}

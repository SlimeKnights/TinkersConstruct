package tconstruct.tools;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import tconstruct.Util;
import tconstruct.library.tools.Material;
import tconstruct.library.tools.PartMaterialWrapper;
import tconstruct.library.tools.ToolCore;
import tconstruct.library.tools.materials.ToolMaterialStats;
import tconstruct.library.utils.ToolBuilder;

public class TestTool extends ToolCore {

  public TestTool(PartMaterialWrapper... requiredComponents) {
    super(requiredComponents);
  }

  @Override
  protected NBTTagCompound buildToolTag(Material[] materials) {
    Util.logger.info("Parts are valid");
    return ToolBuilder.buildSimpleTool(materials[0], materials[1]);
  }

  @Override
  public String getItemType() {
    return "harvest";
  }

  @Override
  public String[] getInformation() {
    return new String[0];
  }
}

package tconstruct.debug;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import tconstruct.library.tinkering.Material;
import tconstruct.library.tinkering.PartMaterialWrapper;
import tconstruct.library.tinkering.TinkersItem;
import tconstruct.library.utils.Log;
import tconstruct.library.utils.ToolBuilder;

public class TestTool extends TinkersItem {

  public TestTool(PartMaterialWrapper... requiredComponents) {
    super(requiredComponents);
  }

  @Override
  protected NBTTagCompound buildTag(Material[] materials) {
    Log.info("Parts are valid");
    return ToolBuilder.buildSimpleTool(materials[0], materials[1]);
  }

  @Override
  public String getItemType() {
    return "harvest";
  }

  @Override
  public String[] getInformation(ItemStack stack) {
    return new String[0];
  }
}

package tconstruct.tools;

import net.minecraft.nbt.NBTTagCompound;

import tconstruct.library.tinkering.Category;
import tconstruct.library.tinkering.Material;
import tconstruct.library.tinkering.PartMaterialType;
import tconstruct.library.tools.ToolCore;
import tconstruct.library.utils.ToolBuilder;

public class Pickaxe extends ToolCore {

  // Pick-head, binding, tool-rod
  public Pickaxe() {
    super(new PartMaterialType.ToolPartType(TinkerTools.pickHead),
          new PartMaterialType.ToolPartType(TinkerTools.binding),
          new PartMaterialType.ToolPartType(TinkerTools.toolrod));

    addCategory(Category.HARVEST);

    // set the toolclass, actual harvestlevel is done by the overridden callback
    this.setHarvestLevel("pickaxe", 0);
  }

  @Override
  protected NBTTagCompound buildTag(Material[] materials) {
    return ToolBuilder.buildSimpleTool(materials[0], materials[2], materials[1]);
  }
}

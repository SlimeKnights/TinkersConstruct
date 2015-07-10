package tconstruct.tools;

import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

import tconstruct.library.materials.Material;
import tconstruct.library.tinkering.Category;
import tconstruct.library.tinkering.PartMaterialType;
import tconstruct.library.tools.ToolCore;
import tconstruct.library.utils.ToolBuilder;

public class Pickaxe extends ToolCore {

  // Pick-head, binding, tool-rod
  public Pickaxe() {
    super(new PartMaterialType.ToolPartType(TinkerTools.pickHead),
          new PartMaterialType.ToolPartType(TinkerTools.toolRod),
          new PartMaterialType.ToolPartType(TinkerTools.binding));

    addCategory(Category.HARVEST);

    // set the toolclass, actual harvestlevel is done by the overridden callback
    this.setHarvestLevel("pickaxe", 0);
  }

  @Override
  public NBTTagCompound buildTag(List<Material> materials) {
    if(materials.size() < requiredComponents.length) {
      return new NBTTagCompound();
    }

    return ToolBuilder.buildSimpleTool(materials.get(0), materials.get(1), materials.get(2));
  }
}

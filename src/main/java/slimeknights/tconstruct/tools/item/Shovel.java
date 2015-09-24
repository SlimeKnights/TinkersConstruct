package slimeknights.tconstruct.tools.item;

import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.utils.ToolBuilder;
import slimeknights.tconstruct.tools.TinkerTools;

public class Shovel extends ToolCore {

  public Shovel() {
    super(new PartMaterialType.ToolPartType(TinkerTools.toolRod),
          new PartMaterialType.ToolPartType(TinkerTools.shovelHead));

    addCategory(Category.HARVEST);

    setHarvestLevel("shovel", 0);
  }


  @Override
  public NBTTagCompound buildTag(List<Material> materials) {
    if(materials.size() < requiredComponents.length) {
      return new NBTTagCompound();
    }
    return ToolBuilder.buildSimpleTool(materials.get(0), materials.get(1));
  }

  @Override
  public float damagePotential() {
    return 0.2f;
  }
}

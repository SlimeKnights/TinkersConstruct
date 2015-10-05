package slimeknights.tconstruct.tools.item;

import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.ToolMaterialStats;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.AoeToolCore;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.tools.TinkerTools;

public class Shovel extends AoeToolCore {

  public Shovel() {
    super(new PartMaterialType.ToolPartType(TinkerTools.toolRod),
          new PartMaterialType.ToolPartType(TinkerTools.shovelHead),
          new PartMaterialType.ToolPartType(TinkerTools.binding));

    addCategory(Category.HARVEST);

    setHarvestLevel("shovel", 0);
  }

  @Override
  public float damagePotential() {
    return 0.2f;
  }

  @Override
  public NBTTagCompound buildTag(List<Material> materials) {
    ToolMaterialStats handle = materials.get(0).getStats(ToolMaterialStats.TYPE);
    ToolMaterialStats head = materials.get(1).getStats(ToolMaterialStats.TYPE);
    ToolMaterialStats binding = materials.get(2).getStats(ToolMaterialStats.TYPE);

    ToolNBT data = new ToolNBT(head);

    // durability is mostly head
    data.durability *= 0.8f;
    data.durability += (0.01f + 0.15f*handle.handleQuality) * handle.durability;
    // flat durability from other parts
    data.durability += 0.05f * binding.durability;

    // binding adds a bit of speed
    data.speed *= 0.9f;
    data.speed += (binding.miningspeed * binding.extraQuality)*0.1f;

    // 3 free modifiers
    data.modifiers = DEFAULT_MODIFIERS;

    return data.get();
  }
}

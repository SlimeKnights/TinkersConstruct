package slimeknights.tconstruct.tools.item;

import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.ToolMaterialStats;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.tools.TinkerTools;

public class BroadSword extends ToolCore {

  public BroadSword() {
    super(new PartMaterialType.ToolPartType(TinkerTools.swordBlade),
          new PartMaterialType.ToolPartType(TinkerTools.toolRod),
          new PartMaterialType.ToolPartType(TinkerTools.wideGuard));

    addCategory(Category.WEAPON);
  }

  @Override
  public float damagePotential() {
    return 1.0f;
  }

  @Override
  public NBTTagCompound buildTag(List<Material> materials) {
    if(materials.size() != requiredComponents.length) {
      return new NBTTagCompound();
    }

    ToolMaterialStats head = materials.get(0).getStats(ToolMaterialStats.TYPE);
    ToolMaterialStats handle = materials.get(1).getStats(ToolMaterialStats.TYPE);
    ToolMaterialStats guard = materials.get(2).getStats(ToolMaterialStats.TYPE);

    ToolNBT data = new ToolNBT(head);

    // sword has 2 base damage!
    data.attack += 2f;

    // attack damage: blade, modified 10% by handle and 20% by guard
    data.attack *= 0.7f + 0.1f*handle.handleQuality + 0.2f*guard.extraQuality;

    // durability: guard adds a bit to it, handle has minimal impact
    data.durability += 0.1f * guard.durability * guard.extraQuality;
    data.durability *= 0.95f + 0.05f*handle.handleQuality;

    // 3 free modifiers
    data.modifiers = 3;

    return data.get();
  }
}

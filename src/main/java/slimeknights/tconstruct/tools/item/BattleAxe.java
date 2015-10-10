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

// Ability: Berserk. Can be activated on demand, gives a speedboost, jump boost, mining boost, damage boost. Also makes you take more damage
// Screen turns red/with a red border (steal from thaumcraft) and you can't switch item while berserk is active
public class BattleAxe extends ToolCore {

  public BattleAxe() {
    super(new PartMaterialType.ToolPartType(TinkerTools.toolRod),
          new PartMaterialType.ToolPartType(TinkerTools.toolRod),
          new PartMaterialType.ToolPartType(TinkerTools.toolRod),
          new PartMaterialType.ToolPartType(TinkerTools.toolRod)); // todo: handle, head1, head2, binding

    addCategory(Category.WEAPON);

    setHarvestLevel("axe", 0);
  }

  @Override
  public float damagePotential() {
    return 2.0f;
  }

  @Override
  public float damageCutoff() {
    return 30f;
  }

  @Override
  public NBTTagCompound buildTag(List<Material> materials) {
    ToolMaterialStats handle = materials.get(0).getStats(ToolMaterialStats.TYPE);
    ToolMaterialStats head1 = materials.get(1).getStats(ToolMaterialStats.TYPE);
    ToolMaterialStats head2 = materials.get(2).getStats(ToolMaterialStats.TYPE);
    ToolMaterialStats binding = materials.get(3).getStats(ToolMaterialStats.TYPE);

    ToolNBT data = new ToolNBT();

    data.harvestLevel = Math.max(head1.harvestLevel, head2.harvestLevel);

    data.durability = head1.durability/2 + head2.durability/2;
    data.durability += head1.durability * (0.2f * head2.extraQuality + 0.2f * binding.extraQuality + 0.1f * handle.handleQuality);
    data.durability += head2.durability * (0.2f * head1.extraQuality + 0.2f * binding.extraQuality + 0.1f * handle.handleQuality);
    data.durability += binding.durability * binding.extraQuality * 0.5f;
    data.durability += handle.durability * 0.1f;

    data.attack = (head1.attack + head2.attack)*2f/3f;
    data.attack += (0.2f + 0.7f * handle.handleQuality * binding.extraQuality) * (head1.attack + head2.attack) / 3f;

    data.speed = head1.miningspeed/2f + head2.miningspeed/2f;
    data.speed *= 0.3f + 0.3f * handle.handleQuality * binding.extraQuality;

    data.modifiers = DEFAULT_MODIFIERS;

    return data.get();
  }
}

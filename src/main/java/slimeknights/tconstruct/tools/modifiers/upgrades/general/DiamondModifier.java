package slimeknights.tconstruct.tools.modifiers.upgrades.general;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import slimeknights.tconstruct.library.modifiers.impl.SingleLevelModifier;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import static slimeknights.tconstruct.common.TinkerTags.Items.ARMOR;
import static slimeknights.tconstruct.common.TinkerTags.Items.DURABILITY;
import static slimeknights.tconstruct.common.TinkerTags.Items.HARVEST;
import static slimeknights.tconstruct.common.TinkerTags.Items.MELEE_OR_UNARMED;

public class DiamondModifier extends SingleLevelModifier {
  @Override
  public void addVolatileData(ToolRebuildContext context, int level, ModDataNBT volatileData) {
    IModifiable.setRarity(volatileData, Rarity.UNCOMMON);
  }

  @Override
  public void addToolStats(ToolRebuildContext context, int level, ModifierStatsBuilder builder) {
    Item item = context.getItem();
    int durabilityBonus = 500;
    if (ARMOR.contains(item)) {
      durabilityBonus = 250;
      ToolStats.ARMOR.add(builder, level);
    }
    if (DURABILITY.contains(item)) {
      ToolStats.DURABILITY.add(builder, level * durabilityBonus);
    }
    if (MELEE_OR_UNARMED.contains(item)) {
      ToolStats.ATTACK_DAMAGE.add(builder, level * 1f);
    }
    if (HARVEST.contains(item)) {
      ToolStats.MINING_SPEED.add(builder, level * 1f);
      ToolStats.HARVEST_TIER.update(builder, Tiers.DIAMOND);
    }
  }
}

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
import static slimeknights.tconstruct.common.TinkerTags.Items.MELEE;

public class EmeraldModifier extends SingleLevelModifier {
  @Override
  public void addVolatileData(ToolRebuildContext context, int level, ModDataNBT volatileData) {
    IModifiable.setRarity(volatileData, Rarity.UNCOMMON);
  }

  @Override
  public void addToolStats(ToolRebuildContext context, int level, ModifierStatsBuilder builder) {
    Item item = context.getItem();
    if (DURABILITY.contains(item)) {
      ToolStats.DURABILITY.multiply(builder, 1 + (level * 0.5f));
    }
    if (MELEE.contains(item)) {
      builder.multiplier(ToolStats.ATTACK_DAMAGE, 1 + (level * 0.25f));
    }
    if (HARVEST.contains(item)) {
      ToolStats.HARVEST_TIER.update(builder, Tiers.IRON);
      builder.multiplier(ToolStats.MINING_SPEED, 1 + (level * 0.25f));
    }
    if (ARMOR.contains(item)) {
      ToolStats.KNOCKBACK_RESISTANCE.add(builder, level * 0.05f);
    }
  }
}

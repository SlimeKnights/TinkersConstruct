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
    if (context.hasTag(DURABILITY)) {
      ToolStats.DURABILITY.multiply(builder, 1 + (level * 0.5f));
    }
    if (context.hasTag(MELEE)) {
      builder.multiplier(ToolStats.ATTACK_DAMAGE, 1 + (level * 0.25f));
    }
    if (context.hasTag(HARVEST)) {
      ToolStats.HARVEST_TIER.update(builder, Tiers.IRON);
      builder.multiplier(ToolStats.MINING_SPEED, 1 + (level * 0.25f));
    }
    if (context.hasTag(ARMOR)) {
      ToolStats.KNOCKBACK_RESISTANCE.add(builder, level * 0.05f);
    }
  }
}

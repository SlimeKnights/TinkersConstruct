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

public class NetheriteModifier extends SingleLevelModifier {
  @Override
  public void addVolatileData(ToolRebuildContext context, int level, ModDataNBT volatileData) {
    volatileData.putBoolean(IModifiable.INDESTRUCTIBLE_ENTITY, true);
    IModifiable.setRarity(volatileData, Rarity.RARE);
  }

  @Override
  public void addToolStats(ToolRebuildContext context, int level, ModifierStatsBuilder builder) {
    Item item = context.getItem();
    if (DURABILITY.contains(item)) {
      ToolStats.DURABILITY.multiply(builder, 1 + (level * 0.20f));
    }
    if (MELEE_OR_UNARMED.contains(item)) {
      ToolStats.ATTACK_DAMAGE.multiply(builder, 1 + (level * 0.10f));
    }
    if (HARVEST.contains(item)) {
      ToolStats.MINING_SPEED.multiply(builder, 1 + (level * 0.10f));
      ToolStats.HARVEST_TIER.update(builder, Tiers.NETHERITE);
    }
    if (ARMOR.contains(item)) {
      ToolStats.ARMOR_TOUGHNESS.add(builder, level);
      ToolStats.KNOCKBACK_RESISTANCE.add(builder, level * 0.05f);
    }
  }
}

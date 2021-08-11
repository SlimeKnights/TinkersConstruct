package slimeknights.tconstruct.tools.modifiers.upgrades.general;

import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import slimeknights.tconstruct.library.modifiers.SingleLevelModifier;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.HarvestLevels;

import static slimeknights.tconstruct.common.TinkerTags.Items.ARMOR;
import static slimeknights.tconstruct.common.TinkerTags.Items.DURABILITY;
import static slimeknights.tconstruct.common.TinkerTags.Items.HARVEST;
import static slimeknights.tconstruct.common.TinkerTags.Items.MELEE;

public class NetheriteModifier extends SingleLevelModifier {
  public NetheriteModifier() {
    super(0x8E7C7F);
  }

  @Override
  public void addVolatileData(Item item, ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT volatileData) {
    volatileData.putBoolean(IModifiable.INDESTRUCTIBLE_ENTITY, true);
    IModifiable.setRarity(volatileData, Rarity.RARE);
  }

  @Override
  public void addToolStats(Item item, ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, IModDataReadOnly volatileData, int level, ModifierStatsBuilder builder) {
    if (item.isIn(DURABILITY)) {
      ToolStats.DURABILITY.multiply(builder, 1 + (level * 0.20f));
    }
    if (item.isIn(MELEE)) {
      ToolStats.ATTACK_DAMAGE.multiply(builder, 1 + (level * 0.10f));
    }
    if (item.isIn(HARVEST)) {
      ToolStats.MINING_SPEED.multiply(builder, 1 + (level * 0.10f));
      ToolStats.HARVEST_LEVEL.set(builder, HarvestLevels.NETHERITE);
    }
    if (item.isIn(ARMOR)) {
      ToolStats.ARMOR_TOUGHNESS.add(builder, level);
      ToolStats.KNOCKBACK_RESISTANCE.add(builder, level * 0.1f);
    }
  }
}

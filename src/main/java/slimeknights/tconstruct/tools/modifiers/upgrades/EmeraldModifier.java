package slimeknights.tconstruct.tools.modifiers.upgrades;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Rarity;
import slimeknights.tconstruct.library.modifiers.SingleLevelModifier;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolAttackContext;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.HarvestLevels;

public class EmeraldModifier extends SingleLevelModifier {
  public EmeraldModifier() {
    super(0x41f384);
  }

  @Override
  public void addVolatileData(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT volatileData) {
    ToolCore.setRarity(volatileData, Rarity.UNCOMMON);
  }

  @Override
  public void addToolStats(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, IModDataReadOnly volatileData, int level, ModifierStatsBuilder builder) {
    ToolStats.DURABILITY.multiply(builder, 1.5f);
    ToolStats.HARVEST_LEVEL.set(builder, HarvestLevels.IRON);
  }

  @Override
  public float getEntityDamage(IModifierToolStack tool, int level, ToolAttackContext context, float baseDamage, float damage) {
    LivingEntity living = context.getLivingTarget();
    if (living != null && living.getCreatureAttribute() == CreatureAttribute.ILLAGER) {
      damage += 2.5f;
    }
    return damage;
  }
}

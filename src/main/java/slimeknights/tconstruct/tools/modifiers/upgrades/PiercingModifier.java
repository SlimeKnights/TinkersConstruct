package slimeknights.tconstruct.tools.modifiers.upgrades;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.List;

public class PiercingModifier extends IncrementalModifier {
  private static final ResourceLocation PIERCING_DEBUFF = TConstruct.getResource("piercing_debuff");
  public PiercingModifier() {
    super(0x9FA76D);
  }

  @Override
  public void addVolatileData(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT volatileData) {
    float toRemove = 0.5f * getScaledLevel(persistentData, level);
    float baseDamage = baseStats.getFloat(ToolStats.ATTACK_DAMAGE);
    if (baseDamage < toRemove) {
      volatileData.putFloat(PIERCING_DEBUFF, toRemove - baseDamage);
    }
  }

  @Override
  public void addToolStats(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, IModDataReadOnly volatileData, int level, ModifierStatsBuilder builder) {
    float toRemove = 0.5f * getScaledLevel(persistentData, level) - volatileData.getFloat(PIERCING_DEBUFF);
    ToolStats.ATTACK_DAMAGE.add(builder, -toRemove);
  }

  @Override
  public int afterEntityHit(IModifierToolStack tool, int level, ToolAttackContext context, float damageDealt) {
    // deals 0.5 pierce damage per level, scaled, half of sharpness
    DamageSource source;
    PlayerEntity player = context.getPlayerAttacker();
    if (player != null) {
      source = DamageSource.causePlayerDamage(player);
    } else {
      source = DamageSource.causeMobDamage(context.getAttacker());
    }
    source.setDamageBypassesArmor();
    float secondaryDamage = (getScaledLevel(tool, level) * tool.getModifier(ToolStats.ATTACK_DAMAGE) - tool.getVolatileData().getFloat(PIERCING_DEBUFF)) * context.getCooldown();
    if (context.isCritical()) {
      secondaryDamage *= 1.5f;
    }
    ToolAttackUtil.attackEntitySecondary(source, secondaryDamage, context.getTarget(), context.getLivingTarget(), true);
    return 0;
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, boolean isAdvanced, boolean detailed) {
    addDamageTooltip(tool, getScaledLevel(tool, level) * 1.0f - tool.getVolatileData().getFloat(PIERCING_DEBUFF), tooltip);
  }
}

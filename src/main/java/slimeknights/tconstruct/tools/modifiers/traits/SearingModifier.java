package slimeknights.tconstruct.tools.modifiers.traits;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class SearingModifier extends Modifier {
  private static final float BASELINE_TEMPERATURE = 0.75f;

  public SearingModifier() {
    super(0x4F4A47);
  }

  @Override
  public void addVolatileData(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT volatileData) {
    TinkerModifiers.tank.get().addCapacity(volatileData, MaterialValues.INGOT * 2);
  }

  /** Applies the temperature boost */
  private static float temperatureBoost(float temperature, int level) {
    // produces 0 at 0.75t. Caps at level * 2.5 at 2.0t, or at level * -2.5 at -0.5t
    return (temperature - BASELINE_TEMPERATURE) * (level * 2);
  }

  @Override
  public float applyLivingDamage(IModifierToolStack tool, int level, LivingEntity attacker, LivingEntity target, float baseDamage, float damage, boolean isCritical, boolean fullyCharged) {
    BlockPos attackerPos = attacker.getPosition();
    // only decrease damage, increase is handled in afterLivingHit
    float temperature = attacker.world.getBiome(attackerPos).getTemperature(attackerPos);
    if (temperature < BASELINE_TEMPERATURE) {
      damage += temperatureBoost(temperature, level);
    }
    return damage;
  }

  @Override
  public int afterLivingHit(IModifierToolStack tool, int level, LivingEntity attacker, LivingEntity target, float damageDealt, boolean isCritical, float cooldown) {
    BlockPos attackerPos = attacker.getPosition();
    // only increase damage, decrease in applyLivingDamage
    float temperature = attacker.world.getBiome(attackerPos).getTemperature(attackerPos);
    if (temperature > BASELINE_TEMPERATURE) {
      DamageSource source;
      if (attacker instanceof PlayerEntity) {
        source = DamageSource.causePlayerDamage((PlayerEntity)attacker);
      } else {
        source = DamageSource.causeMobDamage(attacker);
      }
      target.hurtResistantTime = 0;
      attackEntitySecondary(source.setFireDamage(), temperatureBoost(temperature, level) * cooldown, target, false);
    }
    return 0;
  }
}

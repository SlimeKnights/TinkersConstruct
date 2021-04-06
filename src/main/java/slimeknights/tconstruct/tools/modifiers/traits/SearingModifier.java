package slimeknights.tconstruct.tools.modifiers.traits;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class SearingModifier extends Modifier {
  private static final float BASELINE_TEMPERATURE = 0.75f;

  public SearingModifier() {
    super(0x3f3f3f);
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
  public int afterLivingHit(IModifierToolStack tool, int level, LivingEntity attacker, LivingEntity target, float damageDealt, boolean isCritical, boolean fullyCharged) {
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
      attackEntitySecondary(source.setFireDamage(), temperatureBoost(temperature, level), target, false);
    }
    return 0;
  }
}

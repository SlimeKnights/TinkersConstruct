package slimeknights.tconstruct.tools.modifiers.traits;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class SearingModifier extends Modifier {
  public SearingModifier() {
    super(0x3f3f3f);
  }

  @Override
  public float applyLivingDamage(IModifierToolStack tool, int level, LivingEntity attacker, LivingEntity target, float baseDamage, float damage, boolean isCritical, boolean fullyCharged) {
    BlockPos attackerPos = attacker.getPosition();
    // simple slope, 0.5 is neutral, +- 1 damage per 0.5
    float temperature = attacker.world.getBiome(attackerPos).getTemperature(attackerPos);
    if (temperature < 0.5) {
      damage += (temperature - 0.5f) * (level * 2);
    }
    return damage;
  }

  @Override
  public int afterLivingHit(IModifierToolStack tool, int level, LivingEntity attacker, LivingEntity target, float damageDealt, boolean isCritical, boolean fullyCharged) {
    BlockPos attackerPos = attacker.getPosition();
    // simple slope, 0.5 is neutral, +- 1 damage per 0.5
    float temperature = attacker.world.getBiome(attackerPos).getTemperature(attackerPos);
    if (temperature > 0.5) {
      DamageSource source;
      if (attacker instanceof PlayerEntity) {
        source = DamageSource.causePlayerDamage((PlayerEntity)attacker);
      } else {
        source = DamageSource.causeMobDamage(attacker);
      }
      target.hurtResistantTime = 0;
      target.attackEntityFrom(source.setFireDamage(), (temperature - 0.5f) * (level * 2));
    }
    return 0;
  }
}

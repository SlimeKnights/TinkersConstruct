package slimeknights.tconstruct.tools.modifiers.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.world.server.ServerWorld;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.tools.modifiers.traits.melee.LaceratingModifier;

/**
 * Potion effect from {@link LaceratingModifier}
 */
public class BleedingEffect extends NoMilkEffect {
  private static final String SOURCE_KEY = TConstruct.prefix("bleed");
  public BleedingEffect() {
    super(EffectType.HARMFUL, 0xa80000, true);
  }

  @Override
  public boolean isDurationEffectTick(int tick, int level) {
    // every half second
    return tick > 0 && tick % 20 == 0;
  }

  @Override
  public void applyEffectTick(LivingEntity target, int level) {
    // attribute to player kill
    LivingEntity lastAttacker = target.getLastHurtMob();
    DamageSource source;
    if(lastAttacker != null) {
      source = new EntityDamageSource(SOURCE_KEY, lastAttacker);
    }
    else {
      source = new DamageSource(SOURCE_KEY);
    }

    // perform damage
    int hurtResistantTime = target.invulnerableTime;
    ToolAttackUtil.attackEntitySecondary(source, (level + 1f) / 2f, target, target, true);
    target.invulnerableTime = hurtResistantTime;

    // damage particles
    if (target.level instanceof ServerWorld) {
      ((ServerWorld)target.level).sendParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getY(0.5), target.getZ(), 1, 0.1, 0, 0.1, 0.2);
    }
  }
}

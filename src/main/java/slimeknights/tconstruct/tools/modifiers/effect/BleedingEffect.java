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
  public boolean isReady(int tick, int level) {
    // every half second
    return tick > 0 && tick % 20 == 0;
  }

  @Override
  public void performEffect(LivingEntity target, int level) {
    // attribute to player kill
    LivingEntity lastAttacker = target.getLastAttackedEntity();
    DamageSource source;
    if(lastAttacker != null) {
      source = new EntityDamageSource(SOURCE_KEY, lastAttacker);
    }
    else {
      source = new DamageSource(SOURCE_KEY);
    }

    // perform damage
    int hurtResistantTime = target.hurtResistantTime;
    ToolAttackUtil.attackEntitySecondary(source, (level + 1f) / 2f, target, target, true);
    target.hurtResistantTime = hurtResistantTime;

    // damage particles
    if (target.world instanceof ServerWorld) {
      ((ServerWorld)target.world).spawnParticle(ParticleTypes.DAMAGE_INDICATOR, target.getPosX(), target.getPosYHeight(0.5), target.getPosZ(), 1, 0.1, 0, 0.1, 0.2);
    }
  }
}

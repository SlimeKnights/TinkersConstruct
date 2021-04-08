package slimeknights.tconstruct.library.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectType;

public class TinkerEffect extends StatusEffect {

  private final boolean show;

  public TinkerEffect(StatusEffectType typeIn, boolean show) {
    this(typeIn, 0xffffff, show);
  }

  public TinkerEffect(StatusEffectType typeIn, int color, boolean show) {
    super(typeIn, color);
    this.show = show;
  }

  /* Visibility */

  @Override
  public boolean shouldRender(StatusEffectInstance effect) {
    return this.show;
  }

  @Override
  public boolean shouldRenderInvText(StatusEffectInstance effect) {
    return this.show;
  }

  @Override
  public boolean shouldRenderHUD(StatusEffectInstance effect) {
    return this.show;
  }


  /* Helpers */

  /**
   * Applies this potion to an entity
   * @param entity    Entity
   * @param duration  Duration
   * @return  Applied instance
   */
  public StatusEffectInstance apply(LivingEntity entity, int duration) {
    return this.apply(entity, duration, 0);
  }

  /**
   * Applies this potion to an entity
   * @param entity    Entity
   * @param duration  Duration
   * @param level     Effect level
   * @return  Applied instance
   */
  public StatusEffectInstance apply(LivingEntity entity, int duration, int level) {
    StatusEffectInstance effect = new StatusEffectInstance(this, duration, level, false, false);
    entity.addStatusEffect(effect);
    return effect;
  }

  /**
   * Gets the level of the effect on the entity, or -1 if not active
   * @param entity  Entity to check
   * @return  Level, or -1 if inactive
   */
  public int getLevel(LivingEntity entity) {
    StatusEffectInstance effect = entity.getStatusEffect(this);
    if (effect != null) {
      return effect.getAmplifier();
    }
    return -1;
  }

}

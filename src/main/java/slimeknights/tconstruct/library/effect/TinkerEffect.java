package slimeknights.tconstruct.library.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;

public class TinkerEffect extends Effect {

  private final boolean show;

  public TinkerEffect(EffectType typeIn, boolean show) {
    this(typeIn, 0xffffff, show);
  }

  public TinkerEffect(EffectType typeIn, int color, boolean show) {
    super(typeIn, color);
    this.show = show;
  }

  /* Visibility */

  @Override
  public boolean shouldRender(EffectInstance effect) {
    return this.show;
  }

  @Override
  public boolean shouldRenderInvText(EffectInstance effect) {
    return this.show;
  }

  @Override
  public boolean shouldRenderHUD(EffectInstance effect) {
    return this.show;
  }


  /* Helpers */

  /**
   * Applies this potion to an entity
   * @param entity    Entity
   * @param duration  Duration
   * @return  Applied instance
   */
  public EffectInstance apply(LivingEntity entity, int duration) {
    return this.apply(entity, duration, 0);
  }

  /**
   * Applies this potion to an entity
   * @param entity    Entity
   * @param duration  Duration
   * @param level     Effect level
   * @return  Applied instance
   */
  public EffectInstance apply(LivingEntity entity, int duration, int level) {
    EffectInstance effect = new EffectInstance(this, duration, level, false, false);
    entity.addPotionEffect(effect);
    return effect;
  }

  /**
   * Gets the level of the effect on the entity, or -1 if not active
   * @param entity  Entity to check
   * @return  Level, or -1 if inactive
   */
  public int getLevel(LivingEntity entity) {
    EffectInstance effect = entity.getActivePotionEffect(this);
    if (effect != null) {
      return effect.getAmplifier();
    }
    return -1;
  }

}

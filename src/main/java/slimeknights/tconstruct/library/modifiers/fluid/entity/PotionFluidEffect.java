package slimeknights.tconstruct.library.modifiers.fluid.entity;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.fluid.EffectLevel;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext.Entity;
import slimeknights.tconstruct.library.recipe.TagPredicate;

import java.util.List;

/** Spilling effect that pulls the potion from a NBT potion fluid and applies it */
public record PotionFluidEffect(float scale, TagPredicate predicate) implements FluidEffect<FluidEffectContext.Entity> {
  public static final RecordLoadable<PotionFluidEffect> LOADER = RecordLoadable.create(
    FloatLoadable.FROM_ZERO.requiredField("scale", e -> e.scale),
    TagPredicate.LOADABLE.defaultField("nbt", TagPredicate.ANY, e -> e.predicate),
    PotionFluidEffect::new);

  @Override
  public RecordLoadable<PotionFluidEffect> getLoader() {
    return LOADER;
  }

  @Override
  public float apply(FluidStack fluid, EffectLevel level, Entity context, FluidAction action) {
    LivingEntity target = context.getLivingTarget();
    LivingEntity attacker = context.getEntity();
    // must match the tag predicate
    if (target != null && predicate.test(fluid.getTag())) {
      List<MobEffectInstance> effects = PotionUtils.getPotion(fluid.getTag()).getEffects();
      if (!effects.isEmpty()) {
        // prevent effects like instant damage from hitting hurt resistance
        int oldInvulnerableTime = target.invulnerableTime;
        // report whichever effect used the most
        float used = 0;
        for (MobEffectInstance instance : effects) {
          MobEffect effect = instance.getEffect();
          if (effect.isInstantenous()) {
            // instant effects just apply full value always
            used = level.value();
            if (action.execute()) {
              target.invulnerableTime = 0;
              effect.applyInstantenousEffect(attacker, attacker, target, instance.getAmplifier(), used * scale);
            }
          } else {
            // if the potion already exists, we scale up the existing time
            MobEffectInstance existingEffect = target.getEffect(effect);
            int duration;
            if (existingEffect != null && existingEffect.getAmplifier() >= instance.getAmplifier()) {
              // if the existing level is larger, just skip, would be a cheese to increase said level
              // lower levels we treat as not having the effect, must be exact match to extend
              if (existingEffect.getAmplifier() > instance.getAmplifier()) {
                continue;
              }
              float existingLevel = existingEffect.getDuration() / scale / instance.getDuration();
              float effective = level.effective(existingLevel);
              // no potion to add? just save effort and stop here
              if (effective <= existingLevel) {
                continue;
              }
              duration = (int) (instance.getDuration() * scale * effective);
              // update how much we used, which is likely less than our max possible
              used = Math.max(used, effective - existingLevel);
            } else {
              // no relevant effect? just compute duration directly
              used = level.value();
              duration = (int) (instance.getDuration() * scale * used);
            }
            if (action.execute()) {
              target.addEffect(new MobEffectInstance(effect, duration, instance.getAmplifier(), instance.isAmbient(), instance.isVisible(), instance.showIcon()));
            }
          }
        }
        target.invulnerableTime = oldInvulnerableTime;
        return used;
      }
    }
    return 0;
  }
}

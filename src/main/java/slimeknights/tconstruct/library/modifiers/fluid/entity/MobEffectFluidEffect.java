package slimeknights.tconstruct.library.modifiers.fluid.entity;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.fluid.EffectLevel;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext.Entity;
import slimeknights.tconstruct.library.modifiers.fluid.FluidMobEffect;
import slimeknights.tconstruct.library.modifiers.fluid.TimeAction;

/**
 * Spilling effect to apply a potion effect
 * @param effect  Effect to apply
 * @param action  How the time scales
 * @see FluidMobEffect.Builder
 */
public record MobEffectFluidEffect(FluidMobEffect effect, TimeAction action) implements FluidEffect<FluidEffectContext.Entity> {
  public static final RecordLoadable<MobEffectFluidEffect> LOADER = RecordLoadable.create(
    FluidMobEffect.LOADABLE.directField(e -> e.effect),
    TimeAction.LOADABLE.requiredField("action", e -> e.action),
    MobEffectFluidEffect::new);

  @Override
  public RecordLoadable<MobEffectFluidEffect> getLoader() {
    return LOADER;
  }

  @Override
  public float apply(FluidStack fluid, EffectLevel scale, Entity context, FluidAction action) {
    // first, need a target
    LivingEntity target = context.getLivingTarget();
    if (target != null) {
      float used;
      int time;
      // add and set both have distinct behavior under an existing effect, same otherwise
      MobEffectInstance existingInstance = target.getEffect(effect.effect());
      if (existingInstance != null && existingInstance.getAmplifier() >= effect.amplifier()) {
        // if the existing level is larger, just skip, would be a cheese to increase said level
        // lower levels we treat as not having the effect, must be exact match to extend
        if (existingInstance.getAmplifier() > effect.amplifier()) {
          return 0;
        }
        if (this.action == TimeAction.ADD) {
          int extraTime = (int)(effect.time() * scale.value());
          if (extraTime <= 0) {
            return 0;
          }
          // add simply sums existing time with the desired time. If the level is higher than ours, it produces a hidden effect
          time = existingInstance.getDuration() + extraTime;
          used = scale.value();
        } else {
          // set can produce time up to the maximum allowed by scale,
          float existing = existingInstance.getDuration() / (float)effect.time();
          float effective = scale.effective(existing);
          // no change? means we skip applying entirely
          if (effective < existing) {
            return 0;
          }
          used = effective - existing;
          time = (int)(effect.time() * effective);
        }
      } else {
        // if no existing, time and set behave the same way, use maximum amount to compute time
        time = (int)(effect.time() * scale.value());
        used = scale.value();
      }
      // if we got time, add the effect
      if (time > 0) {
        MobEffectInstance newInstance = effect.effectWithTime(time);
        // if simulating, just ask if it's applicable. Won't handle event canceling, but thats acceptable.
        if (action.simulate()) {
          return target.canBeAffected(newInstance) ? used : 0;
        }
        return target.addEffect(newInstance) ? used : 0;
      }
    }
    return 0;
  }
}

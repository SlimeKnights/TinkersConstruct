package slimeknights.tconstruct.library.modifiers.fluid.entity;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.fluid.EffectLevel;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;
import slimeknights.tconstruct.library.modifiers.fluid.TimeAction;

import java.util.Locale;

/**
 * Effect to set an entity on fire
 * @param action  Determines whether to set or add time
 * @param time    Time in seconds
 */
public record FireFluidEffect(TimeAction action, int time) implements FluidEffect<FluidEffectContext.Entity> {
  public static final RecordLoadable<FireFluidEffect> LOADER = RecordLoadable.create(
    TimeAction.LOADABLE.requiredField("action", e -> e.action),
    IntLoadable.FROM_ONE.requiredField("time", e -> e.time),
    FireFluidEffect::new);

  @Override
  public RecordLoadable<FireFluidEffect> getLoader() {
    return LOADER;
  }

  @Override
  public float apply(FluidStack fluid, EffectLevel level, FluidEffectContext.Entity context, FluidAction action) {
    // if fire immune or bad parameters, fail
    Entity target = context.getTarget();
    if (target.fireImmune()) {
      return 0;
    }
    if (this.action == TimeAction.ADD) {
      float value = level.value();
      if (action.execute()) {
        // current time is in ticks, so need to divide to get seconds, do a rounded divide
        target.setSecondsOnFire(Math.round(time * value) + (target.getRemainingFireTicks() + 10) / 20);
      }
      return value;
    } else {
      // we are allowed to increase fire up to time*level.max, however we may get less if level.value is low
      float existing = target.getRemainingFireTicks() / 20f / time;
      float effective = level.effective(existing);
      if (action.execute()) {
        target.setSecondsOnFire(Math.round(time * effective));
      }
      // only consume what we changed
      return effective - existing;
    }
  }

  @Override
  public Component getDescription(RegistryAccess registryAccess) {
    String prefix = FluidEffect.getTranslationKey(getLoader()) + "." + action.name().toLowerCase(Locale.ROOT);
    return Component.translatable(prefix, time);
  }
}

package slimeknights.tconstruct.library.modifiers.fluid.entity;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.fluid.EffectLevel;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;
import slimeknights.tconstruct.library.modifiers.fluid.TimeAction;

/**
 * Effect to set an entity freezing
 * @param action  Determines whether to set or add time
 * @param time    Time in seconds
 */
public record FreezeFluidEffect(TimeAction action, int time) implements FluidEffect<FluidEffectContext.Entity> {
  public static final RecordLoadable<FreezeFluidEffect> LOADER = RecordLoadable.create(
    TimeAction.LOADABLE.requiredField("action", e -> e.action),
    IntLoadable.FROM_ONE.requiredField("time", e -> e.time),
    FreezeFluidEffect::new);

  @Override
  public RecordLoadable<FreezeFluidEffect> getLoader() {
    return LOADER;
  }

  @Override
  public float apply(FluidStack fluid, EffectLevel level, FluidEffectContext.Entity context, FluidAction action) {
    Entity target = context.getTarget();
    if (!target.canFreeze()) {
      return 0;
    }
    if (this.action == TimeAction.ADD) {
      float value = level.value();
      if (action.execute()) {
        // ensure we have enough time to freeze after applying this
        target.setTicksFrozen(Math.max(target.getTicksRequiredToFreeze(), target.getTicksFrozen()) + Math.round(time * value));
        target.setRemainingFireTicks(0);
      }
      return value;
    } else {
      int freezeTicks = target.getTicksRequiredToFreeze();
      int frozen = target.getTicksFrozen();
      float existing = frozen < freezeTicks ? 0 : (frozen - freezeTicks) / (float) time;
      float effective = level.effective(existing);
      if (action.execute()) {
        target.setTicksFrozen(freezeTicks + Math.round(time * effective));
        target.setRemainingFireTicks(0);
      }
      return effective - existing;
    }
  }
}

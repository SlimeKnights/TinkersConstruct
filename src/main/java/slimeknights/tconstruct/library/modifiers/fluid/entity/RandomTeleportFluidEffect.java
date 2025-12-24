package slimeknights.tconstruct.library.modifiers.fluid.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.events.teleport.FluidEffectTeleportEvent;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.modifiers.fluid.EffectLevel;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext.Entity;
import slimeknights.tconstruct.library.utils.TeleportHelper;

/** Fluid effect that randomly teleports the target. */
public record RandomTeleportFluidEffect(LevelingInt diameter, LevelingInt chances) implements FluidEffect<FluidEffectContext.Entity> {
  private static final LevelingInt DEFAULT = LevelingInt.flat(16);
  // TODO 1.21: remove defaults
  public static final RecordLoadable<RandomTeleportFluidEffect> LOADER = RecordLoadable.create(
    LevelingInt.LOADABLE.defaultField("diameter", DEFAULT, true, RandomTeleportFluidEffect::diameter),
    LevelingInt.LOADABLE.defaultField("chances", DEFAULT, true, RandomTeleportFluidEffect::chances),
    RandomTeleportFluidEffect::new);

  @Override
  public RecordLoadable<RandomTeleportFluidEffect> getLoader() {
    return LOADER;
  }

  @Override
  public float apply(FluidStack fluid, EffectLevel level, Entity context, FluidAction action) {
    LivingEntity target = context.getLivingTarget();
    // must have a full level to apply, unless we have scaling behavior
    boolean isFlat = diameter.eachLevel() == 0 && chances.eachLevel() == 0;
    if (target != null && (level.isFull() || !isFlat)) {
      float value = level.value();
      if (action.execute()) {
        TeleportHelper.randomNearbyTeleport(target, FluidEffectTeleportEvent.TELEPORT_FACTORY, diameter.compute(value), chances.compute(value));
      }
      return isFlat ? 1 : value;
    }
    return 0;
  }
}

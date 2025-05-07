package slimeknights.tconstruct.library.modifiers.fluid.general;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.fluid.EffectLevel;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;
import slimeknights.tconstruct.library.modifiers.fluid.FluidMobEffect;
import slimeknights.tconstruct.library.modifiers.fluid.GroupCost;
import slimeknights.tconstruct.library.modifiers.fluid.TimeAction;

/**
 * Spilling effect to apply a potion effect to all targets within a block position
 * @param effect  Effect to apply
 * @param action  How the time scales
 * @see FluidMobEffect.Builder
 */
public record AreaMobEffectFluidEffect(FluidMobEffect effect, TimeAction action, GroupCost groupCost) implements FluidEffect<FluidEffectContext> {
  public static final RecordLoadable<AreaMobEffectFluidEffect> LOADER = RecordLoadable.create(
    FluidMobEffect.LOADABLE.directField(e -> e.effect),
    TimeAction.LOADABLE.requiredField("action", e -> e.action),
    GroupCost.LOADABLE.requiredField("group_cost", e -> e.groupCost),
    AreaMobEffectFluidEffect::new);

  @Override
  public RecordLoadable<? extends FluidEffect<FluidEffectContext>> getLoader() {
    return LOADER;
  }

  @Override
  public float apply(FluidStack fluid, EffectLevel level, FluidEffectContext context, FluidAction action) {
    float used = 0;
    for(LivingEntity living : context.getLevel().getEntitiesOfClass(LivingEntity.class, new AABB(context.getBlockPos()))) {
      float localUsed = effect.apply(living, level, this.action, action);
      // if summing, reduce the amount remaining for the next target
      if (groupCost == GroupCost.SUM) {
        used += localUsed;
        level = level.subtract(localUsed);
        // if max, keep the largest used amount
      } else if (localUsed > used) {
        used = localUsed;
      }
    }
    return used;
  }

  @Override
  public Component getDescription(RegistryAccess registryAccess) {
    return effect.getDisplayName(action);
  }
}

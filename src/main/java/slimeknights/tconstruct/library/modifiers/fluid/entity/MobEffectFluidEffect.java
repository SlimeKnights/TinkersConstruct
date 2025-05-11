package slimeknights.tconstruct.library.modifiers.fluid.entity;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.fluid.EffectLevel;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;
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
  public float apply(FluidStack fluid, EffectLevel scale, FluidEffectContext.Entity context, FluidAction action) {
    // first, need a target
    LivingEntity target = context.getLivingTarget();
    if (target != null) {
      return effect.apply(target, scale, this.action, action);
    }
    return 0;
  }

  @Override
  public Component getDescription(RegistryAccess registryAccess) {
    return effect.getDisplayName(action);
  }
}

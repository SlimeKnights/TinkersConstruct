package slimeknights.tconstruct.library.modifiers.fluid.entity;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.fluid.EffectLevel;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext.Entity;

/** Spilling effect to remove a specific effect */
public record RemoveEffectFluidEffect(MobEffect effect) implements FluidEffect<FluidEffectContext.Entity> {
  public static final RecordLoadable<RemoveEffectFluidEffect> LOADER = RecordLoadable.create(Loadables.MOB_EFFECT.requiredField("effect", e -> e.effect), RemoveEffectFluidEffect::new);

  @Override
  public RecordLoadable<RemoveEffectFluidEffect> getLoader() {
    return LOADER;
  }

  @Override
  public float apply(FluidStack fluid, EffectLevel level, Entity context, FluidAction action) {
    LivingEntity living = context.getLivingTarget();
    if (living != null && level.isFull()) {
      if (action.simulate()) {
        return living.hasEffect(effect) ? 1 : 0;
      }
      return living.removeEffect(effect) ? 1 : 0;
    }
    return 0;
  }

  @Override
  public Component getDescription(RegistryAccess registryAccess) {
    return FluidEffect.makeTranslation(getLoader(), effect.getDisplayName());
  }
}

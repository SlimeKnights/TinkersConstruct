package slimeknights.tconstruct.library.modifiers.fluid.entity;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.TranslationHelper;
import slimeknights.tconstruct.library.modifiers.fluid.EffectLevel;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext.Entity;

/**
 * Effect to increase or decrease the target's breath.
 * @param amount  Amount to restore
 */
public record AddBreathFluidEffect(int amount) implements FluidEffect<FluidEffectContext.Entity> {
  public static final RecordLoadable<AddBreathFluidEffect> LOADER = RecordLoadable.create(
    IntLoadable.ANY_SHORT.requiredField("amount", e -> e.amount),
    AddBreathFluidEffect::new);

  @Override
  public RecordLoadable<AddBreathFluidEffect> getLoader() {
    return LOADER;
  }

  @Override
  public float apply(FluidStack fluid, EffectLevel level, Entity context, FluidAction action) {
    LivingEntity target = context.getLivingTarget();
    if (target != null) {
      int max = target.getMaxAirSupply();
      int current = target.getAirSupply();
      if (action.execute()) {
        target.setAirSupply(Mth.clamp(current + Math.round(amount * level.value()), -20, max));
      }
      // based on whether we are increasing or decreasing breath, the max change varies
      // only consume fluid based on the air we got/lost
      int maxChange = amount > 0 ? max - current : current;
      return level.computeUsed(maxChange / (float)Math.abs(amount));
    }
    return 0;
  }

  @Override
  public Component getDescription(RegistryAccess registryAccess) {
    String prefix = FluidEffect.getTranslationKey(getLoader()) + (amount >= 0 ? ".add" : ".subtract");
    return Component.translatable(prefix, TranslationHelper.COMMA_FORMAT.format(Math.abs(amount) / 30f));
  }
}

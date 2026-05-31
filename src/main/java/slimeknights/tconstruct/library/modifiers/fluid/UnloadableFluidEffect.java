package slimeknights.tconstruct.library.modifiers.fluid;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

/** Base class for {@link FluidEffect} to simplify creating effects */
public interface UnloadableFluidEffect<C extends FluidEffectContext> {
  /**
   * Called when this fluid is used on the given context
   * @param fluid   Fluid to use, generally is just for NBT, should not be altered
   * @param level   Strength of the effect to apply, will be 0+
   * @param context Context about the attacker and the entity hit. May be client side if {@code action} is {@link FluidAction#SIMULATE}
   * @param action  If {@link FluidAction#SIMULATE}, make no changes to the context. If {@link FluidAction#EXECUTE}, the context may be modified.
   * @return Amount of scale actually used, should be between 0 and {@code scale}.
   */
  float apply(FluidStack fluid, EffectLevel level, C context, FluidAction action);
}

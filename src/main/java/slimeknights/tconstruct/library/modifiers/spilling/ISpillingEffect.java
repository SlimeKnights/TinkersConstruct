package slimeknights.tconstruct.library.modifiers.spilling;

import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.data.GenericRegisteredSerializer;
import slimeknights.mantle.data.GenericRegisteredSerializer.IJsonSerializable;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;

/**
 * Interface to allow multiple effects for a spilling recipe
 */
public interface ISpillingEffect extends IJsonSerializable {
  /** Registry for spilling effect loaders */
  GenericRegisteredSerializer<ISpillingEffect> LOADER = new GenericRegisteredSerializer<>();

  /**
   * Applies the effect
   * @param fluid    Fluid used to perform the recipe, safe to modify
   * @param scale    Number of times to apply the effect. Round fractional amounts down if needed
   * @param context  Attack context
   */
  void applyEffects(FluidStack fluid, float scale, ToolAttackContext context);
}

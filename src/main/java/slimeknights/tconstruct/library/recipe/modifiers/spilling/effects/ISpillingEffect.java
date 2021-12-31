package slimeknights.tconstruct.library.recipe.modifiers.spilling.effects;

import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.utils.GenericLoaderRegistry;
import slimeknights.tconstruct.library.utils.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.utils.GenericLoaderRegistry.IHaveLoader;

/**
 * Interface to allow multiple effects for a spilling recipe
 */
public interface ISpillingEffect extends IHaveLoader {
  /** Registry for spilling effect loaders */
  GenericLoaderRegistry<ISpillingEffect> LOADER = new GenericLoaderRegistry<>();

  /**
   * Applies the effect
   * @param fluid    Fluid used to perform the recipe, safe to modify
   * @param scale    Number of times to apply the effect. Round fractional amounts down if needed
   * @param context  Attack context
   */
  void applyEffects(FluidStack fluid, float scale, ToolAttackContext context);

  /** Gets the loader for this spilling effect */
  @Override
  IGenericLoader<?> getLoader();
}

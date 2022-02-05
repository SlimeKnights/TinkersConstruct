package slimeknights.tconstruct.library.recipe.modifiers.spilling.effects;

import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.data.GenericLoaderRegistry;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.GenericLoaderRegistry.IHaveLoader;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;

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

package slimeknights.tconstruct.library.recipe.modifiers.spilling.effects;

import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.GenericLoaderRegistry.SingletonLoader;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;

/** Effect which extinguishes fire on an entity */
public class ExtinguishSpillingEffect implements ISpillingEffect {
  public static final ExtinguishSpillingEffect INSTANCE = new ExtinguishSpillingEffect();
  public static final IGenericLoader<ExtinguishSpillingEffect> LOADER = new SingletonLoader<>(INSTANCE);
  private ExtinguishSpillingEffect() {}

  @Override
  public void applyEffects(FluidStack fluid, float scale, ToolAttackContext context) {
    context.getTarget().clearFire();
  }

  @Override
  public IGenericLoader<?> getLoader() {
    return LOADER;
  }
}

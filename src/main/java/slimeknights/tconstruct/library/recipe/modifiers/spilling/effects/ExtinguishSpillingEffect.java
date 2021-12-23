package slimeknights.tconstruct.library.recipe.modifiers.spilling.effects;

import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;

/** Effect which extinguishes fire on an entity */
public class ExtinguishSpillingEffect implements ISpillingEffect {
  public static final ExtinguishSpillingEffect INSTANCE = new ExtinguishSpillingEffect();
  public static final ISpillingEffectLoader.Singleton<ExtinguishSpillingEffect> LOADER = new ISpillingEffectLoader.Singleton<>(INSTANCE);
  private ExtinguishSpillingEffect() {}

  @Override
  public void applyEffects(FluidStack fluid, float scale, ToolAttackContext context) {
    context.getTarget().extinguish();
  }

  @Override
  public ISpillingEffectLoader<?> getLoader() {
    return LOADER;
  }
}

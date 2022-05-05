package slimeknights.tconstruct.library.modifiers.spilling;

import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;

import java.util.Collections;
import java.util.List;

/**
 * Data class to keep track of effects for a fluid
 */
public record SpillingFluid(FluidIngredient ingredient, List<ISpillingEffect> effects) {
  public SpillingFluid(FluidIngredient ingredient) {
    this(ingredient, Collections.emptyList());
  }

  /**
   * Checks if the recipe matches the given fluid. Does not consider fluid amount
   * @param fluid  Fluid to test
   * @return  True if this recipe handles the given fluid
   */
  public boolean matches(Fluid fluid) {
    return ingredient.test(fluid);
  }

  /** Checks if this fluid has any effects */
  public boolean hasEffects() {
    return !effects.isEmpty();
  }

  /**
   * Applies any effects for the given recipe
   * @param fluid    Fluid used to perform the recipe, safe to modify
   * @param level    Modifier level
   * @param context  Modifier attack context
   * @return  Fluid stack after applying this recipe
   */
  public FluidStack applyEffects(FluidStack fluid, int level, ToolAttackContext context) {
    int needed = ingredient.getAmount(fluid.getFluid());
    int maxFluid = level * needed;
    float scale = level;
    if (fluid.getAmount() < maxFluid) {
      scale = fluid.getAmount() / (float)maxFluid;
    }
    for (ISpillingEffect effect : effects) {
      effect.applyEffects(fluid, scale, context);
    }
    fluid.shrink(maxFluid);
    return fluid;
  }
}

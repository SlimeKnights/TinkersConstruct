package slimeknights.tconstruct.library.modifiers.fluid;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext.Block;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext.Entity;

import java.util.List;

/** Data class to connect fluids to fluid effects */
public record FluidEffects(FluidIngredient ingredient, List<FluidEffect<? super FluidEffectContext.Block>> blockEffects, List<FluidEffect<? super FluidEffectContext.Entity>> entityEffects, boolean hidden) {
  public static final RecordLoadable<FluidEffects> LOADABLE = RecordLoadable.create(
    FluidIngredient.LOADABLE.requiredField("fluid", e -> e.ingredient),
    FluidEffect.BLOCK_EFFECTS.list(0).defaultField("block_effects", List.of(), e -> e.blockEffects),
    FluidEffect.ENTITY_EFFECTS.list(0).defaultField("entity_effects", List.of(), e -> e.entityEffects),
    BooleanLoadable.INSTANCE.defaultField("hidden", false, false, e -> e.hidden),
    FluidEffects::new);

  /** @apiNote This constructor is internal, use either {@link slimeknights.tconstruct.library.data.tinkering.AbstractFluidEffectProvider} or JSON to instantiate. */
  @Internal
  public FluidEffects {}


  /* Fluid */

  /**
   * Checks if the recipe matches the given fluid
   * @param fluid  Fluid to test
   * @return  True if this recipe handles the given fluid
   */
  public boolean matches(Fluid fluid) {
    return ingredient.test(fluid);
  }

  /** Gets the amount of fluid needed for a single level */
  public int getAmount(Fluid fluid) {
    return ingredient.getAmount(fluid);
  }


  /* Effects present */

  /**
   * Checks if this fluid has any effects of either type
   * @see #hasBlockEffects()
   * @see #hasEntityEffects()
   */
  public boolean hasEffects() {
    return hasBlockEffects() || hasEntityEffects();
  }

  /** Checks if this fluid has any block effects */
  public boolean hasBlockEffects() {
    return !blockEffects.isEmpty();
  }

  /** Checks if this fluid has any entity effects */
  public boolean hasEntityEffects() {
    return !entityEffects.isEmpty();
  }


  /* Running effects */

  /** Runs the effects for a generic context */
  private <C extends FluidEffectContext> int apply(FluidStack fluid, float level, C context, List<FluidEffect<? super C>> effects, FluidAction action) {
    int amountPerLevel = getAmount(fluid.getFluid());
    float scale;
    if (fluid.getAmount() >= amountPerLevel * level) {
      scale = level;
    } else {
      scale = fluid.getAmount() / (float)amountPerLevel;
    }
    EffectLevel effectLevel = new EffectLevel(scale, level);
    float usedScale = 0;
    for (FluidEffect<? super C> effect : effects) {
      float newScale = effect.apply(fluid, effectLevel, context, action);
      if (newScale > usedScale) {
        usedScale = newScale;
      }
    }
    return (int)Math.ceil(amountPerLevel * Math.min(scale, usedScale));
  }

  /**
   * Applies the effect to a block
   * @param fluid    Input fluid, will not be modified
   * @param level    Level of effect to apply
   * @param context  Entity fluid context
   * @param action   If {@link FluidAction#SIMULATE}, makes no changes to the context. If {@link FluidAction#EXECUTE}, the context may be modified.
   * @return  Amount of fluid to consumed from this effect, will return 0 if no effect was performed. Note this may be slightly larger than the input fluid due to rounding.
   */
  @SuppressWarnings("UnusedReturnValue")
  public int applyToBlock(FluidStack fluid, float level, Block context, FluidAction action) {
    return apply(fluid, level, context, blockEffects, action);
  }

  /**
   * Applies the effect to an entity
   * @param fluid    Input fluid, will not be modified
   * @param level    Level of effect to apply
   * @param context  Entity fluid context
   * @param action   If {@link FluidAction#SIMULATE}, makes no changes to the context. If {@link FluidAction#EXECUTE}, the context may be modified.
   * @return  Amount of fluid to consumed from this effect, will return 0 if no effect was performed.
   */
  public int applyToEntity(FluidStack fluid, float level, Entity context, FluidAction action) {
    return apply(fluid, level, context, entityEffects, action);
  }

  /** Entry for storage in the manager */
  public record Entry(ResourceLocation name, FluidEffects effects) {
    public static final RecordLoadable<Entry> LOADABLE = RecordLoadable.create(
      Loadables.RESOURCE_LOCATION.requiredField("name", Entry::name),
      FluidEffects.LOADABLE.requiredField("effects", Entry::effects),
      Entry::new);
  }
}

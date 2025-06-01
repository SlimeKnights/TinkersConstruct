package slimeknights.tconstruct.library.modifiers.fluid.general;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.fluid.EffectLevel;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext.Block;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/** Effect that changes based on the amount of fluid present */
public record ScalingFluidEffect<C extends FluidEffectContext>(List<EffectForLevel<C>> effects, RecordLoadable<ScalingFluidEffect<C>> getLoader) implements FluidEffect<C> {
  public static final RecordLoadable<ScalingFluidEffect<FluidEffectContext.Block>> BLOCK_LOADER = createLoader(EffectForLevel.BLOCK_LOADABLE);
  public static final RecordLoadable<ScalingFluidEffect<FluidEffectContext.Entity>> ENTITY_LOADER = createLoader(EffectForLevel.ENTITY_LOADABLE);

  /** @apiNote This constructor is internal, use the builder via {@link #blocks()} or {@link #entities()} */
  @Internal
  public ScalingFluidEffect {
    // ensure scales are sorted
    for (int i = 1; i < effects.size(); i++) {
      if (effects.get(i-1).level >= effects.get(i).level) {
        TConstruct.LOG.warn("Effect scales should be in ascending order, otherwise results will be inconsistent, found level " + effects.get(i-1).level + " >= " + effects.get(i).level);
      }
    }
  }

  @Override
  public float apply(FluidStack fluid, EffectLevel level, C context, FluidAction action) {
    float scale = level.value();
    for (int i = effects.size() - 1; i >= 0; i--) {
      EffectForLevel<C> effect = effects.get(i);
      if (scale >= effect.level) {
        // rescale the effect level passed into the effect; makes more sense that reaching the threashold of a larger effect wouldn't rescale it
        return effect.effect.apply(fluid, level.divide(effect.level), context, action) * effect.level;
      }
    }
    return 0;
  }

  @Override
  public Component getDescription(RegistryAccess registryAccess) {
    // return the description of the largest effect, as that is closest to what they might want displayed
    return effects.get(effects.size() - 1).effect().getDescription(registryAccess);
  }

  /** @deprecated use {@link #createLoader(RecordLoadable)} */
  @Deprecated(forRemoval = true)
  public static <C extends FluidEffectContext> RecordLoadable<ScalingFluidEffect<C>> createLoader(RecordLoadable<EffectForLevel<C>> effectForScale, Supplier<RecordLoadable<ScalingFluidEffect<C>>> loader) {
    return createLoader(effectForScale);
  }

  /** Creates a loader for the given */
  public static <C extends FluidEffectContext> RecordLoadable<ScalingFluidEffect<C>> createLoader(RecordLoadable<EffectForLevel<C>> effectForScale) {
    return RecordLoadable.withLoader(effectForScale.list(1).requiredField("effects", ScalingFluidEffect::effects), ScalingFluidEffect::new);
  }

  /** Record for a single value in the list of effects */
  public record EffectForLevel<C extends FluidEffectContext>(float level, FluidEffect<? super C> effect) {
    public static final RecordLoadable<EffectForLevel<Block>> BLOCK_LOADABLE = createLoader(FluidEffect.BLOCK_EFFECTS);
    public static final RecordLoadable<EffectForLevel<Entity>> ENTITY_LOADABLE = createLoader(FluidEffect.ENTITY_EFFECTS);

    public static <C extends FluidEffectContext> RecordLoadable<EffectForLevel<C>> createLoader(GenericLoaderRegistry<FluidEffect<? super C>> loader) {
      return RecordLoadable.create(loader.requiredField("effect", EffectForLevel::effect), FloatLoadable.FROM_ZERO.requiredField("level", EffectForLevel::level), (effect1, level1) -> new EffectForLevel<>(level1, effect1));
    }
  }

  /* Builder */

  /** Creates a new builder for block effects */
  public static Builder<FluidEffectContext.Block> blocks() {
    return new Builder<>(BLOCK_LOADER);
  }

  /** @deprecated use {@link #entities()} */
  @Deprecated(forRemoval = true)
  public static Builder<FluidEffectContext.Entity> entites() {
    return entities();
  }

  /** Creates a new builder for entity effects */
  public static Builder<FluidEffectContext.Entity> entities() {
    return new Builder<>(ENTITY_LOADER);
  }

  @RequiredArgsConstructor
  @CanIgnoreReturnValue
  public static class Builder<C extends FluidEffectContext> {
    private final RecordLoadable<ScalingFluidEffect<C>> loader;
    private final List<EffectForLevel<C>> effects = new ArrayList<>();
    private float lastLevel = -1;

    /** Adds an effect to the builder */
    public Builder<C> effect(float level, FluidEffect<? super C> effect) {
      if (level < 0) {
        throw new IllegalArgumentException("Level must be non-negative");
      }
      if (level < lastLevel) {
        throw new IllegalArgumentException("Level must be increasing");
      }
      lastLevel = level;
      effects.add(new EffectForLevel<>(level, effect));
      return this;
    }

    /** Builds the final effect */
    public ScalingFluidEffect<C> build() {
      return new ScalingFluidEffect<>(List.copyOf(effects), loader);
    }
  }
}

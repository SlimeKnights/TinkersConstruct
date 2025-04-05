package slimeknights.tconstruct.library.modifiers.fluid.general;

import lombok.RequiredArgsConstructor;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/** Builder for a fluid effect taking in a list of fluid effects */
@RequiredArgsConstructor(staticName = "builder")
public class FluidEffectListBuilder<C extends FluidEffectContext,R> {
  /** Loadable for a list of block effects */
  public static final Loadable<List<FluidEffect<? super FluidEffectContext.Block>>> BLOCK_EFFECT_LIST = FluidEffect.BLOCK_EFFECTS.list(2);
  /** Loadable for a list of entity effects */
  public static final Loadable<List<FluidEffect<? super FluidEffectContext.Entity>>> ENTITY_EFFECT_LIST = FluidEffect.ENTITY_EFFECTS.list(2);

  private final Function<List<FluidEffect<? super C>>,R> constructor;
  private final List<FluidEffect<? super C>> effects = new ArrayList<>();

  /** Adds an effect to the builder */
  public FluidEffectListBuilder<C,R> effect(FluidEffect<? super C> effect) {
    effects.add(effect);
    return this;
  }

  /** Builds the final object */
  public R build() {
    return constructor.apply(effects);
  }
}

package slimeknights.tconstruct.library.modifiers.fluid.general;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.fluid.EffectLevel;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;

import java.util.List;

/**
 * Fluid effect which applies all effects in a sequence, stopping once an effect fails.
 * @param <C>  Effect context type
 */
public record SequenceFluidEffect<C extends FluidEffectContext>(List<FluidEffect<? super C>> effects, RecordLoadable<SequenceFluidEffect<C>> getLoader) implements FluidEffect<C> {
  public static final RecordLoadable<SequenceFluidEffect<FluidEffectContext.Block>> BLOCK_LOADER = createLoader(FluidEffectListBuilder.BLOCK_EFFECT_LIST);
  public static final RecordLoadable<SequenceFluidEffect<FluidEffectContext.Entity>> ENTITY_LOADER = createLoader(FluidEffectListBuilder.ENTITY_EFFECT_LIST);

  /** @apiNote Internal constructor, use {@link #blocks()} or {@link #entities()} */
  @Internal
  public SequenceFluidEffect {}

  @Override
  public float apply(FluidStack fluid, EffectLevel level, C context, FluidAction action) {
    float maxApplied = 0;
    for (FluidEffect<? super C> effect : effects) {
      float applied = effect.apply(fluid, level, context, action);
      // sequence breaks at first effect that doesn't apply
      if (applied <= 0) {
        break;
      }
      if (applied > maxApplied) {
        maxApplied = applied;
      }
    }
    return maxApplied;
  }

  @Override
  public Component getDescription(RegistryAccess registryAccess) {
    // almost definitely want a book description for this, but might as well generate something useful
    return effects.stream().map(effect -> effect.getDescription(registryAccess)).reduce(MERGE_COMPONENT_LIST).orElse(Component.empty());
  }


  /** Creates a new builder for block effects */
  public static FluidEffectListBuilder<FluidEffectContext.Block,SequenceFluidEffect<FluidEffectContext.Block>> blocks() {
    return FluidEffectListBuilder.builder(list -> new SequenceFluidEffect<>(list, BLOCK_LOADER));
  }

  public static FluidEffectListBuilder<FluidEffectContext.Entity,SequenceFluidEffect<FluidEffectContext.Entity>> entities() {
    return FluidEffectListBuilder.builder(list -> new SequenceFluidEffect<>(list, ENTITY_LOADER));
  }

  /** Creates a loader for the given */
  public static <C extends FluidEffectContext> RecordLoadable<SequenceFluidEffect<C>> createLoader(Loadable<List<FluidEffect<? super C>>> effectList) {
    return RecordLoadable.withLoader(effectList.requiredField("effects", SequenceFluidEffect::effects), SequenceFluidEffect::new);
  }
}

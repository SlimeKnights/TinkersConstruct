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
 * Fluid effect which tries each alternative effect, stopping once the first effect works.
 * @param <C>  Effect context type
 * @see net.minecraft.world.level.storage.loot.entries.AlternativesEntry
 */
public record AlternativesFluidEffect<C extends FluidEffectContext>(List<FluidEffect<? super C>> effects, RecordLoadable<AlternativesFluidEffect<C>> getLoader) implements FluidEffect<C> {
  public static final RecordLoadable<AlternativesFluidEffect<FluidEffectContext.Block>> BLOCK_LOADER = createLoader(FluidEffectListBuilder.BLOCK_EFFECT_LIST);
  public static final RecordLoadable<AlternativesFluidEffect<FluidEffectContext.Entity>> ENTITY_LOADER = createLoader(FluidEffectListBuilder.ENTITY_EFFECT_LIST);

  /** @apiNote Internal constructor, use {@link #blocks()} or {@link #entities()} */
  @Internal
  public AlternativesFluidEffect {}

  @Override
  public float apply(FluidStack fluid, EffectLevel level, C context, FluidAction action) {
    for (FluidEffect<? super C> effect : effects) {
      float applied = effect.apply(fluid, level, context, action);
      if (applied > 0) {
        return applied;
      }
    }
    return 0;
  }

  @Override
  public Component getDescription(RegistryAccess registryAccess) {
    // return the description of the first effect, as that is closest to what they might want displayed
    // you probably will want a book description for this for multiple lines
    return effects.get(0).getDescription(registryAccess);
  }


  /** Creates a new builder for block effects */
  public static FluidEffectListBuilder<FluidEffectContext.Block,AlternativesFluidEffect<FluidEffectContext.Block>> blocks() {
    return FluidEffectListBuilder.builder(list -> new AlternativesFluidEffect<>(list, BLOCK_LOADER));
  }

  public static FluidEffectListBuilder<FluidEffectContext.Entity,AlternativesFluidEffect<FluidEffectContext.Entity>> entities() {
    return FluidEffectListBuilder.builder(list -> new AlternativesFluidEffect<>(list, ENTITY_LOADER));
  }

  /** Creates a loader for the given */
  public static <C extends FluidEffectContext> RecordLoadable<AlternativesFluidEffect<C>> createLoader(Loadable<List<FluidEffect<? super C>>> effectList) {
    return RecordLoadable.withLoader(effectList.requiredField("effects", AlternativesFluidEffect::effects), AlternativesFluidEffect::new);
  }
}

package slimeknights.tconstruct.library.modifiers.fluid.general;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.block.BlockPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.library.json.predicate.TinkerPredicate;
import slimeknights.tconstruct.library.modifiers.fluid.EffectLevel;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;

/**
 * Fluid effect that conditions on the holder and target.
 * @apiNote This class is internal, no need to use directly. Instead, make use of the helpers in {@link slimeknights.tconstruct.library.data.tinkering.AbstractFluidEffectProvider}.
 */
@Internal
public interface ConditionalFluidEffect<C extends FluidEffectContext> extends FluidEffect<C> {
  /** Gets the effect to apply */
  FluidEffect<? super C> effect();

  /** Checks if this effect can be applied */
  boolean canApply(C context);

  @Override
  default float apply(FluidStack fluid, EffectLevel level, C context, FluidAction action) {
    return canApply(context) ? effect().apply(fluid, level, context, action) : 0;
  }

  @Override
  default Component getDescription(RegistryAccess registryAccess) {
    return effect().getDescription(registryAccess);
  }

  /** Conditional block effect */
  record Block(IJsonPredicate<BlockState> block, FluidEffect<? super FluidEffectContext.Block> effect) implements ConditionalFluidEffect<FluidEffectContext.Block> {
    public static final RecordLoadable<Block> LOADER = RecordLoadable.create(
      BlockPredicate.LOADER.defaultField("block", e -> e.block),
      FluidEffect.BLOCK_EFFECTS.requiredField("effect", e -> e.effect),
      Block::new);

    @Override
    public boolean canApply(FluidEffectContext.Block context) {
      return block.matches(context.getBlockState());
    }

    @Override
    public RecordLoadable<Block> getLoader() {
      return LOADER;
    }
  }

  /** Conditional block effect */
  record Entity(IJsonPredicate<LivingEntity> target, FluidEffect<? super FluidEffectContext.Entity> effect) implements ConditionalFluidEffect<FluidEffectContext.Entity> {
    public static final RecordLoadable<Entity> LOADER = RecordLoadable.create(
      LivingEntityPredicate.LOADER.defaultField("target", e -> e.target),
      FluidEffect.ENTITY_EFFECTS.requiredField("effect", e -> e.effect),
      Entity::new);

    @Override
    public boolean canApply(FluidEffectContext.Entity context) {
      return TinkerPredicate.matches(target, context.getLivingTarget());
    }

    @Override
    public RecordLoadable<Entity> getLoader() {
      return LOADER;
    }
  }
}

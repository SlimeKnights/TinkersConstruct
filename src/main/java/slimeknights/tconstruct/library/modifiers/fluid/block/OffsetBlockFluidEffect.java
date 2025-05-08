package slimeknights.tconstruct.library.modifiers.fluid.block;

import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.fluid.EffectLevel;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;

import javax.annotation.Nullable;

/** Block fluid effect that targets the block adjacent to the one hit */
public record OffsetBlockFluidEffect(FluidEffect<? super FluidEffectContext.Block> effect, @Nullable Direction direction) implements FluidEffect<FluidEffectContext.Block> {
  public static final RecordLoadable<OffsetBlockFluidEffect> LOADER = RecordLoadable.create(
    FluidEffect.BLOCK_EFFECTS.directField("offset_type", OffsetBlockFluidEffect::effect),
    Loadables.DIRECTION.nullableField("offset_direction", OffsetBlockFluidEffect::direction),
    OffsetBlockFluidEffect::new);

  public OffsetBlockFluidEffect(FluidEffect<? super FluidEffectContext.Block> effect) {
    this(effect, null);
  }

  @Override
  public RecordLoadable<OffsetBlockFluidEffect> getLoader() {
    return LOADER;
  }

  @Override
  public float apply(FluidStack fluid, EffectLevel level, FluidEffectContext.Block context, FluidAction action) {
    BlockHitResult hitResult = context.getHitResult();
    Direction direction = this.direction != null ? this.direction : hitResult.getDirection();
    FluidEffectContext.Block offset = context.withHitResult(new BlockHitResult(hitResult.getLocation(), direction, hitResult.getBlockPos().relative(direction), hitResult.isInside()));
    return effect.apply(fluid, level, offset, action);
  }

  @Override
  public Component getDescription(RegistryAccess registryAccess) {
    return effect.getDescription(registryAccess);
  }
}

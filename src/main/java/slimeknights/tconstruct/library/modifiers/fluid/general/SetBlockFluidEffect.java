package slimeknights.tconstruct.library.modifiers.fluid.general;

import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.data.loadable.common.BlockStateLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.fluid.EffectLevel;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;
import slimeknights.tconstruct.library.modifiers.fluid.block.BreakBlockFluidEffect;

/**
 * Replaces a block with a different block using a fluid. Unlike {@link BreakBlockFluidEffect}, does not produce block drops or validate the block placememt.
 */
public record SetBlockFluidEffect(BlockState block) implements FluidEffect<FluidEffectContext> {
  public static final RecordLoadable<SetBlockFluidEffect> LOADER = RecordLoadable.create(
    BlockStateLoadable.DIFFERENCE.requiredField("block", SetBlockFluidEffect::block),
    SetBlockFluidEffect::new);
  public static final SetBlockFluidEffect AIR = new SetBlockFluidEffect(Blocks.AIR);

  public SetBlockFluidEffect(Block block) {
    this(block.defaultBlockState());
  }

  @Override
  public RecordLoadable<SetBlockFluidEffect> getLoader() {
    return LOADER;
  }

  @Override
  public float apply(FluidStack fluid, EffectLevel level, FluidEffectContext context, FluidAction action) {
    if (!level.isFull()) {
      return 0;
    }
    // find what was there before
    Level world = context.getLevel();
    BlockPos target = context.getBlockPos();
    BlockState original = world.getBlockState(target);
    if (original != this.block && !context.breakRestricted()) {
      if (action.execute() && !world.isClientSide) {
        if (world.setBlockAndUpdate(target, this.block) && !original.isAir()) {
          world.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, target, Block.getId(original));
        }
      }
      return 1;
    }
    return 0;
  }

  @Override
  public Component getDescription(RegistryAccess registryAccess) {
    return FluidEffect.makeTranslation(getLoader(), Component.translatable(block.getBlock().getDescriptionId()));
  }
}

package slimeknights.tconstruct.smeltery.block.controller;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.util.BlockEntityHelper;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.entity.controller.AlloyerBlockEntity;

import javax.annotation.Nullable;

public class AlloyerBlock extends TinyMultiblockControllerBlock {
  public AlloyerBlock(Properties builder) {
    super(builder);
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
    return new AlloyerBlockEntity(pPos, pState);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> wanted) {
    return pLevel.isClientSide ? null : BlockEntityHelper.castTicker(wanted, TinkerSmeltery.alloyer.get(), AlloyerBlockEntity.SERVER_TICKER);
  }

  @Override
  public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
    Direction direction = Util.directionFromOffset(pos, fromPos);
    if (direction != Direction.DOWN) {
      BlockEntityHelper.get(AlloyerBlockEntity.class, world, pos).ifPresent(te -> te.neighborChanged(direction));
    }
  }

  /*
   * Display
   */

  @Deprecated
  @Override
  public float getShadeBrightness(BlockState state, BlockGetter worldIn, BlockPos pos) {
    return 1.0F;
  }

  @Override
  public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
    return true;
  }

  @Override
  public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
    if (state.getValue(ACTIVE)) {
      double x = pos.getX() + 0.5D;
      double y = (double) pos.getY() + (rand.nextFloat() * 4F) / 16F;
      double z = pos.getZ() + 0.5D;
      double frontOffset = 0.52D;
      double sideOffset = rand.nextDouble() * 0.6D - 0.3D;
      spawnFireParticles(world, state, x, y, z, frontOffset, sideOffset, ParticleTypes.SOUL_FIRE_FLAME);
    }
  }
}

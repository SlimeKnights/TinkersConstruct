package slimeknights.tconstruct.smeltery.block.controller;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.util.BlockEntityHelper;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.entity.controller.FoundryBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.controller.HeatingStructureBlockEntity;

import javax.annotation.Nullable;

public class FoundryControllerBlock extends HeatingControllerBlock {
  public FoundryControllerBlock(Properties builder) {
    super(builder);
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
    return new FoundryBlockEntity(pPos, pState);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type) {
    return HeatingStructureBlockEntity.getTicker(pLevel, type, TinkerSmeltery.foundry.get());
  }

  @Override
  public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    super.setPlacedBy(worldIn, pos, state, placer, stack);
    // check structure
    BlockEntityHelper.get(FoundryBlockEntity.class, worldIn, pos).ifPresent(FoundryBlockEntity::updateStructure);
  }

  @Override
  @Deprecated
  public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
    if (!newState.is(this)) {
      BlockEntityHelper.get(FoundryBlockEntity.class, worldIn, pos).ifPresent(FoundryBlockEntity::invalidateStructure);
    }
    super.onRemove(state, worldIn, pos, newState, isMoving);
  }

  @Override
  public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
    if (state.getValue(ACTIVE)) {
      double x = pos.getX() + 0.5D;
      double y = (double) pos.getY() + (rand.nextFloat() * 6F + 2F) / 16F;
      double z = pos.getZ() + 0.5D;
      double frontOffset = 0.52D;
      double sideOffset = rand.nextDouble() * 0.6D - 0.3D;
      spawnFireParticles(world, state, x, y, z, frontOffset, sideOffset, ParticleTypes.SOUL_FIRE_FLAME);
    }
  }


  /* No rotation if in a structure  */

  @Deprecated
  @Override
  public BlockState rotate(BlockState state, Rotation rotation) {
    if (state.getValue(IN_STRUCTURE)) {
      return state;
    }
    return super.rotate(state, rotation);
  }

  @Deprecated
  @Override
  public BlockState mirror(BlockState state, Mirror mirror) {
    if (state.getValue(IN_STRUCTURE)) {
      return state;
    }
    return super.mirror(state, mirror);
  }
}

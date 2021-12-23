package slimeknights.tconstruct.smeltery.block.controller;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import slimeknights.mantle.util.TileEntityHelper;
import slimeknights.tconstruct.smeltery.tileentity.controller.FoundryTileEntity;

import javax.annotation.Nullable;
import java.util.Random;

public class FoundryControllerBlock extends HeatingControllerBlock {
  public FoundryControllerBlock(Properties builder) {
    super(builder);
  }

  @Override
  public TileEntity createTileEntity(BlockState blockState, IBlockReader iBlockReader) {
    return new FoundryTileEntity();
  }

  @Override
  public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    // check structure
    TileEntityHelper.getTile(FoundryTileEntity.class, worldIn, pos).ifPresent(FoundryTileEntity::updateStructure);
  }

  @Override
  @Deprecated
  public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
    if (!newState.matchesBlock(this)) {
      TileEntityHelper.getTile(FoundryTileEntity.class, worldIn, pos).ifPresent(FoundryTileEntity::invalidateStructure);
    }
    super.onReplaced(state, worldIn, pos, newState, isMoving);
  }

  @Override
  public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
    if (state.get(ACTIVE)) {
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
    if (state.get(IN_STRUCTURE)) {
      return state;
    }
    return super.rotate(state, rotation);
  }

  @Deprecated
  @Override
  public BlockState mirror(BlockState state, Mirror mirror) {
    if (state.get(IN_STRUCTURE)) {
      return state;
    }
    return super.mirror(state, mirror);
  }
}

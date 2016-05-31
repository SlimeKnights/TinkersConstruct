package slimeknights.tconstruct.shared.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

import java.util.Random;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.smeltery.block.BlockTinkerFluid;
import slimeknights.tconstruct.world.TinkerWorld;

public class BlockLiquidSlime extends BlockTinkerFluid {

  public BlockLiquidSlime(Fluid fluid, Material material) {
    super(fluid, material);
  }

  @Override
  public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
    int oldLevel = state.getValue(LEVEL);
    super.updateTick(world, pos, state, rand);

    // no fluid update but flowing?
    if(oldLevel > 0 && oldLevel == state.getValue(LEVEL)) {
      if(rand.nextFloat() > 0.6f) {
        // only if they have dirt below them
        Block blockDown = world.getBlockState(pos.down()).getBlock();
        if(blockDown == Blocks.DIRT) {
          // check if the block we flowed from has slimedirt below it and move the slime with us!
          for(EnumFacing dir : EnumFacing.HORIZONTALS) {
            IBlockState state2 = world.getBlockState(pos.offset(dir));
            // same block and a higher flow
            if(state2.getBlock() == this && state2.getValue(LEVEL) == state.getValue(LEVEL) - 1) {
              IBlockState dirt = world.getBlockState(pos.offset(dir).down());
              if(dirt.getBlock() == TinkerWorld.slimeDirt) {
                // we got a block we flowed from and the block we flowed from has slimedirt below
                // change the dirt below us to slimedirt too
                world.setBlockState(pos.down(), dirt);
              }
              if(dirt.getBlock() == TinkerWorld.slimeGrass) {
                world.setBlockState(pos.down(), TinkerWorld.slimeGrass.getDirtState(dirt));
              }
            }
          }
        }
      }

      world.scheduleBlockUpdate(pos, this, 400 + rand.nextInt(200), 0);
    }
  }

  @Override
  public boolean canCreatureSpawn(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EntityLiving.SpawnPlacementType type) {
    return type == EntityLiving.SpawnPlacementType.IN_WATER || super.canCreatureSpawn(state, world, pos, type);
  }
}

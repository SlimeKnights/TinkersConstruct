package slimeknights.tconstruct.fluids.fluids;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;

import java.util.Random;

public abstract class SlimeFluid extends ForgeFlowingFluid {

  protected SlimeFluid(Properties properties) {
    super(properties);
  }

  @Override
  public void randomTick(World world, BlockPos pos, FluidState state, Random random) {
    int oldLevel = getLevelFromState(state);
    super.randomTick(world, pos, state, random);

    if (oldLevel > 0 && oldLevel == getLevelFromState(state)) {
      if (random.nextFloat() > 0.6f) {
        // only if they have dirt below them
        Block blockDown = world.getBlockState(pos.down()).getBlock();
        if (blockDown == Blocks.DIRT) {
          // check if the block we flowed from has slimedirt below it and move the slime with us!
          for (Direction dir : Direction.Plane.HORIZONTAL) {
            FluidState state2 = world.getFluidState(pos.offset(dir));
            // same block and a higher flow
            if (state2.getFluid() == this && getLevelFromState(state2) == getLevelFromState(state) - 1) {
              BlockState dirt = world.getBlockState(pos.offset(dir).down());
              if (TinkerWorld.slimeDirt.contains(dirt.getBlock())) {
                // we got a block we flowed from and the block we flowed from has slimedirt below
                // change the dirt below us to slimedirt too
                world.setBlockState(pos.down(), dirt);
              }
              if (dirt.getBlock() == TinkerWorld.earthSlimeGrass.get(SlimeGrassBlock.FoliageType.SKY)) {
                world.setBlockState(pos.down(), SlimeGrassBlock.getDirtState(dirt));
              }
            }
          }
        }
      }

      //world.scheduleBlockUpdate(pos, this, 400 + rand.nextInt(200), 0);
    }
  }

  public static class Flowing extends SlimeFluid {

    public Flowing(Properties properties) {
      super(properties);
      this.setDefaultState(this.getStateContainer().getBaseState().with(LEVEL_1_8, 7));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Fluid, FluidState> builder) {
      super.fillStateContainer(builder);
      builder.add(LEVEL_1_8);
    }

    @Override
    public int getLevel(FluidState state) {
      return state.get(LEVEL_1_8);
    }

    @Override
    public boolean isSource(FluidState state) {
      return false;
    }
  }

  public static class Source extends SlimeFluid {

    public Source(Properties properties) {
      super(properties);
    }

    @Override
    public int getLevel(FluidState state) {
      return 8;
    }

    @Override
    public boolean isSource(FluidState state) {
      return true;
    }
  }
}

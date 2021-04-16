package slimeknights.tconstruct.fluids.fluids;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import slimeknights.mantle.registration.object.MantleFluid;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;

import java.util.Random;

public abstract class SlimeFluid extends MantleFluid.Flowing {

  protected SlimeFluid(Item bucketItem, BlockState blockState) {
    super(bucketItem, blockState);
  }

  @Override
  public void onRandomTick(World world, BlockPos pos, FluidState state, Random random) {
    int oldLevel = method_15741(state);
    super.onRandomTick(world, pos, state, random);

    if (oldLevel > 0 && oldLevel == method_15741(state)) {
      if (random.nextFloat() > 0.6f) {
        // only if they have dirt below them
        Block blockDown = world.getBlockState(pos.down()).getBlock();
        if (blockDown == Blocks.DIRT) {
          // check if the block we flowed from has slimedirt below it and move the slime with us!
          for (Direction dir : Direction.Type.HORIZONTAL) {
            FluidState state2 = world.getFluidState(pos.offset(dir));
            // same block and a higher flow
            if (state2.getFluid() == this && method_15741(state2) == method_15741(state) - 1) {
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

    public Flowing(Item bucketItem, BlockState blockState) {
      super(bucketItem, blockState);
      this.setDefaultState(this.getStateManager().getDefaultState().with(LEVEL, 7));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
      super.appendProperties(builder);
      builder.add(LEVEL);
    }

    @Override
    public int getLevel(FluidState state) {
      return state.get(LEVEL);
    }

    @Override
    public boolean isStill(FluidState state) {
      return false;
    }
  }

  public static class Source extends SlimeFluid {

    public Source() {
      super(null, null);
    }

    @Override
    public int getLevel(FluidState state) {
      return 8;
    }

    @Override
    public boolean isStill(FluidState state) {
      return true;
    }
  }
}

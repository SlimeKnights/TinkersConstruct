package slimeknights.tconstruct.fluids.fluids;

import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public abstract class SlimeFluid extends ForgeFlowingFluid {

  protected SlimeFluid(Properties properties) {
    super(properties);
  }

  /* TODO: no idea what this logic is supposed to be doig, it needs rethinking
  @Override
  public void randomTick(Level world, BlockPos pos, FluidState state, RandomSource random) {
    int oldLevel = getLegacyLevel(state);
    super.randomTick(world, pos, state, random);

    if (oldLevel > 0 && oldLevel == getLegacyLevel(state)) {
      if (random.nextFloat() > 0.6f) {
        // only if they have dirt below them
        Block blockDown = world.getBlockState(pos.below()).getBlock();
        if (blockDown == Blocks.DIRT) {
          // check if the block we flowed from has slimedirt below it and move the slime with us!
          for (Direction dir : Direction.Plane.HORIZONTAL) {
            FluidState state2 = world.getFluidState(pos.relative(dir));
            // same block and a higher flow
            if (state2.getType() == this && getLegacyLevel(state2) == getLegacyLevel(state) - 1) {
              BlockState dirt = world.getBlockState(pos.relative(dir).below());
              if (TinkerWorld.slimeDirt.contains(dirt.getBlock())) {
                // we got a block we flowed from and the block we flowed from has slimedirt below
                // change the dirt below us to slimedirt too
                world.setBlockAndUpdate(pos.below(), dirt);
              } else if (dirt.getBlock() == TinkerWorld.earthSlimeGrass.get(FoliageType.SKY)) {
                world.setBlockAndUpdate(pos.below(), SlimeGrassBlock.getDirtState(dirt));
              }
            }
          }
        }
      }

      //world.scheduleBlockUpdate(pos, this, 400 + rand.nextInt(200), 0);
    }

  }
   */

  public static class Flowing extends SlimeFluid {

    public Flowing(Properties properties) {
      super(properties);
      this.registerDefaultState(this.getStateDefinition().any().setValue(LEVEL, 7));
    }

    @Override
    protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
      super.createFluidStateDefinition(builder);
      builder.add(LEVEL);
    }

    @Override
    public int getAmount(FluidState state) {
      return state.getValue(LEVEL);
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
    public int getAmount(FluidState state) {
      return 8;
    }

    @Override
    public boolean isSource(FluidState state) {
      return true;
    }
  }
}

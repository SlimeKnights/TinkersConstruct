package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.ITankTileEntity;

import java.util.Random;

public class AlloyTankBlock extends SearedBlock {
  public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
  public AlloyTankBlock(Properties props) {
    super(props);
    this.setDefaultState(this.getDefaultState().with(ACTIVE, false));
  }

  /*
   * Block state
   */

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(ACTIVE);
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return this.getDefaultState()
      .with(ACTIVE, isValidFuelSource(context.getWorld().getBlockState(context.getPos().down())));
  }

  @Deprecated
  @Override
  public BlockState updatePostPlacement(BlockState state, Direction direction, BlockState neighbor, IWorld world, BlockPos pos, BlockPos neighborPos) {
    if (direction == Direction.DOWN) {
      return state.with(ACTIVE, isValidFuelSource(neighbor));
    }
    return state;
  }

  /**
   * Checks if the given state is a valid fuel source
   * @param state  State instance
   * @return  True if its a valid fuel source
   */
  protected boolean isValidFuelSource(BlockState state) {
    return TinkerSmeltery.searedTank.contains(state.getBlock());
  }


  /*
   * Block behavior
   */

  @Deprecated
  @Override
  @OnlyIn(Dist.CLIENT)
  public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
    return 1.0F;
  }

  @Override
  public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
    return true;
  }

  @Override
  public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
    if (stateIn.get(ACTIVE)) {
      Direction direction = Direction.byIndex(rand.nextInt(5));
      double x = pos.getX() + 0.5D;
      double y = (double) pos.getY() + (rand.nextFloat() * 6.F) / 16F;
      double z = pos.getZ() + 0.5D;
      double frontOffset = 0.52D;
      double sideOffset = rand.nextDouble() * 0.6D - 0.3D;

      MelterBlock.spawnFireParticles(worldIn, direction, x, y, z, frontOffset, sideOffset);
    }
  }


  /*
   * Comparator
   */

  @Deprecated
  @Override
  public boolean hasComparatorInputOverride(BlockState state) {
    return true;
  }

  @Deprecated
  @Override
  public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
    return ITankTileEntity.getComparatorInputOverride(worldIn, pos);
  }
}

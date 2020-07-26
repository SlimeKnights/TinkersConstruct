package slimeknights.tconstruct.smeltery.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.tconstruct.smeltery.tileentity.FaucetTileEntity;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Optional;
import java.util.Random;

public class FaucetBlock extends ContainerBlock {
  public static final DirectionProperty FACING = DirectionProperty.create("facing", (direction) -> direction != Direction.DOWN);
  private static final EnumMap<Direction,VoxelShape> SHAPES = Maps.newEnumMap(ImmutableMap.of(
    Direction.UP, Block.makeCuboidShape(4.0D, 10.0D, 4.0D, 12.0D, 16.0D, 12.0D),
    Direction.NORTH, Block.makeCuboidShape(4.0D, 4.0D, 0.0D, 12.0D, 10.0D, 6.0D),
    Direction.SOUTH, Block.makeCuboidShape(4.0D, 4.0D, 10.0D, 12.0D, 10.0D, 16.D),
    Direction.EAST, Block.makeCuboidShape(10.D, 4.0D, 4.0D, 16.0D, 10.0D, 12.0D),
    Direction.WEST, Block.makeCuboidShape(0.0D, 4.0D, 4.0D, 6.0D, 10.0D, 12.0D)
  ));

  public FaucetBlock(Properties builder) {
    super(builder);
    this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
  }

  /* Blockstate */

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(FACING);
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    Direction dir = context.getFace().getOpposite();
    if (dir == Direction.DOWN) {
      dir = context.getPlacementHorizontalFacing().getOpposite();
    }
    return this.getDefaultState().with(FACING, dir);
  }

  @Deprecated
  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    return SHAPES.get(state.get(FACING));
  }


  /* Tile entity */

  @Override
  @Nullable
  public TileEntity createNewTileEntity(IBlockReader worldIn) {
    return new FaucetTileEntity();
  }

  @Deprecated
  @Override
  public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
    if (player.isSneaking()) {
      return ActionResultType.PASS;
    }
    getFaucet(worldIn, pos).ifPresent(FaucetTileEntity::activate);
    return ActionResultType.SUCCESS;
  }

  @Override
  public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
    // TODO: keep?
    return true;
  }

  @Deprecated
  @Override
  public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
    if (worldIn.isRemote()) {
      return;
    }
    getFaucet(worldIn, pos).ifPresent(faucet -> faucet.handleRedstone(worldIn.isBlockPowered(pos)));
  }

  @Deprecated
  @Override
  public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
    getFaucet(worldIn, pos).ifPresent(FaucetTileEntity::activate);
  }

  /**
   * Gets the facuet tile entity at the given position
   * @param world  World instance
   * @param pos    Faucet position
   * @return  Optional of faucet, empty if missing or wrong type
   */
  private Optional<FaucetTileEntity> getFaucet(World world, BlockPos pos) {
    return Optional.ofNullable(world.getTileEntity(pos))
                   .filter(te -> te instanceof FaucetTileEntity)
                   .map(te -> (FaucetTileEntity)te);
  }

  /* Display */

  @Deprecated
  @Override
  public BlockRenderType getRenderType(BlockState state) {
    return BlockRenderType.MODEL;
  }

  /**
   * Adds particles to the faucet
   * @param state    Faucet state
   * @param worldIn  World instance
   * @param pos      Faucet position
   */
  private static void addParticles(BlockState state, IWorld worldIn, BlockPos pos) {
    Direction direction = state.get(FACING);
    //Direction direction1 = getFacing(state).getOpposite();
    double x = (double)pos.getX() + 0.5D + 0.3D * (double)direction.getXOffset();
    double y = (double)pos.getY() + 0.5D + 0.3D * (double)direction.getYOffset();
    double z = (double)pos.getZ() + 0.5D + 0.3D * (double)direction.getZOffset();
    worldIn.addParticle(new RedstoneParticleData(1.0F, 0.0F, 0.0F, 0.5f), x, y, z, 0.0D, 0.0D, 0.0D);
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
    getFaucet(worldIn, pos).ifPresent(faucet -> {
      if (faucet.isPouring() && faucet.getDrained().isEmpty() && rand.nextFloat() < 0.25F) {
        addParticles(stateIn, worldIn, pos);
      }
    });
  }
}

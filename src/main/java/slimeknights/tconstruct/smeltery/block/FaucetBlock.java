package slimeknights.tconstruct.smeltery.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.mantle.util.TileEntityHelper;
import slimeknights.tconstruct.smeltery.tileentity.FaucetTileEntity;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Optional;
import java.util.Random;

import net.minecraft.block.AbstractBlock.Properties;

public class FaucetBlock extends Block {
  public static final DirectionProperty FACING = BlockStateProperties.FACING_HOPPER;
  private static final EnumMap<Direction,VoxelShape> SHAPES = Maps.newEnumMap(ImmutableMap.of(
    Direction.DOWN,  VoxelShapes.join(box( 4, 10,  4, 12, 16, 12), box( 6, 10,  6, 10, 16, 10), IBooleanFunction.ONLY_FIRST),
    Direction.NORTH, VoxelShapes.join(box( 4,  4, 10, 12, 10, 16), box( 6,  6, 10, 10, 10, 16), IBooleanFunction.ONLY_FIRST),
    Direction.SOUTH, VoxelShapes.join(box( 4,  4,  0, 12, 10,  6), box( 6,  6,  0, 10, 10,  6), IBooleanFunction.ONLY_FIRST),
    Direction.WEST,  VoxelShapes.join(box(10,  4,  4, 16, 10, 12), box(10,  6,  6, 16, 10, 10), IBooleanFunction.ONLY_FIRST),
    Direction.EAST,  VoxelShapes.join(box( 0,  4,  4,  6, 10, 12), box( 0,  6,  6,  6, 10, 10), IBooleanFunction.ONLY_FIRST)));

  public FaucetBlock(Properties properties) {
    super(properties);
    this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
  }

  /* Blockstate */

  @Override
  protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(FACING);
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    Direction dir = context.getClickedFace();
    if (dir == Direction.UP) {
      dir = Direction.DOWN;
    }
    return this.defaultBlockState().setValue(FACING, dir);
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    return SHAPES.get(state.getValue(FACING));
  }

  @Override
  public boolean isPathfindable(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
    return false;
  }


  /* Tile entity */

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new FaucetTileEntity();
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
    if (player.isShiftKeyDown()) {
      return ActionResultType.PASS;
    }
    getFaucet(worldIn, pos).ifPresent(FaucetTileEntity::activate);
    return ActionResultType.SUCCESS;
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
    if (worldIn.isClientSide()) {
      return;
    }
    getFaucet(worldIn, pos).ifPresent(faucet -> {
      faucet.neighborChanged(fromPos);
      faucet.handleRedstone(worldIn.hasNeighborSignal(pos));
    });
  }

  @SuppressWarnings("deprecation")
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
    return TileEntityHelper.getTile(FaucetTileEntity.class, world, pos);
  }

  /* Display */

  /**
   * Adds particles to the faucet
   * @param state    Faucet state
   * @param worldIn  World instance
   * @param pos      Faucet position
   */
  private static void addParticles(BlockState state, IWorld worldIn, BlockPos pos) {
    Direction direction = state.getValue(FACING);
    double x = (double)pos.getX() + 0.5D - 0.3D * (double)direction.getStepX();
    double y = (double)pos.getY() + 0.5D - 0.3D * (double)direction.getStepY();
    double z = (double)pos.getZ() + 0.5D - 0.3D * (double)direction.getStepZ();
    worldIn.addParticle(new RedstoneParticleData(1.0F, 0.0F, 0.0F, 0.5f), x, y, z, 0.0D, 0.0D, 0.0D);
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
    getFaucet(worldIn, pos).ifPresent(faucet -> {
      if (faucet.isPouring() && faucet.getRenderFluid().isEmpty() && rand.nextFloat() < 0.25F) {
        addParticles(stateIn, worldIn, pos);
      }
    });
  }
}

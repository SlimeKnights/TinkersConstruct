package slimeknights.tconstruct.smeltery.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.mantle.util.TileEntityHelper;
import slimeknights.tconstruct.smeltery.tileentity.FaucetTileEntity;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Optional;
import java.util.Random;

public class FaucetBlock extends Block {
  public static final DirectionProperty FACING = Properties.HOPPER_FACING;
  private static final EnumMap<Direction,VoxelShape> SHAPES = Maps.newEnumMap(ImmutableMap.of(
    Direction.DOWN,  VoxelShapes.combineAndSimplify(createCuboidShape( 4, 10,  4, 12, 16, 12), createCuboidShape( 6, 10,  6, 10, 16, 10), BooleanBiFunction.ONLY_FIRST),
    Direction.NORTH, VoxelShapes.combineAndSimplify(createCuboidShape( 4,  4, 10, 12, 10, 16), createCuboidShape( 6,  6, 10, 10, 10, 16), BooleanBiFunction.ONLY_FIRST),
    Direction.SOUTH, VoxelShapes.combineAndSimplify(createCuboidShape( 4,  4,  0, 12, 10,  6), createCuboidShape( 6,  6,  0, 10, 10,  6), BooleanBiFunction.ONLY_FIRST),
    Direction.WEST,  VoxelShapes.combineAndSimplify(createCuboidShape(10,  4,  4, 16, 10, 12), createCuboidShape(10,  6,  6, 16, 10, 10), BooleanBiFunction.ONLY_FIRST),
    Direction.EAST,  VoxelShapes.combineAndSimplify(createCuboidShape( 0,  4,  4,  6, 10, 12), createCuboidShape( 0,  6,  6,  6, 10, 10), BooleanBiFunction.ONLY_FIRST)));

  public FaucetBlock(Settings properties) {
    super(properties);
    this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
  }

  /* Blockstate */

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    builder.add(FACING);
  }

  @Nullable
  @Override
  public BlockState getPlacementState(ItemPlacementContext context) {
    Direction dir = context.getSide();
    if (dir == Direction.UP) {
      dir = Direction.DOWN;
    }
    return this.getDefaultState().with(FACING, dir);
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
    return SHAPES.get(state.get(FACING));
  }


  /* Tile entity */

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Override
  public BlockEntity createTileEntity(BlockState state, BlockView world) {
    return new FaucetTileEntity();
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public ActionResult onUse(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockHitResult hit) {
    if (player.isSneaking()) {
      return ActionResult.PASS;
    }
    getFaucet(worldIn, pos).ifPresent(FaucetTileEntity::activate);
    return ActionResult.SUCCESS;
  }

  @Override
  public boolean canConnectRedstone(BlockState state, BlockView world, BlockPos pos, @Nullable Direction side) {
    // TODO: keep?
    return true;
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public void neighborUpdate(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
    if (worldIn.isClient()) {
      return;
    }
    getFaucet(worldIn, pos).ifPresent(faucet -> {
      faucet.neighborChanged(fromPos);
      faucet.handleRedstone(worldIn.isReceivingRedstonePower(pos));
    });
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public void scheduledTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
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
  private static void addParticles(BlockState state, WorldAccess worldIn, BlockPos pos) {
    Direction direction = state.get(FACING);
    double x = (double)pos.getX() + 0.5D - 0.3D * (double)direction.getOffsetX();
    double y = (double)pos.getY() + 0.5D - 0.3D * (double)direction.getOffsetY();
    double z = (double)pos.getZ() + 0.5D - 0.3D * (double)direction.getOffsetZ();
    worldIn.addParticle(new DustParticleEffect(1.0F, 0.0F, 0.0F, 0.5f), x, y, z, 0.0D, 0.0D, 0.0D);
  }

  @Override
  @Environment(EnvType.CLIENT)
  public void randomDisplayTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
    getFaucet(worldIn, pos).ifPresent(faucet -> {
      if (faucet.isPouring() && faucet.getRenderFluid().isEmpty() && rand.nextFloat() < 0.25F) {
        addParticles(stateIn, worldIn, pos);
      }
    });
  }
}

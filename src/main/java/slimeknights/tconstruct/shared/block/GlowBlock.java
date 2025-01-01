package slimeknights.tconstruct.shared.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class GlowBlock extends Block {

  public static final DirectionProperty FACING = BlockStateProperties.FACING;

  public GlowBlock(Properties properties) {
    super(properties);
    this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.DOWN));
    this.drops = BuiltInLootTables.EMPTY;
  }

  private static final Map<Direction, VoxelShape> BOUNDS = new EnumMap<>(Direction.class);
  static {
    BOUNDS.put(Direction.UP,    Block.box( 0.0D, 15.0D,  0.0D, 16.0D, 16.0D, 16.0D));
    BOUNDS.put(Direction.DOWN,  Block.box( 0.0D,  0.0D,  0.0D, 16.0D,  1.0D, 16.0D));
    BOUNDS.put(Direction.NORTH, Block.box( 0.0D,  0.0D,  0.0D, 16.0D, 16.0D,  1.0D));
    BOUNDS.put(Direction.SOUTH, Block.box( 0.0D,  0.0D, 15.0D, 16.0D, 16.0D, 16.0D));
    BOUNDS.put(Direction.EAST,  Block.box(15.0D,  0.0D,  0.0D, 16.0D, 16.0D, 16.0D));
    BOUNDS.put(Direction.WEST,  Block.box( 0.0D,  0.0D,  0.0D,  1.0D, 16.0D, 16.0D));
  }

  @SuppressWarnings("deprecation")
  @Override
  public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    return Objects.requireNonNull(BOUNDS.get(state.getValue(FACING)));
  }

  @SuppressWarnings("deprecation")
  @Override
  public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    return Shapes.empty();
  }

  @SuppressWarnings("deprecation")
  @Override
  public BlockState rotate(BlockState state, Rotation rot) {
    return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
  }

  @SuppressWarnings("deprecation")
  @Override
  public BlockState mirror(BlockState state, Mirror mirrorIn) {
    return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING);
  }

  @SuppressWarnings("deprecation")
  @Override
  public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean p_220069_6_) {
    if (!this.canBlockStay(worldIn, pos, state.getValue(FACING))) {
      worldIn.removeBlock(pos, false);
    }

    super.neighborChanged(state, worldIn, pos, blockIn, fromPos, p_220069_6_);
  }

  /**
   * Determines if a block side can contain a glow.
   * @param world   World instance
   * @param pos     Position
   * @param facing  Side of the update
   * @return true if the block side is solid and the block at the given BlockPos is not a liquid
   */
  protected boolean canBlockStay(Level world, BlockPos pos, Direction facing) {
    BlockPos placedOn = pos.relative(facing);

    boolean isSolidSide = Block.isFaceFull(world.getBlockState(placedOn).getOcclusionShape(world, pos), facing.getOpposite());
    boolean isLiquid = world.getBlockState(pos).getBlock() instanceof LiquidBlock;

    return !isLiquid && isSolidSide;
  }

  /**
   * Adds a glow block at the given location
   * @param world      World instance
   * @param pos        Position
   * @param direction  Preferred direction, may reorient
   * @return  True if a block was placed
   */
  public boolean addGlow(Level world, BlockPos pos, Direction direction) {
    // only place the block if the current block at the location is replaceable (eg, air, tall grass, etc.)
    BlockState state = world.getBlockState(pos);
    if (state.getBlock() != this && state.canBeReplaced()) {
      // if the location is valid, place the block directly
      if (this.canBlockStay(world, pos, direction)) {
        if (!world.isClientSide) {
          world.setBlockAndUpdate(pos, this.defaultBlockState().setValue(FACING, direction));
        }
        return true;
      } else {
        for (Direction direction1 : Direction.values()) {
          if (this.canBlockStay(world, pos, direction1)) {
            if (!world.isClientSide) {
              world.setBlockAndUpdate(pos, this.defaultBlockState().setValue(FACING, direction1));
            }
            return true;
          }
        }
      }
    }

    return false;
  }

}

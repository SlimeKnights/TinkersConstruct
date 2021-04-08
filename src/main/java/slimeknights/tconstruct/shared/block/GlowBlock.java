package slimeknights.tconstruct.shared.block;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTables;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class GlowBlock extends Block {

  public static final DirectionProperty FACING = Properties.FACING;

  public GlowBlock(Settings properties) {
    super(properties);
    this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.DOWN));
    this.lootTableId = LootTables.EMPTY;
  }

  @Override
  public void addStacksForDisplay(ItemGroup group, DefaultedList<ItemStack> items) {
  }

  private static final ImmutableMap<Direction, VoxelShape> BOUNDS;

  static {
    ImmutableMap.Builder<Direction, VoxelShape> builder = ImmutableMap.builder();
    builder.put(Direction.UP, Block.createCuboidShape(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D));
    builder.put(Direction.DOWN, Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D));
    builder.put(Direction.NORTH, Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D));
    builder.put(Direction.SOUTH, Block.createCuboidShape(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D));
    builder.put(Direction.EAST, Block.createCuboidShape(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D));
    builder.put(Direction.WEST, Block.createCuboidShape(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D));

    BOUNDS = builder.build();
  }

  @Deprecated
  @Override
  public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
    return BOUNDS.get(state.get(FACING));
  }

  @Override
  @Deprecated
  public VoxelShape getCollisionShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
    return VoxelShapes.empty();
  }

  @Deprecated
  @Override
  public BlockRenderType getRenderType(BlockState state) {
    return BlockRenderType.INVISIBLE;
  }

  @Deprecated
  @Override
  public BlockState rotate(BlockState state, BlockRotation rot) {
    return state.with(FACING, rot.rotate(state.get(FACING)));
  }

  @Deprecated
  @Override
  public BlockState mirror(BlockState state, BlockMirror mirrorIn) {
    return state.rotate(mirrorIn.getRotation(state.get(FACING)));
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    builder.add(FACING);
  }

  @Deprecated
  @Override
  public void neighborUpdate(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean p_220069_6_) {
    if (!this.canBlockStay(worldIn, pos, state.get(FACING))) {
      worldIn.removeBlock(pos, false);
    }

    super.neighborUpdate(state, worldIn, pos, blockIn, fromPos, p_220069_6_);
  }

  /**
   * Determines if a block side can contain a glow.
   * @param world   World instance
   * @param pos     Position
   * @param facing  Side of the update
   * @return true if the block side is solid and the block at the given BlockPos is not a liquid
   */
  protected boolean canBlockStay(World world, BlockPos pos, Direction facing) {
    BlockPos placedOn = pos.offset(facing);

    boolean isSolidSide = Block.isFaceFullSquare(world.getBlockState(placedOn).getSidesShape(world, pos), facing.getOpposite());
    boolean isLiquid = world.getBlockState(pos).getBlock() instanceof FluidBlock;

    return !isLiquid && isSolidSide;
  }

  /**
   * Adds a glow block at the given location
   * @param world      World instance
   * @param pos        Position
   * @param direction  Preferred direction, may reorient
   * @return  True if a block was placed
   */
  public boolean addGlow(World world, BlockPos pos, Direction direction) {
    // only place the block if the current block at the location is replaceable (eg, air, tall grass, etc.)
    if (world.getBlockState(pos).getMaterial().isReplaceable()) {
      // if the location is valid, place the block directly
      if (this.canBlockStay(world, pos, direction)) {
        if (!world.isClient) {
          world.setBlockState(pos, this.getDefaultState().with(FACING, direction));
        }
        return true;
      } else {
        for (Direction direction1 : Direction.values()) {
          if (this.canBlockStay(world, pos, direction1)) {
            if (!world.isClient) {
              world.setBlockState(pos, this.getDefaultState().with(FACING, direction1));
            }
            return true;
          }
        }
      }
    }

    return false;
  }

}

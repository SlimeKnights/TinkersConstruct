package slimeknights.tconstruct.shared.block;

import com.google.common.collect.ImmutableMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTables;

public class GlowBlock extends Block {

  public static final DirectionProperty FACING = BlockStateProperties.FACING;

  public GlowBlock(Properties properties) {
    super(properties);
    this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.DOWN));
  }

  @Override
  public ResourceLocation getLootTable() {
    return LootTables.EMPTY;
  }

  @Override
  public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
  }

  private static final ImmutableMap<Direction, VoxelShape> BOUNDS;

  static {
    ImmutableMap.Builder<Direction, VoxelShape> builder = ImmutableMap.builder();
    builder.put(Direction.UP, Block.makeCuboidShape(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D));
    builder.put(Direction.DOWN, Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D));
    builder.put(Direction.NORTH, Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D));
    builder.put(Direction.SOUTH, Block.makeCuboidShape(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D));
    builder.put(Direction.EAST, Block.makeCuboidShape(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D));
    builder.put(Direction.WEST, Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D));

    BOUNDS = builder.build();
  }

  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    return BOUNDS.get(state.get(FACING));
  }

  @Override
  @Deprecated
  public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    return VoxelShapes.empty();
  }

  @Override
  public BlockRenderLayer getRenderLayer() {
    return BlockRenderLayer.TRANSLUCENT;
  }

  /**
   * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
   * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
   * @deprecated call via {@link BlockState#getRenderType()} whenever possible. Implementing/overriding is fine.
   */
  @Override
  public BlockRenderType getRenderType(BlockState state) {
    return BlockRenderType.INVISIBLE;
  }

  /**
   * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
   * blockstate.
   * @deprecated call via {@link BlockState#rotate(Rotation)} whenever possible. Implementing/overriding is
   * fine.
   */
  @Override
  public BlockState rotate(BlockState state, Rotation rot) {
    return state.with(FACING, rot.rotate(state.get(FACING)));
  }

  /**
   * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
   * blockstate.
   * @deprecated call via {@link BlockState#mirror(Mirror)} whenever possible. Implementing/overriding is fine.
   */
  @Override
  public BlockState mirror(BlockState state, Mirror mirrorIn) {
    return state.rotate(mirrorIn.toRotation(state.get(FACING)));
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(FACING);
  }

  @Override
  public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean p_220069_6_) {
    if (!this.canBlockStay(worldIn, pos, state.get(FACING))) {
      worldIn.removeBlock(pos, false);
    }

    super.neighborChanged(state, worldIn, pos, blockIn, fromPos, p_220069_6_);
  }

  /**
   *  Determines if a block side can contain a glow.
   *  Returns true if the block side is solid and the block at the given BlockPos is not a liquid
   */
  protected boolean canBlockStay(World world, BlockPos pos, Direction facing) {
    BlockPos placedOn = pos.offset(facing);

    boolean isSolidSide = Block.hasSolidSide(world.getBlockState(placedOn), world, placedOn, facing.getOpposite());
    boolean isLiquid = world.getBlockState(pos).getBlock() instanceof FlowingFluidBlock;

    return !isLiquid && isSolidSide;
  }

  public boolean addGlow(World world, BlockPos pos, Direction direction) {
    // only place the block if the current block at the location is replacable (eg, air, tall grass, etc.)
    BlockState oldState = world.getBlockState(pos);

    if (oldState.getBlock().getMaterial(oldState).isReplaceable()) {
      // if the location is valid, place the block directly
      if (this.canBlockStay(world, pos, direction)) {
        if (!world.isRemote) {
          world.setBlockState(pos, this.getDefaultState().with(FACING, direction));
        }
        return true;
      }
      else {
        for (Direction direction1 : Direction.values()) {
          if (this.canBlockStay(world, pos, direction1)) {
            if (!world.isRemote) {
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

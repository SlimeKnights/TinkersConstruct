package slimeknights.tconstruct.smeltery.block.component;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import slimeknights.mantle.block.InventoryBlock;
import slimeknights.mantle.util.TileEntityHelper;
import slimeknights.tconstruct.smeltery.tileentity.component.DuctTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.component.SmelteryComponentTileEntity;

import javax.annotation.Nullable;

/** Filtering drain block, have to reimplement either inventory block logic or seared block logic unfortunately */
public class SearedDuctBlock extends InventoryBlock {
  public static final BooleanProperty ACTIVE = SearedBlock.IN_STRUCTURE;
  public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
  public SearedDuctBlock(Properties properties) {
    super(properties);
    this.setDefaultState(this.getDefaultState().with(ACTIVE, false));
  }

  @Override
  public TileEntity createTileEntity(BlockState blockState, IBlockReader iBlockReader) {
    return new DuctTileEntity();
  }


  /* Seared block interaction */

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
    if (!newState.matchesBlock(this)) {
      TileEntityHelper.getTile(SmelteryComponentTileEntity.class, worldIn, pos).ifPresent(te -> te.notifyMasterOfChange(pos, newState));
    }
    super.onReplaced(state, worldIn, pos, newState, isMoving);
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    SmelteryComponentTileEntity.updateNeighbors(world, pos, state);
  }


  /* Orientation */

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block,BlockState> builder) {
    builder.add(ACTIVE, FACING);
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public BlockState rotate(BlockState state, Rotation rotation) {
    return state.with(FACING, rotation.rotate(state.get(FACING)));
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public BlockState mirror(BlockState state, Mirror mirror) {
    return state.with(FACING, mirror.mirror(state.get(FACING)));
  }
}

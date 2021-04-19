package slimeknights.tconstruct.smeltery.block.component;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import slimeknights.mantle.util.TileEntityHelper;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryComponentTileEntity;

import org.jetbrains.annotations.Nullable;

public class SearedBlock extends Block implements BlockEntityProvider {
  public static final BooleanProperty IN_STRUCTURE = BooleanProperty.of("in_structure");

  public SearedBlock(Settings properties) {
    super(properties);
    this.setDefaultState(this.getDefaultState().with(IN_STRUCTURE, false));
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    builder.add(IN_STRUCTURE);
  }

  @Override
  public BlockEntity createBlockEntity(BlockView world) {
    return new SmelteryComponentTileEntity();
  }

  @Override
  @Deprecated
  public void onStateReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
    if (!newState.isOf(this)) {
      TileEntityHelper.getTile(SmelteryComponentTileEntity.class, worldIn, pos).ifPresent(te -> te.notifyMasterOfChange(pos, newState));
    }
    super.onStateReplaced(state, worldIn, pos, newState, isMoving);
  }

  @Override
  public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    SmelteryComponentTileEntity.updateNeighbors(world, pos, state);
  }

  @Override
  @Deprecated
  public boolean onSyncedBlockEvent(BlockState state, World worldIn, BlockPos pos, int id, int param) {
    super.onSyncedBlockEvent(state, worldIn, pos, id, param);
    BlockEntity tileentity = worldIn.getBlockEntity(pos);
    return tileentity != null && tileentity.onSyncedBlockEvent(id, param);
  }
}

package slimeknights.tconstruct.smeltery.block.component;

import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import slimeknights.mantle.util.TileEntityHelper;
import slimeknights.tconstruct.smeltery.tileentity.component.SmelteryComponentTileEntity;

import javax.annotation.Nullable;

// TODO: reassess need
public class SearedSlabBlock extends SlabBlock {

  public SearedSlabBlock(Properties properties) {
    super(properties);
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new SmelteryComponentTileEntity();
  }

  @Override
  @Deprecated
  public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
    if (!newState.matchesBlock(this)) {
      TileEntityHelper.getTile(SmelteryComponentTileEntity.class, worldIn, pos).ifPresent(te -> te.notifyMasterOfChange(pos, newState));
    }
    super.onReplaced(state, worldIn, pos, newState, isMoving);
  }

  @Override
  public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    SmelteryComponentTileEntity.updateNeighbors(worldIn, pos, state);
  }

  @Override
  @Deprecated
  public boolean eventReceived(BlockState state, World worldIn, BlockPos pos, int id, int param) {
    super.eventReceived(state, worldIn, pos, id, param);
    TileEntity tileentity = worldIn.getTileEntity(pos);
    return tileentity != null && tileentity.receiveClientEvent(id, param);
  }
}

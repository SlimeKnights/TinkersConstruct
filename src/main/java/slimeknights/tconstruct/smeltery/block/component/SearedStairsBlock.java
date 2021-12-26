package slimeknights.tconstruct.smeltery.block.component;

import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import slimeknights.mantle.util.TileEntityHelper;
import slimeknights.tconstruct.smeltery.tileentity.component.SmelteryComponentTileEntity;

import javax.annotation.Nullable;
import java.util.function.Supplier;

// TODO: reassess the need
import net.minecraft.block.AbstractBlock.Properties;

public class SearedStairsBlock extends StairsBlock {

  public SearedStairsBlock(Supplier<BlockState> state, Properties properties) {
    super(state, properties);
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
  public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
    if (!newState.is(this)) {
      TileEntityHelper.getTile(SmelteryComponentTileEntity.class, worldIn, pos).ifPresent(te -> te.notifyMasterOfChange(pos, newState));
    }
    super.onRemove(state, worldIn, pos, newState, isMoving);
  }

  @Override
  public void setPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    SmelteryComponentTileEntity.updateNeighbors(world, pos, state);
  }

  @Override
  @Deprecated
  public boolean triggerEvent(BlockState state, World worldIn, BlockPos pos, int id, int param) {
    super.triggerEvent(state, worldIn, pos, id, param);

    TileEntity tileentity = worldIn.getBlockEntity(pos);

    return tileentity != null && tileentity.triggerEvent(id, param);
  }
}

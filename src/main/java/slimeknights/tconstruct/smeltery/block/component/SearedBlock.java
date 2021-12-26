package slimeknights.tconstruct.smeltery.block.component;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import slimeknights.mantle.util.TileEntityHelper;
import slimeknights.tconstruct.smeltery.tileentity.component.SmelteryComponentTileEntity;

import javax.annotation.Nullable;

import net.minecraft.block.AbstractBlock.Properties;

public class SearedBlock extends Block {
  public static final BooleanProperty IN_STRUCTURE = BooleanProperty.create("in_structure");

  public SearedBlock(Properties properties) {
    super(properties);
    this.registerDefaultState(this.defaultBlockState().setValue(IN_STRUCTURE, false));
  }

  @Override
  protected void createBlockStateDefinition(Builder<Block,BlockState> builder) {
    builder.add(IN_STRUCTURE);
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

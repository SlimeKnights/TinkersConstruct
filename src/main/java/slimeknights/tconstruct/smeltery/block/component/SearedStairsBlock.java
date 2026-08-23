package slimeknights.tconstruct.smeltery.block.component;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.smeltery.block.entity.component.SmelteryComponentBlockEntity;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class SearedStairsBlock extends StairBlock implements EntityBlock {

  public SearedStairsBlock(Supplier<BlockState> state, Properties properties) {
    super(state, properties);
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
    return new SmelteryComponentBlockEntity(pPos, pState);
  }

  @Override
  @Deprecated
  public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
    if (!newState.is(this) && world.getBlockEntity(pos) instanceof SmelteryComponentBlockEntity te) {
      te.notifyMasterOfChange(pos, newState);
    }
    super.onRemove(state, world, pos, newState, isMoving);
  }

  @Override
  public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    SmelteryComponentBlockEntity.updateNeighbors(world, pos, state);
  }

  @Override
  @Deprecated
  public boolean triggerEvent(BlockState state, Level worldIn, BlockPos pos, int id, int param) {
    super.triggerEvent(state, worldIn, pos, id, param);

    BlockEntity tileentity = worldIn.getBlockEntity(pos);

    return tileentity != null && tileentity.triggerEvent(id, param);
  }
}

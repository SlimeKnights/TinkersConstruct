package slimeknights.tconstruct.tables.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.tables.block.entity.table.PartBuilderBlockEntity;

import javax.annotation.Nullable;

public class PartBuilderBlock extends RetexturedTableBlock {

  public PartBuilderBlock(Properties builder) {
    super(builder);
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
    return new PartBuilderBlockEntity(pPos, pState);
  }
}

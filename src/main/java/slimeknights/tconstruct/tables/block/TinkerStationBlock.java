package slimeknights.tconstruct.tables.block;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.tables.block.entity.table.TinkerStationBlockEntity;

import javax.annotation.Nullable;

public class TinkerStationBlock extends RetexturedTableBlock {
  @Getter
  private final int slotCount;

  public TinkerStationBlock(Properties builder, int slotCount) {
    super(builder);
    this.slotCount = slotCount;
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
    return new TinkerStationBlockEntity(pPos, pState, getSlotCount());
  }
}

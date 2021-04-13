package slimeknights.tconstruct.tables.block.table;

import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import slimeknights.tconstruct.tables.tileentity.table.tinkerstation.TinkerStationTileEntity;

public class TinkerStationBlock extends RetexturedTableBlock {
  @Getter
  private final int slotCount;

  public TinkerStationBlock(Settings builder, int slotCount) {
    super(builder);
    this.slotCount = slotCount;
  }

//  @Override
//  public BlockEntity createTileEntity(BlockState blockState, BlockView iBlockReader) {
//    return new TinkerStationTileEntity(getSlotCount());
//  }
}

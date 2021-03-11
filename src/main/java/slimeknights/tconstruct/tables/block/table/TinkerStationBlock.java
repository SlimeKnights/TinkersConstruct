package slimeknights.tconstruct.tables.block.table;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import slimeknights.tconstruct.tables.block.TableSortKeys;
import slimeknights.tconstruct.tables.tileentity.table.tinkerstation.TinkerStationTileEntity;

public class TinkerStationBlock extends RetexturedTableBlock {

  public TinkerStationBlock(Properties builder) {
    super(builder);
  }

  @Override
  public TileEntity createTileEntity(BlockState blockState, IBlockReader iBlockReader) {
    return new TinkerStationTileEntity();
  }

  @Override
  public int getSortKey() {
    return TableSortKeys.TINKER_STATION;
  }
}

package slimeknights.tconstruct.tables.block.table;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import slimeknights.tconstruct.tables.block.TableTypes;
import slimeknights.tconstruct.tables.block.TinkerTableBlock;
import slimeknights.tconstruct.tables.tileentity.table.ToolStationTileEntity;

import javax.annotation.Nonnull;

public class ToolStationBlock extends TinkerTableBlock {

  public ToolStationBlock(Properties builder) {
    super(builder);
  }

  @Nonnull
  @Override
  public TileEntity createTileEntity(BlockState blockState, IBlockReader iBlockReader) {
    return new ToolStationTileEntity();
  }

  @Override
  public TableTypes getType() {
    return TableTypes.ToolStation;
  }
}

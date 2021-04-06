package slimeknights.tconstruct.tables.block.table;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import slimeknights.tconstruct.tables.tileentity.table.PartBuilderTileEntity;

import javax.annotation.Nonnull;

public class PartBuilderBlock extends RetexturedTableBlock {

  public PartBuilderBlock(Properties builder) {
    super(builder);
  }

  @Nonnull
  @Override
  public TileEntity createTileEntity(BlockState blockState, IBlockReader iBlockReader) {
    return new PartBuilderTileEntity();
  }
}

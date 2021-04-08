package slimeknights.tconstruct.tables.block.table;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import slimeknights.tconstruct.tables.tileentity.table.PartBuilderTileEntity;

import javax.annotation.Nonnull;

public class PartBuilderBlock extends RetexturedTableBlock {

  public PartBuilderBlock(Settings builder) {
    super(builder);
  }

  @Nonnull
  @Override
  public BlockEntity createTileEntity(BlockState blockState, BlockView iBlockReader) {
    return new PartBuilderTileEntity();
  }
}

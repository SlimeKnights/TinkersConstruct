package slimeknights.tconstruct.tables.block.table;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import slimeknights.tconstruct.tables.tileentity.table.CraftingStationTileEntity;

import org.jetbrains.annotations.NotNull;

public class CraftingStationBlock extends RetexturedTableBlock {

  public CraftingStationBlock(Settings builder) {
    super(builder);
  }

  @NotNull
  @Override
  public BlockEntity createTileEntity(BlockState blockState, BlockView iBlockReader) {
    return new CraftingStationTileEntity();
  }
}

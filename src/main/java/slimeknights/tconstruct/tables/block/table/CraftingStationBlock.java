package slimeknights.tconstruct.tables.block.table;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.tables.tileentity.table.CraftingStationTileEntity;

import javax.annotation.Nullable;

public class CraftingStationBlock extends RetexturedTableBlock {

  public CraftingStationBlock(Properties builder) {
    super(builder);
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
    return new CraftingStationTileEntity(pPos, pState);
  }
}

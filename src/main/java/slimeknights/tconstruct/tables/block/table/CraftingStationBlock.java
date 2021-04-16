package slimeknights.tconstruct.tables.block.table;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.tables.tileentity.table.CraftingStationTileEntity;

import org.jetbrains.annotations.NotNull;

public class CraftingStationBlock extends RetexturedTableBlock implements BlockEntityProvider {

  public CraftingStationBlock(Settings builder) {
    super(builder);
  }

  @Nullable
  @Override
  public BlockEntity createBlockEntity(BlockView world) {
    return new CraftingStationTileEntity();
  }
}

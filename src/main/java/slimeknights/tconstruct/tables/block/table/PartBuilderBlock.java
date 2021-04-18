package slimeknights.tconstruct.tables.block.table;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.tables.tileentity.table.PartBuilderTileEntity;

import org.jetbrains.annotations.NotNull;

public class PartBuilderBlock extends RetexturedTableBlock implements BlockEntityProvider {

  public PartBuilderBlock(Settings builder) {
    super(builder);
  }

  @Nullable
  @Override
  public BlockEntity createBlockEntity(BlockView world) {
    return new PartBuilderTileEntity();
  }
}

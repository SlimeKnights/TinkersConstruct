package slimeknights.tconstruct.tables.block.table;

import lombok.Getter;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.tables.tileentity.table.tinkerstation.TinkerStationTileEntity;

public class TinkerStationBlock extends RetexturedTableBlock implements BlockEntityProvider {
  @Getter
  private final int slotCount;

  public TinkerStationBlock(Settings builder, int slotCount) {
    super(builder);
    this.slotCount = slotCount;
  }

  @Nullable
  @Override
  public BlockEntity createBlockEntity(BlockView world) {
    return new TinkerStationTileEntity(getSlotCount());
  }
}

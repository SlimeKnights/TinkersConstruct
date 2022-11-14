package slimeknights.tconstruct.tables.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/** Generic block shared by any that don't need special stuff on top */
public class GenericTableBlock extends RetexturedTableBlock {
  private final BlockEntitySupplier<? extends BlockEntity> blockEntity;
  public GenericTableBlock(Properties builder, BlockEntitySupplier<? extends BlockEntity> blockEntity) {
    super(builder);
    this.blockEntity = blockEntity;
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return blockEntity.create(pos, state);
  }
}

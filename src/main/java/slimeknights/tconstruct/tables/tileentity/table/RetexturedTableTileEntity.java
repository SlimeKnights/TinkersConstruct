package slimeknights.tconstruct.tables.tileentity.table;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.LazyValue;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.model.data.IModelData;
import slimeknights.mantle.tileentity.IRetexturedTileEntity;
import slimeknights.mantle.util.RetexturedHelper;
import slimeknights.tconstruct.shared.tileentity.TableTileEntity;

public abstract class RetexturedTableTileEntity extends TableTileEntity implements IRetexturedTileEntity {
  private final LazyValue<IModelData> data = new LazyValue<>(this::getRetexturedModelData);
  public RetexturedTableTileEntity(TileEntityType<?> type, String name, int size) {
    super(type, name, size);
  }

  @Override
  public IModelData getModelData() {
    return this.data.get();
  }

  @Override
  public AxisAlignedBB getRenderBoundingBox() {
    return new AxisAlignedBB(worldPosition, worldPosition.offset(1, 2, 1));
  }

  @Override
  public void load(BlockState blockState, CompoundNBT tags) {
    String oldName = getTextureName();
    super.load(blockState, tags);
    String newName = getTextureName();
    // if the texture name changed, mark the position for rerender
    if (!oldName.equals(newName) && level != null && level.isClientSide) {
      data.get().setData(RetexturedHelper.BLOCK_PROPERTY, getTexture());
      requestModelDataUpdate();
      level.sendBlockUpdated(worldPosition, blockState, blockState, 0);
    }
  }
}

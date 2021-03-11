package slimeknights.tconstruct.tables.tileentity.table;

import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.LazyValue;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.model.data.IModelData;
import slimeknights.mantle.tileentity.IRetexturedTileEntity;
import slimeknights.tconstruct.shared.tileentity.TableTileEntity;

public abstract class RetexturedTableTileEntity extends TableTileEntity implements IRetexturedTileEntity {
  private final LazyValue<IModelData> data = new LazyValue<>(this::getRetexturedModelData);
  public RetexturedTableTileEntity(TileEntityType<?> type, String name, int size) {
    super(type, name, size);
  }

  @Override
  public IModelData getModelData() {
    return this.data.getValue();
  }

  @Override
  public AxisAlignedBB getRenderBoundingBox() {
    return new AxisAlignedBB(pos, pos.add(1, 2, 1));
  }
}

package slimeknights.tconstruct.tables.tileentity.table;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Lazy;
import net.minecraft.util.math.Box;
import net.minecraftforge.client.model.data.IModelData;
import slimeknights.mantle.tileentity.IRetexturedTileEntity;
import slimeknights.tconstruct.shared.tileentity.TableTileEntity;

public abstract class RetexturedTableTileEntity extends TableTileEntity implements IRetexturedTileEntity {
  private final Lazy<IModelData> data = new Lazy<>(this::getRetexturedModelData);
  public RetexturedTableTileEntity(BlockEntityType<?> type, String name, int size) {
    super(type, name, size);
  }

  @Override
  public IModelData getModelData() {
    return this.data.get();
  }

  @Override
  public Box getRenderBoundingBox() {
    return new Box(pos, pos.add(1, 2, 1));
  }
}

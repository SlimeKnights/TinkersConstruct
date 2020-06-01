package slimeknights.tconstruct.tables.client.model;

import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nullable;

public class TableModelData implements IModelData {

  @Override
  public boolean hasProperty(ModelProperty<?> prop) {
    return false;
  }

  @Nullable
  @Override
  public <T> T getData(ModelProperty<T> prop) {
    return null;
  }

  @Nullable
  @Override
  public <T> T setData(ModelProperty<T> prop, T data) {
    return null;
  }
}

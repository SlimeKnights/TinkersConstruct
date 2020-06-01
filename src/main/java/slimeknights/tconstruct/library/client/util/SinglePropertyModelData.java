package slimeknights.tconstruct.library.client.util;

import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nullable;

public class SinglePropertyModelData<T> implements IModelData {

  private T value;
  private final ModelProperty<T> prop;

  public SinglePropertyModelData(T value, ModelProperty<T> prop) {
    this.value = value;
    this.prop = prop;
  }


  @Override
  public boolean hasProperty(ModelProperty<?> prop) {
    return prop == this.prop;
  }

  @Nullable
  @Override
  public <T2> T2 getData(ModelProperty<T2> prop) {
    if (hasProperty(prop))
      return (T2) value;
    return null;
  }

  @Nullable
  @Override
  public <T2> T2 setData(ModelProperty<T2> prop, T2 data) {
    if (hasProperty(prop)) {
      value = (T) data;
      return data;
    } else
      return null;
  }
}

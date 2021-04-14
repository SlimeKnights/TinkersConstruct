package slimeknights.tconstruct.library.client.model;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.model.IModelData;
import slimeknights.mantle.util.ModelProperty;

public class SinglePropertyData<D> implements IModelData {
  private final ModelProperty<D> property;
  private D data = null;

  public SinglePropertyData(ModelProperty<D> property, D data) {
    Preconditions.checkArgument(property.test(data), "Value is invalid for this property");
    this.property = property;
    this.data = data;
  }

  public boolean hasProperty(ModelProperty<?> prop) {
    return prop == this.property;
  }

  @Nullable
  public <T> T getData(ModelProperty<T> prop) {
    return prop == this.property ? (T) this.data : null;
  }

  @Nullable
  public <T> T setData(ModelProperty<T> prop, T data) {
    Preconditions.checkArgument(prop.test(data), "Value is invalid for this property");
    if (prop == this.property) {
      this.data = (D) data;
      return data;
    } else {
      return null;
    }
  }

  public SinglePropertyData(ModelProperty<D> property) {
    this.property = property;
  }
}

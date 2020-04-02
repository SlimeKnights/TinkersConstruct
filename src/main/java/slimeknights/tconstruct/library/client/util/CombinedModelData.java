package slimeknights.tconstruct.library.client.util;

import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nullable;

public class CombinedModelData implements IModelData {

  private final IModelData[] subData;

  public CombinedModelData(IModelData... data) {
    this.subData = data;
  }

  @Override
  public boolean hasProperty(ModelProperty<?> prop) {
    for (IModelData d : this.subData)
      if (d.hasProperty(prop))
        return true;
    return false;
  }

  @Nullable
  @Override
  public <T2> T2 getData(ModelProperty<T2> prop) {
    for (IModelData d : this.subData) {
      if (d.hasProperty(prop)) {
        return d.getData(prop);
      }
    }
    return null;
  }

  @Nullable
  @Override
  public <T2> T2 setData(ModelProperty<T2> prop, T2 data) {
    //TODO implement
    for (IModelData d : this.subData) {
      if (d.hasProperty(prop)) {
        d.setData(prop, data);
      }
    }
    return data;
  }
}

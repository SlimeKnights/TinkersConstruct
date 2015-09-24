package slimeknights.mantle.property;

import com.google.common.collect.Lists;

import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.common.property.IUnlistedProperty;

import java.util.List;

public class PropertyTableItem implements IUnlistedProperty<PropertyTableItem.TableItems> {

  @Override
  public String getName() {
    return "TableItems";
  }

  @Override
  public boolean isValid(TableItems value) {
    return value != null && value.items != null;
  }

  @Override
  public Class<PropertyTableItem.TableItems> getType() {
    return TableItems.class;
  }

  @Override
  public String valueToString(PropertyTableItem.TableItems value) {
    return value.toString();
  }

  public static class TableItems {
    public List<TableItem> items = Lists.newLinkedList();
  }

  public static class TableItem {
    public final IFlexibleBakedModel model;
    public float x,y,z;
    public float s;
    public float r;

    public TableItem(IFlexibleBakedModel model) {
      this(model, 0,0,0);
    }

    public TableItem(IFlexibleBakedModel model, float x, float y, float z) {
      this(model, x,y,z, 1, 0);
    }

    public TableItem(IFlexibleBakedModel model, float x, float y, float z, float s, float r) {
      this.model = model;
      this.x = x;
      this.y = y;
      this.z = z;
      this.s = s;
      this.r = r;
    }
  }
}

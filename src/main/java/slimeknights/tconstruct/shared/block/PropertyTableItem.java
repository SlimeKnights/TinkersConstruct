package slimeknights.tconstruct.shared.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;
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

    public static final TableItems EMPTY = new TableItems();

    static {
      EMPTY.items = ImmutableList.of();
    }

    public List<TableItem> items = Lists.newLinkedList();
  }

  public static class TableItem {

    public final ItemStack stack;
    public final IBakedModel model;
    public float x, y, z;
    public float s;
    public float r;

    public TableItem(ItemStack stack, IBakedModel model) {
      this(stack, model, 0, 0, 0);
    }

    public TableItem(ItemStack stack, IBakedModel model, float x, float y, float z) {
      this(stack, model, x, y, z, 1, 0);
    }

    public TableItem(ItemStack stack, IBakedModel model, float x, float y, float z, float s, float r) {
      this.stack = stack;
      this.model = model;
      this.x = x;
      this.y = y;
      this.z = z;
      this.s = s;
      this.r = r;
    }

    @Override
    public boolean equals(Object o) {
      if(this == o) {
        return true;
      }
      if(o == null || getClass() != o.getClass()) {
        return false;
      }

      TableItem tableItem = (TableItem) o;

      if(Float.compare(tableItem.x, x) != 0) {
        return false;
      }
      if(Float.compare(tableItem.y, y) != 0) {
        return false;
      }
      if(Float.compare(tableItem.z, z) != 0) {
        return false;
      }
      if(Float.compare(tableItem.s, s) != 0) {
        return false;
      }
      if(Float.compare(tableItem.r, r) != 0) {
        return false;
      }
      return stack != null ? ItemStack.areItemStacksEqual(stack, tableItem.stack) : tableItem.stack == null;
    }

    @Override
    public int hashCode() {
      int result = 0;
      if(stack != null) {
        result = stack.getItem().hashCode();
        result = 31 * result + (stack.getCount());
        result = 31 * result + (stack.getTagCompound() != null ? stack.getTagCompound().hashCode() : 0);
      }
      result = 31 * result + (x != +0.0f ? Float.floatToIntBits(x) : 0);
      result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
      result = 31 * result + (z != +0.0f ? Float.floatToIntBits(z) : 0);
      result = 31 * result + (s != +0.0f ? Float.floatToIntBits(s) : 0);
      result = 31 * result + (r != +0.0f ? Float.floatToIntBits(r) : 0);
      return result;
    }
  }
}

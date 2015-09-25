package slimeknights.mantle.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemMetaDynamic extends ItemMeta {
  private static int MAX = Long.SIZE;

  protected long availabilityMask;

  public ItemMetaDynamic() {
    super(0);

  }

  public void addMeta(int meta) {
    if(meta > MAX) {
      throw new IllegalArgumentException(String.format("Metadata too high, highest supported value is %d. Meta was %d", MAX, meta));
    }
    if(meta > maxMeta) {
      maxMeta = meta;
      setValid(meta);
    }
  }

  @Override
  public void getSubItems(Item itemIn, CreativeTabs tab, List subItems) {
    for(int i = 0; i <= maxMeta; i++) {
      if(isValid(i)) {
        subItems.add(new ItemStack(itemIn, 1, i));
      }
    }
  }

  @Override
  public int getMetadata(int damage) {
    int meta = super.getMetadata(damage);
    return isValid(meta) ? meta : 0;
  }

  protected void setValid(int meta) {
    availabilityMask |= 1 << meta;
  }

  protected boolean isValid(int meta) {
    if(meta > MAX) {
      return false;
    }
    return ((availabilityMask >> meta) & 1) == 1;
  }
}

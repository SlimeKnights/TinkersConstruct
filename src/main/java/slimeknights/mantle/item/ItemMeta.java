package slimeknights.mantle.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import java.util.List;

public class ItemMeta extends Item {

  private int maxMeta;

  public ItemMeta(int maxMeta) {
    setHasSubtypes(true);
    this.maxMeta = maxMeta;
  }

  @Override
  public void getSubItems(Item itemIn, CreativeTabs tab, List subItems) {
    super.getSubItems(itemIn, tab, subItems);
  }

  @Override
  public int getMetadata(int damage) {
    if(damage < 0 || damage > maxMeta)
      return 0;

    return damage;
  }
}

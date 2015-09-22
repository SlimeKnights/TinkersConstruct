package slimeknights.mantle.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemMeta extends Item {

  private int maxMeta;

  public ItemMeta(int maxMeta) {
    setHasSubtypes(true);
    this.maxMeta = maxMeta;
  }

  @Override
  public void getSubItems(Item itemIn, CreativeTabs tab, List subItems) {
    for(int i = 0; i < maxMeta; i++) {
      subItems.add(new ItemStack(itemIn, 1, i));
    }
  }

  @Override
  public int getMetadata(int damage) {
    if(damage < 0 || damage > maxMeta)
      return 0;

    return damage;
  }
}

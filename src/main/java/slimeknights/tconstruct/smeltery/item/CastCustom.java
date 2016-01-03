package slimeknights.tconstruct.smeltery.item;

import gnu.trove.map.hash.TIntIntHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

import slimeknights.mantle.item.ItemMetaDynamic;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.smeltery.ICast;
import slimeknights.tconstruct.library.tools.IToolPart;

public class CastCustom extends ItemMetaDynamic implements ICast {

  protected TIntIntHashMap values = new TIntIntHashMap();

  public CastCustom() {
    this.setCreativeTab(TinkerRegistry.tabSmeltery);
  }

  public ItemStack addMeta(int meta, String name, int amount) {
    values.put(meta, amount);
    ItemStack ret = addMeta(meta, name);
    return ret;
  }

  @Override
  public ItemStack addMeta(int meta, String name) {
    if(!values.containsKey(meta)) {
      throw new RuntimeException("Usage of wrong function. Use the addMeta function that has an amount paired with it with this implementation");
    }
    return super.addMeta(meta, name);
  }

  @Override
  public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
    int meta = stack.getItemDamage();
    if(values.containsKey(meta)) {
      tooltip.add(Util.translateFormatted("tooltip.cast.cost", values.get(meta)));
    }
  }
}

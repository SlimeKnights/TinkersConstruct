package slimeknights.tconstruct.library.smeltery;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.smeltery.ICast;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.Pattern;

public class Cast extends Pattern implements ICast {

  public Cast() {
    this.setCreativeTab(TinkerRegistry.tabSmeltery);
  }

  @Override
  public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
    subItems.add(new ItemStack(this));

    for(Item toolpart : TinkerRegistry.getCastItems()) {
      ItemStack stack = new ItemStack(this);
      setTagForPart(stack, toolpart);

      subItems.add(stack);
    }
  }

  @Override
  public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
    Item part = getPartFromTag(stack);
    if(part != null && part instanceof IToolPart) {
      tooltip.add(Util.translateFormatted("tooltip.cast.cost", ((IToolPart)part).getCost()));
    }
  }
}

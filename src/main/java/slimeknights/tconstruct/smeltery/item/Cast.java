package slimeknights.tconstruct.smeltery.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.tools.Pattern;

public class Cast extends Pattern {

  @Override
  public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
    subItems.add(new ItemStack(this));

    for(Item toolpart : TinkerRegistry.getCastItems()) {
      ItemStack stack = new ItemStack(this);
      setTagForPart(stack, toolpart);

      subItems.add(stack);
    }
  }

}

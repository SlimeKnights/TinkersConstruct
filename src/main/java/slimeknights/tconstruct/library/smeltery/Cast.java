package slimeknights.tconstruct.library.smeltery;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.tools.Pattern;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

public class Cast extends Pattern implements ICast {

  public Cast() {
    this.setCreativeTab(TinkerRegistry.tabSmeltery);
  }

  @Override
  public void getSubItems(@Nonnull Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
    if(this == TinkerSmeltery.cast) {
      subItems.add(new ItemStack(this));
    }

    for(Item toolpart : TinkerRegistry.getCastItems()) {
      ItemStack stack = new ItemStack(this);
      setTagForPart(stack, toolpart);

      subItems.add(stack);
    }
  }
}

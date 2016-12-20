package slimeknights.tconstruct.shared.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import javax.annotation.Nonnull;

import slimeknights.mantle.item.ItemMetaDynamic;
import slimeknights.tconstruct.TinkerIntegration;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerFluids;

public class ItemMetaDynamicTinkers extends ItemMetaDynamic {

  @SideOnly(Side.CLIENT)
  @Override
  public void getSubItems(@Nonnull Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
    for(int i = 0; i <= availabilityMask.length; i++) {
      if(isValid(i)) {
        // prevent the addition of alubrass if it's not present
        if((this == TinkerCommons.ingots || this == TinkerCommons.nuggets) && !TinkerIntegration.isIntegrated(TinkerFluids.alubrass) && i == 5) {
          continue;
        }
        subItems.add(new ItemStack(itemIn, 1, i));
      }
    }
  }
}

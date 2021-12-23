package slimeknights.tconstruct.shared.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import slimeknights.mantle.item.ItemMetaDynamic;
import slimeknights.tconstruct.TinkerIntegration;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerFluids;

public class ItemMetaDynamicTinkers extends ItemMetaDynamic {

  @Override
  public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
    if(this.isInCreativeTab(tab)) {
      for(int i = 0; i <= availabilityMask.length; i++) {
        if(isValid(i)) {
          // prevent the addition of alubrass if it's not present
          if((this == TinkerCommons.ingots || this == TinkerCommons.nuggets) && !TinkerIntegration.isIntegrated(TinkerFluids.alubrass) && i == 5) {
            continue;
          }
          subItems.add(new ItemStack(this, 1, i));
        }
      }
    }
  }
}

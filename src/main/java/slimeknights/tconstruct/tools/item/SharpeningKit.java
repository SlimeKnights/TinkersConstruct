package slimeknights.tconstruct.tools.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

import javax.annotation.Nonnull;

import slimeknights.mantle.util.LocUtils;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tools.ToolPart;

public class SharpeningKit extends ToolPart {

  public SharpeningKit() {
    super(Material.VALUE_Shard * 3);
  }

  @Override
  public void getSubItems(@Nonnull Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
    // this adds a variant of each material to the creative menu
    for(Material mat : TinkerRegistry.getAllMaterialsWithStats(HeadMaterialStats.TYPE)) {
      subItems.add(getItemstackWithMaterial(mat));
    }
  }

  @Override
  public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
    tooltip.addAll(LocUtils.getTooltips(Util.translate("item.tconstruct.sharpening_kit.tooltip")));
    if(!checkMissingMaterialTooltip(stack, tooltip, HeadMaterialStats.TYPE)) {
      Material material = getMaterial(stack);
      HeadMaterialStats stats = material.getStats(HeadMaterialStats.TYPE);
      if(stats != null) {
        tooltip.add(HeadMaterialStats.formatHarvestLevel(stats.harvestLevel));
      }
    }
  }
}

package slimeknights.tconstruct.library.tools;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;

public class Shard extends ToolPart {

  public Shard() {
    super(Material.VALUE_Shard);
  }

  @Override
  public void getSubItems(@Nonnull Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
    // this adds a variant of each material to the creative menu
    for(Material mat : TinkerRegistry.getAllMaterials()) {
      if(mat.hasStats(MaterialTypes.HEAD) && (mat.isCraftable() || mat.isCastable())) {
        subItems.add(getItemstackWithMaterial(mat));
        if(!Config.listAllMaterials) {
          break;
        }
      }
    }
  }

  @Override
  public boolean canUseMaterial(Material mat) {
    return true;
  }

  @Override
  public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
    // no stats n stuff
  }
}

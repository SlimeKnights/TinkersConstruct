package slimeknights.mantle.client;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTab extends CreativeTabs {

  private ItemStack icon;

  // a vanilla icon in case the other one isn't present
  public CreativeTab(String label, ItemStack backupIcon) {
    super(label);

    this.icon = backupIcon;
  }

  public void setDisplayIcon(ItemStack displayIcon) {
    if(displayIcon != null && displayIcon.getItem() != null)
      this.icon = displayIcon;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public ItemStack getIconItemStack() {
    return icon;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public int getIconItemDamage() {
    return icon.getItemDamage();
  }

  @SideOnly(Side.CLIENT)
  @Override
  public Item getTabIconItem() {
    return icon.getItem();
  }
}

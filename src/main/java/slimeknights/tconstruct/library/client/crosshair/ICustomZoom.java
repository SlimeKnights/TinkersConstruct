package slimeknights.tconstruct.library.client.crosshair;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ICustomZoom {

  /** Current zoom, 1f means regular zoom */
  float getZoomLevel(ItemStack itemStack, EntityPlayer entityPlayer);
}

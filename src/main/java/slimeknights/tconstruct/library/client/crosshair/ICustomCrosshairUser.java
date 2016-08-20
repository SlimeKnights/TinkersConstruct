package slimeknights.tconstruct.library.client.crosshair;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ICustomCrosshairUser {

  /**
   * Crosshair rendering information
   * @return The crosshair to render, return DEFAULT for default crosshair
   */
  ICrosshair getCrosshair(ItemStack itemStack, EntityPlayer player);

  /**
   * Additional render info for the crosshair, usually the accuracy or charge progress.
   * In that case, 1.0 means 100% accuracy/fully charged.
   */
  float getCrosshairState(ItemStack itemStack, EntityPlayer player);
}

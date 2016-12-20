package slimeknights.tconstruct.library.client.crosshair;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ICustomCrosshairUser {

  /**
   * Crosshair rendering information
   * @return The crosshair to render, return DEFAULT for default crosshair
   */
  @SideOnly(Side.CLIENT)
  ICrosshair getCrosshair(ItemStack itemStack, EntityPlayer player);

  /**
   * Additional render info for the crosshair, usually the accuracy or charge progress.
   * In that case, 1.0 means 100% accuracy/fully charged.
   */
  @SideOnly(Side.CLIENT)
  float getCrosshairState(ItemStack itemStack, EntityPlayer player);
}

package slimeknights.tconstruct.library.utils;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/** Pseudo-copy of the vanilla flag, extended with screen context */
public enum TooltipFlag {
  /** Normal tooltip */
  NORMAL,
  /** Advanced tooltips are enabled */
  ADVANCED,
  /** Display in a station screen */
  DETAILED;

  /** Translates vanilla's client only flag into our's that exists on both sides */
  @OnlyIn(Dist.CLIENT)
  public static TooltipFlag fromVanilla(ITooltipFlag flag) {
    return flag.isAdvanced() ? ADVANCED : NORMAL;
  }
}

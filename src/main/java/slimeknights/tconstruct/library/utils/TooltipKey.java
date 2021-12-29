package slimeknights.tconstruct.library.utils;

import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/** Options for which tooltip is being used on an item */
public enum TooltipKey {
  /** Tooltip with neither shift nor control */
  NORMAL,
  /** Tooltip with shift held */
  SHIFT,
  /** Tooltip with control held */
  CONTROL,
  /** Tooltip with alt held */
  ALT;

  /** Gets the tooltip type for the given screen properties */
  @OnlyIn(Dist.CLIENT)
  public static TooltipKey fromScreen() {
    if (Screen.hasShiftDown()) {
      return SHIFT;
    }
    if (Screen.hasControlDown()) {
      return CONTROL;
    }
    if (Screen.hasAltDown()) {
      return ALT;
    }
    return NORMAL;
  }
}

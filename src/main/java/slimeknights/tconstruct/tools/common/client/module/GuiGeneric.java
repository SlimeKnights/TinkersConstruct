package slimeknights.tconstruct.tools.common.client.module;

import net.minecraft.util.ResourceLocation;

import slimeknights.mantle.client.gui.GuiElement;
import slimeknights.mantle.client.gui.GuiElementScalable;
import slimeknights.tconstruct.library.Util;

public final class GuiGeneric {

  public static final ResourceLocation LOCATION = Util.getResource("textures/gui/generic.png");

  // first one sets default texture w/h
  public static final GuiElement cornerTopLeft = new GuiElement(0, 0, 7, 7, 64, 64);
  public static final GuiElement cornerTopRight = new GuiElement(64 - 7, 0, 7, 7);
  public static final GuiElement cornerBottomLeft = new GuiElement(0, 64 - 7, 7, 7);
  public static final GuiElement cornerBottomRight = new GuiElement(64 - 7, 64 - 7, 7, 7);

  public static final GuiElementScalable borderTop = new GuiElementScalable(7, 0, 64 - 7 - 7, 7);
  public static final GuiElementScalable borderBottom = new GuiElementScalable(7, 64 - 7, 64 - 7 - 7, 7);
  public static final GuiElementScalable borderLeft = new GuiElementScalable(0, 7, 7, 64 - 7 - 7);
  public static final GuiElementScalable borderRight = new GuiElementScalable(64 - 7, 7, 7, 64 - 7 - 7);

  public static final GuiElementScalable overlap = new GuiElementScalable(21, 45, 7, 14);
  public static final GuiElement overlapTopLeft = new GuiElement(7, 40, 7, 7);
  public static final GuiElement overlapTopRight = new GuiElement(14, 40, 7, 7);
  public static final GuiElement overlapBottomLeft = new GuiElement(7, 47, 7, 7);
  public static final GuiElement overlapBottomRight = new GuiElement(14, 47, 7, 7);

  public static final GuiElementScalable textBackground = new GuiElementScalable(7 + 18, 7, 18, 10);

  public static final GuiElementScalable slot = new GuiElementScalable(7, 7, 18, 18);
  public static final GuiElementScalable slotEmpty = new GuiElementScalable(7 + 18, 7, 18, 18);

  public static final GuiElement sliderNormal = new GuiElement(7, 25, 10, 15);
  public static final GuiElement sliderLow = new GuiElement(17, 25, 10, 15);
  public static final GuiElement sliderHigh = new GuiElement(27, 25, 10, 15);
  public static final GuiElement sliderTop = new GuiElement(43, 7, 12, 1);
  public static final GuiElement sliderBottom = new GuiElement(43, 38, 12, 1);
  public static final GuiElementScalable sliderBackground = new GuiElementScalable(43, 8, 12, 30);

  private GuiGeneric() {
  }
}

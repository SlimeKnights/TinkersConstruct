package slimeknights.tconstruct.tools.common.client.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import slimeknights.mantle.client.gui.GuiElement;
import slimeknights.mantle.client.gui.GuiElementScalable;
import slimeknights.mantle.client.gui.GuiWidget;

public class GuiWidgetTextField extends GuiWidget {

  public static final GuiElement FieldLeft = new GuiElement(0, 0, 2, 12);
  public static final GuiElement FieldRight = new GuiElement(0, 0, 2, 12);
  public static final GuiElementScalable FieldCenter = new GuiElementScalable(2, 0, 98, 12);

  public GuiElement left = FieldLeft;
  public GuiElement right = FieldRight;
  public GuiElementScalable center = FieldCenter;

  public GuiElement leftHighlight = FieldLeft.shift(0, FieldLeft.h);
  public GuiElement rightHighlight = FieldRight.shift(0, FieldRight.h);
  public GuiElementScalable centerHighlight = FieldCenter.shift(0, FieldCenter.h);

  public boolean highlighted;
  public String text;

  public FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

  @Override
  public void draw() {
    int x = xPos;
    int y = yPos;


    // background
    if(highlighted) {
      x += leftHighlight.draw(x, y);
      x += centerHighlight.drawScaledX(x, y, width - left.w - right.w);
      rightHighlight.draw(x, y);
    }
    else {
      x += left.draw(x, y);
      x += center.drawScaledX(x, y, width - left.w - right.w);
      right.draw(x, y);
    }

    // text
    x = xPos + left.w + 1;
    //fontRenderer.drawStringWithShadow()
  }
}

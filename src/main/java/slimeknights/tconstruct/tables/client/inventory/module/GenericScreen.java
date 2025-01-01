package slimeknights.tconstruct.tables.client.inventory.module;

import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.mantle.client.screen.ScalableElementScreen;
import slimeknights.tconstruct.TConstruct;

public class GenericScreen {
  public static final ResourceLocation LOCATION = TConstruct.getResource("textures/gui/generic.png");

  // first one sets default texture w/h
  public static final ElementScreen cornerTopLeft = new ElementScreen(LOCATION, 0, 0, 7, 7, 64, 64);
  public static final ElementScreen cornerTopRight = cornerTopLeft.move(64 - 7, 0, 7, 7);
  public static final ElementScreen cornerBottomLeft = cornerTopLeft.move(0, 64 - 7, 7, 7);
  public static final ElementScreen cornerBottomRight = cornerTopLeft.move(64 - 7, 64 - 7, 7, 7);

  public static final ScalableElementScreen borderTop = new ScalableElementScreen(LOCATION, 7, 0, 64 - 7 - 7, 7, 64, 64);
  public static final ScalableElementScreen borderBottom = borderTop.move(7, 64 - 7, 64 - 7 - 7, 7);
  public static final ScalableElementScreen borderLeft = borderTop.move(0, 7, 7, 64 - 7 - 7);
  public static final ScalableElementScreen borderRight = borderTop.move(64 - 7, 7, 7, 64 - 7 - 7);

  public static final ScalableElementScreen overlap = borderTop.move(21, 45, 7, 14);
  public static final ElementScreen overlapTopLeft = cornerTopLeft.move(7, 40, 7, 7);
  public static final ElementScreen overlapTopRight = cornerTopLeft.move(14, 40, 7, 7);
  public static final ElementScreen overlapBottomLeft = cornerTopLeft.move(7, 47, 7, 7);
  public static final ElementScreen overlapBottomRight = cornerTopLeft.move(14, 47, 7, 7);

  public static final ScalableElementScreen textBackground = borderTop.move(7 + 18, 7, 18, 10);

  public static final ScalableElementScreen slot = borderTop.move(7, 7, 18, 18);
  public static final ScalableElementScreen slotEmpty = borderTop.move(7 + 18, 7, 18, 18);

  public static final ElementScreen sliderNormal = cornerTopLeft.move(7, 25, 10, 15);
  public static final ElementScreen sliderLow = cornerTopLeft.move(17, 25, 10, 15);
  public static final ElementScreen sliderHigh = cornerTopLeft.move(27, 25, 10, 15);
  public static final ElementScreen sliderTop = cornerTopLeft.move(43, 7, 12, 1);
  public static final ElementScreen sliderBottom = cornerTopLeft.move(43, 38, 12, 1);
  public static final ScalableElementScreen sliderBackground = borderTop.move(43, 8, 12, 30);
}

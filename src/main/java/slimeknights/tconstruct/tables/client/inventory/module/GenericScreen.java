package slimeknights.tconstruct.tables.client.inventory.module;

import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.client.screen.ScalableElementScreen;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.tconstruct.TConstruct;

public class GenericScreen {

  public static final ResourceLocation LOCATION = TConstruct.getResource("textures/gui/generic.png");

  // first one sets default texture w/h
  public static final ElementScreen cornerTopLeft = new ElementScreen(0, 0, 7, 7, 64, 64);
  public static final ElementScreen cornerTopRight = new ElementScreen(64 - 7, 0, 7, 7);
  public static final ElementScreen cornerBottomLeft = new ElementScreen(0, 64 - 7, 7, 7);
  public static final ElementScreen cornerBottomRight = new ElementScreen(64 - 7, 64 - 7, 7, 7);

  public static final ScalableElementScreen borderTop = new ScalableElementScreen(7, 0, 64 - 7 - 7, 7);
  public static final ScalableElementScreen borderBottom = new ScalableElementScreen(7, 64 - 7, 64 - 7 - 7, 7);
  public static final ScalableElementScreen borderLeft = new ScalableElementScreen(0, 7, 7, 64 - 7 - 7);
  public static final ScalableElementScreen borderRight = new ScalableElementScreen(64 - 7, 7, 7, 64 - 7 - 7);

  public static final ScalableElementScreen overlap = new ScalableElementScreen(21, 45, 7, 14);
  public static final ElementScreen overlapTopLeft = new ElementScreen(7, 40, 7, 7);
  public static final ElementScreen overlapTopRight = new ElementScreen(14, 40, 7, 7);
  public static final ElementScreen overlapBottomLeft = new ElementScreen(7, 47, 7, 7);
  public static final ElementScreen overlapBottomRight = new ElementScreen(14, 47, 7, 7);

  public static final ScalableElementScreen textBackground = new ScalableElementScreen(7 + 18, 7, 18, 10);

  public static final ScalableElementScreen slot = new ScalableElementScreen(7, 7, 18, 18);
  public static final ScalableElementScreen slotEmpty = new ScalableElementScreen(7 + 18, 7, 18, 18);

  public static final ElementScreen sliderNormal = new ElementScreen(7, 25, 10, 15);
  public static final ElementScreen sliderLow = new ElementScreen(17, 25, 10, 15);
  public static final ElementScreen sliderHigh = new ElementScreen(27, 25, 10, 15);
  public static final ElementScreen sliderTop = new ElementScreen(43, 7, 12, 1);
  public static final ElementScreen sliderBottom = new ElementScreen(43, 38, 12, 1);
  public static final ScalableElementScreen sliderBackground = new ScalableElementScreen(43, 8, 12, 30);
}

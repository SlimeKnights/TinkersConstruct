package slimeknights.tconstruct.library.client.crosshair;

import slimeknights.tconstruct.library.Util;

public interface Crosshairs {
  Crosshair SQUARE = new Crosshair(Util.getResource("textures/gui/crosshair/square.png"));
  Crosshair X = new CrosshairTriangle(Util.getResource("textures/gui/crosshair/x.png"));
  Crosshair INVERSE = new CrosshairTriangle(Util.getResource("textures/gui/crosshair/inverse.png"));
  Crosshair PLUS = new Crosshair(Util.getResource("textures/gui/crosshair/plus.png"));
}

package slimeknights.tconstruct.library.client.crosshair;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.tconstruct.library.Util;

@SideOnly(Side.CLIENT)
public interface Crosshairs {
  Crosshair SQUARE = new Crosshair(Util.getResource("textures/gui/crosshair/square.png"));
  Crosshair X = new CrosshairTriangle(Util.getResource("textures/gui/crosshair/x.png"));
  Crosshair INVERSE = new CrosshairTriangle(Util.getResource("textures/gui/crosshair/inverse.png"));
  Crosshair PLUS = new Crosshair(Util.getResource("textures/gui/crosshair/plus.png"));
  Crosshair T = new CrosshairInverseT(Util.getResource("textures/gui/crosshair/t.png"), 15);
}

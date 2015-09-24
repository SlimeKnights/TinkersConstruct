package slimeknights.mantle.client;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;


/**
 * Custom renderer based on CoFHs CoFHFontRenderer.
 * Uses code from CoFHCore. Credit goes to the CoFH team, KingLemming and RWTema.
 */
@SideOnly(Side.CLIENT)
public class CustomFontRenderer extends FontRenderer {

  private boolean dropShadow;

  public CustomFontRenderer(GameSettings gameSettingsIn, ResourceLocation location, TextureManager textureManagerIn) {
    super(gameSettingsIn, location, textureManagerIn, true);
  }

  @Override
  public int renderString(String text, float x, float y, int color, boolean dropShadow) {
    this.dropShadow = dropShadow;
    return super.renderString(text, x, y, color, dropShadow);
  }

  @Override
  protected float renderUnicodeChar(char letter, boolean italic) {
    // special color settings through char code
    // we use \u2700 to \u27FF, where the lower byte represents the Hue of the color
    if((int)letter >= 0x2700 && (int)letter <= 0x27FF) {
      int hue = letter & 0xFF;
      int color = Color.HSBtoRGB((float)hue/255f, 0.65f, 0.8f);

      if ((color & -67108864) == 0)
      {
        color |= -16777216;
      }

      if (dropShadow)
      {
        color = (color & 16579836) >> 2 | color & -16777216;
      }

      this.setColor(((color >> 16) & 255)/255f,
                    ((color >>  8) & 255)/255f,
                    ((color >>  0) & 255)/255f,
                    ((color >> 24) & 255)/255f);
      return 0;
    }

    return super.renderUnicodeChar(letter, italic);
  }

  /**
   * Takes a value between 0.0 and 1.0.
   * Returns a color between red and green, depending on the value. 1.0 is green.
   * If the value goes above 1.0 it continues along the color spectrum.
   */
  public static char valueToColorCode(float v) {
    // 0.0 -> 0 = red
    // 1.0 -> 1/3 = green
    // 1.5 -> 1/2 = aqua
    v /= 3f;
    v = MathHelper.clamp_float(v, 0.01f, 0.5f);
    return (char)(0x2700 + (int)(v*255f));
  }
}

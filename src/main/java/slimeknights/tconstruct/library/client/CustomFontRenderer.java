package slimeknights.tconstruct.library.client;

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
  protected static int MARKER = 0x2700;

  private boolean dropShadow;
  private int state = 0;
  private int red;
  private int green;
  private int blue;

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
    if((int)letter >= MARKER && (int)letter <= MARKER + 0xFF) {
      int value = letter & 0xFF;
      switch(state) {
        case 0: red = value; break;
        case 1: green = value; break;
        case 2: blue = value; break;
        default: this.setColor(1f, 1f, 1f, 1f); return 0;
      }

      state = ++state % 3;

      int color = (red << 16) | (green << 8) | blue | (0xff << 24);
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

    // invalid sequence encountered
    if(state != 0) {
      state = 0;
      this.setColor(1f, 1f, 1f, 1f);
    }

    return super.renderUnicodeChar(letter, italic);
  }

  public static String encodeColor(int color) {
    int r = ((color >> 16) & 255);
    int g = ((color >>  8) & 255);
    int b = ((color >>  0) & 255);
    return encodeColor(r, g, b);
  }

  public static String encodeColor(float r, float g, float b) {
    return encodeColor(r*255, g*255, b*255);
  }

  public static String encodeColor(int r, int g, int b) {
    return String.format("%c%c%c",
                         ((char)(MARKER + (r&0xFF))),
                         ((char)(MARKER + (g&0xFF))),
                         ((char)(MARKER + (b&0xFF))));
  }

  /**
   * Takes a value between 0.0 and 1.0.
   * Returns a color between red and green, depending on the value. 1.0 is green.
   * If the value goes above 1.0 it continues along the color spectrum.
   */
  public static String valueToColorCode(float v) {
    // 0.0 -> 0 = red
    // 1.0 -> 1/3 = green
    // 1.5 -> 1/2 = aqua
    v /= 3f;
    v = MathHelper.clamp_float(v, 0.01f, 0.5f);
    int color = Color.HSBtoRGB(v, 0.65f, 0.8f);
    return encodeColor(color);
  }
}

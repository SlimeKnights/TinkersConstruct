package slimeknights.tconstruct.library.client;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


/**
 * Custom renderer based on CoFHs CoFHFontRenderer.
 * Uses code from CoFHCore. Credit goes to the CoFH team, KingLemming and RWTema.
 */
@SideOnly(Side.CLIENT)
public class CustomFontRenderer extends FontRenderer {

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
    if((int)letter >= CustomFontColor.MARKER && (int)letter <= CustomFontColor.MARKER + 0xFF) {
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

}

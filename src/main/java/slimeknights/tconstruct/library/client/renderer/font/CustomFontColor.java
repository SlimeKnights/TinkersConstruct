package slimeknights.tconstruct.library.client.renderer.font;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import slimeknights.tconstruct.library.Util;

import java.util.function.UnaryOperator;

import static java.awt.Color.HSBtoRGB;

// TODO: extract?
public class CustomFontColor {

  public static final TextColor MAX = valueToColor(1, 1);
  private static final UnaryOperator<Style> APPLY_MAX = style -> style.withColor(MAX);

  private CustomFontColor() {}

  /**
   * Takes a value between 0.0 and 1.0.
   * Returns a color between red and green, depending on the value. 1.0 is green.
   * If the value goes above 1.0 it continues along the color spectrum.
   */
  public static TextColor valueToColor(float value, float max) {
    // 0.0 -> 0 = red
    // 1.0 -> 1/3 = green
    // 1.5 -> 1/2 = aqua
    float hue = MathHelper.clamp(((value / max) / 3), 0.01f, 0.5f);
    return TextColor.fromRgb(HSBtoRGB(hue, 0.65f, 0.8f));
  }

  public static Text formatPartialAmount(int value, int max) {
    return new LiteralText(Util.df.format(value))
      .styled(style -> style.withColor(CustomFontColor.valueToColor(value, max)))
      .append(new LiteralText(" / ").formatted(Formatting.GRAY))
      .append(new LiteralText(Util.df.format(max)).styled(APPLY_MAX));
  }
}

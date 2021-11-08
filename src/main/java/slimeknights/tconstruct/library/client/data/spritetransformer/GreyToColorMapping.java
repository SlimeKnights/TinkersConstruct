package slimeknights.tconstruct.library.client.data.spritetransformer;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static net.minecraft.client.renderer.texture.NativeImage.getAlpha;
import static net.minecraft.client.renderer.texture.NativeImage.getBlue;
import static net.minecraft.client.renderer.texture.NativeImage.getCombined;
import static net.minecraft.client.renderer.texture.NativeImage.getGreen;
import static net.minecraft.client.renderer.texture.NativeImage.getRed;

/** Color mapping that maps greyscale values to a palette for each value */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class GreyToColorMapping implements IColorMapping {
  private final List<ColorMapping> mappings;
  private final Integer[] recolorCache = new Integer[256];

  /**
   * Interpolates two numbers
   * @param a        First number
   * @param b        Second number
   * @param x        Amount of A, such that x / divisor is the percentage from A to B
   * @param divisor  Divisor to use with X
   * @return  Interpolated value
   */
  private static int interpolate(int a, int b, int x, int divisor) {
    return a + (((b - a) * x) / divisor);
  }

  /** Gets the color value without using the cache */
  private int getColorUncached(int grey) {
    // we need to find up to two colors, a less and a greater than
    // ideally we find a direct match, but if not we interpolate
    int size = mappings.size();
    ColorMapping first = mappings.get(0);
    // grey is before the first point, return the first value
    if (size == 1 || grey <= first.getGrey()) {
      return first.getColor();
    }

    // grey is after the first point, so try to find two points its between
    ColorMapping second = mappings.get(1);
    for (int i = 1; i < size; i++) {
      // locate an upper bound, once we find one we have a pair to use
      int newGrey = second.getGrey();
      if (grey < newGrey) {
        break;
      }
      // if the upper bound is an exact match, nothing else to do
      if (grey == newGrey) {
        return second.getColor();
      }
      first = second;
      second = mappings.get(i);
    }

    // if its bigger than the last, return the last value
    if (grey > second.getGrey()) {
      return second.getColor();
    }

    // at this point, grey is strictly between first and second, interpolate between the two
    int diff = grey - first.getGrey();
    int divisor = second.getGrey() - first.getGrey();
    int colorA = first.getColor();
    int colorB = second.getColor();
    // interpolate each pair of colors
    int alpha = interpolate(getAlpha(colorA), getAlpha(colorB), diff, divisor);
    int red   = interpolate(getRed(colorA),   getRed(colorB),   diff, divisor);
    int green = interpolate(getGreen(colorA), getGreen(colorB), diff, divisor);
    int blue  = interpolate(getBlue(colorA),  getBlue(colorB),  diff, divisor);
    return getCombined(alpha, blue, green, red);
  }

  /**
   * Gets the color for the given greyscale from the palette, using the cache
   * @param grey  Grey value
   * @return  Color
   */
  private int getColor(int grey) {
    // if we already processed this color, use our old result
    Integer cached = recolorCache[grey];
    if (cached != null) {
      return cached;
    }
    int calculated = getColorUncached(grey);
    recolorCache[grey] = calculated;
    return calculated;
  }

  @Override
  public int mapColor(int color) {
    // if fully transparent, just return fully transparent
    // we do not do 0 alpha RGB values to save effort
    int alpha = getAlpha(color);
    if (alpha == 0) {
      return 0x00000000;
    }
    // figure out our new greyscale from the given color, we just base it on the largest
    int red = getRed(color);
    int green = getGreen(color);
    int blue = getBlue(color);
    int grey = Math.max(red, Math.max(green, blue));
    int newColor = getColor(grey);
    // if the original color was partially transparent, set the alpha
    if (alpha < 255) newColor = (newColor & 0x00FFFFFF) | ((alpha * getAlpha(newColor) / 255) << 24);
    // if any of RGB are lower than the max, scale it down
    if (red   < grey) newColor = (newColor & 0xFFFFFF00) | (((newColor & 0x000000FF) * red   / grey) & 0x000000FF);
    if (green < grey) newColor = (newColor & 0xFFFF00FF) | (((newColor & 0x0000FF00) * green / grey) & 0x0000FF00);
    if (blue  < grey) newColor = (newColor & 0xFF00FFFF) | (((newColor & 0x00FF0000) * blue  / grey) & 0x00FF0000);

    // final color
    return newColor;
  }

  /** Creates a new grey to color builder */
  public static Builder builder() {
    return new Builder();
  }

  /** Creates a new grey to color builder starting with greyscale 0 as white */
  public static Builder builderFromBlack() {
    return builder().addABGR(0, 0xFF000000);
  }

  /** Mapping from greyscale to color */
  @Data
  private static class ColorMapping {
    private final int grey;
    private final int color;
  }

  /** Builder to create a palette of this type */
  public static class Builder {
    private final ImmutableList.Builder<ColorMapping> builder = ImmutableList.builder();
    private int lastGrey = -1;

    /** Validates the given grey value */
    private void checkGrey(int grey) {
      if (grey < 0 || grey > 255) {
        throw new IllegalArgumentException("Invalid grey value, must be between 0 and 255, inclusive");
      }
      if (grey <= lastGrey) {
        throw new IllegalArgumentException("Grey value must be greater than the previous value");
      }
      lastGrey = grey;
    }

    /** Adds a color to the palette in ABGR format */
    public Builder addABGR(int grey, int color) {
      checkGrey(grey);
      builder.add(new ColorMapping(grey, color));
      return this;
    }

    /** Adds a color to the palette in ARGB format */
    public Builder addARGB(int grey, int color) {
      checkGrey(grey);
      int newColor = (color & 0xFF00FF00) | (((color & 0x00FF0000) >> 16) & 0x000000FF) | (((color & 0x000000FF) << 16) & 0x00FF0000);
      builder.add(new ColorMapping(grey, newColor));
      return this;
    }

    /** Builds a color mapping */
    public IColorMapping build() {
      List<ColorMapping> list = builder.build();
      if (list.size() < 2) {
        throw new IllegalStateException("Too few colors in palette, must have at least 2");
      }
      return new GreyToColorMapping(list);
    }
  }
}

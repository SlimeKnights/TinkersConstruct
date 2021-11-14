package slimeknights.tconstruct.library.client.data.spritetransformer;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.ToIntFunction;

import static net.minecraft.client.renderer.texture.NativeImage.getAlpha;
import static net.minecraft.client.renderer.texture.NativeImage.getBlue;
import static net.minecraft.client.renderer.texture.NativeImage.getCombined;
import static net.minecraft.client.renderer.texture.NativeImage.getGreen;
import static net.minecraft.client.renderer.texture.NativeImage.getRed;

/** Color mapping that maps greyscale values to a palette for each value */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class GreyToColorMapping implements IColorMapping {
  public static final ResourceLocation NAME = TConstruct.getResource("grey_to_color");
  public static final Deserializer DESERIALIZER = new Deserializer();

  private final List<ColorMapping> mappings;
  private final Integer[] recolorCache = new Integer[256];

  /** Function to interpolate color values of two colors */
  private static final Interpolate<ColorMapping,Integer> INTERPOLATE_COLORS = (first, second, grey) -> {
    if (first == null) {
      assert second != null;
      return second.getColor();
    }
    if (second == null) {
      return first.getColor();
    }
    return interpolateColors(first.getColor(), first.getGrey(), second.getColor(), second.getGrey(), grey);
  };

  /** Gets the grey value of a color */
  private static final ToIntFunction<ColorMapping> GET_GREY = ColorMapping::getGrey;

  /**
   * Gets the color for the given greyscale from the palette, using the cache
   * @param grey  Grey value
   * @return  Color
   */
  public int getColorForGrey(int grey) {
    if (recolorCache[grey] == null) {
      int calculated = getNearestByGrey(mappings, GET_GREY, grey, INTERPOLATE_COLORS);
      recolorCache[grey] = calculated;
    }
    return recolorCache[grey];
  }

  @Override
  public int mapColor(int color) {
    // if fully transparent, just return fully transparent
    // we do not do 0 alpha RGB values to save effort
    if (getAlpha(color) == 0) {
      return 0x00000000;
    }
    int grey = getGrey(color);
    return scaleColor(color, getColorForGrey(grey), grey);
  }

  @Override
  public JsonObject serialize(JsonSerializationContext context) {
    JsonObject object = new JsonObject();
    object.addProperty("type", NAME.toString());
    JsonArray colors = new JsonArray();
    for (ColorMapping mapping : mappings) {
      JsonObject pair = new JsonObject();
      pair.addProperty("grey", mapping.grey);
      pair.addProperty("color", String.format("%08X", Util.translateColorBGR(mapping.color)));
      colors.add(pair);
    }
    object.add("palette", colors);
    return object;
  }

  /** Serializer for a recolor sprite transformer */
  protected static class Deserializer implements JsonDeserializer<GreyToColorMapping> {
    @Override
    public GreyToColorMapping deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject object = json.getAsJsonObject();
      JsonArray palette = JSONUtils.getJsonArray(object, "palette");
      GreyToColorMapping.Builder paletteBuilder = GreyToColorMapping.builder();
      for (int i = 0; i < palette.size(); i++) {
        JsonObject palettePair = JSONUtils.getJsonObject(palette.get(i), "palette["+i+']');
        int grey = JSONUtils.getInt(palettePair, "grey");
        int color = JsonHelper.parseColor(JSONUtils.getString(palettePair, "color"));
        if (i == 0 && grey != 0) {
          paletteBuilder.addABGR(0, 0xFF000000);
        }
        paletteBuilder.addARGB(grey, color);
      }
      return paletteBuilder.build();
    }
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

  /** Helper to interpolate two color mappings */
  @FunctionalInterface
  public interface Interpolate<T, R> {
    /**
     * Gets the interpolation of these two values, note only one of first and second will be null, never both
     * @param first   First object
     * @param second  Second object
     * @param grey    Interpolation value
     * @return  Result
     */
    R interpolate(@Nullable T first, @Nullable T second, int grey);
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
      builder.add(new ColorMapping(grey, Util.translateColorBGR(color)));
      return this;
    }

    /** Builds a color mapping */
    public GreyToColorMapping build() {
      List<ColorMapping> list = builder.build();
      if (list.size() < 2) {
        throw new IllegalStateException("Too few colors in palette, must have at least 2");
      }
      return new GreyToColorMapping(list);
    }
  }


  /* Utilities */

  /**
   * Interpolates two numbers
   * @param a        First number
   * @param b        Second number
   * @param x        Amount of A, such that x / divisor is the percentage from A to B
   * @param divisor  Divisor to use with X
   * @return  Interpolated value
   */
  public static int interpolate(int a, int b, int x, int divisor) {
    return a + (((b - a) * x) / divisor);
  }

  /**
   * Interpolates two colors
   * @param colorBefore  First color
   * @param greyBefore   Grey value of first color
   * @param colorAfter   Second color
   * @param greyAfter    Grey value of second color
   * @param grey         Grey value for interpolation between the two, should be between greyBefore and greyAfter
   * @return  Interpolated color
   */
  public static int interpolateColors(int colorBefore, int greyBefore, int colorAfter, int greyAfter, int grey) {
    // at this point, grey is strictly between first and second, interpolate between the two
    int diff = grey - greyBefore;
    int divisor = greyAfter - greyBefore;
    // interpolate each pair of colors
    int alpha = interpolate(getAlpha(colorBefore), getAlpha(colorAfter), diff, divisor);
    int red   = interpolate(getRed(colorBefore),   getRed(colorAfter),   diff, divisor);
    int green = interpolate(getGreen(colorBefore), getGreen(colorAfter), diff, divisor);
    int blue  = interpolate(getBlue(colorBefore),  getBlue(colorAfter),  diff, divisor);
    return getCombined(alpha, blue, green, red);
  }

  /** Gets the largest grey value for the given color */
  public static int getGrey(int color) {
    return Math.max(getRed(color), Math.max(getGreen(color), getBlue(color)));
  }

  /** Scales the new color based on the original color values and the grey value */
  public static int scaleColor(int original, int newColor, int grey) {
    // if the original color was partially transparent, set the alpha
    int alpha = getAlpha(original);
    if (alpha < 255) newColor = (newColor & 0x00FFFFFF) | ((alpha * getAlpha(newColor) / 255) << 24);

    // grey is based on largest, so scale down as needed
    // if any of RGB are lower than the max, scale it down
    int red = getRed(original);
    if (red   < grey) newColor = (newColor & 0xFFFFFF00) | (((newColor & 0x000000FF) * red   / grey) & 0x000000FF);
    int green = getGreen(original);
    if (green < grey) newColor = (newColor & 0xFFFF00FF) | (((newColor & 0x0000FF00) * green / grey) & 0x0000FF00);
    int blue = getBlue(original);
    if (blue  < grey) newColor = (newColor & 0xFF00FFFF) | (((newColor & 0x00FF0000) * blue  / grey) & 0x00FF0000);

    // final color
    return newColor;
  }

  /** Gets the color value without using the cache */
  public static <T, R> R getNearestByGrey(List<T> list, ToIntFunction<T> greyMap, int grey, Interpolate<T,R> interpolate) {
    // we need to find up to two colors, a less and a greater than
    // ideally we find a direct match, but if not we interpolate
    int size = list.size();
    T first = list.get(0);
    // grey is before the first point, return the first value
    if (size == 1 || grey <= greyMap.applyAsInt(first)) {
      return interpolate.interpolate(null, first, grey);
    }

    // grey is after the first point, so try to find two points its between
    T second = list.get(1);
    for (int i = 1; i < size; i++) {
      // locate an upper bound, once we find one we have a pair to use
      int newGrey = greyMap.applyAsInt(second);
      if (grey < newGrey) {
        break;
      }
      // if the upper bound is an exact match, nothing else to do
      if (grey == newGrey) {
        return interpolate.interpolate(first, second, grey);
      }
      first = second;
      second = list.get(i);
    }

    // if its bigger than the last, return the last value
    if (grey > greyMap.applyAsInt(second)) {
      return interpolate.interpolate(second, null, grey);
    }
    // actually got a pair inbetween, return that
    return interpolate.interpolate(first, second, grey);
  }
}

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

import java.lang.reflect.Type;
import java.util.List;

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
}

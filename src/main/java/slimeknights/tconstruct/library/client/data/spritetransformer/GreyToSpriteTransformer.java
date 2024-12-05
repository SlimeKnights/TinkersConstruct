package slimeknights.tconstruct.library.client.data.spritetransformer;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.mojang.blaze3d.platform.NativeImage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.mantle.data.loadable.common.ColorLoadable;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.data.material.MaterialPartTextureGenerator;
import slimeknights.tconstruct.library.client.data.spritetransformer.GreyToColorMapping.Interpolate;
import slimeknights.tconstruct.library.client.data.util.AbstractSpriteReader;
import slimeknights.tconstruct.library.client.data.util.DataGenSpriteReader;
import slimeknights.tconstruct.library.client.data.util.ResourceManagerSpriteReader;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.ToIntFunction;

import static com.mojang.blaze3d.platform.NativeImage.getA;

/**
 * Supports including sprites as "part of the palette"
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class GreyToSpriteTransformer implements ISpriteTransformer {
  public static final ResourceLocation NAME = TConstruct.getResource("grey_to_sprite");
  public static final Deserializer<GreyToSpriteTransformer> DESERIALIZER = new Deserializer<>((builder, json) -> builder.build());

  /** Base folder for texture backgrounds */
  private static final String TEXTURE_FOLDER = "textures";
  /** Sprite reader instance, filled in by events */
  @Nullable
  static AbstractSpriteReader READER = null;
  /** List of all sprite mappings with cached data that need to be cleared */
  private static final List<SpriteMapping> MAPPINGS_TO_CLEAR = new ArrayList<>();

  /** List of sprites to try */
  private final List<SpriteMapping> sprites;

  /** Cache of the sprites to use for each color value */
  private final SpriteRange[] foundSpriteCache = new SpriteRange[256];

  /** Constructor for search */
  private static final Interpolate<SpriteMapping, SpriteRange> SPRITE_RANGE = (first, second, grey) -> new SpriteRange(first, second);
  /** Gets the grey value of a color */
  private static final ToIntFunction<SpriteMapping> GET_GREY = SpriteMapping::getGrey;

  /** Gets the sprite for the given color */
  protected SpriteRange getSpriteRange(int grey) {
    if (foundSpriteCache[grey] == null) {
      foundSpriteCache[grey] = GreyToColorMapping.getNearestByGrey(sprites, GET_GREY, grey, SPRITE_RANGE);
    }
    return foundSpriteCache[grey];
  }

  /** Gets the color at the given location from its full color value */
  private int getNewColor(int color, int x, int y) {
    // if fully transparent, just return fully transparent
    // we do not do 0 alpha RGB values to save effort
    if (getA(color) == 0) {
      return 0x00000000;
    }
    int grey = GreyToColorMapping.getGrey(color);
    int newColor = getSpriteRange(grey).getColor(x, y, grey);
    return GreyToColorMapping.scaleColor(color, newColor, grey);
  }

  @Override
  public void transform(NativeImage image, boolean allowAnimated) {
    for (int x = 0; x < image.getWidth(); x++) {
      for (int y = 0; y < image.getHeight(); y++) {
        image.setPixelRGBA(x, y, getNewColor(image.getPixelRGBA(x, y), x, y));
      }
    }
  }

  @Override
  public int getFallbackColor() {
    return getSpriteRange(216).getAverage(216);
  }

  /* Serializing */

  @Override
  public JsonObject serialize(JsonSerializationContext context) {
    JsonObject object = new JsonObject();
    object.addProperty("type", NAME.toString());
    JsonArray colors = new JsonArray();
    for (SpriteMapping mapping : sprites) {
      JsonObject pair = new JsonObject();
      pair.addProperty("grey", mapping.grey);
      // color used by both types
      if (mapping.color != -1 || mapping.path == null) {
        pair.addProperty("color", String.format("%08X", Util.translateColorBGR(mapping.color)));
      }
      // path by one
      if (mapping.path != null) {
        pair.addProperty("path", mapping.path.toString());
      }
      colors.add(pair);
    }
    object.add("palette", colors);
    return object;
  }

  /** Serializer for a recolor sprite transformer */
  protected record Deserializer<T extends GreyToSpriteTransformer>(BiFunction<GreyToSpriteTransformer.Builder, JsonObject, T> constructor) implements JsonDeserializer<T> {
    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject object = json.getAsJsonObject();
      JsonArray palette = GsonHelper.getAsJsonArray(object, "palette");
      GreyToSpriteTransformer.Builder paletteBuilder = GreyToSpriteTransformer.builder();
      for (int i = 0; i < palette.size(); i++) {
        JsonObject palettePair = GsonHelper.convertToJsonObject(palette.get(i), "palette["+i+']');
        int grey = GsonHelper.getAsInt(palettePair, "grey");
        if (i == 0 && grey != 0) {
          paletteBuilder.addABGR(0, 0xFF000000);
        }
        // get the proper type
        int color = ColorLoadable.ALPHA.getOrDefault(palettePair, "color", -1);
        if (palettePair.has("path")) {
          paletteBuilder.addTexture(grey, JsonHelper.getResourceLocation(palettePair, "path"), color);
        } else {
          paletteBuilder.addARGB(grey, color);
        }
      }
      return constructor.apply(paletteBuilder, object);
    }
  }


  /* Builder */

  /** Creates a new grey to color builder */
  public static Builder builder() {
    return new Builder();
  }

  /** Creates a new grey to color builder starting with greyscale 0 as white */
  public static Builder builderFromBlack() {
    return builder().addABGR(0, 0xFF000000);
  }

  /** Builder to create a palette of this type */
  public static class Builder {
    private final ImmutableList.Builder<SpriteMapping> builder = ImmutableList.builder();
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
      builder.add(new SpriteMapping(grey, color, null));
      return this;
    }

    /** Adds a color to the palette in ARGB format */
    @SuppressWarnings("UnusedReturnValue")
    public Builder addARGB(int grey, int color) {
      return addABGR(grey, Util.translateColorBGR(color));
    }

    /** Adds a texture to the palette */
    public Builder addTexture(int grey, ResourceLocation texture, int tint) {
      checkGrey(grey);
      builder.add(new SpriteMapping(grey, Util.translateColorBGR(tint), texture));
      return this;
    }

    /** Adds a texture to the palette */
    public Builder addTexture(int grey, ResourceLocation texture) {
      return addTexture(grey, texture, -1);
    }

    /** Builds a color mapping */
    public GreyToSpriteTransformer build() {
      List<SpriteMapping> list = builder.build();
      if (list.size() < 2) {
        throw new IllegalStateException("Too few colors in palette, must have at least 2");
      }
      return new GreyToSpriteTransformer(list);
    }

    /** Builds an animated transformer */
    public AnimatedGreyToSpriteTransformer animated(ResourceLocation metaPath, int frames) {
      List<SpriteMapping> list = builder.build();
      if (list.size() < 2) {
        throw new IllegalStateException("Too few colors in palette, must have at least 2");
      }
      return new AnimatedGreyToSpriteTransformer(list, metaPath, frames);
    }
  }


  /* Data classes */

  /** Mapping from greyscale to color */
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  static class SpriteMapping {
    @Getter
    private final int grey;
    private final int color;
    /** Path of the sprite relative to the textures folder */
    @Nullable
    private final ResourceLocation path;

    /** Loaded image */
    private transient NativeImage image = null;

    /** Gets the image for this mapping */
    @Nullable
    private NativeImage getImage() {
      if (path != null && image == null) {
        if (READER == null) {
          throw new IllegalStateException("Cannot get image for a sprite without reader");
        }
        try {
          image = READER.read(path);
        } catch (IOException ex) {
          throw new IllegalStateException("Failed to load required image", ex);
        }
        MAPPINGS_TO_CLEAR.add(this);
      }
      return image;
    }

    /** Gets the color for the given X, Y, and frame */
    public int getColor(int x, int y, int frame) {
      if (path != null) {
        NativeImage image = getImage();
        if (image != null) {
          int spriteColor;
          // -1 means we are not doing frames, treat the whole image as one thing. This notably does not require it to be square
          if (frame == -1) {
            spriteColor = image.getPixelRGBA(x % image.getWidth(), y % image.getHeight());
          } else {
            // assume the frames of this are square, otherwise we have to store the ratio somewhere
            int width = image.getWidth();
            // ensure the x and y coordinates are within the individual frame by wrapping, needed notably for large tool sprites
            // then offset the y value, and ensure the offset is within the final height
            spriteColor = image.getPixelRGBA(x % width, (y % width + frame * width) % image.getHeight());
          }
          // if we have a color set, treat it as a tint
          if (color != -1) {
            spriteColor = GreyToColorMapping.scaleColor(spriteColor, color, 255);
          }
          return spriteColor;
        }
      }
      return color;
    }

    /** Gets the average color of this sprite in ARGB format, or the base color if no path */
    public int getAverage() {
      if (path != null) {
        NativeImage image = getImage();
        if (image != null) {
          int red = 0;
          int green = 0;
          int blue = 0;
          int alpha = 0;
          for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
              int color = image.getPixelRGBA(x, y);
              red   += NativeImage.getR(color);
              green += NativeImage.getG(color);
              blue  += NativeImage.getB(color);
              alpha += NativeImage.getA(color);
            }
          }
          int pixels = image.getWidth() * image.getHeight();
          int spriteColor = NativeImage.combine(alpha / pixels, blue / pixels, green / pixels, red / pixels);
          // if we have a color set, treat it as a tint
          if (color != -1) {
            spriteColor = GreyToColorMapping.scaleColor(spriteColor, color, 255);
          }
          return spriteColor;
        }
      }
      return color;
    }

    /** Checks if these two mappings have the same values */
    public boolean isSame(SpriteMapping other) {
      return this == other || (this.color == other.color && Objects.equals(this.path, other.path));
    }
  }

  /** Result from a sprite search for a given color */
  protected record SpriteRange(@Nullable SpriteMapping before, @Nullable SpriteMapping after) {
    /**
     * Gets the color of this range
     */
    public int getColor(int x, int y, int grey) {
      return getColor(x, y, -1, grey);
    }

    /**
     * Gets the color of this range for the given frame
     */
    public int getColor(int x, int y, int frame, int grey) {
      // after only
      if (before == null) {
        assert after != null;
        return after.getColor(x, y, frame);
      }
      if (after == null || before.isSame(after)) {
        return before.getColor(x, y, frame);
      }
      return GreyToColorMapping.interpolateColors(
        before.getColor(x, y, frame), before.getGrey(),
        after.getColor(x, y, frame), after.getGrey(),
        grey);
    }

    /** Gets the average value for the given grey value */
    public int getAverage(int grey) {
      if (before == null) {
        assert after != null;
        return after.getAverage();
      }
      if (after == null || before.isSame(after)) {
        return before.getAverage();
      }
      return GreyToColorMapping.interpolateColors(
        before.getAverage(), before.getGrey(),
        after.getAverage(), after.getGrey(),
        grey);
    }
  }


  /* Event listeners */

  /** If true, the event listeners are registered */
  private static boolean init = false;

  /** Registers this transformer where relevant */
  public static void init() {
    if (!init) {
      init = true;
      ISpriteTransformer.SERIALIZER.registerDeserializer(NAME, DESERIALIZER);
      ISpriteTransformer.SERIALIZER.registerDeserializer(AnimatedGreyToSpriteTransformer.NAME, AnimatedGreyToSpriteTransformer.DESERIALIZER);
      MaterialPartTextureGenerator.registerCallback(GreyToSpriteTransformer::textureCallback);
    }
  }

  /** Called before generating to set up the reader */
  private static void textureCallback(@Nullable ExistingFileHelper existingFileHelper, @Nullable ResourceManager manager) {
    if (READER != null) {
      MAPPINGS_TO_CLEAR.forEach(mapping -> mapping.image = null);
      MAPPINGS_TO_CLEAR.clear();
      READER.closeAll();
      READER = null;
    }
    if (existingFileHelper != null) {
      READER = new DataGenSpriteReader(existingFileHelper, TEXTURE_FOLDER);
    } else if (manager != null) {
      READER = new ResourceManagerSpriteReader(manager, TEXTURE_FOLDER);
    }
  }
}

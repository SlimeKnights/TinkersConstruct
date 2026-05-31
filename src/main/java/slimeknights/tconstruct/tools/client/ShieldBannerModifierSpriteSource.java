package slimeknights.tconstruct.tools.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.sources.LazyLoadedImage;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatterns;
import net.neoforged.neoforge.client.event.RegisterSpriteSourceTypesEvent;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/** Sprite source creating modifier textures for banners using shield banner textures */
public record ShieldBannerModifierSpriteSource(int cropX, int cropY, int cropWidth, int cropHeight, ResourceLocation destinationPrefix, int offsetX, int offsetY, int outSize) implements SpriteSource {
  private static final Codec<Integer> NON_NEGATIVE = ExtraCodecs.intRange(0, Integer.MAX_VALUE);
  private static final Codec<Integer> SHIELD_SIZE = ExtraCodecs.intRange(0, 64);
  public static final MapCodec<ShieldBannerModifierSpriteSource> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
    SHIELD_SIZE.fieldOf("crop_x").forGetter(ShieldBannerModifierSpriteSource::cropX),
    SHIELD_SIZE.fieldOf("crop_y").forGetter(ShieldBannerModifierSpriteSource::cropY),
    SHIELD_SIZE.fieldOf("crop_width").forGetter(ShieldBannerModifierSpriteSource::cropWidth),
    SHIELD_SIZE.fieldOf("crop_height").forGetter(ShieldBannerModifierSpriteSource::cropHeight),
    ResourceLocation.CODEC.fieldOf("destination_prefix").forGetter(ShieldBannerModifierSpriteSource::destinationPrefix),
    NON_NEGATIVE.fieldOf("offset_x").forGetter(ShieldBannerModifierSpriteSource::offsetX),
    NON_NEGATIVE.fieldOf("offset_y").forGetter(ShieldBannerModifierSpriteSource::offsetY),
    NON_NEGATIVE.fieldOf("output_size").forGetter(ShieldBannerModifierSpriteSource::outSize)
  ).apply(inst, ShieldBannerModifierSpriteSource::new));
  /** Registered type set on init */
  private static SpriteSourceType TYPE = null;
  /** Vanilla banner patterns. Banner patterns are datapack controlled in 1.21, but texture generation needs a static client-side list. */
  private static final List<ResourceKey<BannerPattern>> VANILLA_PATTERNS = List.of(
    BannerPatterns.BASE,
    BannerPatterns.SQUARE_BOTTOM_LEFT,
    BannerPatterns.SQUARE_BOTTOM_RIGHT,
    BannerPatterns.SQUARE_TOP_LEFT,
    BannerPatterns.SQUARE_TOP_RIGHT,
    BannerPatterns.STRIPE_BOTTOM,
    BannerPatterns.STRIPE_TOP,
    BannerPatterns.STRIPE_LEFT,
    BannerPatterns.STRIPE_RIGHT,
    BannerPatterns.STRIPE_CENTER,
    BannerPatterns.STRIPE_MIDDLE,
    BannerPatterns.STRIPE_DOWNRIGHT,
    BannerPatterns.STRIPE_DOWNLEFT,
    BannerPatterns.STRIPE_SMALL,
    BannerPatterns.CROSS,
    BannerPatterns.STRAIGHT_CROSS,
    BannerPatterns.TRIANGLE_BOTTOM,
    BannerPatterns.TRIANGLE_TOP,
    BannerPatterns.TRIANGLES_BOTTOM,
    BannerPatterns.TRIANGLES_TOP,
    BannerPatterns.DIAGONAL_LEFT,
    BannerPatterns.DIAGONAL_RIGHT,
    BannerPatterns.DIAGONAL_LEFT_MIRROR,
    BannerPatterns.DIAGONAL_RIGHT_MIRROR,
    BannerPatterns.CIRCLE_MIDDLE,
    BannerPatterns.RHOMBUS_MIDDLE,
    BannerPatterns.HALF_VERTICAL,
    BannerPatterns.HALF_HORIZONTAL,
    BannerPatterns.HALF_VERTICAL_MIRROR,
    BannerPatterns.HALF_HORIZONTAL_MIRROR,
    BannerPatterns.BORDER,
    BannerPatterns.CURLY_BORDER,
    BannerPatterns.GRADIENT,
    BannerPatterns.GRADIENT_UP,
    BannerPatterns.BRICKS,
    BannerPatterns.GLOBE,
    BannerPatterns.CREEPER,
    BannerPatterns.SKULL,
    BannerPatterns.FLOWER,
    BannerPatterns.MOJANG,
    BannerPatterns.PIGLIN,
    BannerPatterns.FLOW,
    BannerPatterns.GUSTER);

  /** Registers this sprite source */
  @Internal
  public static void register(RegisterSpriteSourceTypesEvent event) {
    if (TYPE == null) {
      TYPE = new SpriteSourceType(CODEC);
      event.register(TConstruct.getResource("shield_banner_to_modifier"), TYPE);
    }
  }

  @Override
  public void run(ResourceManager manager, Output output) {
    VANILLA_PATTERNS.forEach(pattern -> {
      Material material = new Material(Sheets.SHIELD_SHEET, pattern.location().withPrefix("entity/shield/"));
      ResourceLocation input = TEXTURE_ID_CONVERTER.idToFile(material.texture());
      Optional<Resource> resource = manager.getResource(input);
      if (resource.isEmpty()) {
        TConstruct.LOG.warn("Unable to find shield texture {} to create modifier sprite", input);
      } else {
        LazyLoadedImage image = new LazyLoadedImage(input, resource.get(), 1);
        ResourceLocation destination = destinationPrefix.withSuffix(MaterialRenderInfo.getSuffix(pattern.location()));
        output.add(destination, new BannerModifierSpriteSupplier(image, input, destination));
      }
    });
  }

  @Override
  public SpriteSourceType type() {
    return TYPE;
  }

  /** Generates a cropped sprite lazily */
  @RequiredArgsConstructor
  private class BannerModifierSpriteSupplier implements SpriteSupplier {
    private final LazyLoadedImage original;
    private final ResourceLocation input, output;

    @Nullable
    @Override
    public SpriteContents apply(SpriteResourceLoader loader) {
      try {
        // its possible the original is bigger than we expect due to HD pack, if so scale it accordingly
        // we only support scaling if it is a multiple of width
        NativeImage original = this.original.get();
        int scale = original.getWidth() / 64;
        if (scale == 0) {
          TConstruct.LOG.warn("Unable to crop {} to produce {} as texture size is less than 64", input, output);
        } else {
          NativeImage generated = new NativeImage(outSize * scale, outSize * scale, true);
          original.copyRect(generated, cropX * scale, cropY * scale, offsetX * scale, offsetY * scale, cropWidth * scale, cropHeight * scale, false, false);
          return new SpriteContents(this.output, new FrameSize(generated.getWidth(), generated.getHeight()), generated, ResourceMetadata.EMPTY);
        }
      } catch (IllegalArgumentException | IOException ex) {
        TConstruct.LOG.warn("Unable to crop {} to produce {}", this.input, this.output, ex);
      } finally {
        this.original.release();
      }
      return null;
    }
  }
}

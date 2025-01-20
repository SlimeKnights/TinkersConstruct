package slimeknights.tconstruct.library.client.data.material;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.library.client.data.GenericTextureGenerator;
import slimeknights.tconstruct.library.client.data.material.AbstractMaterialSpriteProvider.MaterialSpriteInfo;
import slimeknights.tconstruct.library.client.data.material.AbstractPartSpriteProvider.PartSpriteInfo;
import slimeknights.tconstruct.library.client.data.material.GeneratorPartTextureJsonGenerator.StatOverride;
import slimeknights.tconstruct.library.client.data.spritetransformer.ISpriteTransformer;
import slimeknights.tconstruct.library.client.data.util.AbstractSpriteReader;
import slimeknights.tconstruct.library.client.data.util.DataGenSpriteReader;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * Texture generator to generate textures for materials, supports adding a set of sprites to recolor, alongside a set of materials
 *
 * Note this only supports generating the crossproduct of materials and textures. If your addon adds both materials and tools, the best setup is having two generators:
 * <ul>
 *   <li>A generator adding all TiC and custom materials for your new sprites</li>
 *   <li>A generator adding all custom materials for TiC sprites</li>
 * </ul>
 * In case you need to divide into more than those two, it will be most efficient if each sprite is handled by only a single generator, so always split over sets of materials.
 */
public class MaterialPartTextureGenerator extends GenericTextureGenerator {
  /** Path to textures outputted by this generator */
  public static final String FOLDER = "textures";
  private final DataGenSpriteReader spriteReader;
  private final ExistingFileHelper existingFileHelper;
  /** Sprite provider */
  private final AbstractPartSpriteProvider partProvider;
  /** Materials to provide */
  private final AbstractMaterialSpriteProvider[] materialProviders;
  private final StatOverride overrides;

  public MaterialPartTextureGenerator(PackOutput packOutput, ExistingFileHelper existingFileHelper, AbstractPartSpriteProvider spriteProvider, AbstractMaterialSpriteProvider... materialProviders) {
    this(packOutput, existingFileHelper, spriteProvider, StatOverride.EMPTY, materialProviders);
  }

  public MaterialPartTextureGenerator(PackOutput packOutput, ExistingFileHelper existingFileHelper, AbstractPartSpriteProvider spriteProvider, StatOverride overrides, AbstractMaterialSpriteProvider... materialProviders) {
    super(packOutput, FOLDER);
    this.spriteReader = new DataGenSpriteReader(existingFileHelper, FOLDER);
    this.existingFileHelper = existingFileHelper;
    this.partProvider = spriteProvider;
    this.overrides = overrides;
    this.materialProviders = materialProviders;
  }

  @Override
  public String getName() {
    StringBuilder name = new StringBuilder();
    name.append("Material Part Generator - ");
    name.append(partProvider.getName());
    name.append(" - ");
    name.append(materialProviders[0].getName());
    for (int i = 1; i < materialProviders.length; i++) {
      name.append(", ").append(materialProviders[i].getName());
    }
    return name.toString();
  }


  @Override
  public CompletableFuture<?> run(CachedOutput cache) {
    runCallbacks(existingFileHelper, null);
    
    // ensure we have parts
    List<PartSpriteInfo> parts = partProvider.getSprites();
    if (parts.isEmpty()) {
      throw new IllegalStateException(partProvider.getName() + " has no parts, must have at least one part to generate");
    }

    // for each material list, generate sprites
    List<CompletableFuture<?>> tasks = new ArrayList<>();
    BiConsumer<ResourceLocation, NativeImage> saver = (path, image) -> tasks.add(saveImage(cache, path, image));
    BiConsumer<ResourceLocation, JsonObject> metaSaver = (path, meta) -> tasks.add(saveMetadata(cache, path, meta));
    for (AbstractMaterialSpriteProvider materialProvider : materialProviders) {
      Collection<MaterialSpriteInfo> materials = materialProvider.getMaterials().values();
      if (materials.isEmpty()) {
        throw new IllegalStateException(materialProvider.getName() + " has no materials, must have at least one material to generate");
      }
      // want cross product of textures
      for (MaterialSpriteInfo material : materials) {
        for (PartSpriteInfo part : parts) {
          // if the part skips variants and the material is a variant, skip
          if (!material.isVariant() || !part.isSkipVariants()) {
            // if any stat type matches, generate it
            for (MaterialStatsId statType : part.getStatTypes()) {
              if (material.supportStatType(statType) || overrides.hasOverride(statType, material.getTexture())) {
                ResourceLocation spritePath = outputPath(part, material);
                if (!spriteReader.exists(spritePath)) {
                  generateSprite(spriteReader, material, part, spritePath, saver, metaSaver);
                }
                break;
              }
            }
          }
        }
      }
    }
    return allOf(tasks).thenRunAsync(() -> {
      spriteReader.closeAll();
      partProvider.cleanCache();
      runCallbacks(null, null);
    });
  }

  /** Gets the output path for a given sprite */
  public static ResourceLocation outputPath(PartSpriteInfo part, MaterialSpriteInfo material) {
    // path format: pNamespace:pPath_mNamespace_mPath
    ResourceLocation materialTexture = material.getTexture();
    return part.getPath().withSuffix("_" + materialTexture.getNamespace() + "_" + materialTexture.getPath());
  }

  /**
   * Generates the given sprite
   * @param spriteReader    Reader to find existing sprites
   * @param material        Material for the sprite
   * @param part            Part for the sprites
   * @param saver           Function to save the images
   * @param metaSaver       Function to save the animation metadata
   */
  public static void generateSprite(AbstractSpriteReader spriteReader, MaterialSpriteInfo material, PartSpriteInfo part, ResourceLocation spritePath, BiConsumer<ResourceLocation, NativeImage> saver, BiConsumer<ResourceLocation,JsonObject> metaSaver) {
    // image does not exist? first step is to find a base image
    NativeImage base = null;
    for (String fallback : material.getFallbacks()) {
      base = part.getTexture(spriteReader, fallback);
      if (base != null) {
        break;
      }
    }
    // no fallback existed, try the main one
    if (base == null) {
      base = part.getTexture(spriteReader, "");
    }
    if (base == null) {
      throw new IllegalStateException("Missing sprite at " + part.getPath() + ".png, cannot generate textures");
    }
    // successfully found a texture, now transform and save
    ISpriteTransformer transformer = material.getTransformer();
    NativeImage transformed = transformer.transformCopy(base, part.isAllowAnimated());
    spriteReader.track(transformed);
    saver.accept(spritePath, transformed);
    if (part.isAllowAnimated()) {
      JsonObject meta = transformer.animationMeta(base);
      if (meta != null) {
        metaSaver.accept(spritePath, meta);
      }
    }
  }


  /* Static callbacks, handled this way as the event bus is a pain to use during datagen */

  /** List of callbacks */
  private static final List<IPartTextureCallback> TEXTURE_CALLBACKS = new ArrayList<>();

  /** Registers a callback to run whenever sprites are generated. */
  public static void registerCallback(IPartTextureCallback callback) {
    TEXTURE_CALLBACKS.add(callback);
  }

  /** Runs all callbacks */
  public static void runCallbacks(@Nullable ExistingFileHelper existingFileHelper, @Nullable ResourceManager manager) {
    for (IPartTextureCallback callback : TEXTURE_CALLBACKS) {
      callback.accept(existingFileHelper, manager);
    }
  }

  public interface IPartTextureCallback {
    /**
     * Tells the given callback that texture generating is either starting or ending. Both parameters being null means texture generating is ending
     * @param existingFileHelper  If nonnull, datagenerators are starting
     * @param manager             If nonnull, command is starting
     */
    void accept(@Nullable ExistingFileHelper existingFileHelper, @Nullable ResourceManager manager);
  }
}

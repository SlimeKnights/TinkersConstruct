package slimeknights.tconstruct.shared.client;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.ClickEvent.Action;
import net.minecraftforge.common.MinecraftForge;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.data.material.AbstractMaterialSpriteProvider.MaterialSpriteInfo;
import slimeknights.tconstruct.library.client.data.material.AbstractPartSpriteProvider;
import slimeknights.tconstruct.library.client.data.material.AbstractPartSpriteProvider.PartSpriteInfo;
import slimeknights.tconstruct.library.client.data.material.MaterialPartTextureGenerator;
import slimeknights.tconstruct.library.client.data.spritetransformer.GreyToColorMapping;
import slimeknights.tconstruct.library.client.data.spritetransformer.RecolorSpriteTransformer;
import slimeknights.tconstruct.library.client.data.util.AbstractSpriteReader;
import slimeknights.tconstruct.library.client.data.util.ResourceManagerSpriteReader;
import slimeknights.tconstruct.library.client.events.GeneratePartTexturesEvent;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.utils.JsonUtils;
import slimeknights.tconstruct.shared.network.GeneratePartTexturesPacket.Operation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/** Actual logic to generate tool textures */
@Log4j2
public class ClientGeneratePartTexturesCommand {
  private static final String SUCCESS_KEY = TConstruct.makeTranslationKey("command", "generate_part_textures.finish");
  /** Path to add the data */
  private static final String PACK_NAME = "TinkersConstructGeneratedPartTextures";

  /** Gets the clickable output link */
  protected static ITextComponent getOutputComponent(File file) {
    return (new StringTextComponent(file.getAbsolutePath())).modifyStyle((style) -> style.setUnderlined(true).setClickEvent(new ClickEvent(Action.OPEN_FILE, file.getAbsolutePath())));
  }

  /** Generates all textures using the resource pack list */
  public static void generateTextures(Operation operation, String modId, String materialPath) {
    long time = System.nanoTime();
    // first step: get a list of all materials and sprites to generate
    GeneratePartTexturesEvent event = new GeneratePartTexturesEvent();
    MinecraftForge.EVENT_BUS.post(event);
    // Predicate to check if a material ID is valid
    Predicate<ResourceLocation> validMaterialId = loc -> (modId.isEmpty() || modId.equals(loc.getNamespace())) && (materialPath.isEmpty() || materialPath.equals(loc.getPath()));
    // get all materials, filtered by the given parameters
    Map<ResourceLocation,MaterialSpriteInfo> spriteInfoMap = event.getMaterialSprites().stream()
                                                                  .flatMap(provider -> provider.getMaterials().values().stream())
                                                                  .filter(sprite -> validMaterialId.test(sprite.getTexture()))
                                                                  .collect(Collectors.toMap(MaterialSpriteInfo::getTexture, Function.identity()));

    // next, update the map with generators from JSON
    IResourceManager manager = Minecraft.getInstance().getResourceManager();
    loadMaterialRenderInfoGenerators(manager, validMaterialId, spriteInfoMap::put);

    // prepare the output directory
    Path path = Minecraft.getInstance().getFileResourcePacks().toPath().resolve(PACK_NAME);
    BiConsumer<ResourceLocation,NativeImage> saver = (outputPath, image) -> saveImage(path, outputPath, image);

    // create a pack.mcmeta so its a valid resource pack
    savePackMcmeta(path);

    // predicate for whether we should generate the texture
    AbstractSpriteReader spriteReader = new ResourceManagerSpriteReader(manager, MaterialPartTextureGenerator.FOLDER);
    Predicate<ResourceLocation> shouldGenerate;
    if (operation == Operation.ALL) {
      shouldGenerate = exists -> true;
    } else {
      shouldGenerate = loc -> !spriteReader.exists(loc);
    }

    // at this point in time we have all our materials, time to generate our sprites
    for (MaterialSpriteInfo material : spriteInfoMap.values()) {
      for (AbstractPartSpriteProvider spriteProvider : event.getPartSprites()) {
        for (PartSpriteInfo part : spriteProvider.getSprites()) {
          if (material.supportStatType(part.getStatType())) {
            MaterialPartTextureGenerator.generateSprite(spriteReader, material, part, shouldGenerate, saver);
          }
        }
        spriteProvider.cleanCache();
      }
    }
    spriteReader.closeAll();

    // success message
    long deltaTime = System.nanoTime() - time;
    log.info("Finished generating textures in {} ms", deltaTime / 1000000f);
    if (Minecraft.getInstance().player != null) {
      Minecraft.getInstance().player.sendStatusMessage(new TranslationTextComponent(SUCCESS_KEY, (deltaTime / 1000000) / 1000f, getOutputComponent(path.toFile())), false);
    }
  }

  /** Creates the MCMeta to make this a valid resource pack */
  private static void savePackMcmeta(Path folder) {
    Path path = folder.resolve("pack.mcmeta");
    JsonObject meta = new JsonObject();
    JsonObject pack = new JsonObject();
    pack.addProperty("description", "Generated Resources from the Tinkers' Construct Part Texture Generator");
    pack.addProperty("pack_format", 6);
    meta.add("pack", pack);

    try {
      Files.createDirectories(path.getParent());
      String json = MaterialRenderInfoLoader.GSON.toJson(meta);
      try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path)) {
        bufferedwriter.write(json);
      }
    } catch (IOException e) {
      log.error("Couldn't create pack.mcmeta for part textures", e);
    }
  }

  /** Saves an image to the output folder */
  private static void saveImage(Path folder, ResourceLocation location, NativeImage image) {
    Path path = folder.resolve(Paths.get(ResourcePackType.CLIENT_RESOURCES.getDirectoryName(),
                location.getNamespace(), MaterialPartTextureGenerator.FOLDER, location.getPath() + ".png"));
    try {
      Files.createDirectories(path.getParent());
      image.write(path);
    } catch (IOException e) {
      log.error("Couldn't create image for {}", location, e);
    }
  }

  /**
   * Loads all material render info that contain palette generator info into the given consumer
   * @param manager          Resource manager instance
   * @param validMaterialId  Predicate to check if a material ID should be considered
   * @param consumer         Consumer for completed material sprite info
   */
  private static void loadMaterialRenderInfoGenerators(IResourceManager manager, Predicate<ResourceLocation> validMaterialId, BiConsumer<ResourceLocation, MaterialSpriteInfo> consumer) {
    int trim = MaterialRenderInfoLoader.FOLDER.length() + 1;
    for(ResourceLocation location : manager.getAllResourceLocations(MaterialRenderInfoLoader.FOLDER, loc -> loc.endsWith(".json"))) {
      // clean up ID by trimming off the extension
      String path = location.getPath();
      MaterialId id = new MaterialId(location.getNamespace(), path.substring(trim, path.length() - 5));

      // ensure its a material we care about
      if (validMaterialId.test(id)) {
        try (
          IResource iresource = manager.getResource(location);
          InputStream inputstream = iresource.getInputStream();
          Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8))
        ) {
          // there are two keys we care about: fallbacks and generator
          JsonObject json = MaterialRenderInfoLoader.GSON.fromJson(reader, JsonObject.class);
          if (json.has("generator")) {
            // generator tells us what stats we care about and gives us our palette
            JsonObject generator = JSONUtils.getJsonObject(json, "generator");
            Set<MaterialStatsId> stats = ImmutableSet.copyOf(JsonHelper.parseList(generator, "supported_stats", (element, name) -> new MaterialStatsId(JsonUtils.getResourceLocation(element, name))));
            JsonArray palette = JSONUtils.getJsonArray(generator, "palette");
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

            // finally, we want fallbacks if present
            String[] fallbacks = new String[0];
            if (json.has("fallbacks")) {
              fallbacks = JsonHelper.parseList(json, "fallbacks", JSONUtils::getString).toArray(fallbacks);
            }
            // finally, build the sprite info
            consumer.accept(id, new MaterialSpriteInfo(id, fallbacks, new RecolorSpriteTransformer(paletteBuilder.build()), stats));
          }
        } catch (JsonSyntaxException e) {
          log.error("Failed to read tool part texture generator info for {}", id, e);
        } catch (Exception e) {
          // NO-OP, that is a resource pack bug, not our job
        }
      }
    }
  }
}

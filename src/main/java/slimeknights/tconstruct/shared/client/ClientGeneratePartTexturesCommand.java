package slimeknights.tconstruct.shared.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.NativeImage;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.mutable.MutableInt;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.data.material.AbstractMaterialSpriteProvider.MaterialSpriteInfo;
import slimeknights.tconstruct.library.client.data.material.AbstractPartSpriteProvider.PartSpriteInfo;
import slimeknights.tconstruct.library.client.data.material.GeneratorPartTextureJsonGenerator.StatOverride;
import slimeknights.tconstruct.library.client.data.material.MaterialPartTextureGenerator;
import slimeknights.tconstruct.library.client.data.util.AbstractSpriteReader;
import slimeknights.tconstruct.library.client.data.util.ResourceManagerSpriteReader;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoJson;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoJson.MaterialGeneratorJson;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.client.model.DynamicTextureLoader;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.shared.network.GeneratePartTexturesPacket.Operation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/** Actual logic to generate tool textures */
@Log4j2
public class ClientGeneratePartTexturesCommand {
  private static final String SUCCESS_KEY = TConstruct.makeTranslationKey("command", "generate_part_textures.finish");
  private static final Component NO_PARTS = TConstruct.makeTranslation("command", "generate_part_textures.no_parts");
  private static final Component NO_MATERIALS = TConstruct.makeTranslation("command", "generate_part_textures.no_materials");
  /** Path to add the data */
  private static final String PACK_NAME = "TinkersConstructGeneratedPartTextures";
  /** Part file to load, pulls from all namespaces, but no merging */
  private static final String GENERATOR_PART_TEXTURES = "tinkering/generator_part_textures.json";

  /** Gets the clickable output link */
  protected static Component getOutputComponent(File file) {
    return (Component.literal(file.getAbsolutePath())).withStyle((style) -> style.withUnderlined(true).withClickEvent(new ClickEvent(Action.OPEN_FILE, file.getAbsolutePath())));
  }

  /** Generates all textures using the resource pack list */
  public static void generateTextures(Operation operation, String modId, String materialPath) {
    long time = System.nanoTime();
    ResourceManager manager = Minecraft.getInstance().getResourceManager();
    // the forge mod bus is annoying, but stuck using it due to the normal bus not existing at datagen time
    MaterialPartTextureGenerator.runCallbacks(null, manager);

    Player player = Minecraft.getInstance().player;

    // get the list of sprites
    GeneratorConfiguration generatorConfig = loadGeneratorConfig(manager);
    if (generatorConfig.sprites.isEmpty()) {
      if (player != null) {
        player.displayClientMessage(NO_PARTS, false);
      }
      return;
    }

    // Predicate to check if a material ID is valid
    // TODO: variant filter?
    Predicate<MaterialVariantId> validMaterialId = loc -> (modId.isEmpty() || modId.equals(loc.getId().getNamespace())) && (materialPath.isEmpty() || materialPath.equals(loc.getId().getPath()));

    // get all materials, filtered by the given parameters
    List<MaterialSpriteInfo> materialSprites = loadMaterialRenderInfoGenerators(manager, validMaterialId);
    if (materialSprites.isEmpty()) {
      if (player != null) {
        player.displayClientMessage(NO_MATERIALS, false);
      }
      return;
    }

    // prepare the output directory
    Path path = Minecraft.getInstance().getResourcePackDirectory().toPath().resolve(PACK_NAME);
    BiConsumer<ResourceLocation,NativeImage> saver = (outputPath, image) -> saveImage(path, outputPath, image);
    BiConsumer<ResourceLocation,JsonObject> metaSaver = (outputPath, image) -> saveMetadata(path, outputPath, image);

    // create a pack.mcmeta so its a valid resource pack
    savePackMcmeta(path);

    // predicate for whether we should generate the texture
    AbstractSpriteReader spriteReader = new ResourceManagerSpriteReader(manager, MaterialPartTextureGenerator.FOLDER);
    MutableInt generated = new MutableInt(0); // keep track of how many generated
    Predicate<ResourceLocation> shouldGenerate;
    if (operation == Operation.ALL) {
      shouldGenerate = exists -> {
        generated.add(1);
        return true;
      };
    } else {
      shouldGenerate = loc -> {
        if (!spriteReader.exists(loc)) {
          generated.add(1);
          return true;
        }
        return false;
      };
    }

    // at this point in time we have all our materials, time to generate our sprites
    for (MaterialSpriteInfo material : materialSprites) {
      for (PartSpriteInfo part : generatorConfig.sprites) {
        for (MaterialStatsId statType : part.getStatTypes()) {
          if (material.supportStatType(statType) || generatorConfig.statOverrides.hasOverride(statType, material.getTexture())) {
            MaterialPartTextureGenerator.generateSprite(spriteReader, material, part, shouldGenerate, saver, metaSaver);
            break;
          }
        }
      }
    }
    spriteReader.closeAll();
    DynamicTextureLoader.clearCache();

    // success message
    long deltaTime = System.nanoTime() - time;
    int count = generated.getValue();
    MaterialPartTextureGenerator.runCallbacks(null, null);
    log.info("Finished generating {} textures in {} ms", count, deltaTime / 1000000f);
    if (Minecraft.getInstance().player != null) {
      Minecraft.getInstance().player.displayClientMessage(Component.translatable(SUCCESS_KEY, count, (deltaTime / 1000000) / 1000f, getOutputComponent(path.toFile())), false);
    }
  }

  /** Creates the MCMeta to make this a valid resource pack */
  private static void savePackMcmeta(Path folder) {
    Path path = folder.resolve("pack.mcmeta");
    JsonObject meta = new JsonObject();
    JsonObject pack = new JsonObject();
    pack.addProperty("description", "Generated Resources from the Tinkers' Construct Part Texture Generator");
    pack.addProperty("pack_format", 8);
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
    Path path = folder.resolve(Paths.get(PackType.CLIENT_RESOURCES.getDirectory(),
                location.getNamespace(), MaterialPartTextureGenerator.FOLDER, location.getPath() + ".png"));
    try {
      Files.createDirectories(path.getParent());
      image.writeToFile(path);
    } catch (IOException e) {
      log.error("Couldn't create image for {}", location, e);
    }
  }

  /** Saves metadata to the output folder */
  private static void saveMetadata(Path folder, ResourceLocation location, JsonObject meta) {
    Path path = folder.resolve(Paths.get(PackType.CLIENT_RESOURCES.getDirectory(),
                                         location.getNamespace(), MaterialPartTextureGenerator.FOLDER, location.getPath() + ".png.mcmeta"));
    try {
      Files.createDirectories(path.getParent());
      String json = MaterialRenderInfoLoader.GSON.toJson(meta);
      try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path)) {
        bufferedwriter.write(json);
      }
    } catch (IOException e) {
      log.error("Couldn't create metadata for {}", location, e);
    }
  }

  /** Record holding config from the generator JSON file */
  private record GeneratorConfiguration(Collection<PartSpriteInfo> sprites, StatOverride statOverrides) {}

  /** Loads all part sprites file */
  private static GeneratorConfiguration loadGeneratorConfig(ResourceManager manager) {
    Map<ResourceLocation,PartSpriteInfo> builder = new HashMap<>();
    StatOverride.Builder stats = new StatOverride.Builder();

    // each namespace loads separately
    for (String namespace : manager.getNamespaces()) {
      ResourceLocation location = new ResourceLocation(namespace, GENERATOR_PART_TEXTURES);
      List<Resource> resources = manager.getResourceStack(location);
      if (!resources.isEmpty()) {
        // if the namespace has the file, we will start building
        // start from the top most pack and work down, lets us break the loop as soon as we find a "replace"
        for (int r = resources.size() - 1; r >= 0; r--) {
          Resource resource = resources.get(r);
          try (BufferedReader reader = resource.openAsReader()) {
            JsonObject object = GsonHelper.parse(reader);
            for (PartSpriteInfo part : PartSpriteInfo.LIST_LOADABLE.getIfPresent(object, "parts")) {
              // if the element already exists, merge it. We already know the path matches
              builder.merge(part.getPath(), part, (part1, part2) -> {
                // allow animated should match, if not default to not animated as they probably had a good reason to disallow
                boolean allowAnimated = part1.isAllowAnimated();
                if (allowAnimated != part2.isAllowAnimated()) {
                  TConstruct.LOG.error("Texture {} has mismatching allowAnimated, forcing allow animated to false", part1.getPath());
                }
                // merge stat types together
                return new PartSpriteInfo(
                  part1.getPath(),
                  Streams.concat(part1.getStatTypes().stream(), part2.getStatTypes().stream()).collect(Collectors.toSet()),
                  allowAnimated);
              });
            }
            if (object.has("overrides")) {
              for (Entry<String,JsonElement> entry : GsonHelper.getAsJsonObject(object, "overrides").entrySet()) {
                String key = entry.getKey();
                MaterialStatsId statId = MaterialStatsId.PARSER.tryParse(key);
                if (statId == null) {
                  TConstruct.LOG.error("Invalid stat ID " + key);
                } else {
                  JsonArray array = GsonHelper.convertToJsonArray(entry.getValue(), key);
                  for (int i = 0; i < array.size(); i++) {
                    stats.addVariant(statId, MaterialVariantId.parse(GsonHelper.convertToString(array.get(i), key + '[' + i + ']')));
                  }
                }
              }
            }

            // if we find replace, don't process lower files from this namespace
            if (GsonHelper.getAsBoolean(object, "replace", false)) {
              break;
            }
          } catch (IOException ex) {
            log.error("Failed to load modifier models from {} for pack {}", location, resource.sourcePackId(), ex);
          }
        }
      }
    }
    return new GeneratorConfiguration(builder.values(), stats.build());
  }

  /**
   * Loads all material render info that contain palette generator info into the given consumer
   * @param manager          Resource manager instance
   * @param validMaterialId  Predicate to check if a material ID should be considered
   * @return List of material sprites loaded
   */
  private static List<MaterialSpriteInfo> loadMaterialRenderInfoGenerators(ResourceManager manager, Predicate<MaterialVariantId> validMaterialId) {
    ImmutableList.Builder<MaterialSpriteInfo> builder = ImmutableList.builder();

    for(Entry<ResourceLocation,Resource> entry : manager.listResources(MaterialRenderInfoLoader.FOLDER, loc -> loc.getPath().endsWith(".json")).entrySet()) {
      // clean up ID by trimming off the extension
      ResourceLocation location = entry.getKey();
      String localPath = JsonHelper.localize(location.getPath(), MaterialRenderInfoLoader.FOLDER, ".json");

      // locate variant as a subfolder, and create final ID
      String variant = "";
      int slashIndex = localPath.lastIndexOf('/');
      if (slashIndex >= 0) {
        variant = localPath.substring(slashIndex + 1);
        localPath = localPath.substring(0, slashIndex);
      }
      MaterialVariantId id = MaterialVariantId.create(location.getNamespace(), localPath, variant);

      // ensure its a material we care about
      if (validMaterialId.test(id)) {
        try (Reader reader = entry.getValue().openAsReader()) {
          // if the JSON has generator info, add it to the consumer
          MaterialRenderInfoJson json = MaterialRenderInfoLoader.GSON.fromJson(reader, MaterialRenderInfoJson.class);
          MaterialGeneratorJson generator = json.getGenerator();
          if (generator != null) {
            builder.add(new MaterialSpriteInfo(Objects.requireNonNullElse(json.getTexture(), id.getLocation('_')), Objects.requireNonNullElse(json.getFallbacks(), new String[0]), generator));
          }
        } catch (JsonSyntaxException e) {
          log.error("Failed to read tool part texture generator info for {}", id, e);
        } catch (Exception e) {
          // NO-OP, that is a resource pack bug, not our job
        }
      }
    }
    return builder.build();
  }
}

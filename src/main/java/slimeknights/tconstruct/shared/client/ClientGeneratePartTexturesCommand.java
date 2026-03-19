package slimeknights.tconstruct.shared.client;

import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.NativeImage;
import lombok.extern.log4j.Log4j2;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.mutable.MutableInt;
import slimeknights.mantle.command.GeneratePackHelper;
import slimeknights.mantle.data.datamap.RegistryDataMapLoader;
import slimeknights.mantle.data.loadable.ErrorFactory;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.mantle.util.typed.TypedMap;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.data.material.AbstractMaterialSpriteProvider.MaterialSpriteInfo;
import slimeknights.tconstruct.library.client.data.material.AbstractPartSpriteProvider.PartSpriteInfo;
import slimeknights.tconstruct.library.client.data.material.GeneratorPartTextureJsonGenerator.StatOverride;
import slimeknights.tconstruct.library.client.data.material.MaterialPartTextureGenerator;
import slimeknights.tconstruct.library.client.data.util.AbstractSpriteReader;
import slimeknights.tconstruct.library.client.data.util.ResourceManagerSpriteReader;
import slimeknights.tconstruct.library.client.materials.MaterialGeneratorInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.shared.network.GeneratePartTexturesPacket.Operation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/** Actual logic to generate tool textures */
@Log4j2
public class ClientGeneratePartTexturesCommand {
  private static final String SUCCESS_KEY = TConstruct.makeTranslationKey("command", "generate_part_textures.finish");
  private static final String FAILURE_KEY = TConstruct.makeTranslationKey("command", "generate_part_textures.failure");
  private static final Component NO_PARTS = TConstruct.makeTranslation("command", "generate_part_textures.no_parts");
  private static final Component NO_MATERIALS = TConstruct.makeTranslation("command", "generate_part_textures.no_materials");
  /** Path to add the data */
  private static final String PACK_NAME = "TinkersConstructGeneratedPartTextures";
  /** Part file to load, pulls from all namespaces, but no merging */
  private static final String GENERATOR_PART_TEXTURES = "tinkering/generator_part_textures.json";

  /** @deprecated use {@link GeneratePackHelper#getOutputComponent(File)} */
  @Deprecated(forRemoval = true)
  protected static Component getOutputComponent(File file) {
    return GeneratePackHelper.getOutputComponent(file);
  }

  /** Generates all textures using the resource pack list */
  public static void generateTextures(Operation operation, String modId, String materialPath) {
    long time = System.nanoTime();
    try {
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
      Path path = Minecraft.getInstance().getResourcePackDirectory().resolve(PACK_NAME);
      BiConsumer<ResourceLocation, NativeImage> saver = (outputPath, image) -> saveImage(path, outputPath, image);
      BiConsumer<ResourceLocation, JsonObject> metaSaver = (outputPath, image) -> saveMetadata(path, outputPath, image);

      // create a pack.mcmeta so its a valid resource pack
      GeneratePackHelper.saveMcmeta(path, PackType.CLIENT_RESOURCES, "Generated Resources from the Tinkers' Construct Part Texture Generator");

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
          // if the part skips variants and the material is a variant, skip
          if (!material.isVariant() || !part.isSkipVariants()) {
            for (MaterialStatsId statType : part.getStatTypes()) {
              if (material.supportStatType(statType) || generatorConfig.statOverrides.hasOverride(statType, material.getTexture())) {
                ResourceLocation spritePath = MaterialPartTextureGenerator.outputPath(part, material);
                if (shouldGenerate.test(spritePath)) {
                  MaterialPartTextureGenerator.generateSprite(spriteReader, material, part, spritePath, saver, metaSaver);
                }
                break;
              }
            }
          }
        }
      }
      spriteReader.closeAll();

      // success message
      long deltaTime = System.nanoTime() - time;
      int count = generated.getValue();
      MaterialPartTextureGenerator.runCallbacks(null, null);
      log.info("Finished generating {} textures in {} ms", count, deltaTime / 1000000f);
      if (Minecraft.getInstance().player != null) {
        Minecraft.getInstance().player.displayClientMessage(Component.translatable(SUCCESS_KEY, count, (deltaTime / 1000000) / 1000f, GeneratePackHelper.getOutputComponent(path.toFile())), false);
      }
    } catch (Exception e) {
      long deltaTime = System.nanoTime() - time;
      log.error("Failed to generate part textures after {} ms", deltaTime / 1000000f, e);
      if (Minecraft.getInstance().player != null) {
        Minecraft.getInstance().player.displayClientMessage(Component.translatable(FAILURE_KEY, (deltaTime / 1000000) / 1000f, e.getMessage()).withStyle(ChatFormatting.RED), false);
      }
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
      String json = JsonHelper.DEFAULT_GSON.toJson(meta);
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
  @SuppressWarnings("removal")
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
                  allowAnimated = false;
                }
                // merge stat types together
                return new PartSpriteInfo(
                  part1.getPath(),
                  Streams.concat(part1.getStatTypes().stream(), part2.getStatTypes().stream()).collect(Collectors.toSet()),
                  allowAnimated,
                  // if either sprite wants variants we are going to need them
                  part1.isSkipVariants() && part2.isSkipVariants());
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
          } catch (Exception ex) {
            log.error("Failed to load generator config from {} for pack {}", location, resource.sourcePackId(), ex);
          }
        }
      }
    }
    return new GeneratorConfiguration(builder.values(), stats.build());
  }

  /** We are using loadables just for JSON parsing. Its not *exactly* made for that so we are basically using this to no-op the getter */
  private static final MaterialRenderInfo EMPTY = new MaterialRenderInfo(IMaterial.UNKNOWN_ID, null, new String[0], -1, 0);

  /** Loadable for a sprite info */
  private static final RecordLoadable<MaterialSpriteInfo> SPRITE_LOADER = RecordLoadable.create(
    MaterialRenderInfo.LOADABLE.directField(info -> EMPTY),
    MaterialGeneratorInfo.LOADABLE.requiredField("generator", Function.identity()),
    ErrorFactory.FIELD,
    (render, generator, error) -> {
      ResourceLocation texture = render.texture();
      if (texture == null) {
        throw error.create("Unable to create generator for material " + render.id() + " as it has no texture despite having a generator");
      }
      return new MaterialSpriteInfo(texture, render.fallbacks(), generator);
    });

  /**
   * Loads all material render info that contain palette generator info into the given consumer
   * @param manager          Resource manager instance
   * @param validMaterialId  Predicate to check if a material ID should be considered
   * @return List of material sprites loaded
   */
  private static List<MaterialSpriteInfo> loadMaterialRenderInfoGenerators(ResourceManager manager, Predicate<MaterialVariantId> validMaterialId) {
    // first, we need to fetch all relevant JSON files
    Map<ResourceLocation,JsonElement> jsons = new HashMap<>();
    SimpleJsonResourceReloadListener.scanDirectory(manager, MaterialRenderInfoLoader.FOLDER, JsonHelper.DEFAULT_GSON, jsons);
    // final results map from texture name to sprite info
    Map<ResourceLocation,MaterialSpriteInfo> builder = new HashMap<>();

    for(Entry<ResourceLocation, JsonElement> entry : jsons.entrySet()) {
      // clean up ID by trimming off the extension
      ResourceLocation location = entry.getKey();
      MaterialVariantId id = MaterialRenderInfoLoader.variant(location);

      // ensure its a material we care about
      if (validMaterialId.test(id)) {
        try {
          // can save some time parsing if they lack a generator as that means no textures to make
          // parent might mean they have a generator indirectly, but we only care if that parent is more than a redirect
          JsonObject json = GsonHelper.convertToJsonObject(entry.getValue(), location.toString());
          if (json.has("generator")) {
            TypedMap context = MaterialRenderInfoLoader.createContext(id);
            MaterialSpriteInfo info = RegistryDataMapLoader.parseData("Material Generator Info", jsons, location, json, null, SPRITE_LOADER, context);
            MaterialSpriteInfo oldInfo = builder.putIfAbsent(info.getTexture(), info);
            if (oldInfo != null) {
              TConstruct.LOG.error("Received multiple generators for texture {}: {}, {}", info.getTexture(), oldInfo.getTransformer(), info.getTransformer());
            }
          }
        } catch (JsonSyntaxException e) {
          log.error("Failed to read tool part texture generator info for {}", id, e);
        }
      }
    }
    // ensure we only have at most 1 generator with a given texture
    return List.copyOf(builder.values());
  }
}

package slimeknights.tconstruct.shared.client;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.NativeImage;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
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
import slimeknights.tconstruct.library.client.data.material.MaterialPartTextureGenerator;
import slimeknights.tconstruct.library.client.data.util.AbstractSpriteReader;
import slimeknights.tconstruct.library.client.data.util.ResourceManagerSpriteReader;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoJson;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoJson.MaterialGeneratorJson;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
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
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

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
    return (new TextComponent(file.getAbsolutePath())).withStyle((style) -> style.setUnderlined(true).withClickEvent(new ClickEvent(Action.OPEN_FILE, file.getAbsolutePath())));
  }

  /** Generates all textures using the resource pack list */
  public static void generateTextures(Operation operation, String modId, String materialPath) {
    long time = System.nanoTime();
    ResourceManager manager = Minecraft.getInstance().getResourceManager();
    // the forge mod bus is annoying, but stuck using it due to the normal bus not existing at datagen time
    MaterialPartTextureGenerator.runCallbacks(null, manager);

    Player player = Minecraft.getInstance().player;

    // get the list of sprites
    List<PartSpriteInfo> partSprites = loadPartSprites(manager);
    if (partSprites.isEmpty()) {
      if (player != null) {
        player.displayClientMessage(NO_PARTS, false);
      }
      return;
    }

    // Predicate to check if a material ID is valid
    Predicate<ResourceLocation> validMaterialId = loc -> (modId.isEmpty() || modId.equals(loc.getNamespace())) && (materialPath.isEmpty() || materialPath.equals(loc.getPath()));

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
      for (PartSpriteInfo part : partSprites) {
        if (material.supportStatType(part.getStatType())) {
          MaterialPartTextureGenerator.generateSprite(spriteReader, material, part, shouldGenerate, saver);
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
      Minecraft.getInstance().player.displayClientMessage(new TranslatableComponent(SUCCESS_KEY, count, (deltaTime / 1000000) / 1000f, getOutputComponent(path.toFile())), false);
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
    Path path = folder.resolve(Paths.get(PackType.CLIENT_RESOURCES.getDirectory(),
                location.getNamespace(), MaterialPartTextureGenerator.FOLDER, location.getPath() + ".png"));
    try {
      Files.createDirectories(path.getParent());
      image.writeToFile(path);
    } catch (IOException e) {
      log.error("Couldn't create image for {}", location, e);
    }
  }

  /** Loads all part sprites file */
  private static List<PartSpriteInfo> loadPartSprites(ResourceManager manager) {
    ImmutableList.Builder<PartSpriteInfo> builder = ImmutableList.builder();

    // each namespace loads separately
    for (String namespace : manager.getNamespaces()) {
      ResourceLocation location = new ResourceLocation(namespace, GENERATOR_PART_TEXTURES);
      if (manager.hasResource(location)) {
        // if the namespace has the file, we will start building
        try {
          // start from the top most pack and work down, lets us break the loop as soon as we find a "replace"
          List<Resource> resources = manager.getResources(location);
          for (int r = resources.size() - 1; r >= 0; r--) {
            Resource resource = resources.get(r);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
              JsonObject object = GsonHelper.parse(reader);
              List<PartSpriteInfo> parts = JsonHelper.parseList(object, "parts", (element, name) -> {
                JsonObject part = GsonHelper.convertToJsonObject(element, name);
                ResourceLocation path = JsonHelper.getResourceLocation(part, "path");
                MaterialStatsId statId = new MaterialStatsId(JsonHelper.getResourceLocation(part, "statType"));
                return new PartSpriteInfo(path, statId);
              });
              builder.addAll(parts);

              // if we find replace, don't process lower files from this namespace
              if (GsonHelper.getAsBoolean(object, "replace", false)) {
                break;
              }
            } catch (IOException ex) {
              log.error("Failed to load modifier models from {} for pack {}", location, resource.getSourceName(), ex);
            }
          }
        } catch (IOException ex) {
          log.error("Failed to load modifier models from {}", location, ex);
        }
      }
    }
    return builder.build();
  }

  /**
   * Loads all material render info that contain palette generator info into the given consumer
   * @param manager          Resource manager instance
   * @param validMaterialId  Predicate to check if a material ID should be considered
   * @return List of material sprites loaded
   */
  private static List<MaterialSpriteInfo> loadMaterialRenderInfoGenerators(ResourceManager manager, Predicate<ResourceLocation> validMaterialId) {
    ImmutableList.Builder<MaterialSpriteInfo> builder = ImmutableList.builder();

    int trim = MaterialRenderInfoLoader.FOLDER.length() + 1;
    for(ResourceLocation location : manager.listResources(MaterialRenderInfoLoader.FOLDER, loc -> loc.endsWith(".json"))) {
      // clean up ID by trimming off the extension
      String path = location.getPath();
      MaterialId id = new MaterialId(location.getNamespace(), path.substring(trim, path.length() - 5));

      // ensure its a material we care about
      if (validMaterialId.test(id)) {
        try (
          Resource iresource = manager.getResource(location);
          InputStream inputstream = iresource.getInputStream();
          Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8))
        ) {
          // if the JSON has generator info, add it to the consumer
          MaterialRenderInfoJson json = MaterialRenderInfoLoader.GSON.fromJson(reader, MaterialRenderInfoJson.class);
          MaterialGeneratorJson generator = json.getGenerator();
          if (generator != null) {
            builder.add(new MaterialSpriteInfo(Objects.requireNonNullElse(json.getTexture(), id), Objects.requireNonNullElse(json.getFallbacks(), new String[0]), generator));
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

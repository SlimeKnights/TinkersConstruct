package slimeknights.tconstruct.library.client.data.material;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.mantle.data.ResourceLocationSerializer;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.data.material.AbstractPartSpriteProvider.PartSpriteInfo;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

import java.io.IOException;

/** Generates the file that tells the part generator command which parts are needed for your tools */
public class GeneratorPartTextureJsonGenerator extends GenericDataProvider {
  /** GSON adapter for material info deserializing */
  public static final Gson GSON = (new GsonBuilder())
    .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
    .registerTypeAdapter(MaterialStatsId.class, new ResourceLocationSerializer<>(MaterialStatsId::new, TConstruct.MOD_ID))
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create();

  private final String modId;
  private final AbstractPartSpriteProvider spriteProvider;
  public GeneratorPartTextureJsonGenerator(DataGenerator generator, String modId, AbstractPartSpriteProvider spriteProvider) {
    super(generator, PackType.CLIENT_RESOURCES, "tinkering", GSON);
    this.modId = modId;
    this.spriteProvider = spriteProvider;
  }

  @Override
  public void run(HashCache cache) throws IOException {
    JsonObject json = new JsonObject();
    json.addProperty("replace", false);
    JsonArray parts = new JsonArray();
    for (PartSpriteInfo spriteInfo : spriteProvider.getSprites()) {
      parts.add(GSON.toJsonTree(spriteInfo));
    }
    json.add("parts", parts);
    saveThing(cache, new ResourceLocation(modId, "generator_part_textures"), json);
  }

  @Override
  public String getName() {
    return modId + " Command Part Texture JSON Generator";
  }
}

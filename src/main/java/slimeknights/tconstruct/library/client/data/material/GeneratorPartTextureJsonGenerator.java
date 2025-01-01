package slimeknights.tconstruct.library.client.data.material;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.mantle.data.gson.ResourceLocationSerializer;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.data.material.AbstractPartSpriteProvider.PartSpriteInfo;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

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
  private final StatOverride overrides;
  
  public GeneratorPartTextureJsonGenerator(PackOutput packOutput, String modId, AbstractPartSpriteProvider spriteProvider) {
    this(packOutput, modId, spriteProvider, StatOverride.EMPTY);
  }

  public GeneratorPartTextureJsonGenerator(PackOutput packOutput, String modId, AbstractPartSpriteProvider spriteProvider, StatOverride overrides) {
    super(packOutput, Target.RESOURCE_PACK, "tinkering", GSON);
    this.modId = modId;
    this.spriteProvider = spriteProvider;
    this.overrides = overrides;
  }

  @Override
  public CompletableFuture<?> run(CachedOutput cache) {
    JsonObject json = new JsonObject();
    json.addProperty("replace", false);
    json.add("parts", PartSpriteInfo.LIST_LOADABLE.serialize(spriteProvider.getSprites()));
    if (!overrides.overrides.isEmpty()) {
      json.add("overrides", overrides.serialize());
    }
    return saveJson(cache, new ResourceLocation(modId, "generator_part_textures"), json);
  }

  @Override
  public String getName() {
    return modId + " Command Part Texture JSON Generator";
  }

  /** Class representing an override allowing a stat type to include a material withot modifying its render info */
  public record StatOverride(Map<MaterialStatsId,Set<ResourceLocation>> overrides) {
    public static final StatOverride EMPTY = new StatOverride(Collections.emptyMap());

    /** Checks if the material has the given override */
    public boolean hasOverride(MaterialStatsId statsId, ResourceLocation location) {
      return overrides.getOrDefault(statsId, Collections.emptySet()).contains(location);
    }

    /** Serializes this to JSON */
    public JsonObject serialize() {
      JsonObject json = new JsonObject();
      for (Entry<MaterialStatsId, Set<ResourceLocation>> entry : overrides.entrySet()) {
        JsonArray array = new JsonArray();
        for (ResourceLocation value : entry.getValue()) {
          array.add(value.toString());
        }
        json.add(entry.getKey().toString(), array);
      }
      return json;
    }

    @SuppressWarnings("UnusedReturnValue")
    public static class Builder {
      private final Map<MaterialStatsId,ImmutableSet.Builder<ResourceLocation>> builder = new LinkedHashMap<>();

      /** Adds a texture to the builder */
      public Builder add(MaterialStatsId statsId, ResourceLocation texture) {
        builder.computeIfAbsent(statsId, id -> ImmutableSet.builder()).add(texture);
        return this;
      }

      /** Adds a texture to the builder */
      public Builder addVariant(MaterialStatsId statsId, MaterialVariantId texture) {
        return add(statsId, texture.getLocation('_'));
      }

      /** Builds the final instance */
      public StatOverride build() {
        if (builder.isEmpty()) {
          return EMPTY;
        }

        ImmutableMap.Builder<MaterialStatsId,Set<ResourceLocation>> builder = ImmutableMap.builder();
        for (Entry<MaterialStatsId,ImmutableSet.Builder<ResourceLocation>> entry : this.builder.entrySet()) {
          builder.put(entry.getKey(), entry.getValue().build());
        }
        return new StatOverride(builder.build());
      }
    }
  }
}

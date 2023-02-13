package slimeknights.tconstruct.library.client.book.sectiontransformer.materials;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.mantle.client.book.transformer.BookTransformer;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.book.content.AbstractMaterialContent;
import slimeknights.tconstruct.library.client.book.content.ContentMaterial;
import slimeknights.tconstruct.library.materials.IMaterialRegistry;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialManager;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Section transformer to show a range of materials tiers in the book
 */
public class TierRangeMaterialSectionTransformer extends BookTransformer {
  private static final Set<MaterialStatsId> MELEE_HARVEST = ImmutableSet.of(HeadMaterialStats.ID, HandleMaterialStats.ID, ExtraMaterialStats.ID);
  private static final ResourceLocation KEY = TConstruct.getResource("material_tier");

  private static final Map<ResourceLocation,MaterialType> MATERIAL_TYPES = new HashMap<>();

  public static final TierRangeMaterialSectionTransformer INSTANCE = new TierRangeMaterialSectionTransformer();

  /** Registers a new group of stat types to show on a page */
  public static void registerMaterialType(ResourceLocation id, BiFunction<MaterialVariantId,Boolean,AbstractMaterialContent> constructor, MaterialStatsId... stats) {
    if (MATERIAL_TYPES.putIfAbsent(id, new MaterialType(constructor, ImmutableSet.copyOf(stats))) != null) {
      throw new IllegalArgumentException("Duplicate material stat group " + id);
    }
  }

  @Override
  public void transform(BookData book) {
    for (SectionData section : book.sections) {
      JsonElement json = section.extraData.get(KEY);
      if (json != null) {
        try {
          int min = 0;
          int max = Integer.MAX_VALUE;
          Function<MaterialVariantId,AbstractMaterialContent> pageBuilder;
          Set<MaterialStatsId> visibleStats;
          TagKey<IMaterial> tag = null;

          // if primitive, its either an int tier, or a tag
          if (json.isJsonPrimitive()) {
            if (json.getAsJsonPrimitive().isNumber()) {
              min = json.getAsInt();
              max = min;
            } else {
              tag = MaterialManager.getTag(JsonHelper.convertToResourceLocation(json, KEY.toString()));
            }
            pageBuilder = id -> new ContentMaterial(id, false);
            visibleStats = MELEE_HARVEST;
            TConstruct.LOG.warn("Using tconstruct:material_tier with a number or tag is deprecated");
          } else if (json.isJsonObject()) {
            // object means we have a tier/min/max, or potentially a tag
            JsonObject jsonObject = json.getAsJsonObject();
            if (jsonObject.has("tier")) {
              min = GsonHelper.getAsInt(jsonObject, "tier");
              max = min;
            } else {
              min = GsonHelper.getAsInt(jsonObject, "min", 0);
              max = GsonHelper.getAsInt(jsonObject, "max", Integer.MAX_VALUE);
            }
            if (jsonObject.has("tag")) {
              tag = MaterialManager.getTag(JsonHelper.getResourceLocation(jsonObject, "tag"));
            }
            ResourceLocation type = jsonObject.has("type") ? JsonHelper.getResourceLocation(jsonObject, "type") : TConstruct.getResource("melee_harvest");
            MaterialType typeData = MATERIAL_TYPES.get(type);
            if (typeData == null) {
              throw new JsonSyntaxException("Invalid material section type " + type);
            }
            visibleStats = typeData.visibleStats();
            pageBuilder = typeData.getMapping(GsonHelper.getAsBoolean(jsonObject, "detailed", false));
          } else {
            throw new JsonSyntaxException("Invalid tconstruct:material_tier, expected number or JSON object");
          }
          AbstractMaterialSectionTransformer.createPages(book, section, new ValidMaterial(visibleStats, min, max, tag), pageBuilder);
        } catch (JsonSyntaxException e) {
          TConstruct.LOG.error("Failed to parse material tier section data", e);
        }
      }
    }
  }

  /** Helper to create a material predicate */
  public record ValidMaterial(Set<MaterialStatsId> visibleStats, int min, int max, @Nullable TagKey<IMaterial> tag) implements Predicate<IMaterial> {
    @Deprecated
    public ValidMaterial(Set<MaterialStatsId> visibleStats, int min, int max) {
      this(visibleStats, min, max, null);
    }

    @Override
    public boolean test(IMaterial material) {
      int tier = material.getTier();
      if (tier < min || tier > max) {
        return false;
      }
      IMaterialRegistry registry = MaterialRegistry.getInstance();
      MaterialId id = material.getIdentifier();
      if (tag != null && !registry.isInTag(id, tag)) {
        return false;
      }
      // only show material stats for types with the proper stat types, as otherwise the page will be empty
      // if you want to show another stat type, just override this method/implement the parent class
      for (IMaterialStats stats : registry.getAllStats(id)) {
        if (visibleStats.contains(stats.getIdentifier())) {
          return true;
        }
      }
      return false;
    }
  }

  /** Internal record from the registry */
  private record MaterialType(BiFunction<MaterialVariantId,Boolean,AbstractMaterialContent> pageConstructor, Set<MaterialStatsId> visibleStats) {
    public Function<MaterialVariantId,AbstractMaterialContent> getMapping(boolean detailed) {
      return id -> pageConstructor.apply(id, detailed);
    }
  }
}

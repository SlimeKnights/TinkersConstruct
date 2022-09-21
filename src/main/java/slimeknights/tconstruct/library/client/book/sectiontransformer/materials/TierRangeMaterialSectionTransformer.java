package slimeknights.tconstruct.library.client.book.sectiontransformer.materials;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.mantle.client.book.transformer.BookTransformer;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.book.content.ContentMaterial;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import java.util.Set;
import java.util.function.Predicate;

/**
 * Section transformer to show a range of materials tiers in the book
 */
public class TierRangeMaterialSectionTransformer extends BookTransformer {
  private static final Set<MaterialStatsId> VISIBLE_STATS = ImmutableSet.of(HeadMaterialStats.ID, HandleMaterialStats.ID, ExtraMaterialStats.ID);
  private static final ResourceLocation KEY = TConstruct.getResource("material_tier");

  public static final TierRangeMaterialSectionTransformer INSTANCE = new TierRangeMaterialSectionTransformer();

  @Override
  public void transform(BookData book) {
    for (SectionData section : book.sections) {
      JsonElement json = section.extraData.get(KEY);
      if (json != null) {
        try {
          int min, max;
          boolean detailed;
          if (json.isJsonPrimitive()) {
            min = json.getAsInt();
            max = min;
            detailed = false;
          } else if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();
            if (jsonObject.has("tier")) {
              min = GsonHelper.getAsInt(jsonObject, "tier");
              max = min;
            } else {
              min = GsonHelper.getAsInt(jsonObject, "min", 0);
              max = GsonHelper.getAsInt(jsonObject, "max", Integer.MAX_VALUE);
            }
            detailed = GsonHelper.getAsBoolean(jsonObject, "detailed", false);
          } else {
            throw new JsonSyntaxException("Invalid tconstruct:material_tier, expected number or JSON object");
          }
          AbstractMaterialSectionTransformer.createPages(book, section, new ValidMaterial(VISIBLE_STATS, min, max), id -> new ContentMaterial(id, detailed));
        } catch (JsonSyntaxException e) {
          TConstruct.LOG.error("Failed to parse material tier section data", e);
        }
      }
    }
  }

  /** Helper to create a material predicate */
  public record ValidMaterial(Set<MaterialStatsId> visibleStats, int min, int max) implements Predicate<IMaterial> {
    @Override
    public boolean test(IMaterial material) {
      int tier = material.getTier();
      if (tier < min || tier > max) {
        return false;
      }
      // only show material stats for types with the proper stat types, as otherwise the page will be empty
      // if you want to show another stat type, just override this method/implement the parent class
      for (IMaterialStats stats : MaterialRegistry.getInstance().getAllStats(material.getIdentifier())) {
        if (visibleStats.contains(stats.getIdentifier())) {
          return true;
        }
      }
      return false;
    }
  }
}

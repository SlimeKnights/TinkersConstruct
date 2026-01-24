package slimeknights.tconstruct.library.data.material;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.json.MaterialStatJson;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsManager;
import slimeknights.tconstruct.tools.modules.ArmorModuleBuilder;
import slimeknights.tconstruct.tools.modules.ArmorModuleBuilder.ArmorShieldModuleBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/** Base data generator for use in addons, depends on the regular material provider */
public abstract class AbstractMaterialStatsDataProvider extends GenericDataProvider {
  /** All material stats generated so far */
  private final Map<MaterialId, MaterialStats> allMaterialStats = new HashMap<>();
  /* Materials data provider for validation */
  private final AbstractMaterialDataProvider materials;

  public AbstractMaterialStatsDataProvider(PackOutput packOutput, AbstractMaterialDataProvider materials) {
    super(packOutput, Target.DATA_PACK, MaterialStatsManager.FOLDER);
    this.materials = materials;
  }

  /** Adds all relevant material stats */
  protected abstract void addMaterialStats();

  @Override
  public CompletableFuture<?> run(CachedOutput cache) {
    addMaterialStats();

    // ensure we have stats for all materials
    Set<MaterialId> materialsGenerated = materials.getAllMaterials();
    for (MaterialId material : materialsGenerated) {
      if (!allMaterialStats.containsKey(material)) {
        throw new IllegalStateException(String.format("Missing material stats for '%s'", material));
      }
    }
    // does not ensure we have materials for all stats, we may be adding stats for another mod
    // generate finally
    return allOf(allMaterialStats.entrySet().stream().map(entry -> saveJson(cache, entry.getKey(), entry.getValue().serialize())));
  }


  /* Helpers */

  /** Gets the stats object for the given material */
  private MaterialStats getStats(MaterialId material) {
    return allMaterialStats.computeIfAbsent(material, id -> new MaterialStats(new ArrayList<>(), new ArrayList<>()));
  }

  /**
   * Adds a set of material stats for the given material ID
   * @param location  Material ID
   * @param stats     Stats to add
   */
  protected void addMaterialStats(MaterialId location, IMaterialStats... stats) {
    Collections.addAll(getStats(location).required, stats);
  }

  /**
   * Adds a set of optional material stats for the given material ID. Optional stats will not error if the serializer is absent.
   * @param location  Material ID
   * @param stats     Stats to add
   */
  @SuppressWarnings("unused") // API
  protected void addOptionalStats(MaterialId location, IMaterialStats... stats) {
    Collections.addAll(getStats(location).optional, stats);
  }

  /**
   * Adds material stats from the given armor builder
   * @param location     Material ID
   * @param statBuilder  Stat builder
   * @param otherStats   Other stat types to add after the builder
   */
  protected void addArmorStats(MaterialId location, ArmorModuleBuilder<? extends IMaterialStats> statBuilder, IMaterialStats... otherStats) {
    IMaterialStats[] stats = new IMaterialStats[4];
    for (ArmorItem.Type slotType : ArmorItem.Type.values()) {
      stats[slotType.ordinal()] = statBuilder.build(slotType);
    }
    addMaterialStats(location, stats);
    if (otherStats.length > 0) {
      addMaterialStats(location, otherStats);
    }
  }

  /**
   * Adds material stats from the given armor and shield builder
   * @param location     Material ID
   * @param statBuilder  Stat builder
   * @param otherStats   Other stat types to add after the builder
   */
  protected void addArmorShieldStats(MaterialId location, ArmorShieldModuleBuilder<? extends IMaterialStats> statBuilder, IMaterialStats... otherStats) {
    addArmorStats(location, statBuilder, otherStats);
    addMaterialStats(location, statBuilder.buildShield());
  }

  /* Internal */

  /** Handles a pair of required and optional stats */
  private record MaterialStats(List<IMaterialStats> required, List<IMaterialStats> optional) {
    /** Deals with generics for the stat encoder */
    @SuppressWarnings("unchecked")
    private static <T extends IMaterialStats> JsonObject encodeStats(IMaterialStats stats, MaterialStatType<T> type) {
      JsonObject json = new JsonObject();
      type.getLoadable().serialize((T)stats, json);
      return json;
    }

    /** Serializes this to JSON */
    public MaterialStatJson serialize() {
      Map<ResourceLocation,JsonElement> map = new HashMap<>();
      for (IMaterialStats stat : required) {
        map.put(stat.getIdentifier(), encodeStats(stat, stat.getType()));
      }
      for (IMaterialStats stat : optional) {
        JsonObject encoded = encodeStats(stat, stat.getType());
        encoded.addProperty("optional", true);
        map.put(stat.getIdentifier(), encoded);
      }
      return new MaterialStatJson(map);
    }
  }
}

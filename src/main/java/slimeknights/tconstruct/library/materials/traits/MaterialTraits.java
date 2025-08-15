package slimeknights.tconstruct.library.materials.traits;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.materials.json.MaterialTraitsJson;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Data object holding a list of traits and a map of stat type to trait
 */
@AllArgsConstructor
public class MaterialTraits {
  @Getter
  private final List<ModifierEntry> defaultTraits;
  @Getter(AccessLevel.PROTECTED)
  private final Map<MaterialStatsId,List<ModifierEntry>> traitsPerStats;

  /**
   * Checks if the stats ID has unique traits
   * @param statsId  Stats ID
   * @return  True if the traits for this stat type are unique
   */
  public boolean hasUniqueTraits(MaterialStatsId statsId) {
    return traitsPerStats.containsKey(statsId);
  }

  /**
   * Gets the traits for a stat type
   * @param statsId  Stats ID
   * @return List of traits
   */
  public List<ModifierEntry> getTraits(MaterialStatsId statsId) {
    return traitsPerStats.getOrDefault(statsId, defaultTraits);
  }

  /**
   * Writes this object to the packet buffer
   * @param buffer  Buffer instance
   */
  public void write(FriendlyByteBuf buffer) {
    writeTraitList(buffer, defaultTraits);
    // write map of traits
    buffer.writeVarInt(traitsPerStats.size());
    for (Entry<MaterialStatsId,List<ModifierEntry>> entry : traitsPerStats.entrySet()) {
      buffer.writeResourceLocation(entry.getKey());
      writeTraitList(buffer, entry.getValue());
    }
  }

  /**
   * Reads this object from the packet buffer
   * @param buffer  Buffer
   * @return Read MaterialTraits
   */
  public static MaterialTraits read(FriendlyByteBuf buffer) {
    List<ModifierEntry> defaultTraits = readTraitList(buffer);
    int statTypeCount = buffer.readVarInt();
    Map<MaterialStatsId,List<ModifierEntry>> statsTraits = new HashMap<>(statTypeCount);
    for (int i = 0; i < statTypeCount; i++) {
      MaterialStatsId statsId = new MaterialStatsId(buffer.readResourceLocation());
      List<ModifierEntry> traitsList = readTraitList(buffer);
      statsTraits.put(statsId, traitsList);
    }
    return new MaterialTraits(defaultTraits, statsTraits);
  }

  /**
   * Reads a single list of traits from the buffer
   * @param buffer  Buffer
   * @return  List of traits
   */
  private static List<ModifierEntry> readTraitList(FriendlyByteBuf buffer) {
    ImmutableList.Builder<ModifierEntry> builder = ImmutableList.builder();
    int count = buffer.readVarInt();
    for (int i = 0; i < count; i++) {
      builder.add(ModifierEntry.LOADABLE.decode(buffer));
    }
    return builder.build();
  }

  /**
   * Writes a single list of traits to the buffer
   * @param buffer  Buffer
   * @param traits  List of traits
   */
  private static void writeTraitList(FriendlyByteBuf buffer, List<ModifierEntry> traits) {
    buffer.writeVarInt(traits.size());
    for (ModifierEntry entry : traits) {
      ModifierEntry.LOADABLE.encode(buffer, entry);
    }
  }

  /** Builder for use in deserializing and datagen */
  public static class Builder {
    @Getter
    private List<ModifierEntry> defaultTraits = null;
    @Getter(AccessLevel.PROTECTED)
    private final Map<MaterialStatsId,List<ModifierEntry>> traitsPerStats = new HashMap<>();

    /**
     * Sets the default traits
     * @param traits   Traits list
     */
    public void setDefaultTraits(@Nullable List<ModifierEntry> traits) {
      if (traits != null) {
        this.defaultTraits = traits;
      }
    }

    /**
     * Sets the traits for a stat type
     * @param statsId  Stats ID
     * @param traits   Traits list
     */
    public void setTraits(MaterialStatsId statsId, @Nullable List<ModifierEntry> traits) {
      if (traits != null) {
        traitsPerStats.put(statsId, traits);
      } else {
        // allow higher level datapacks to reset a stat type to default
        traitsPerStats.remove(statsId);
      }
    }

    /** @deprecated use {@link slimeknights.tconstruct.library.data.material.AbstractMaterialTraitDataProvider.MaterialTraitsBuilder} */
    @Deprecated(forRemoval = true)
    public MaterialTraitsJson serialize() {
      // need to adjust the map to the right generics
      // also suppress the map if no stat types were defined
      Map<ResourceLocation,List<ModifierEntry>> newMap = null;
      if (!traitsPerStats.isEmpty()) {
        newMap = new HashMap<>(traitsPerStats.size());
        newMap.putAll(traitsPerStats);
      }
      return new MaterialTraitsJson(defaultTraits, newMap);
    }

    /**
     * Builds this into a material trait object
     * @param  fallbacks Map of stat type fallbacks
     * @return  Material traits
     */
    public MaterialTraits build(Map<MaterialStatsId,MaterialStatsId> fallbacks) {
      List<ModifierEntry> defaultTraits = this.defaultTraits;
      if (defaultTraits == null || defaultTraits.isEmpty()) {
        defaultTraits = Collections.emptyList();
      }
      Map<MaterialStatsId,List<ModifierEntry>> traitsPerStats;
      if (this.traitsPerStats.isEmpty()) {
        traitsPerStats = Collections.emptyMap();
      } else {
        // add in fallbacks now so no hit to lookup times
        ImmutableMap.Builder<MaterialStatsId,List<ModifierEntry>> builder = ImmutableMap.builder();
        builder.putAll(this.traitsPerStats);
        for (Entry<MaterialStatsId,MaterialStatsId> fallback : fallbacks.entrySet()) {
          MaterialStatsId statType = fallback.getKey();
          if (!this.traitsPerStats.containsKey(statType)) {
            List<ModifierEntry> fallbackTraits = this.traitsPerStats.get(fallback.getValue());
            if (fallbackTraits != null) {
              builder.put(statType, fallbackTraits);
            }
          }
        }
        traitsPerStats = builder.build();
      }
      return new MaterialTraits(defaultTraits, traitsPerStats);
    }
  }
}

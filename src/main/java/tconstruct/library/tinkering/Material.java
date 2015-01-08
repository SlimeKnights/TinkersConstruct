package tconstruct.library.tinkering;

import net.minecraft.util.EnumChatFormatting;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nonnull;

import tconstruct.library.TinkerAPIException;
import tconstruct.library.tinkering.materials.IMaterialStats;
import tconstruct.library.tinkering.traits.IMaterialTrait;

public class Material {

  public static final Material UNKNOWN = new Material();

  /**
   * This String uniquely identifies a material.
   */
  @Nonnull
  public final String identifier;
  /**
   * This ID is used to map the material to metadata for items. Has to be between 0 and 65535
   */
  public final int metadata;

  public final int colorLow;
  public final int colorMid;
  public final int colorHigh;
  public final EnumChatFormatting textColor; // used in tooltips and other text
  // todo: maybe make this dynamic people can supply their own colorable textures?
  public final SurfaceType surfaceType;


  // we use a Treemap for 2 reasons:
  // * A Map so we can obtain the stats we want quickly
  // * A treemap because we can sort it, so that all materials have the same order when iterating
  protected final Map<String, IMaterialStats> stats = new TreeMap<>();
  protected final Map<String, IMaterialTrait> traits = new TreeMap<>();

  private Material() {
    this.identifier = "Unknown";
    this.colorHigh = 0xffffff;
    this.colorMid = 0xffffff;
    this.colorLow = 0xffffff;
    this.metadata = -1;
    this.textColor = EnumChatFormatting.WHITE;
    this.surfaceType = SurfaceType.METAL;
  }

  // simple white material
  public Material(String identifier, int metadata) {
    this(identifier, metadata, 0xffffff, EnumChatFormatting.GRAY);
  }

  // one-colored material
  public Material(String identifier, int metadata, int color, EnumChatFormatting textColor) {
    this(identifier, metadata, color, color, color, SurfaceType.METAL, textColor);
  }

  // complex material with 3 colors and a real surface texture!
  public Material(String identifier, int metadata, int colorLow, int colorMedium, int colorHigh,
                  SurfaceType surfaceType, EnumChatFormatting textColor) {

    // check metadata bounds: 0 to (2^16)-1
    if (metadata < 0 || metadata > 65535) {
      throw new TinkerAPIException(
          String.format("Metadata for Material \"%s\" is out of bounds: %d", identifier, metadata));
    }

    this.identifier = identifier;
    this.metadata = metadata;
    this.colorLow = colorLow;
    this.colorMid = colorMedium;
    this.colorHigh = colorHigh;
    this.surfaceType = surfaceType;
    this.textColor = textColor;
  }

  /* Stats */
  public void addStats(IMaterialStats materialStats) {
    this.stats.put(materialStats.getMaterialType(), materialStats);
  }

  /**
   * Returns the given type of stats if the material has them. Returns null Otherwise.
   */
  private IMaterialStats getStatsSafe(String identifier) {
    if (identifier == null || identifier.isEmpty()) {
      return null;
    }

    for (IMaterialStats stat : stats.values()) {
      if (identifier.equals(stat.getMaterialType())) {
        return stat;
      }
    }

    return null;
  }

  /**
   * Returns the material stats of the given type of this material.
   *
   * @param identifier Identifier of the material.
   * @param <T>        Type of the Stats are determined by return value. Use the correct
   * @return The stats found or null if none present.
   */
  @SuppressWarnings("unchecked")
  public <T extends IMaterialStats> T getStats(String identifier) {
    return (T) getStatsSafe(identifier);
  }

  public Collection<IMaterialStats> getAllStats() {
    return stats.values();
  }

  public boolean hasStats(String identifier) {
    return getStats(identifier) != null;
  }

  /* Traits */
  public void addTrait(IMaterialTrait materialTrait) {
    this.traits.put(materialTrait.getIdentifier(), materialTrait);
  }

  /**
   * Returns whether the material has a trait with that identifier.
   */
  public boolean hasTrait(String identifier) {
    if (identifier == null || identifier.isEmpty()) {
      return false;
    }

    for (IMaterialTrait trait : traits.values()) {
      if (identifier.equals(trait.getIdentifier())) {
        return true;
      }
    }

    return false;
  }

  public Collection<IMaterialTrait> getAllTraits() {
    return this.traits.values();
  }


  // used to determine the texture for coloring
  enum SurfaceType {
    METAL,
    ROCKY,
    GLOSSY
  }
}

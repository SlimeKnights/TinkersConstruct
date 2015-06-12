package tconstruct.library.tinkering;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nonnull;

import tconstruct.library.client.MaterialRenderInfo;
import tconstruct.library.tinkering.materials.IMaterialStats;
import tconstruct.library.tinkering.traits.ITrait;

public class Material {

  public static final Material UNKNOWN = new Material();

  /**
   * This String uniquely identifies a material.
   */
  @Nonnull
  public final String identifier;

  /**
   * How the material will be rendered on tinker tools etc.
   */
  public final MaterialRenderInfo renderInfo;

  public final EnumChatFormatting textColor; // used in tooltips and other text


  // we use a Treemap for 2 reasons:
  // * A Map so we can obtain the stats we want quickly
  // * A treemap because we can sort it, so that all materials have the same order when iterating
  protected final Map<String, IMaterialStats> stats = new TreeMap<>();
  protected final Map<String, ITrait> traits = new TreeMap<>();

  private Material() {
    this.identifier = "Unknown";
    this.renderInfo = new MaterialRenderInfo.Default(0xffffff);
    this.textColor = EnumChatFormatting.WHITE;
  }

  // simple white material
  public Material(String identifier) {
    this(identifier, 0xffffff, EnumChatFormatting.GRAY);
  }

  // one-colored material
  public Material(String identifier, int color, EnumChatFormatting textColor) {
    this(identifier, new MaterialRenderInfo.Default(color), textColor);
  }

  // multi-colored material
  public Material(String identifier, int colorLow, int colorMid, int colorHigh,
                  EnumChatFormatting textColor) {
    this(identifier, new MaterialRenderInfo.Default(colorLow, colorMid, colorHigh), textColor);
  }

  // complex material with 3 colors and a real surface texture!
  public Material(String identifier, MaterialRenderInfo renderInfo, EnumChatFormatting textColor) {
    this.identifier = identifier;
    this.renderInfo = renderInfo;
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
  public void addTrait(ITrait materialTrait) {
    this.traits.put(materialTrait.getIdentifier(), materialTrait);
  }

  /**
   * Returns whether the material has a trait with that identifier.
   */
  public boolean hasTrait(String identifier) {
    if (identifier == null || identifier.isEmpty()) {
      return false;
    }

    for (ITrait trait : traits.values()) {
      if (identifier.equals(trait.getIdentifier())) {
        return true;
      }
    }

    return false;
  }

  public Collection<ITrait> getAllTraits() {
    return this.traits.values();
  }

  public String getLocalizedName() {
    return StatCollector.translateToLocal(String.format("material.%s.name", identifier));
  }
}

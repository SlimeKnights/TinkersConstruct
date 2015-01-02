package tconstruct.library.tools;

import net.minecraft.util.EnumChatFormatting;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nonnull;

import tconstruct.library.tools.materials.IMaterialStats;
import tconstruct.library.tools.traits.IMaterialTrait;

public class Material {

  @Nonnull
  public final String identifier;

  public final int colorLow;
  public final int colorMid;
  public final int colorHigh;
  public final EnumChatFormatting textColor; // used in tooltips and other text
  // todo: maybe make this dynamic people can supply their own colorable textures?
  public final SurfaceType surfaceType;


  // we use a Treemap for 2 reasons:
  // * A Map so we can obtain the stats we want quickly
  // * A treemap because we can sort it, so that all materials have the same order when iterating
  protected final Map<Class<? extends IMaterialStats>, IMaterialStats> stats = new TreeMap<>();
  protected final Map<Class<? extends IMaterialTrait>, IMaterialTrait> traits = new TreeMap<>();

  // simple white material
  public Material(String identifier) {
    this.identifier = identifier;
    // white
    this.colorHigh = 0xffffff;
    this.colorMid = 0xffffff;
    this.colorLow = 0xffffff;
    this.surfaceType = SurfaceType.METAL;

    this.textColor = EnumChatFormatting.GRAY;
  }

  // one-colored material
  public Material(String identifier, int color, EnumChatFormatting textColor) {
    this.identifier = identifier;
    this.colorLow = color;
    this.colorMid = color;
    this.colorHigh = color;
    this.surfaceType = SurfaceType.METAL;
    this.textColor = textColor;
  }

  // complex material with 3 colors and a real surface texture!
  public Material(String identifier, int colorLow, int colorMedium, int colorHigh,
                  SurfaceType surfaceType, EnumChatFormatting textColor) {
    this.identifier = identifier;
    this.colorLow = colorLow;
    this.colorMid = colorMedium;
    this.colorHigh = colorHigh;
    this.surfaceType = surfaceType;
    this.textColor = textColor;
  }

  /* Stats */
  public void addStats(IMaterialStats materialStats) {
    this.stats.put(materialStats.getClass(), materialStats);
  }

  /**
   * Returns the given type of stats if the material has them.
   * Returns null Otherwise.
   */
  public <T extends IMaterialStats> T getStats(Class<T> clazz) {
    if(this.stats.containsKey(clazz))
      return null;

    return clazz.cast(this.stats.get(clazz));
  }

  /**
   * Returns the given type of stats if the material has them.
   * Returns null Otherwise.
   * Remark: Slower than obtaining it by class.
   */
  public IMaterialStats getStats(String identifier) {
    if(identifier == null || identifier.isEmpty())
      return null;

    for(IMaterialStats stat : stats.values())
      if(identifier.equals(stat.getMaterialType()))
        return stat;

    return null;
  }

  public Collection<IMaterialStats> getAllStats() {
    return stats.values();
  }

  /* Traits */
  public void addTrait(IMaterialTrait materialTrait) {
    this.traits.put(materialTrait.getClass(), materialTrait);
  }

  /**
   * Returns wether the material has a trait of the given type.
   */
  public boolean hasTrait(Class<? extends IMaterialTrait> clazz) {
    return this.traits.containsKey(clazz);
  }

  /**
   * Returns wether the material has a trait with that identifier.
   * Remark: Slower than searching by class
   */
  public boolean hasTrait(String identifier) {
    if(identifier == null || identifier.isEmpty())
      return false;

    for(IMaterialTrait trait : traits.values())
      if(identifier.equals(trait.getIdentifier()))
        return true;

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

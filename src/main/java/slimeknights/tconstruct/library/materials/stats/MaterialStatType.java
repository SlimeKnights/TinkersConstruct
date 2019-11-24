package slimeknights.tconstruct.library.materials.stats;

// todo: possibly switch this directly to a class and implement an interface that signifies the registry stuff?

import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.Util;

import java.util.Objects;

/**
 * <p>Part types are actually different material stat types.
 * Think of them as a collection of attributes a material has, when it's used for a specific part.
 * e.g. for a material to be used as a bowstring, it needs to have bowstring material stats.</p>
 *
 * <p>Each instance of this class should be unique. If two instances with the same id exist, internal systems might break.</p>
 */
public final class MaterialStatType {
  
   public static final MaterialStatType HEAD = new MaterialStatType(Util.getResource("head"));
   public static final MaterialStatType HANDLE = new MaterialStatType(Util.getResource("handle"));
   public static final MaterialStatType EXTRA = new MaterialStatType(Util.getResource("extra"));
 
   public static final MaterialStatType BOW = new MaterialStatType(Util.getResource("bow"));
   public static final MaterialStatType BOWSTRING = new MaterialStatType(Util.getResource("bowstring"));

   public static final MaterialStatType PROJECTILE = new MaterialStatType(Util.getResource("projectile"));
   public static final MaterialStatType SHAFT = new MaterialStatType(Util.getResource("shaft"));
   public static final MaterialStatType FLETCHING = new MaterialStatType(Util.getResource("fletching"));

  private final ResourceLocation identifier;

  // todo: keep a set of all created identifiers and throw an exception or log error if one is created twice. Each one should only exist once.
  public MaterialStatType(ResourceLocation identifier) {
    this.identifier = identifier;
  }

  public ResourceLocation getIdentifier() {
    return identifier;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MaterialStatType that = (MaterialStatType) o;
    return identifier.equals(that.identifier);
  }

  @Override
  public int hashCode() {
    return Objects.hash(identifier);
  }
}

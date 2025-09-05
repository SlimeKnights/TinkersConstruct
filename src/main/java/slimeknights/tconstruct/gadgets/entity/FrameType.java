package slimeknights.tconstruct.gadgets.entity;

import lombok.Getter;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

/** All frame variants for the entity */
public enum FrameType implements StringRepresentable {
  // order is weird for the sake of preserving backwards compat, as its saved in the entity as an int
  REVERSED_GOLD, // rotation timer
  DIAMOND, // slowly winds down
  MANYULLYN, // item inside rendered full bright
  GOLD, // rotation timer
  CLEAR, // frame hidden when filled, extra large items
  NETHERITE; // immune to fire and explosions

  private static final FrameType[] VALUES = values();
  @Getter
  private final int id = ordinal();

  public static FrameType byId(int id) {
    if (id < 0 || id >= VALUES.length) {
      id = 0;
    }

    return VALUES[id];
  }

  /** If true, this type supports 16 rotations instead of 8 */
  public boolean hasMoreRotations() {
    return this == DIAMOND || this == MANYULLYN || this == NETHERITE;
  }

  /** If true, this type supports 16 rotations instead of 8 */
  public static boolean hasMoreRotations(int id) {
    return id == DIAMOND.getId() || id == MANYULLYN.getId() || id == NETHERITE.getId();
  }

  @Override
  public String getSerializedName() {
    return this.toString().toLowerCase(Locale.US);
  }
}

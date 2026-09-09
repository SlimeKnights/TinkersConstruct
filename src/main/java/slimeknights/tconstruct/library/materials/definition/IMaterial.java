package slimeknights.tconstruct.library.materials.definition;

import net.minecraft.world.item.Rarity;

/**
 * Base interface for all materials.
 * TODO 1.21: Make {@link slimeknights.mantle.registration.object.IdAwareObject}
 */
public interface IMaterial extends Comparable<IMaterial> {
  /** @deprecated use {@link MaterialId#UNKNOWN} */
  @Deprecated(forRemoval = true)
  MaterialId UNKNOWN_ID = MaterialId.UNKNOWN;

  /**
   * Fallback material. Used for operations where a material or specific aspects of a material are used,
   * but the given input is missing or does not match the requirements.
   * Think of this as "anti-crash" when trying to build invalid tools.
   * <p>
   * The fallback material needs to have all part types associated with it.
   */
  IMaterial UNKNOWN = new Material(MaterialId.UNKNOWN, false, true);

  /**
   * Used to identify the material in NBT and other constructs.
   * Basically everywhere where the material has to be referenced and persisted.
   */
  MaterialId getIdentifier();

  /**
   * If the material can be crafted into items in the part builder.
   * TODO 1.21: move to material tags.
   *
   * @return Return false if the material can only be cast or is not craftable at all.
   */
  boolean isCraftable();

  /**
   * If true, this material is hidden from display, such as in JEI and the books.
   * TODO 1.21: move to material tags.
   */
  boolean isHidden();


  /* Display */

  /** Gets the progression tier of this material */
  int getTier();

  /** Gets the sort location within this tier */
  int getSortOrder();

  /** Gets the rarity for this material's display name */
  default Rarity getRarity() {
    return Rarity.COMMON;
  }

  @Override
  default int compareTo(IMaterial other) {
    // tier first, then sort order, fallback to unique ID
    if (this.getTier() != other.getTier()) {
      return Integer.compare(this.getTier(), other.getTier());
    }
    if (this.getSortOrder() != other.getSortOrder()) {
      return Integer.compare(this.getSortOrder(), other.getSortOrder());
    }
    return this.getIdentifier().compareTo(other.getIdentifier());
  }

  /** Checks if the given material is the same material as the other, matches by ID */
  default boolean matches(IMaterial other) {
    return this == other || this.getIdentifier().matches(other);
  }


  /* Helpers */

  /** Computes the default rarity for a given tier */
  static Rarity computeRarity(int tier) {
    if (tier >= 5) {
      return Rarity.EPIC;
    }
    if (tier == 4) {
      return Rarity.RARE;
    }
    if (tier == 3) {
      return Rarity.UNCOMMON;
    }
    return Rarity.COMMON;
  }
}

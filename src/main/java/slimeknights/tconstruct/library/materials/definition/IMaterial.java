package slimeknights.tconstruct.library.materials.definition;

import net.minecraft.Util;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import slimeknights.tconstruct.TConstruct;

public interface IMaterial extends Comparable<IMaterial> {
  /** ID of fallback material */
  MaterialId UNKNOWN_ID = new MaterialId(TConstruct.MOD_ID, "unknown");

  /**
   * Fallback material. Used for operations where a material or specific aspects of a material are used,
   * but the given input is missing or does not match the requirements.
   * Think of this as "anti-crash" when trying to build invalid tools.
   * <p>
   * The fallback material needs to have all part types associated with it.
   */
  IMaterial UNKNOWN = new Material(UNKNOWN_ID, false, true);

  /**
   * Used to identify the material in NBT and other constructs.
   * Basically everywhere where the material has to be referenced and persisted.
   */
  MaterialId getIdentifier();

  /**
   * If the material can be crafted into items in the part builder.
   *
   * @return Return false if the material can only be cast or is not craftable at all.
   */
  boolean isCraftable();

  /**
   * Gets the translation key for this material
   * @return the translation key
   */
  default String getTranslationKey() {
    return Util.makeDescriptionId("material", getIdentifier());
  }

  /**
   * Gets the display name for this material
   * @return the translation key
   */
  default Component getDisplayName() {
    return new TranslatableComponent(getTranslationKey());
  }

  /**
   * Gets the display name for this material
   * @return the translation key
   */
  default Component getColoredDisplayName() {
    return new TranslatableComponent(getTranslationKey()).withStyle(style -> style.withColor(getColor()));
  }

  /**
   * Gets the text color for this material
   * @return the text color
   */
  TextColor getColor();

  /** If true, this material is hidden from display, such as in JEI and the books */
  boolean isHidden();


  /* Display */

  /** Gets the progression tier of this material */
  int getTier();

  /** Gets the sort location within this tier */
  int getSortOrder();

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
}

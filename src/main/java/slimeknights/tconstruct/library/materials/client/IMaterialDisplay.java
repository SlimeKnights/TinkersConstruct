package slimeknights.tconstruct.library.materials.client;

import net.minecraft.item.ItemStack;

/**
 * Contains display information for a material.
 * Used when showing material related information to the player.
 */
public interface IMaterialDisplay {

  String LOC_Name = "material.%s.name";
  String LOC_Prefix = "material.%s.prefix";

  /**
   * Used for internal or secret materials that cannot be obtained/built.
   * An example for this would be the materials used for the tool buttons in the tool station.
   *
   * @return If true, the material will not be shown in books, JEI,...
   */
  boolean isHidden();

  /**
   * The main item associated with this material, displayed in the book etc.
   * e.g. iron ingot for iron, bone for bone, ...
   * Must be an item that actually is associated with this material
   *
   * @return The itemstack for this material. Uses shard as fallback if none is present.
   */
  ItemStack getRepresentativeItem();

  /**
   * Name to display for this material.
   */
  String getLocalizedName();

  /**
   * Prefix the given itemname with the material, e.g. "wooden pickaxe".
   * This is a special function since different materials require different handling in different languages.
   * E.g. the material is "wood", but the item name is "wood<em>en</em>> spatula", while in french
   * it'd be "pioche <em>de bois</em>", as suffix.
   * @param itemName The already translated itemname
   * @return The items name combined with the material to create the name of the item made out of the material
   */
  String getLocalizedItemName(String itemName);

  /**
   * The localized name but with the color tags for the material. Can be used anywhere Tinkers custom renderer is used for
   * rendering text, to color the name with the color of the material.
   * Does not contain color termination!
   */
  String getLocalizedNameColored();

  /**
   * The text color to use for the material.
   * Returns a non-printable tag that causes the custom font renderer to color the text with this materials color.
   * Does not contain color termination!
   */
  String getTextColor();
}

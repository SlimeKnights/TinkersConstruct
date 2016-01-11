package slimeknights.tconstruct.library.modifiers;

/**
 * Contains the common things modifiers and traits share.
 * Basically basic information that relates to user display
 */
public interface IToolMod {

  String getIdentifier();

  String getLocalizedName();
  /** A short description to tell the user what the trait does */
  String getLocalizedDesc();

  /** Return true to hide the trait from the user.
   *  Useful for internal stuff.
   */
  boolean isHidden();
}

package slimeknights.tconstruct.library.materials;

import java.util.List;

public interface IMaterialStats {

  /**
   * Returns a unique String to identify the type of stats the material has.
   */
  String getIdentifier();

  /**
   * Returns the name of the stat type, to be displayed to the player.
   */
  String getLocalizedName();

  /**
   * Returns a list containing a String for each player-relevant value.</br>
   * Each line should consist of the name of the value followed by the value itself.</br>
   * Example: "Durability: 25"</br>
   * </br>
   * This is used to display properties of materials to the user.
   */
  List<String> getLocalizedInfo();

  /**
   * Returns a list containing a String describing each player-relevant value.</br>
   * The indices of the lines must line up with the lines from getLocalizedInfo()!</br>
   * *
   * This is used to display properties of materials to the user.
   */
  List<String> getLocalizedDesc();
}

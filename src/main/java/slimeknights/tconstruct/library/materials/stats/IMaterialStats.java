package slimeknights.tconstruct.library.materials.stats;

import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.network.INetworkSendable;

import java.util.List;

/**
 * Basic interface for all material stats.
 * Note that you should extend {@link BaseMaterialStats} for your material to load from the JSONs.
 */
public interface IMaterialStats extends INetworkSendable {

  /**
   * Returns a unique ResourceLocation to identify the type of stats the material has.
   */
  MaterialStatsId getIdentifier();

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
  List<ITextComponent> getLocalizedInfo();

  /**
   * Returns a list containing a Text Component describing each player-relevant value.</br>
   * The indices of the lines must line up with the lines from getLocalizedInfo()!</br>
   * *
   * This is used to display properties of materials to the user.
   * @return a list of Text Components
   */
  List<ITextComponent> getLocalizedDescriptions();
}

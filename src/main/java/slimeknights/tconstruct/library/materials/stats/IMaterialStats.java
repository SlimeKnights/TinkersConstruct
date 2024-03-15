package slimeknights.tconstruct.library.materials.stats;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import slimeknights.tconstruct.library.utils.Util;

import java.util.List;

/**
 * Basic interface for all material stats.
 * Note that you should extend {@link BaseMaterialStats} for your material to load from the JSONs.
 */
public interface IMaterialStats {

  /**
   * Returns a unique ResourceLocation to identify the type of stats the material has.
   */
  MaterialStatsId getIdentifier();

  /**
   * Returns the name of the stat type, to be displayed to the player.
   */
  default MutableComponent getLocalizedName() {
    return Component.translatable(Util.makeTranslationKey("stat", getIdentifier()));
  }

  /**
   * Returns a list containing a String for each player-relevant value.</br>
   * Each line should consist of the name of the value followed by the value itself.</br>
   * Example: "Durability: 25"</br>
   * </br>
   * This is used to display properties of materials to the user.
   */
  List<Component> getLocalizedInfo();

  /**
   * Returns a list containing a Text Component describing each player-relevant value.</br>
   * The indices of the lines must line up with the lines from getLocalizedInfo()!</br>
   * *
   * This is used to display properties of materials to the user.
   * @return a list of Text Components
   */
  List<Component> getLocalizedDescriptions();

  /** Encodes these stats to the buffer */
  void encode(FriendlyByteBuf buffer);
}

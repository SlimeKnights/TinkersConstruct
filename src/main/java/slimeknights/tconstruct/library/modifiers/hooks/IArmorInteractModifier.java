package slimeknights.tconstruct.library.modifiers.hooks;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

/** Hooks for interacting on a helmet, left generic for addonss to use for other armor pieces */
public interface IArmorInteractModifier {
  /**
   * Called when the helmet keybinding is pressed to interact with this helmet modifier
   * @param tool     Tool instance
   * @param level    Modifier level
   * @param player   Player wearing the helmet
   * @param slot     Slot source of the interaction
   * @return  True if no other modifiers should process
   */
  default boolean startArmorInteract(IModifierToolStack tool, int level, Player player, EquipmentSlot slot) {
    return false;
  }

  /**
   * Called when the helmet keybinding is released to interact with this modifier
   * @param tool     Tool instance
   * @param level    Modifier level
   * @param player   Player wearing the helmet
   * @param slot     Slot source of the interaction
   */
  default void stopArmorInteract(IModifierToolStack tool, int level, Player player, EquipmentSlot slot) {}
}

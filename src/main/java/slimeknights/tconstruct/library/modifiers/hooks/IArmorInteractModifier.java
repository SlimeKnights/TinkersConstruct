package slimeknights.tconstruct.library.modifiers.hooks;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/** Hooks for interacting on a helmet, left generic for addonss to use for other armor pieces */
public interface IArmorInteractModifier {
  /** @deprecated use {@link #startArmorInteract(IToolStackView, int, Player, EquipmentSlot, TooltipKey)} */
  @Deprecated
  default boolean startArmorInteract(IToolStackView tool, int level, Player player, EquipmentSlot slot) {
    return false;
  }
  /**
   * Called when the helmet keybinding is pressed to interact with this helmet modifier
   * @param tool         Tool instance
   * @param level        Modifier level
   * @param player       Player wearing the helmet
   * @param slot         Slot source of the interaction
   * @param keyModifier  Currently pressed keys
   * @return  True if no other modifiers should process
   */
  default boolean startArmorInteract(IToolStackView tool, int level, Player player, EquipmentSlot slot, TooltipKey keyModifier) {
    return startArmorInteract(tool, level, player, slot);
  }

  /**
   * Called when the helmet keybinding is released to interact with this modifier
   * @param tool     Tool instance
   * @param level    Modifier level
   * @param player   Player wearing the helmet
   * @param slot     Slot source of the interaction
   */
  default void stopArmorInteract(IToolStackView tool, int level, Player player, EquipmentSlot slot) {}
}

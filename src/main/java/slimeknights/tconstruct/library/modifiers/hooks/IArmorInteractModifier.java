package slimeknights.tconstruct.library.modifiers.hooks;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.hook.InteractModifierHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/**
 * Hooks for interacting on a helmet, left generic for addonss to use for other armor pieces
 * @deprecated use {@link InteractModifierHook}
 */
@SuppressWarnings("DeprecatedIsStillUsed")
@Deprecated
public interface IArmorInteractModifier extends InteractModifierHook {
  /**
   * Called when the helmet keybinding is pressed to interact with this helmet modifier
   * @param tool     Tool instance
   * @param level    Modifier level
   * @param player   Player wearing the helmet
   * @param slot     Slot source of the interaction
   * @return  True if no other modifiers should process
   * @deprecated use {@link #startInteract(IToolStackView, ModifierEntry, Player, EquipmentSlot)}
   */
  @Deprecated
  default boolean startArmorInteract(IToolStackView tool, int level, Player player, EquipmentSlot slot) {
    return false;
  }

  /**
   * Called when the helmet keybinding is released to interact with this modifier
   * @param tool     Tool instance
   * @param level    Modifier level
   * @param player   Player wearing the helmet
   * @param slot     Slot source of the interaction
   * @deprecated use {@link #stopInteract(IToolStackView, ModifierEntry, Player, EquipmentSlot)}
   */
  @Deprecated
  default void stopArmorInteract(IToolStackView tool, int level, Player player, EquipmentSlot slot) {}


  /** New interface fallback to make transition easier */

  @Override
  default boolean startInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot) {
    return startArmorInteract(tool, modifier.getLevel(), player, slot);
  }

  @Override
  default void stopInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot) {
    stopArmorInteract(tool, modifier.getLevel(), player, slot);
  }
}

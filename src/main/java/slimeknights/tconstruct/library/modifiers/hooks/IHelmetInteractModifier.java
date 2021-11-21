package slimeknights.tconstruct.library.modifiers.hooks;

import net.minecraft.entity.player.PlayerEntity;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public interface IHelmetInteractModifier {
  /**
   * Called when the helmet keybinding is pressed to interact with this helmet modifier
   * @param tool     Tool instance
   * @param level    Modifier level
   * @param player   Player wearing the helmet
   * @return  True if no other modifiers should process
   */
  default boolean startHelmetInteract(IModifierToolStack tool, int level, PlayerEntity player) {
    return false;
  }

  /**
   * Called when the helmet keybinding is released to interact with this modifier
   * @param tool     Tool instance
   * @param level    Modifier level
   * @param player   Player wearing the helmet
   */
  default void stopHelmetInteract(IModifierToolStack tool, int level, PlayerEntity player) {}
}

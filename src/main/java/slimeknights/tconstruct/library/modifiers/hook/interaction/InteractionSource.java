package slimeknights.tconstruct.library.modifiers.hook.interaction;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import slimeknights.tconstruct.library.modifiers.hook.KeybindInteractModifierHook;

/**
 * Enum representing different sources of interaction
 */
public enum InteractionSource {
  /**
   * Standard interaction hook from right-clicking with a tool
   */
  RIGHT_CLICK,
  /**
   * Interaction from left-clicking a tool, used on bows and slimestaffs
   */
  LEFT_CLICK,
  /**
   * Interaction from chestplates with an empty hand. See also {@link KeybindInteractModifierHook}
   */
  ARMOR;

  /** Translates the context to a slot for the sake of breaking animations */
  public EquipmentSlot getSlot(InteractionHand hand) {
    return switch (this) {
      case RIGHT_CLICK -> switch (hand) {
        case MAIN_HAND -> EquipmentSlot.MAINHAND;
        case OFF_HAND -> EquipmentSlot.OFFHAND;
      };
      case LEFT_CLICK -> EquipmentSlot.MAINHAND;
      case ARMOR -> EquipmentSlot.CHEST;
    };
  }

  /**
   * Translates the equipment slot to an interaction source. Will never return {@link #LEFT_CLICK}.
   * @param slot  Original slot
   * @return  Proper interaction source
   */
  public static InteractionSource fromEquipmentSlot(EquipmentSlot slot) {
    return switch (slot.getType()) {
      case ARMOR -> ARMOR;
      case HAND -> RIGHT_CLICK;
    };
  }
}

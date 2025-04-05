package slimeknights.tconstruct.library.modifiers.hook.interaction;

import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.Collection;

/** Modifier hook to implement behavior of interacting with a tool in a slot */
public interface SlotStackModifierHook {
  /**
   * Run when attempting to stack a tool on another slot.
   * As preconditions, we are right-clicking, the tool is stack size 1, and the slot allows modification.
   * @param heldTool  Tool currently held by the mouse
   * @param modifier  Modifier running this hook
   * @param slot      Slot clicked with the tool
   * @param player    Player in the inventory
   * @return  True if standard slot interactions should be prevented
   */
  default boolean overrideStackedOnOther(IToolStackView heldTool, ModifierEntry modifier, Slot slot, Player player) {
    return false;
  }

  /**
   * As preconditions, we are right clicking, the tool is stack size 1, and the slot allows modification.
   * @param slotTool  Tool currently in the slot
   * @param modifier  Modifier running this hook
   * @param held      Stack held by the mouse
   * @param slot      Slot containing the tool
   * @param player    Player in the inventory
   * @param access    Slot access
   * @return  True if standard slot interactions should be prevented
   */
  default boolean overrideOtherStackedOnMe(IToolStackView slotTool, ModifierEntry modifier, ItemStack held, Slot slot, Player player, SlotAccess access) {
    return false;
  }

  /** Helper to implement {@link net.minecraft.world.item.Item#overrideStackedOnOther(ItemStack, Slot, ClickAction, Player)} */
  static boolean overrideStackedOnOther(ItemStack held, Slot slot, ClickAction action, Player player) {
    if (action == ClickAction.SECONDARY && held.getCount() == 1 && slot.allowModification(player)) {
      ToolStack tool = ToolStack.from(held);
      for (ModifierEntry entry : tool.getModifiers()) {
        if (entry.getHook(ModifierHooks.SLOT_STACK).overrideStackedOnOther(tool, entry, slot, player)) {
          return true;
        }
      }
    }
    return false;
  }

  /** Helper to implement {@link net.minecraft.world.item.Item#overrideOtherStackedOnMe(ItemStack, ItemStack, Slot, ClickAction, Player, SlotAccess)} */
  static boolean overrideOtherStackedOnMe(ItemStack slotStack, ItemStack held, Slot slot, ClickAction action, Player player, SlotAccess access) {
    if (action == ClickAction.SECONDARY && slotStack.getCount() == 1 && slot.allowModification(player)) {
      ToolStack tool = ToolStack.from(slotStack);
      for (ModifierEntry entry : tool.getModifiers()) {
        if (entry.getHook(ModifierHooks.SLOT_STACK).overrideOtherStackedOnMe(tool, entry, held, slot, player, access)) {
          return true;
        }
      }
    }
    return false;
  }

  /** Merger that stops after the first success */
  record FirstMerger(Collection<SlotStackModifierHook> modules) implements SlotStackModifierHook {
    @Override
    public boolean overrideStackedOnOther(IToolStackView heldTool, ModifierEntry modifier, Slot slot, Player player) {
      for (SlotStackModifierHook module : modules) {
        if (module.overrideStackedOnOther(heldTool, modifier, slot, player)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public boolean overrideOtherStackedOnMe(IToolStackView slotTool, ModifierEntry modifier, ItemStack held, Slot slot, Player player, SlotAccess access) {
      for (SlotStackModifierHook module : modules) {
        if (module.overrideOtherStackedOnMe(slotTool, modifier, held, slot, player, access)) {
          return true;
        }
      }
      return false;
    }
  }
}

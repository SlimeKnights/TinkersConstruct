package slimeknights.tconstruct.library.modifiers.hook.armor;

import net.minecraft.world.entity.EquipmentSlot;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

/** Hook used when a tool is added or removed from any of the six {@link EquipmentSlot}. */
public interface EquipmentChangeModifierHook {
  /**
   * Called when a tinker tool is equipped to an entity
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link #onUnequip(IToolStackView, ModifierEntry, EquipmentChangeContext)}: Called when a tool is removed from an entity</li>
   *   <li>{@link #onEquipmentChange(IToolStackView, ModifierEntry, EquipmentChangeContext, EquipmentSlot)}: Called on all other slots did not change</li>
   * </ul>
   * @param tool         Tool equipped
   * @param modifier     Level of the modifier
   * @param context      Context about the event
   */
  default void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {}

  /**
   * Called when a tinker tool is unequipped from an entity
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link #onEquip(IToolStackView, ModifierEntry, EquipmentChangeContext)}}: Called when a tool is added to an entity</li>
   *   <li>{@link #onEquipmentChange(IToolStackView, ModifierEntry, EquipmentChangeContext, EquipmentSlot)}: Called on all other slots that did not change</li>
   * </ul>
   * @param tool         Tool unequipped
   * @param modifier     Level of the modifier
   * @param context      Context about the event
   */
  default void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {}

  /**
   * Called when a stack in a different slot changed. Not called on the slot that changed
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link #onUnequip(IToolStackView, ModifierEntry, EquipmentChangeContext)}: Called when a tool is removed from an entity</li>
   *   <li>{@link #onEquip(IToolStackView, ModifierEntry, EquipmentChangeContext)}: Called when a tool is added to an entity. Called instead of this hook for the new item</li>
   * </ul>
   * @param tool      Tool instance
   * @param modifier  Level of the modifier
   * @param context   Context describing the change
   * @param slotType  Slot containing this tool, did not change
   */
  default void onEquipmentChange(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context, EquipmentSlot slotType) {}

  /** Record that runs all nested hooks */
  record AllMerger(Collection<EquipmentChangeModifierHook> modules) implements EquipmentChangeModifierHook {
    @Override
    public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
      for (EquipmentChangeModifierHook module : modules) {
        module.onEquip(tool, modifier, context);
      }
    }

    @Override
    public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
      for (EquipmentChangeModifierHook module : modules) {
        module.onUnequip(tool, modifier, context);
      }
    }

    @Override
    public void onEquipmentChange(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context, EquipmentSlot slotType) {
      EquipmentChangeModifierHook.super.onEquipmentChange(tool, modifier, context, slotType);
      for (EquipmentChangeModifierHook module : modules) {
        module.onEquipmentChange(tool, modifier, context, slotType);
      }
    }
  }
}

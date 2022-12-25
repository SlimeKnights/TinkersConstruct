package slimeknights.tconstruct.library.tools.capability;

import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.capability.ToolInventoryCapability.InventoryModifierHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/**
 * Shared logic to iterate fluid capabilities for {@link ToolFluidCapability}
 */
abstract class InventoryModifierHookIterator<I> extends CompoundIndexHookIterator<InventoryModifierHook,I> {
  /** Entry from {@link #findHook(IToolStackView, int)}, will be set during or before iteration */
  protected ModifierEntry indexEntry = null;

  @Override
  protected int getSize(IToolStackView tool, InventoryModifierHook hook) {
    return hook.getSlots(tool, indexEntry);
  }
}

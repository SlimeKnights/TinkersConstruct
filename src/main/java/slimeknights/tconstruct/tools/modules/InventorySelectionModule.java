package slimeknights.tconstruct.tools.modules;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability.InventoryModifierHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

/** Common logic between {@link slimeknights.tconstruct.tools.modules.ranged.TrickQuiverModule} and {@link slimeknights.tconstruct.tools.modules.armor.MinimapModule} */
public interface InventorySelectionModule {
  /** Displays a message when the disabled slot is selected */
  void onDisableSelection(IToolStackView tool, ModifierEntry modifier, Player player);

  /** Displays a message when a stack is selected */
  void onInventorySelect(IToolStackView tool, ModifierEntry modifier, Player player, int newIndex, ItemStack stack);

  /**
   * Helper to call in {@link slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook#onToolUse(IToolStackView, ModifierEntry, Player, InteractionHand, InteractionSource)}
   * or {@link slimeknights.tconstruct.library.modifiers.hook.interaction.KeybindInteractModifierHook#startInteract(IToolStackView, ModifierEntry, Player, EquipmentSlot, TooltipKey)}
   */
  default boolean selectNext(IToolStackView tool, ModifierEntry modifier, Player player, ResourceLocation selectedSlot) {
    // first, find the new number
    ModDataNBT data = tool.getPersistentData();
    InventoryModifierHook inventory = modifier.getHook(ToolInventoryCapability.HOOK);
    int totalSlots = inventory.getSlots(tool, modifier);
    int current = data.getInt(selectedSlot);
    // support going 1 above max to disable the trick arrows
    int newSelected = (current + 1) % (totalSlots + 1);
    // skip over empty slots; helps when you don't use the full space
    while (newSelected < totalSlots && inventory.getStack(tool, modifier, newSelected).isEmpty()) {
      newSelected++;
    }

    // display a message about what is now selected
    if (newSelected != current) {
      if (!player.level().isClientSide) {
        data.putInt(selectedSlot, newSelected);
        if (newSelected == totalSlots) {
          onDisableSelection(tool, modifier, player);
        } else {
          onInventorySelect(tool, modifier, player, newSelected, inventory.getStack(tool, modifier, newSelected));
        }
      }
      return true;
    }
    return false;
  }
}

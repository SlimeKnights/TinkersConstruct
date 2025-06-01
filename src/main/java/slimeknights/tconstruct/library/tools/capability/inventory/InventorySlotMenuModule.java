package slimeknights.tconstruct.library.tools.capability.inventory;

import lombok.Getter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.SlotStackModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/**
 * Module to open the inventory menu on right click.
 * Note in most cases it is not needed to use this module as {@link InventoryModule} already provides it.
 * This module is used for special interactions with the crafting table module.
 */
public enum InventorySlotMenuModule implements ModifierModule, SlotStackModifierHook {
  INSTANCE;

  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<InventorySlotMenuModule>defaultHooks(ModifierHooks.SLOT_STACK);

  @Getter
  private final SingletonLoader<InventorySlotMenuModule> loader = new SingletonLoader<>(this);

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public boolean overrideOtherStackedOnMe(IToolStackView slotTool, ModifierEntry modifier, ItemStack held, Slot slot, Player player, SlotAccess access) {
    if (held.isEmpty() && slot.container == player.getInventory() && isValidContainer(player.containerMenu)) {
      if (!player.level().isClientSide) {
        // during inventory slot interactions, the menu calls `suppressRemoteUpdates()` and calls `resumeRemoteUpdates()` later
        // But we are swapping the open menu, so resume will never get called and we are possibly improperly resumed
        // mostly is an issue for inventoryMenu itself
        // so resume updates on the container we are about to open
        player.containerMenu.resumeRemoteUpdates();
        ToolInventoryCapability.tryOpenContainer(slot.getItem(), slotTool, slotTool.getDefinition(), player, slot.getSlotIndex());
        // then suppress updates on the new container after opening
        player.containerMenu.suppressRemoteUpdates();
      }
      return true;
    }
    return false;
  }

  /** Checks if the given menu supports opening the menu */
  public static boolean isValidContainer(AbstractContainerMenu menu) {
    // player inventory has a null type, which throws when used through the getter
    if (menu.menuType == null) {
      return true;
    }
    // because vanilla set the throw precedent, add protection for other cases, just in case
    // the try here is basically free
    try {
      return RegistryHelper.contains(BuiltInRegistries.MENU, TinkerTags.MenuTypes.TOOL_INVENTORY_REPLACEMENTS, menu.getType());
    }
    catch (UnsupportedOperationException e) {
      return false;
    }
  }
}

package slimeknights.tconstruct.tools.modules.armor;

import com.google.common.collect.ImmutableSet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.KeybindInteractModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability.InventoryModifierHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;
import java.util.Set;

import static slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability.isBlacklisted;

/** Module implementing hotbar swap on key press */
public record ToolBeltModule(Set<TooltipKey> keys) implements ModifierModule, KeybindInteractModifierHook {
  public static final RecordLoadable<ToolBeltModule> LOADER = RecordLoadable.create(TinkerLoadables.TOOLTIP_KEY.set().requiredField("on_key", ToolBeltModule::keys), ToolBeltModule::new);
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ToolBeltModule>defaultHooks(ModifierHooks.ARMOR_INTERACT, ModifierHooks.ARMOR_INTERACT);

  public ToolBeltModule(TooltipKey... keys) {
    this(ImmutableSet.copyOf(keys));
  }

  @Override
  public RecordLoadable<ToolBeltModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public boolean startInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot equipmentSlot, TooltipKey keyModifier) {
    if (keys.contains(keyModifier)) {
      if (player.level.isClientSide) {
        return true;
      }

      // swap non-blacklisted items
      InventoryModifierHook belt = modifier.getHook(ToolInventoryCapability.HOOK);
      Inventory inventory = player.getInventory();
      int slots = Math.min(inventory.items.size(), belt.getSlots(tool, modifier));
      boolean didChange = false;
      for (int slot = 0; slot < slots; slot++) {
        ItemStack original = inventory.getItem(slot);
        if (original.isEmpty() || !isBlacklisted(original)) {
          ItemStack beltItem = belt.getStack(tool, modifier, slot);
          inventory.setItem(slot, beltItem);
          belt.setStack(tool, modifier, slot, original);
          didChange = !original.isEmpty() || !beltItem.isEmpty();
        }
      }

      // sound effect
      if (didChange) {
        player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARMOR_EQUIP_GENERIC, SoundSource.PLAYERS, 1.0f, 1.0f);
      }
      return true;
    }
    return false;
  }
}

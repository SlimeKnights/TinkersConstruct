package slimeknights.tconstruct.tools.modules.armor;

import com.google.common.collect.ImmutableSet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
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

/** Module implementing shield strap, which swaps the contents of the inventory with the offhand */
public record ShieldStrapModule(Set<TooltipKey> keys) implements ModifierModule, KeybindInteractModifierHook {
  public static final RecordLoadable<ShieldStrapModule> LOADER = RecordLoadable.create(TinkerLoadables.TOOLTIP_KEY.set().requiredField("on_key", ShieldStrapModule::keys), ShieldStrapModule::new);
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ShieldStrapModule>defaultHooks(ModifierHooks.ARMOR_INTERACT, ModifierHooks.ARMOR_INTERACT);

  public ShieldStrapModule(TooltipKey... keys) {
    this(ImmutableSet.copyOf(keys));
  }

  @Override
  public RecordLoadable<ShieldStrapModule> getLoader() {
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
      // offhand must be able to go in the pants
      ItemStack offhand = player.getOffhandItem();
      if (offhand.isEmpty() || !ToolInventoryCapability.isBlacklisted(offhand)) {
        InventoryModifierHook inventory = modifier.getHook(ToolInventoryCapability.HOOK);
        int slots = inventory.getSlots(tool, modifier);

        // new offhand is first slot
        ItemStack newOffhand = inventory.getStack(tool, modifier, 0);
        player.setItemInHand(InteractionHand.OFF_HAND, newOffhand);
        // shift all other slots back by 1;
        for (int i = 1; i < slots; i++) {
          inventory.setStack(tool, modifier, i - 1, inventory.getStack(tool, modifier, i));
        }
        // put old offhand in last slot
        inventory.setStack(tool, modifier, slots - 1, offhand);

        // sound effect
        if (!newOffhand.isEmpty() || !offhand.isEmpty()) {
          player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARMOR_EQUIP_GENERIC, SoundSource.PLAYERS, 1.0f, 1.0f);
        }
        return true;
      }
    }
    return false;
  }
}

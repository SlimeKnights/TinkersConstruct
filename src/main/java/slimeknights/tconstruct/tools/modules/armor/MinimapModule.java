package slimeknights.tconstruct.tools.modules.armor;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IHaveLoader;
import slimeknights.mantle.util.LogicHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.KeybindInteractModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability.InventoryModifierHook;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;
import java.util.List;

/** Module implementing the minimap module. Should be paired with an {@link slimeknights.tconstruct.library.tools.capability.inventory.InventoryModule} */
public enum MinimapModule implements ModifierModule, EquipmentChangeModifierHook, KeybindInteractModifierHook, InventoryTickModifierHook, ModifierRemovalHook {
  INSTANCE;
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<MinimapModule>defaultHooks(ModifierHooks.EQUIPMENT_CHANGE, ModifierHooks.ARMOR_INTERACT, ModifierHooks.INVENTORY_TICK, ModifierHooks.REMOVE);
  public static final RecordLoadable<MinimapModule> LOADER = new SingletonLoader<>(INSTANCE);
  /** Data key for the active map on the player */
  public static final TinkerDataKey<ItemStack> MAP = TConstruct.createKey("current_map");
  /** Key for the currently selected map */
  private static final ResourceLocation SELECTED_SLOT = TConstruct.getResource("minimap_selected");
  /** Message when disabling the minimap */
  private static final Component DISABLED = TConstruct.makeTranslation("modifier", "minimap.disabled");
  /** Message to display selected slot */
  private static final String SELECTED = TConstruct.makeTranslationKey("modifier", "minimap.selected");

  @Override
  public RecordLoadable<? extends IHaveLoader> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public Integer getPriority() {
    return 30; // after slurping, before zoom
  }

  @Override
  public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (context.getChangedSlot() == EquipmentSlot.HEAD) {
      TinkerDataCapability.Holder data = LogicHelper.orElseNull(context.getTinkerData());
      if (data != null) {
        // set the map to the selected one
        ItemStack map = modifier.getHook(ToolInventoryCapability.HOOK).getStack(tool, modifier, tool.getPersistentData().getInt(SELECTED_SLOT));
        if (!map.isEmpty()) {
          data.put(MAP, map);
        } else {
          data.remove(MAP);
        }
      }
    }
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (context.getChangedSlot() == EquipmentSlot.HEAD) {
      TinkerDataCapability.Holder data = LogicHelper.orElseNull(context.getTinkerData());
      if (data != null) {
        data.remove(MAP);
      }
    }
  }

  @Override
  public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
    if (isCorrectSlot && !world.isClientSide) {
      TinkerDataCapability.Holder data = TinkerDataCapability.getData(holder);
      if (data != null) {
        ItemStack map = data.get(MAP, ItemStack.EMPTY);
        if (!map.isEmpty()) {
          // goal: we want to tick the map so it continues to explore. We could copy code from MapItem, but this gives us compatability with custom maps
          // hack: map logic requires the map to be in the selected slot to tick properly, so put it in the invnetory temporarily
          ItemStack held = holder.getOffhandItem();
          holder.setItemInHand(InteractionHand.OFF_HAND, map);
          map.inventoryTick(world, holder, Inventory.SLOT_OFFHAND, true);
          holder.setItemInHand(InteractionHand.OFF_HAND, held);
          if (holder instanceof ServerPlayer player) {
            MapItemSavedData mapData = MapItem.getSavedData(map, world);
            Integer id = MapItem.getMapId(map);
            if (mapData != null && id != null) {
              Packet<?> packet = mapData.getUpdatePacket(id, player);
              if (packet != null) {
                player.connection.send(packet);
              }
            }
          }
        }
      }
    }
  }

  @Override
  public boolean startInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot, TooltipKey keyModifier) {
    if (keyModifier == TooltipKey.NORMAL || keyModifier == TooltipKey.CONTROL) {
      // first, find the new number
      ModDataNBT data = tool.getPersistentData();
      InventoryModifierHook inventory = modifier.getHook(ToolInventoryCapability.HOOK);
      int totalSlots = inventory.getSlots(tool, modifier);
      int current = data.getInt(SELECTED_SLOT);
      // support going 1 above max to disable the map
      int newSelected = (current + 1) % (totalSlots + 1);
      // skip over empty slots; helps when you don't use the full space
      while (newSelected < totalSlots && inventory.getStack(tool, modifier, newSelected).isEmpty()) {
        newSelected++;
      }

      // only mark as doing something if we changed something
      if (newSelected != current) {
        if (!player.level().isClientSide) {
          data.putInt(SELECTED_SLOT, newSelected);

          // display a message about what is now selected
          if (newSelected == totalSlots) {
            player.displayClientMessage(DISABLED, true);
          } else {
            ItemStack selectedStack = inventory.getStack(tool, modifier, newSelected);
            player.displayClientMessage(Component.translatable(SELECTED, selectedStack.getHoverName(), MapItem.getMapId(selectedStack), newSelected + 1), true);
          }
        }
        return true;
      }
    }
    return false;
  }

  @Nullable
  @Override
  public Component onRemoved(IToolStackView tool, Modifier modifier) {
    tool.getPersistentData().remove(SELECTED_SLOT);
    return null;
  }
}

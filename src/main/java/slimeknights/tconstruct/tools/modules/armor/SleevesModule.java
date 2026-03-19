package slimeknights.tconstruct.tools.modules.armor;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability.InventoryModifierHook;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.modules.InventorySelectionModule;

import java.util.List;

/** Module implementing the sleeves modifier */
public enum SleevesModule implements ModifierModule, GeneralInteractionModifierHook, InventorySelectionModule {
  INSTANCE;

  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<SleevesModule>defaultHooks(ModifierHooks.GENERAL_INTERACT);
  public static final RecordLoadable<SleevesModule> LOADER = new SingletonLoader<>(INSTANCE);
  /** Key for the currently selected item */
  public static final ResourceLocation SELECTED_SLOT = TConstruct.getResource("sleeves_selected");
  /** Message when disabling the trick quiver */
  private static final Component DISABLED = TConstruct.makeTranslation("modifier", "sleeves.disabled");
  /** Message to display selected slot */
  private static final String SELECTED = TConstruct.makeTranslationKey("modifier", "sleeves.selected");

  @Override
  public RecordLoadable<SleevesModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void onInventorySelect(IToolStackView tool, ModifierEntry modifier, Player player, int newIndex, ItemStack stack) {
    player.displayClientMessage(Component.translatable(SELECTED, stack.getHoverName(), newIndex + 1), true);
  }

  @Override
  public void onDisableSelection(IToolStackView tool, ModifierEntry modifier, Player player) {
    player.displayClientMessage(DISABLED, true);
  }

  @Override
  public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
    if (tool.getHook(ToolHooks.INTERACTION).canInteract(tool, modifier.getId(), source)) {
      if (!player.isCrouching()) {
        InventoryModifierHook inventory = modifier.getHook(ToolInventoryCapability.HOOK);
        int selected = tool.getPersistentData().getInt(SELECTED_SLOT);
        ItemStack ammo = inventory.getStack(tool, modifier, selected);
        // if we have nothing, fallback to the slot toggle
        if (!ammo.isEmpty()) {
          if (!player.getCooldowns().isOnCooldown(ammo.getItem())) {
            // to use the item, we need it in the hand, but something else might be there, so temporarily swap
            ItemStack held = player.getItemInHand(hand);
            player.setItemInHand(hand, ammo);
            // use the item
            InteractionResultHolder<ItemStack> result = ammo.use(player.level(), player, hand);
            // restore original hand item
            player.setItemInHand(hand, held);
            // ensure the use action did not start us using items
            if (player.isUsingItem()) {
              player.stopUsingItem();
            }
            // handle result
            inventory.setStack(tool, modifier, selected, result.getObject());
            return result.getResult();
          } else {
            // toggle if we just were unable to use the item
            return InteractionResult.PASS;
          }
          // don't toggle if set to disable
        } else if (selected == inventory.getSlots(tool, modifier)) {
          return InteractionResult.PASS;
        }
      }
      return selectNext(tool, modifier, player, SELECTED_SLOT) ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }
    return InteractionResult.PASS;
  }
}

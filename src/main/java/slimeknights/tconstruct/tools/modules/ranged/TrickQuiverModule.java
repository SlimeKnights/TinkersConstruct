package slimeknights.tconstruct.tools.modules.ranged;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.json.SingletonRecordLoadable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.hook.ranged.BowAmmoModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability.InventoryModifierHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import java.util.List;
import java.util.function.Predicate;

/** Module implementing trick quiver, with a selectable arrow slot */
public enum TrickQuiverModule implements ModifierModule, BowAmmoModifierHook, GeneralInteractionModifierHook {
  INSTANCE;
  public static final SingletonRecordLoadable<TrickQuiverModule> LOADER = new SingletonRecordLoadable<>(INSTANCE);
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<TrickQuiverModule>defaultHooks(ModifierHooks.BOW_AMMO, ModifierHooks.GENERAL_INTERACT);
  /** Key for the currently selected arrow */
  private static final ResourceLocation SELECTED_SLOT = TConstruct.getResource("trick_quiver_selected");
  /** Message when disabling the trick quiver */
  private static final Component DISABLED = TConstruct.makeTranslation("modifier", "trick_quiver.disabled");
  /** Message displayed when the selected slot is empty */
  private static final String EMPTY = TConstruct.makeTranslationKey("modifier", "trick_quiver.empty");
  /** Message to display selected slot */
  private static final String SELECTED = TConstruct.makeTranslationKey("modifier", "trick_quiver.selected");

  @Override
  public RecordLoadable<TrickQuiverModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public ItemStack findAmmo(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, ItemStack standardAmmo, Predicate<ItemStack> ammoPredicate) {
    // if selected is too big (disabled), will automatially return nothing
    return modifier.getHook(ToolInventoryCapability.HOOK).getStack(tool, modifier, tool.getPersistentData().getInt(SELECTED_SLOT));
  }

  @Override
  public void shrinkAmmo(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, ItemStack ammo, int needed) {
    // assume no one else touched our selected slot, good assumption
    ammo.shrink(needed);
    modifier.getHook(ToolInventoryCapability.HOOK).setStack(tool, modifier, tool.getPersistentData().getInt(SELECTED_SLOT), ammo);
  }

  @Override
  public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
    if (!player.isCrouching()) {
      if (!player.level.isClientSide) {
        // first, increment the number
        ModDataNBT data = tool.getPersistentData();
        InventoryModifierHook inventory = modifier.getHook(ToolInventoryCapability.HOOK);
        int totalSlots = inventory.getSlots(tool, modifier);
        // support going 1 above max to disable the trick arrows
        int newSelected = (data.getInt(SELECTED_SLOT) + 1) % (totalSlots + 1);
        data.putInt(SELECTED_SLOT, newSelected);

        // display a message about what is now selected
        if (newSelected == totalSlots) {
          player.displayClientMessage(DISABLED, true);
        } else {
          ItemStack selectedStack = inventory.getStack(tool, modifier, newSelected);
          if (selectedStack.isEmpty()) {
            player.displayClientMessage(Component.translatable(EMPTY, newSelected + 1), true);
          } else {
            player.displayClientMessage(Component.translatable(SELECTED, selectedStack.getHoverName(), newSelected + 1), true);
          }
        }
      }
      return InteractionResult.SUCCESS;
    }
    return InteractionResult.PASS;
  }
}

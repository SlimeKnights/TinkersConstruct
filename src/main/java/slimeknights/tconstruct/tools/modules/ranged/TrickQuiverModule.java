package slimeknights.tconstruct.tools.modules.ranged;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.hook.ranged.BowAmmoModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.modules.InventorySelectionModule;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

/**
 * Module implementing trick quiver, with a selectable arrow slot.
 * TODO 1.21: move to {@link slimeknights.tconstruct.tools.modules.ranged.bow}
 */
public enum TrickQuiverModule implements ModifierModule, BowAmmoModifierHook, GeneralInteractionModifierHook, ModifierRemovalHook, InventorySelectionModule {
  INSTANCE;
  public static final SingletonLoader<TrickQuiverModule> LOADER = new SingletonLoader<>(INSTANCE);
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<TrickQuiverModule>defaultHooks(ModifierHooks.BOW_AMMO, ModifierHooks.GENERAL_INTERACT, ModifierHooks.REMOVE);
  /** Key for the currently selected arrow */
  private static final ResourceLocation SELECTED_SLOT = TConstruct.getResource("trick_quiver_selected");
  /** Message when disabling the trick quiver */
  private static final Component DISABLED = TConstruct.makeTranslation("modifier", "trick_quiver.disabled");
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
  public void onDisableSelection(IToolStackView tool, ModifierEntry modifier, Player player) {
    player.displayClientMessage(DISABLED, true);
  }

  @Override
  public void onInventorySelect(IToolStackView tool, ModifierEntry modifier, Player player, int newIndex, ItemStack stack) {
    player.displayClientMessage(Component.translatable(SELECTED, stack.getHoverName(), newIndex + 1), true);
  }

  @Override
  public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
    if (!player.isCrouching()) {
      return selectNext(tool, modifier, player, SELECTED_SLOT) ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }
    return InteractionResult.PASS;
  }

  @Nullable
  @Override
  public Component onRemoved(IToolStackView tool, Modifier modifier) {
    tool.getPersistentData().remove(SELECTED_SLOT);
    return null;
  }
}

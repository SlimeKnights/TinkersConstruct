package slimeknights.tconstruct.library.tools.capability.inventory;

import lombok.RequiredArgsConstructor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.primitive.EnumLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.display.DisplayNameModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.hook.interaction.KeybindInteractModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.definition.module.interaction.DualOptionInteraction;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;
import java.util.function.Function;

/**
 * Modifier module that opens a tool's inventory on right click or armor interact.
 * Note this does not add an inventory to the tool, it simply allows opening it using the modifier priority system.
 */
@RequiredArgsConstructor
public enum InventoryMenuModule implements ModifierModule, KeybindInteractModifierHook, GeneralInteractionModifierHook, DisplayNameModifierHook {
  ANY(TooltipKey.UNKNOWN),
  NORMAL(TooltipKey.NORMAL),
  SHIFT(TooltipKey.SHIFT),
  CONTROL(TooltipKey.CONTROL),
  ALT(TooltipKey.ALT);

  public static final RecordLoadable<InventoryMenuModule> LOADER = RecordLoadable.create(new EnumLoadable<>(InventoryMenuModule.class).requiredField("on_key", Function.identity()), Function.identity());
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<InventoryMenuModule>defaultHooks(ModifierHooks.ARMOR_INTERACT, ModifierHooks.GENERAL_INTERACT, ModifierHooks.DISPLAY_NAME);

  private final TooltipKey requiredKey;

  @Override
  public RecordLoadable<InventoryMenuModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public Integer getPriority() {
    // run late so keybind does not prevent shield strap or tool belt
    return 75;
  }

  @Override
  public Component getDisplayName(IToolStackView tool, ModifierEntry entry, Component name) {
    return DualOptionInteraction.formatModifierName(tool, entry.getModifier(), name);
  }

  @Override
  public boolean startInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot, TooltipKey keyModifier) {
    if (requiredKey == TooltipKey.UNKNOWN || requiredKey == keyModifier) {
      return ToolInventoryCapability.tryOpenContainer(player.getItemBySlot(slot), tool, player, slot).consumesAction();
    }
    return false;
  }

  @Override
  public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
    if (player.isCrouching() && tool.getHook(ToolHooks.INTERACTION).canInteract(tool, modifier.getId(), source)) {
      EquipmentSlot slot = source.getSlot(hand);
      return ToolInventoryCapability.tryOpenContainer(player.getItemBySlot(slot), tool, player, slot);
    }
    return InteractionResult.PASS;
  }
}

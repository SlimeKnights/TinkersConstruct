package slimeknights.tconstruct.library.modifiers.dynamic;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.hook.interaction.KeybindInteractModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.InventoryModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.definition.module.interaction.DualOptionInteraction;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/** @deprecated use {@link slimeknights.tconstruct.library.tools.capability.inventory.InventoryMenuModule} and {@link slimeknights.tconstruct.library.tools.capability.inventory.InventoryModule} */
@SuppressWarnings({"DeprecatedIsStillUsed", "removal"})
@Deprecated(forRemoval = true)
public class InventoryMenuModifier extends InventoryModifier implements KeybindInteractModifierHook, GeneralInteractionModifierHook {
  /** Loader instance */
  public static final RecordLoadable<InventoryMenuModifier> LOADER = RecordLoadable.create(IntLoadable.FROM_ONE.requiredField("size", m -> m.slotsPerLevel), InventoryMenuModifier::new);

  public InventoryMenuModifier(int size) {
    super(size);
  }

  public InventoryMenuModifier(ResourceLocation key, int size) {
    super(key, size);
  }

  @Override
  public int getPriority() {
    return 75; // run latest so the keybind does not prevent shield strap or tool belt
  }

  @Override
  public Component getDisplayName(IToolStackView tool, ModifierEntry entry) {
    return DualOptionInteraction.formatModifierName(tool, this, super.getDisplayName(tool, entry));
  }

  @Override
  public boolean startInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot, TooltipKey keyModifier) {
    return ToolInventoryCapability.tryOpenContainer(player.getItemBySlot(slot), tool, player, slot).consumesAction();
  }

  @Override
  public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
    if (player.isCrouching() && tool.getHook(ToolHooks.INTERACTION).canInteract(tool, modifier.getId(), source)) {
      EquipmentSlot slot = source.getSlot(hand);
      return ToolInventoryCapability.tryOpenContainer(player.getItemBySlot(slot), tool, player, slot);
    }
    return InteractionResult.PASS;
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.ARMOR_INTERACT, ModifierHooks.GENERAL_INTERACT);
  }

  @Override
  public IGenericLoader<? extends Modifier> getLoader() {
    return LOADER;
  }
}

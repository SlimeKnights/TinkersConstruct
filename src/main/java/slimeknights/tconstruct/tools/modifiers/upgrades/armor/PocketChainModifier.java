package slimeknights.tconstruct.tools.modifiers.upgrades.armor;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hooks.IArmorInteractModifier;
import slimeknights.tconstruct.library.modifiers.impl.SingleLevelModifier;
import slimeknights.tconstruct.library.tools.capability.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

public class PocketChainModifier extends SingleLevelModifier implements IArmorInteractModifier {
  @Override
  public boolean startArmorInteract(IToolStackView tool, int level, Player player, EquipmentSlot slot) {
    if (player.isShiftKeyDown()) {
      return ToolInventoryCapability.tryOpenContainer(player.getItemBySlot(slot), tool, player, slot).consumesAction();
    }
    return false;
  }

  @Override
  public boolean startInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot) {
    return startArmorInteract(tool, modifier.getLevel(), player, slot);
  }

  @SuppressWarnings("unchecked")
  @Nullable
  @Override
  public <T> T getModule(Class<T> type) {
    if (type == IArmorInteractModifier.class) {
      return (T) this;
    }
    return super.getModule(type);
  }

  @Override
  protected boolean isSelfHook(ModifierHook<?> hook) {
    return hook == TinkerHooks.ARMOR_INTERACT || super.isSelfHook(hook);
  }
}

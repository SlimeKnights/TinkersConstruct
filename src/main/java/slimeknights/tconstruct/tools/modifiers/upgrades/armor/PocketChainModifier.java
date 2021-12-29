package slimeknights.tconstruct.tools.modifiers.upgrades.armor;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import slimeknights.tconstruct.library.modifiers.SingleLevelModifier;
import slimeknights.tconstruct.library.modifiers.hooks.IArmorInteractModifier;
import slimeknights.tconstruct.library.tools.capability.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import javax.annotation.Nullable;

public class PocketChainModifier extends SingleLevelModifier implements IArmorInteractModifier {
  public PocketChainModifier() {
    super(0x3E4453);
  }

  @Override
  public boolean startArmorInteract(IModifierToolStack tool, int level, Player player, EquipmentSlot slot) {
    if (player.isShiftKeyDown()) {
      return ToolInventoryCapability.tryOpenContainer(player.getItemBySlot(slot), tool, player, slot).consumesAction();
    }
    return false;
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
}

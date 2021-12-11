package slimeknights.tconstruct.tools.modifiers.slotless;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.modifiers.hooks.IArmorInteractModifier;
import slimeknights.tconstruct.library.tools.capability.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import javax.annotation.Nullable;

public class PocketChainModifier extends SingleUseModifier implements IArmorInteractModifier {
  public PocketChainModifier() {
    super(0x3E4453);
  }

  @Override
  public boolean startArmorInteract(IModifierToolStack tool, int level, PlayerEntity player, EquipmentSlotType slot) {
    if (player.isSneaking()) {
      return ToolInventoryCapability.tryOpenContainer(player.getItemStackFromSlot(slot), tool, player, slot).isSuccessOrConsume();
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

package slimeknights.tconstruct.tools.modifiers.ability.armor;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.hooks.IArmorInteractModifier;
import slimeknights.tconstruct.library.modifiers.impl.InventoryModifier;
import slimeknights.tconstruct.library.tools.capability.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import javax.annotation.Nullable;

public class PocketsModifier extends InventoryModifier implements IArmorInteractModifier {
  private static final ResourceLocation INVENTORY_KEY = TConstruct.getResource("pockets");
  public PocketsModifier() {
    super(0x976997, INVENTORY_KEY, 9);
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

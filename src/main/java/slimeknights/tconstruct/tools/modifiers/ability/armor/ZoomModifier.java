package slimeknights.tconstruct.tools.modifiers.ability.armor;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.modifiers.hooks.IArmorInteractModifier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import javax.annotation.Nullable;

public class ZoomModifier extends SingleUseModifier implements IArmorInteractModifier {
  public static final TinkerDataKey<Float> ZOOM_MULTIPLIER = TConstruct.createKey("zoom_multiplier");
  public ZoomModifier() {
    super(-1);
  }

  @Override
  public void onUnequip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    context.getTinkerData().ifPresent(data -> data.remove(ZOOM_MULTIPLIER));
  }

  @Override
  public boolean startArmorInteract(IModifierToolStack tool, int level, PlayerEntity player, EquipmentSlotType slot) {
    if (player.isSneaking()) {
      player.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> data.put(ZOOM_MULTIPLIER, 0.1f));
      return true;
    }
    return false;
  }

  @Override
  public void stopArmorInteract(IModifierToolStack tool, int level, PlayerEntity player, EquipmentSlotType slot) {
    player.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> data.remove(ZOOM_MULTIPLIER));
  }

  @Nullable
  @Override
  public <T> T getModule(Class<T> type) {
    return tryModuleMatch(type, IArmorInteractModifier.class, this);
  }
}

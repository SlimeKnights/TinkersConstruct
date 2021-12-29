package slimeknights.tconstruct.tools.modifiers.ability.armor;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.modifiers.hooks.IArmorInteractModifier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import javax.annotation.Nullable;

public class ZoomModifier extends SingleUseModifier implements IArmorInteractModifier {
  private static final ResourceLocation ZOOM = TConstruct.getResource("zoom");
  public ZoomModifier() {
    super(-1);
  }

  @Override
  public void onUnequip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    if (context.getEntity().level.isClientSide) {
      context.getTinkerData().ifPresent(data -> data.computeIfAbsent(TinkerDataKeys.FOV_MODIFIER).remove(ZOOM));
    }
  }

  @Override
  public boolean startArmorInteract(IModifierToolStack tool, int level, Player player, EquipmentSlot slot) {
    if (player.isShiftKeyDown()) {
      if (player.level.isClientSide()) {
        player.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> data.computeIfAbsent(TinkerDataKeys.FOV_MODIFIER).set(ZOOM, 0.1f));
      }
      return true;
    }
    return false;
  }

  @Override
  public void stopArmorInteract(IModifierToolStack tool, int level, Player player, EquipmentSlot slot) {
    if (player.level.isClientSide()) {
      player.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> data.computeIfAbsent(TinkerDataKeys.FOV_MODIFIER).remove(ZOOM));
    }
  }

  @Nullable
  @Override
  public <T> T getModule(Class<T> type) {
    return tryModuleMatch(type, IArmorInteractModifier.class, this);
  }
}

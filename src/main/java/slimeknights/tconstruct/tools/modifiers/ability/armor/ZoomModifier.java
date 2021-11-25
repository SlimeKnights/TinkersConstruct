package slimeknights.tconstruct.tools.modifiers.ability.armor;

import net.minecraft.entity.player.PlayerEntity;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.modifiers.hooks.IHelmetInteractModifier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import javax.annotation.Nullable;

public class ZoomModifier extends SingleUseModifier implements IHelmetInteractModifier {
  public static final TinkerDataKey<Float> ZOOM_MULTIPLIER = TConstruct.createKey("zoom_multiplier");
  public ZoomModifier() {
    super(-1);
  }

  @Override
  public void onUnequip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    context.getEntity().getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> data.remove(ZOOM_MULTIPLIER));
  }

  @Override
  public boolean startHelmetInteract(IModifierToolStack tool, int level, PlayerEntity player) {
    if (player.isSneaking()) {
      player.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> data.put(ZOOM_MULTIPLIER, 0.1f));
      return true;
    }
    return false;
  }

  @Override
  public void stopHelmetInteract(IModifierToolStack tool, int level, PlayerEntity player) {
    player.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> data.remove(ZOOM_MULTIPLIER));
  }

  @Nullable
  @Override
  public <T> T getModule(Class<T> type) {
    return tryModuleMatch(type, IHelmetInteractModifier.class, this);
  }
}

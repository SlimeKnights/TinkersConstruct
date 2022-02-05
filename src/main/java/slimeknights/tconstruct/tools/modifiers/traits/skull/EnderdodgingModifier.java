package slimeknights.tconstruct.tools.modifiers.traits.skull;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.events.teleport.EnderdodgingTeleportEvent;
import slimeknights.tconstruct.library.modifiers.impl.SingleUseModifier;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.TeleportHelper;
import slimeknights.tconstruct.library.utils.TeleportHelper.ITeleportEventFactory;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class EnderdodgingModifier extends SingleUseModifier {
  private static final ITeleportEventFactory FACTORY = EnderdodgingTeleportEvent::new;

  @Override
  public boolean isSourceBlocked(IToolStackView tool, int level, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount) {
    // teleport always from projectiles
    LivingEntity self = context.getEntity();
    if (!self.hasEffect(TinkerModifiers.teleportCooldownEffect.get()) && source instanceof IndirectEntityDamageSource) {
      if (TeleportHelper.randomNearbyTeleport(context.getEntity(), FACTORY)) {
        TinkerModifiers.teleportCooldownEffect.get().apply(self, 15 * 20, 0, true);
        ToolDamageUtil.damageAnimated(tool, (int)amount, self, slotType);
        return true;
      }
      return false;
    }
    return false;
  }

  @Override
  public void onAttacked(IToolStackView tool, int level, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
    // teleport randomly from other damage
    LivingEntity self = context.getEntity();
    if (!self.hasEffect(TinkerModifiers.teleportCooldownEffect.get()) && source.getEntity() instanceof LivingEntity && RANDOM.nextInt(10) == 0) {
      if (TeleportHelper.randomNearbyTeleport(context.getEntity(), FACTORY)) {
        TinkerModifiers.teleportCooldownEffect.get().apply(self, 15 * 20, 1, true);
      }
    }
  }
}

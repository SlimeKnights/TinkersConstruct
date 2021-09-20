package slimeknights.tconstruct.tools.modifiers.ability.armor;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.modifiers.SingleLevelModifier;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.utils.SlimeBounceHandler;

public class BouncyModifier extends SingleLevelModifier {
  public BouncyModifier() {
    super(0x27C6C6);
    MinecraftForge.EVENT_BUS.addListener(this::onFall);
  }

  /** Called when an entity lands to handle the event */
  private void onFall(LivingFallEvent event) {
    LivingEntity living = event.getEntityLiving();
    // using fall distance as the event distance could be reduced by jump boost
    if (living == null || living.fallDistance <= 2f) {
      return;
    }
    // can the entity bounce?
    ItemStack feet = living.getItemStackFromSlot(EquipmentSlotType.FEET);
    if (ModifierUtil.getModifierLevel(feet, this) == 0) {
      return;
    }

    // reduced fall damage when crouching
    if (living.isSuppressingBounce()) {
      event.setDamageMultiplier(0.2f);
      return;
    } else {
      event.setDamageMultiplier(0.0f);
    }

    // skip further client processing on players
    if (living.getEntityWorld().isRemote) {
      living.playSound(Sounds.SLIMY_BOUNCE.getSound(), 1f, 1f);
      SlimeBounceHandler.addBounceHandler(living);
      return;
    }

    // server players behave differently than non-server players, they have no velocity during the event, so we need to reverse engineer it
    Vector3d motion = living.getMotion();
    if (living instanceof ServerPlayerEntity) {
      // velocity is lost on server players, but we dont have to defer the bounce
      double gravity = living.getAttributeValue(ForgeMod.ENTITY_GRAVITY.get());
      double time = Math.sqrt(living.fallDistance / gravity);
      double velocity = gravity * time;
      living.setMotion(motion.x / 0.95f, velocity, motion.z / 0.95f);
      living.velocityChanged = true;

      // preserve momentum
      SlimeBounceHandler.addBounceHandler(living);
    } else {
      // for non-players, need to deferr the bounce
      // only slow down half as much when bouncing
      living.setMotion(motion.x / 0.95f, motion.y * -0.9, motion.z / 0.95f);
      SlimeBounceHandler.addBounceHandler(living, living.getMotion().y);
    }
    // update airborn status
    living.isAirBorne = true;
    living.setOnGround(false);
    event.setDistance(0.0F);
    event.setCanceled(true);
    living.playSound(Sounds.SLIMY_BOUNCE.getSound(), 1f, 1f);
  }
}

package slimeknights.tconstruct.tools.modifiers.traits.skull;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.Explosion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.modifiers.hooks.IArmorInteractModifier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import javax.annotation.Nullable;
import java.util.UUID;

public class SelfDestructiveModifier extends SingleUseModifier implements IArmorInteractModifier {
  private static final AttributeModifier SPEED_MODIFIER = new AttributeModifier(UUID.fromString("68ee3026-1d50-4eb4-914e-a8b05fbfdb71"), TConstruct.prefix("self_destruct_slowdown"), -0.9f, Operation.MULTIPLY_TOTAL);
  /** Self damage source */
  private static final DamageSource SELF_DESTRUCT = (new DamageSource(TConstruct.prefix("self_destruct"))).setDamageBypassesArmor().setExplosion();
  /** Key for the time the fuse finises */
  private static final TinkerDataKey<Integer> FUSE_FINISH = TConstruct.createKey("self_destruct_finish");

  public SelfDestructiveModifier() {
    super(0x95D78E);
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerTickEvent.class, SelfDestructiveModifier::playerTick);
  }

  @Override
  public boolean startArmorInteract(IModifierToolStack tool, int level, PlayerEntity player, EquipmentSlotType slot) {
    if (player.isSneaking()) {
      player.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> data.put(FUSE_FINISH, player.ticksExisted + 30));
      player.playSound(SoundEvents.ENTITY_CREEPER_PRIMED, 1.0F, 0.5F);
      // make the player slow
      ModifiableAttributeInstance instance = player.getAttributeManager().createInstanceIfAbsent(Attributes.MOVEMENT_SPEED);
      if (instance != null) {
        instance.applyNonPersistentModifier(SPEED_MODIFIER);
      }
      return true;
    }
    return false;
  }

  /** Restores speed to full */
  private static void restoreSpeed(LivingEntity livingEntity) {
    ModifiableAttributeInstance instance = livingEntity.getAttributeManager().createInstanceIfAbsent(Attributes.MOVEMENT_SPEED);
    if (instance != null) {
      instance.removeModifier(SPEED_MODIFIER);
    }
  }

  @Override
  public void stopArmorInteract(IModifierToolStack tool, int level, PlayerEntity player, EquipmentSlotType slot) {
    player.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> data.remove(FUSE_FINISH));
    restoreSpeed(player);
  }

  @Override
  public void onUnequip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    context.getTinkerData().ifPresent(data -> data.remove(FUSE_FINISH));
    restoreSpeed(context.getEntity());
  }

  @Nullable
  @Override
  public <T> T getModule(Class<T> type) {
    return tryModuleMatch(type, IArmorInteractModifier.class, this);
  }

  /** Called on player tick to update the fuse */
  private static void playerTick(PlayerTickEvent event) {
    if (event.phase == Phase.START && !event.player.getEntityWorld().isRemote) {
      event.player.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
        Integer fuseFinish = data.get(FUSE_FINISH);
        if (fuseFinish != null && fuseFinish <= event.player.ticksExisted) {
          event.player.world.createExplosion(event.player, event.player.getPosX(), event.player.getPosY(), event.player.getPosZ(), 3, Explosion.Mode.DESTROY);
          event.player.attackEntityFrom(SELF_DESTRUCT, 99999);
          if (event.player.getHealth() > 0) {
            restoreSpeed(event.player);
          }
          data.remove(FUSE_FINISH);
        }
      });
    }
  }
}

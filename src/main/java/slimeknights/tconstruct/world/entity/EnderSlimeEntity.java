package slimeknights.tconstruct.world.entity;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.events.teleport.EnderSlimeTeleportEvent;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.utils.TeleportHelper;
import slimeknights.tconstruct.library.utils.TeleportHelper.ITeleportEventFactory;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.world.TinkerWorld;

public class EnderSlimeEntity extends ArmoredSlimeEntity {
  /** Predicate for this ender slime to allow teleporting */
  private final ITeleportEventFactory teleportPredicate = (entity, x, y, z) -> new EnderSlimeTeleportEvent(entity, x, y, z, this);

  public EnderSlimeEntity(EntityType<? extends EnderSlimeEntity> type, Level worldIn) {
    super(type, worldIn);
  }

  @Override
  protected ParticleOptions getParticleType() {
    return TinkerWorld.enderSlimeParticle.get();
  }

  @Override
  public void doEnchantDamageEffects(LivingEntity slime, Entity target) {
    super.doEnchantDamageEffects(slime, target);
    if (target instanceof LivingEntity) {
      TeleportHelper.randomNearbyTeleport((LivingEntity) target, teleportPredicate);
    }
  }

  @Override
  protected void actuallyHurt(DamageSource damageSrc, float damageAmount) {
    float oldHealth = getHealth();
    super.actuallyHurt(damageSrc, damageAmount);
    if (isAlive() && getHealth() < oldHealth) {
      TeleportHelper.randomNearbyTeleport(this, teleportPredicate);
    }
  }

  @Override
  protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
    // ender slime spawns with slimeskulls with a random material
    // vanilla logic but simplified down to just helmets
    float multiplier = difficulty.getSpecialMultiplier();
    if (this.random.nextFloat() < 0.15F * difficulty.getSpecialMultiplier()) {
      // 2.5% chance of plate
      ItemStack helmet = new ItemStack(TinkerTools.slimesuit.get(ArmorItem.Type.HELMET));
      // just init stats, will set random material
      ToolStack.from(helmet).ensureHasData();
      // finally, give the slime the helmet
      this.setItemSlot(EquipmentSlot.HEAD, helmet);
    }
  }
}

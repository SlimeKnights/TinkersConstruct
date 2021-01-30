package slimeknights.tconstruct.tools.melee;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import slimeknights.tconstruct.library.tools.SwordCore;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;

public class BroadSword extends SwordCore {

  public BroadSword(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  // sword sweep attack
  @Override
  public boolean dealDamage(ItemStack stack, LivingEntity player, Entity targetEntity, float damage) {
    // deal damage first
    boolean hit = super.dealDamage(stack, player, targetEntity, damage);

    if (hit && !ToolDamageUtil.isBroken(stack)) {
      // sweep code from EntityPlayer#attackTargetEntityWithCurrentItem()
      // basically: no crit, no sprinting and has to stand on the ground for sweep. Also has to move regularly slowly
      double d0 = player.distanceWalkedModified - player.prevDistanceWalkedModified;
      boolean flag = true;

      if (player instanceof PlayerEntity) {
        flag = ((PlayerEntity) player).getCooledAttackStrength(0.5F) > 0.9f;
      }

      boolean flag2 = player.fallDistance > 0.0F && !player.isOnGround() && !player.isOnLadder() && !player.isInWater() && !player.isPotionActive(Effects.BLINDNESS) && !player.isPassenger();

      if (flag && !player.isSprinting() && !flag2 && player.isOnGround() && d0 < (double) player.getAIMoveSpeed()) {
        for (LivingEntity livingEntity : player.getEntityWorld().getEntitiesWithinAABB(LivingEntity.class, targetEntity.getBoundingBox().grow(1.0D, 0.25D, 1.0D))) {
          if (livingEntity != player && livingEntity != targetEntity && !player.isOnSameTeam(livingEntity) && (!(livingEntity instanceof ArmorStandEntity)
            || !((ArmorStandEntity) livingEntity).hasMarker()) && player.getDistanceSq(livingEntity) < 9.0D) {
            livingEntity.applyKnockback(0.4F, MathHelper.sin(player.rotationYaw * ((float) Math.PI / 180F)), -MathHelper.cos(player.rotationYaw * ((float) Math.PI / 180F)));

            super.dealDamage(stack, player, livingEntity, 1f);
          }
        }

        player.getEntityWorld().playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0F, 1.0F);

        if (player instanceof PlayerEntity) {
          ((PlayerEntity) player).spawnSweepParticles();
        }
      }
    }

    return hit;
  }

  /*@Override
  public float getRepairModifierForPart(int index) {
    return DURABILITY_MODIFIER;
  }*/
}

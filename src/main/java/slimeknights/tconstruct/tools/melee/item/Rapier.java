package slimeknights.tconstruct.tools.melee.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.library.client.particle.Particles;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.SwordCore;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.shared.client.ParticleEffect;
import slimeknights.tconstruct.tools.TinkerTools;

public class Rapier extends SwordCore {

  public static final float DURABILITY_MODIFIER = 0.8f;

  public Rapier() {
    super(PartMaterialType.handle(TinkerTools.toolRod),
          PartMaterialType.head(TinkerTools.swordBlade),
          PartMaterialType.extra(TinkerTools.crossGuard));

    addCategory(Category.WEAPON);
  }

  @Override
  public float damagePotential() {
    return 0.55f;
  }

  @Override
  public float damageCutoff() {
    return 13f;
  }

  @Override
  public double attackSpeed() {
    return 3;
  }

  @Override
  public float knockback() {
    return 0.6f;
  }

  @Override
  public boolean dealDamage(ItemStack stack, EntityLivingBase player, Entity entity, float damage) {
    boolean hit;
    if(player instanceof EntityPlayer) {
      hit = dealHybridDamage(DamageSource.causePlayerDamage((EntityPlayer) player), entity, damage);
    }
    else {
      hit = dealHybridDamage(DamageSource.causeMobDamage(player), entity, damage);
    }

    if(hit && readyForSpecialAttack(player)) {
      TinkerTools.proxy.spawnAttackParticle(Particles.RAPIER_ATTACK, player, 0.8d);
    }

    return hit;
  }

  // changes the passed in damagesource, but the default method calls we use always create a new object
  public static boolean dealHybridDamage(DamageSource source, Entity target, float damage) {
    if(target instanceof EntityLivingBase) {
      damage /= 2f;
    }

    // half damage normal, half damage armor bypassing
    boolean hit = target.attackEntityFrom(source, damage);
    if(hit && target instanceof EntityLivingBase) {
      EntityLivingBase targetLiving = (EntityLivingBase) target;
      // reset things to deal damage again
      targetLiving.hurtResistantTime = 0;
      targetLiving.lastDamage = 0;
      targetLiving.attackEntityFrom(source.setDamageBypassesArmor(), damage);

      int count = Math.round(damage / 2f);
      if(count > 0) {
        TinkerTools.proxy.spawnEffectParticle(ParticleEffect.Type.HEART_ARMOR, targetLiving, count);
      }
    }
    return hit;
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
    ItemStack itemStackIn = playerIn.getHeldItem(hand);
    if(playerIn.onGround) {
      playerIn.addExhaustion(0.1f);
      playerIn.motionY += 0.32;
      float f = 0.5F;
      playerIn.motionX = MathHelper.sin(playerIn.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(playerIn.rotationPitch / 180.0F * (float) Math.PI) * f;
      playerIn.motionZ = -MathHelper.cos(playerIn.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(playerIn.rotationPitch / 180.0F * (float) Math.PI) * f;
      playerIn.getCooldownTracker().setCooldown(itemStackIn.getItem(), 4);
    }
    return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
  }

  @Override
  public float getRepairModifierForPart(int index) {
    return DURABILITY_MODIFIER;
  }

  @Override
  public ToolNBT buildTagData(List<Material> materials) {
    ToolNBT data = buildDefaultTag(materials);

    data.durability *= DURABILITY_MODIFIER;

    return data;
  }
}

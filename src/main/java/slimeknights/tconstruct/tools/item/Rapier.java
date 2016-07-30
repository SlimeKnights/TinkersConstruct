package slimeknights.tconstruct.tools.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.shared.client.ParticleEffect;
import slimeknights.tconstruct.tools.TinkerTools;

public class Rapier extends ToolCore {

  public static final float DURABILITY_MODIFIER = 0.8f;

  public Rapier() {
    super(PartMaterialType.handle(TinkerTools.toolRod),
          PartMaterialType.head(TinkerTools.swordBlade),
          PartMaterialType.extra(TinkerTools.crossGuard));

    addCategory(Category.WEAPON);
  }

  @Override
  public boolean isEffective(IBlockState state) {
    return BroadSword.effective_materials.contains(state.getMaterial());
  }

  @Override
  public float getStrVsBlock(ItemStack stack, IBlockState state) {
    if(state.getBlock() == Blocks.WEB) {
      return super.getStrVsBlock(stack, state) * 7.5f;
    }
    return super.getStrVsBlock(stack, state);
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
  public boolean dealDamage(ItemStack stack, EntityLivingBase player, EntityLivingBase entity, float damage) {
    boolean hit;
    if(player instanceof EntityPlayer) {
      hit = dealHybridDamage(DamageSource.causePlayerDamage((EntityPlayer) player), entity, damage);
    }
    else {
      hit = dealHybridDamage(DamageSource.causeMobDamage(player), entity, damage);
    }

    if(hit && readyForSpecialAttack(entity)) {
      TinkerTools.proxy.spawnAttackParticle(Particles.RAPIER_ATTACK, player, 0.8d);
    }

    return hit;
  }

  // changes the passed in damagesource, but the default method calls we use always create a new object
  private boolean dealHybridDamage(DamageSource source, EntityLivingBase target, float damage) {
    // half damage normal, half damage armor bypassing
    boolean hit = target.attackEntityFrom(source, damage / 2f);
    if(hit) {
      // reset things to deal damage again
      target.hurtResistantTime = 0;
      target.lastDamage = 0;
      target.attackEntityFrom(source.setDamageBypassesArmor(), damage / 2f);

      int count = Math.round(damage / 2f);
      if(count > 0) {
        TinkerTools.proxy.spawnEffectParticle(ParticleEffect.Type.HEART_ARMOR, target, count);
      }
    }
    return hit;
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(@Nonnull ItemStack itemStackIn, World worldIn, EntityPlayer player, EnumHand hand) {
    if(player.onGround) {
      player.addExhaustion(0.1f);
      player.motionY += 0.32;
      float f = 0.5F;
      player.motionX = (double) (MathHelper.sin(player.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI) * f);
      player.motionZ = (double) (-MathHelper.cos(player.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI) * f);
    }
    return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
  }

  @Override
  public float getRepairModifierForPart(int index) {
    return DURABILITY_MODIFIER;
  }

  @Override
  public NBTTagCompound buildTag(List<Material> materials) {
    ToolNBT data = buildDefaultTag(materials);

    data.durability *= DURABILITY_MODIFIER;

    return data.get();
  }
}

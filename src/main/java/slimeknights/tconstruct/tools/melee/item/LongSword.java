package slimeknights.tconstruct.tools.melee.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
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
import slimeknights.tconstruct.tools.TinkerTools;

public class LongSword extends SwordCore {

  public static final float DURABILITY_MODIFIER = 1.05f;

  public LongSword() {
    super(PartMaterialType.handle(TinkerTools.toolRod),
          PartMaterialType.head(TinkerTools.swordBlade),
          PartMaterialType.extra(TinkerTools.handGuard));

    addCategory(Category.WEAPON);
  }

  @Override
  public float damagePotential() {
    return 1.1f;
  }

  @Override
  public double attackSpeed() {
    return 1.4;
  }

  @Override
  public float damageCutoff() {
    return 18f;
  }

  @Nonnull
  @Override
  public EnumAction getItemUseAction(ItemStack stack) {
    return EnumAction.NONE;
  }

  @Override
  public int getMaxItemUseDuration(ItemStack stack) {
    return 200;
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
    ItemStack itemStackIn = playerIn.getHeldItem(hand);
    // don't allow free flight when using an elytra, should use fireworks
    if(playerIn.isElytraFlying()) {
      return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);
    }
    playerIn.setActiveHand(hand);
    return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
  }

  @Override
  public boolean dealDamage(ItemStack stack, EntityLivingBase player, Entity entity, float damage) {
    boolean hit = super.dealDamage(stack, player, entity, damage);

    // slash particle
    if(hit && readyForSpecialAttack(player)) {
      TinkerTools.proxy.spawnAttackParticle(Particles.LONGSWORD_ATTACK, player, 0.7d);
    }

    return hit;
  }

  @Override
  public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
    // has to be done in onUpdate because onTickUsing is too early and gets overwritten. bleh.
    preventSlowDown(entityIn, 0.9f);

    super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
  }

  @Override
  public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase player, int timeLeft) {
    int time = this.getMaxItemUseDuration(stack) - timeLeft;
    if(time > 5) {
      if(player instanceof EntityPlayer) {
        ((EntityPlayer) player).addExhaustion(0.2F);
      }
      player.setSprinting(true);

      float increase = (float) (0.02 * time + 0.2);
      if(increase > 0.56f) {
        increase = 0.56f;
      }
      player.motionY += increase;

      float speed = 0.05F * time;
      if(speed > 0.925f) {
        speed = 0.925f;
      }
      player.motionX = (double) (-MathHelper.sin(player.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI) * speed);
      player.motionZ = (double) (MathHelper.cos(player.rotationYaw / 180.0F * (float) Math.PI) * MathHelper
          .cos(player.rotationPitch / 180.0F * (float) Math.PI) * speed);
    }

    super.onPlayerStoppedUsing(stack, world, player, timeLeft);
  }

  @Override
  public float getRepairModifierForPart(int index) {
    return DURABILITY_MODIFIER;
  }

  @Override
  public ToolNBT buildTagData(List<Material> materials) {
    ToolNBT data = buildDefaultTag(materials);

    data.attack += 0.5f;
    data.durability *= DURABILITY_MODIFIER;

    return data;
  }
}

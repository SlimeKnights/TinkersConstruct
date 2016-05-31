package slimeknights.tconstruct.tools.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.client.particle.Particles;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.utils.EntityUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerTools;

public class FryPan extends ToolCore {

  public FryPan() {
    super(PartMaterialType.handle(TinkerTools.toolRod),
          PartMaterialType.head(TinkerTools.panHead));

    addCategory(Category.WEAPON);
  }

  @Override
  public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
    addDefaultSubItems(subItems);
    ItemStack tool = getInfiTool("Bane of Pigs");

    if(tool != null) {
      for(int i = 0; i < 25 * 5; i++) {
        TinkerTools.modFiery.apply(tool);
      }

      if(hasValidMaterials(tool)) {
        subItems.add(tool);
      }
    }
  }

  @Override
  public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase player, int timeLeft) {
    if(world.isRemote)
      return;

    float progress = Math.min(1f, (float)(getMaxItemUseDuration(stack) - timeLeft)/30f);
    float strength = .1f + 2.5f*progress*progress;

    float range = 3.2f;

    // is the player currently looking at an entity?
    Vec3d eye = new Vec3d(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ); // Entity.getPositionEyes
    Vec3d look = player.getLook(1.0f);
    RayTraceResult mop = EntityUtil.raytraceEntity(player, eye, look, range, true);

    // nothing hit :(
    if(mop == null) {
      return;
    }

    // we hit something. let it FLYYYYYYYYY
    if(mop.typeOfHit == RayTraceResult.Type.ENTITY) {
      Entity entity = mop.entityHit;
      double x = look.xCoord * strength;
      double y = look.yCoord/3f * strength + 0.1f + 0.4f * progress;
      double z = look.zCoord * strength;

      // bonus damage!
      AttributeModifier modifier = new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", progress * 5f, 0);
      AttributeModifier old = player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getModifier(ATTACK_DAMAGE_MODIFIER);

      // we set the entity on fire for the hit if it was fully charged
      // this makes it so it drops cooked stuff.. and it'funny :D
      boolean flamingStrike = progress >= 1f && !entity.isBurning();
      if(flamingStrike) entity.setFire(1);
      player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).removeModifier(old);
      player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).applyModifier(modifier);
      ToolHelper.attackEntity(stack, this, player, entity);
      player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).removeModifier(modifier);
      player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).applyModifier(old);
      if(flamingStrike) entity.extinguish();

      world.playSound(null, player.getPosition(), Sounds.frypan_boing, SoundCategory.PLAYERS, 1.5f, 0.6f + 0.2f * TConstruct.random.nextFloat());
      entity.addVelocity(x,y,z);
      TinkerTools.proxy.spawnAttackParticle(Particles.FRYPAN_ATTACK, player, 0.6d);
      if(entity instanceof EntityPlayerMP) {
        ((EntityPlayerMP)entity).connection.sendPacket(new SPacketEntityVelocity(entity));
      }
    }
  }

  @Override
  public boolean dealDamage(ItemStack stack, EntityLivingBase player, EntityLivingBase entity, float damage) {
    boolean hit = super.dealDamage(stack, player, entity, damage);
    if(hit || player.worldObj.isRemote) {
      player.playSound(Sounds.frypan_boing, 2f, 1f);
    }
    if(hit && readyForSpecialAttack(player)) {
      TinkerTools.proxy.spawnAttackParticle(Particles.FRYPAN_ATTACK, player, 0.8d);
    }
    return hit;
  }

  @Override
  public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
    return stack;
  }

  @Override
  public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
    // has to be done in onUpdate because onTickUsing is too early and gets overwritten. bleh.
    preventSlowDown(entityIn, 0.7f);

    super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
  }

  /**
   * How long it takes to use or consume an item
   */
  @Override
  public int getMaxItemUseDuration(ItemStack stack)
  {
    return 5 * 20;
  }

  /**
   * returns the action that specifies what animation to play when the items is being used
   */
  @Override
  public EnumAction getItemUseAction(ItemStack stack)
  {
    return EnumAction.BOW;
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
    playerIn.setActiveHand(hand);
    return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
  }

  @Override
  public float damagePotential() {
    return 1.0f;
  }

  @Override
  public float knockback() {
    return 2f;
  }

  @Override
  public double attackSpeed() {
    return 1.4d;
  }

  @Override
  public NBTTagCompound buildTag(List<Material> materials) {
    return buildDefaultTag(materials).get();
  }
}

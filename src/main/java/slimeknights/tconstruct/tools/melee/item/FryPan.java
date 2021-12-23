package slimeknights.tconstruct.tools.melee.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.library.client.particle.Particles;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.TinkerToolCore;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.EntityUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerTools;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public class FryPan extends TinkerToolCore {

  protected static final UUID FRYPAN_CHARGE_BONUS = UUID.fromString("b8f6d5f0-8d5a-11e6-ae22-56b6b6499611");

  public FryPan() {
    super(PartMaterialType.handle(TinkerTools.toolRod),
          PartMaterialType.head(TinkerTools.panHead));

    addCategory(Category.WEAPON);
  }

  @Override
  public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
    if(this.isInCreativeTab(tab)) {
      addDefaultSubItems(subItems);
      ItemStack tool = getInfiTool("Bane of Pigs");

      if(tool != null) {
        for(int i = 0; i < 25 * 5; i++) {
          TinkerModifiers.modFiery.apply(tool);
        }

        if(hasValidMaterials(tool)) {
          subItems.add(tool);
        }
      }
    }
  }

  @Override
  public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase player, int timeLeft) {
    if(world.isRemote) {
      return;
    }

    float progress = Math.min(1f, (getMaxItemUseDuration(stack) - timeLeft) / 30f);
    float strength = .1f + 2.5f * progress * progress;

    float range = 3.2f;

    // is the player currently looking at an entity?
    Vec3d eye = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ); // Entity.getPositionEyes
    Vec3d look = player.getLook(1.0f);
    RayTraceResult mop = EntityUtil.raytraceEntity(player, eye, look, range, true);

    // nothing hit :(
    if(mop == null) {
      return;
    }

    // we hit something. let it FLYYYYYYYYY
    if(mop.typeOfHit == RayTraceResult.Type.ENTITY) {
      Entity entity = mop.entityHit;
      double x = look.x * strength;
      double y = look.y / 3f * strength + 0.1f + 0.4f * progress;
      double z = look.z * strength;

      // bonus damage!
      AttributeModifier modifier = new AttributeModifier(FRYPAN_CHARGE_BONUS, "Frypan charge bonus", progress * 5f, 0);

      // we set the entity on fire for the hit if it was fully charged
      // this makes it so it drops cooked stuff.. and it'funny :D
      boolean flamingStrike = progress >= 1f && !entity.isBurning();
      if(flamingStrike) {
        entity.setFire(1);
      }
      player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).applyModifier(modifier);
      ToolHelper.attackEntity(stack, this, player, entity);
      player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).removeModifier(modifier);
      if(flamingStrike) {
        entity.extinguish();
      }

      world.playSound(null, player.getPosition(), Sounds.frypan_boing, SoundCategory.PLAYERS, 1.5f, 0.6f + 0.2f * TConstruct.random.nextFloat());
      entity.addVelocity(x, y, z);
      TinkerTools.proxy.spawnAttackParticle(Particles.FRYPAN_ATTACK, player, 0.6d);
      if(entity instanceof EntityPlayerMP) {
        TinkerNetwork.sendPacket(entity, new SPacketEntityVelocity(entity));
      }
    }
  }

  @Override
  public boolean dealDamage(ItemStack stack, EntityLivingBase player, Entity entity, float damage) {
    boolean hit = super.dealDamage(stack, player, entity, damage);
    if(hit || player.getEntityWorld().isRemote) {
      player.playSound(Sounds.frypan_boing, 2f, 1f);
    }
    if(hit && readyForSpecialAttack(player)) {
      TinkerTools.proxy.spawnAttackParticle(Particles.FRYPAN_ATTACK, player, 0.8d);
    }
    return hit;
  }

  @Override
  public boolean canDestroyBlockInCreative(World world, BlockPos pos, ItemStack stack, EntityPlayer player) {
    return false;
  }

  @Override
  public ItemStack onItemUseFinish(@Nonnull ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
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
  public int getMaxItemUseDuration(ItemStack stack) {
    return 5 * 20;
  }

  /**
   * returns the action that specifies what animation to play when the items is being used
   */
  @Nonnull
  @Override
  public EnumAction getItemUseAction(ItemStack stack) {
    return EnumAction.BOW;
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
    ItemStack itemStackIn = playerIn.getHeldItem(hand);
    playerIn.setActiveHand(hand);
    return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
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
  public ToolNBT buildTagData(List<Material> materials) {
    return buildDefaultTag(materials);
  }
}

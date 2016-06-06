package slimeknights.tconstruct.tools.item;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

import java.util.List;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerTools;

public class BroadSword extends ToolCore {

  public static final ImmutableSet<net.minecraft.block.material.Material> effective_materials =
      ImmutableSet.of(net.minecraft.block.material.Material.WEB,
                      net.minecraft.block.material.Material.VINE,
                      net.minecraft.block.material.Material.CORAL,
                      net.minecraft.block.material.Material.GOURD,
                      net.minecraft.block.material.Material.LEAVES);

  public BroadSword() {
    this(PartMaterialType.handle(TinkerTools.toolRod),
         PartMaterialType.head(TinkerTools.swordBlade),
         PartMaterialType.extra(TinkerTools.wideGuard));
  }

  protected BroadSword(PartMaterialType... requiredComponents) {
    super(requiredComponents);

    addCategory(Category.WEAPON);
  }

  @Override
  public boolean isEffective(IBlockState block) {
    return effective_materials.contains(block.getMaterial());
  }

  @Override
  public float getStrVsBlock(ItemStack stack, IBlockState state) {
    if(state.getBlock() == Blocks.WEB) {
      return super.getStrVsBlock(stack, state)*7.5f;
    }
    return super.getStrVsBlock(stack, state);
  }

  @Override
  public float damagePotential() {
    return 1.0f;
  }

  @Override
  public double attackSpeed() {
    return 1.6d; // default vanilla sword speed
  }

  @Override
  public float miningSpeedModifier() {
    return 0.5f; // slooow, because it's a swooooord
  }

  // sword sweep attack
  @Override
  public boolean dealDamage(ItemStack stack, EntityLivingBase player, EntityLivingBase entity, float damage) {
    // deal damage first
    boolean hit = super.dealDamage(stack, player, entity, damage);
    // and then sweep
    if(hit && !ToolHelper.isBroken(stack)) {
      // sweep code from EntityPlayer#attackTargetEntityWithCurrentItem()
      // basically: no crit, no sprinting and has to stand on the ground for sweep. Also has to move regularly slowly
      double d0 = (double)(player.distanceWalkedModified - player.prevDistanceWalkedModified);
      boolean flag = true;
      if(player instanceof EntityPlayer) {
        flag = ((EntityPlayer)player).getCooledAttackStrength(0.5F) > 0.9f;
      }
      boolean flag2 = player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder() && !player.isInWater() && !player.isPotionActive(MobEffects.BLINDNESS) && !player.isRiding();
      if (flag && !player.isSprinting() && !flag2 && player.onGround && d0 < (double)player.getAIMoveSpeed()) {
        for(EntityLivingBase entitylivingbase : player.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, entity.getEntityBoundingBox().expand(1.0D, 0.25D, 1.0D))) {
          if(entitylivingbase != player && entitylivingbase != entity && !player.isOnSameTeam(entitylivingbase) && player.getDistanceSqToEntity(entitylivingbase) < 9.0D) {
            entitylivingbase.knockBack(player, 0.4F, (double) MathHelper.sin(player.rotationYaw * 0.017453292F), (double) (-MathHelper.cos(player.rotationYaw * 0.017453292F)));
            super.dealDamage(stack, player, entitylivingbase, 1f);
          }
        }

        player.worldObj.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0F, 1.0F);
        if(player instanceof EntityPlayer) {
          ((EntityPlayer) player).spawnSweepParticles();
        }
      }
    }

    return hit;
  }

  @Override
  public NBTTagCompound buildTag(List<Material> materials) {
    ToolNBT data = buildDefaultTag(materials);
    // 2 base damage, like vanilla swords
    data.attack += 1f;
    data.durability *= 1.1f;
    return data.get();
  }
}

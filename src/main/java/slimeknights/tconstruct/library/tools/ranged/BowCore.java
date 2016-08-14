package slimeknights.tconstruct.library.tools.ranged;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.List;

import javax.annotation.Nonnull;

import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.IAmmoUser;
import slimeknights.tconstruct.library.tools.ProjectileLauncherNBT;
import slimeknights.tconstruct.library.utils.AmmoHelper;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.weapons.ranged.TinkerRangedWeapons;

public abstract class BowCore extends ProjectileLauncherCore implements IAmmoUser {

  public BowCore(PartMaterialType... requiredComponents) {
    super(requiredComponents);
  }

  /* Bow usage stuff */

  @Nonnull
  @Override
  public EnumAction getItemUseAction(ItemStack stack) {
    return EnumAction.BOW;
  }

  @Override
  public int getMaxItemUseDuration(ItemStack stack) {
    return 72000;
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(@Nonnull ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
    if(!ToolHelper.isBroken(itemStackIn)) {
      boolean hasAmmo = findAmmo(itemStackIn, playerIn) != null;

      ActionResult<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onArrowNock(itemStackIn, worldIn, playerIn, hand, hasAmmo);
      if(ret != null) {
        return ret;
      }

      if(playerIn.capabilities.isCreativeMode || hasAmmo) {
        playerIn.setActiveHand(hand);
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
      }
    }

    return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStackIn);
  }

  @Override
  public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
    if(ToolHelper.isBroken(stack) || !(entityLiving instanceof EntityPlayer)) {
      return;
    }
    EntityPlayer player = (EntityPlayer) entityLiving;
    ItemStack ammo = findAmmo(stack, entityLiving);
    if(ammo == null && !player.capabilities.isCreativeMode) {
      return;
    }

    int useTime = this.getMaxItemUseDuration(stack) - timeLeft;
    useTime = ForgeEventFactory.onArrowLoose(stack, worldIn, player, useTime, ammo != null);

    if(useTime < 0) {
      return;
    }

    if(ammo == null) {
      ammo = getCreativeProjectileStack();
    }

    ProjectileLauncherNBT data = getData(stack);
    float power = ItemBow.getArrowVelocity(useTime) * data.range * 3f;

    if(!worldIn.isRemote) {
      Entity projectile = getProjectileEntity(ammo, worldIn, player, power, 0f);

      if(projectile != null) {
        ToolHelper.damageTool(stack, 1, player);
        worldIn.spawnEntityInWorld(projectile);
      }
    }

    playShootSound(power, worldIn, player);

    consumeAmmo(ammo, player);
    player.addStat(StatList.getObjectUseStats(this));
  }

  protected Entity getProjectileEntity(ItemStack ammo, World world, EntityPlayer player, float power, float inaccuracy) {
    if(ammo.getItem() instanceof IAmmo) {
      return ((IAmmo) ammo.getItem()).getProjectile(ammo, world, player, power, 0f);
    }
    else if(ammo.getItem() instanceof ItemArrow) {
      EntityArrow projectile = ((ItemArrow) ammo.getItem()).createArrow(world, ammo, player);
      projectile.setAim(player, player.rotationPitch, player.rotationYaw, 0.0F, power, inaccuracy);
      if(player.capabilities.isCreativeMode) {
        projectile.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
      }
      return projectile;
    }
    // shizzle-foo, this fizzles too!
    return null;
  }

  protected void consumeAmmo(ItemStack ammo, EntityPlayer player) {
    if(ammo.getItem() instanceof IAmmo) {
      ((IAmmo) ammo.getItem()).useAmmo(ammo, player);
    }
    else {
      ammo.stackSize--;
      if (ammo.stackSize == 0)
      {
        player.inventory.deleteStack(ammo);
      }
    }
  }

  protected ItemStack getCreativeProjectileStack() {
    return new ItemStack(Items.ARROW);
  }

  public void playShootSound(float power, World world, EntityPlayer entityPlayer) {
    world.playSound(null, entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + power * 0.5F);
  }

  @Override
  public ItemStack findAmmo(ItemStack weapon, EntityLivingBase player) {
    RecipeMatch.Match match = AmmoHelper.findAmmoFromInventory(getAmmoItems(), player);
    // only 1 item max.
    return match != null ? match.stacks.get(0) : null;
  }

  @Override
  public ItemStack getAmmoToRender(ItemStack weapon, EntityLivingBase player) {
    if(ToolHelper.isBroken(weapon)) {
      return null;
    }
    return findAmmo(weapon, player);
  }

  protected abstract List<RecipeMatch> getAmmoItems();
}

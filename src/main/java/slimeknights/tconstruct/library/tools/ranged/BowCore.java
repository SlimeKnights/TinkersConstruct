package slimeknights.tconstruct.library.tools.ranged;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import slimeknights.tconstruct.library.client.BooleanItemPropertyGetter;
import slimeknights.tconstruct.library.events.ProjectileEvent;
import slimeknights.tconstruct.library.events.TinkerToolEvent;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.IAmmoUser;
import slimeknights.tconstruct.library.tools.ProjectileLauncherNBT;
import slimeknights.tconstruct.library.utils.AmmoHelper;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.ranged.TinkerRangedWeapons;

public abstract class BowCore extends ProjectileLauncherCore implements IAmmoUser {

  protected static final ResourceLocation PROPERTY_PULL = new ResourceLocation("pull");
  protected static final ResourceLocation PROPERTY_PULLING = new ResourceLocation("pulling");

  public BowCore(PartMaterialType... requiredComponents) {
    super(requiredComponents);

    addCategory(Category.LAUNCHER);

    this.addPropertyOverride(PROPERTY_PULL, new IItemPropertyGetter() {
      @Override
      @SideOnly(Side.CLIENT)
      public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
        if(entityIn == null) {
          return 0.0F;
        }
        else {
          ItemStack itemstack = entityIn.getActiveItemStack();
          return getDrawbackProgress(itemstack, entityIn);
        }
      }
    });
    this.addPropertyOverride(PROPERTY_PULLING, new BooleanItemPropertyGetter() {
      @Override
      @SideOnly(Side.CLIENT)
      public boolean applyIf(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
        return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack;
      }
    });
  }

  /* Stuff to override */

  protected float baseInaccuracy() {
    return 0f;
  }

  protected float baseProjectileSpeed() {
    return 3f;
  }

  protected int getDrawTime() {
    return 20;
  }

  protected float getDrawbackProgress(ItemStack itemStack, EntityLivingBase entityIn) {
    ItemStack itemstack = entityIn.getActiveItemStack();
    if(itemstack != null && itemstack.getItem() == BowCore.this) {
      int timePassed = itemstack.getMaxItemUseDuration() - entityIn.getItemInUseCount();
      return getDrawbackProgress(itemstack, timePassed);
    }
    else {
      return 0f;
    }
  }

  protected float getDrawbackProgress(ItemStack itemStack, int timePassed) {
    float drawProgress = ProjectileLauncherNBT.from(itemStack).drawSpeed * (float) timePassed;
    return Math.min(1f, drawProgress / (float) getDrawTime());
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

    if(useTime < 5) {
      return;
    }

    if(ammo == null) {
      ammo = getCreativeProjectileStack();
    }

    shootProjectile(ammo, stack, worldIn, player, useTime);

    player.addStat(StatList.getObjectUseStats(this));

    // needs to be done manually for the overrides to work out correctly
    // since TiC tools don't get updated by default due to their custom equip check
    // this interferes with the item properties since it gets the wrong itemstack
    // causing animations not to work
    TinkerRangedWeapons.proxy.updateEquippedItemForRendering(entityLiving.getActiveHand());
    TagUtil.setResetFlag(stack, true);
  }

  public void shootProjectile(ItemStack ammo, ItemStack bow, World worldIn, EntityPlayer player, int useTime) {
    float power = ItemBow.getArrowVelocity(useTime) * getDrawbackProgress(bow, useTime) * baseProjectileSpeed();
    power *= ProjectileLauncherNBT.from(bow).range;

    if(!worldIn.isRemote) {
      TinkerToolEvent.OnBowShoot event = TinkerToolEvent.OnBowShoot.fireEvent(bow, ammo, player, useTime);

      for(int i = 0; i < event.projectileCount; i++) {
        boolean usedAmmo = false;
        if(i == 0 || event.consumeAmmoPerProjectile) {
          usedAmmo = consumeAmmo(ammo, player);
        }
        EntityArrow projectile = getProjectileEntity(ammo, bow, worldIn, player, power, baseInaccuracy(), usedAmmo);

        if(projectile != null && ProjectileEvent.OnLaunch.fireEvent(projectile, bow, player)) {
          if(!player.capabilities.isCreativeMode) {
            ToolHelper.damageTool(bow, 1, player);
          }
          worldIn.spawnEntityInWorld(projectile);
        }
      }
    }

    playShootSound(power, worldIn, player);
  }

  public EntityArrow getProjectileEntity(ItemStack ammo, ItemStack bow, World world, EntityPlayer player, float power, float inaccuracy, boolean usedAmmo) {
    if(ammo.getItem() instanceof IAmmo) {
      return ((IAmmo) ammo.getItem()).getProjectile(ammo, bow, world, player, power, inaccuracy, usedAmmo);
    }
    else if(ammo.getItem() instanceof ItemArrow) {
      EntityArrow projectile = ((ItemArrow) ammo.getItem()).createArrow(world, ammo, player);
      projectile.setAim(player, player.rotationPitch, player.rotationYaw, 0.0F, power, inaccuracy);
      if(player.capabilities.isCreativeMode) {
        projectile.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
      }
      else if(!usedAmmo) {
        projectile.pickupStatus = EntityArrow.PickupStatus.DISALLOWED;
      }
      return projectile;
    }
    // shizzle-foo, this fizzles too!
    return null;
  }

  public boolean consumeAmmo(ItemStack ammo, EntityPlayer player) {
    // no ammo consumption in creative
    if(player.capabilities.isCreativeMode) {
      return false;
    }

    if(ammo.getItem() instanceof IAmmo) {
      return ((IAmmo) ammo.getItem()).useAmmo(ammo, player);
    }
    else {
      ammo.stackSize--;
      if (ammo.stackSize == 0)
      {
        player.inventory.deleteStack(ammo);
      }
      return true;
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
    return AmmoHelper.findAmmoFromInventory(getAmmoItems(), player);
  }

  @Override
  public ItemStack getAmmoToRender(ItemStack weapon, EntityLivingBase player) {
    if(ToolHelper.isBroken(weapon)) {
      return null;
    }
    return findAmmo(weapon, player);
  }

  protected abstract List<Item> getAmmoItems();
}

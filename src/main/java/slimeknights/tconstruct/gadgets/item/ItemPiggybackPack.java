package slimeknights.tconstruct.gadgets.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.potion.TinkerPotion;

public class ItemPiggybackPack extends ItemArmor {

  // todo: turn this into a config
  private static final int MAX_ENTITY_STACK = 3; // how many entities can be carried at once
  public static ArmorMaterial PIGGYBACK_MATERIAL = EnumHelper.addArmorMaterial("PIGGYBACK", Util.resource("piggyback"), 0, new int[]{0, 0, 0, 0}, 0, SoundEvents.BLOCK_SLIME_PLACE, 0);

  public ItemPiggybackPack() {
    super(PIGGYBACK_MATERIAL, 0, EntityEquipmentSlot.CHEST);

    this.setMaxStackSize(16);
  }

  @Override
  public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
    // is the chest slot empty?
    ItemStack chestArmor = playerIn.getItemStackFromSlot(this.armorType);

    // need enough space to exchange the chest armor
    if(chestArmor != null && chestArmor.getItem() != this && playerIn.inventory.getFirstEmptyStack() == -1) {
      // not enough inventory space
      return false;
    }

    if(pickupEntity(playerIn, target)) {
      // unequip old armor
      if(chestArmor != null && chestArmor.getItem() != this) {
        ItemHandlerHelper.giveItemToPlayer(playerIn, chestArmor);
        chestArmor = null;
      }

      // we could pick it up just fine, check if we need to "equip" more of the item
      if(chestArmor == null) {
        playerIn.setItemStackToSlot(this.armorType, stack.splitStack(1));
      } else if(chestArmor.stackSize < getEntitiesCarriedCount(playerIn)) {
        stack.splitStack(1);
        chestArmor.stackSize++;
      }
      // successfully picked up an entity
      return true;
    }

    return false;
  }

  public boolean pickupEntity(EntityPlayer player, Entity target) {
    // silly players, clicking on entities they're already carrying
    if(target.getRidingEntity() == player) {
      return false;
    }

    int count = 0;
    Entity toRide = player;
    while(toRide.isBeingRidden() && count < MAX_ENTITY_STACK) {
      toRide = toRide.getPassengers().get(0);
      count++;
    }

    // can only ride one entity each
    if(!toRide.isBeingRidden() && count < MAX_ENTITY_STACK) {
      // todo: possibly throw off all passengers of the target
      return target.startRiding(toRide, true);
    }
    return false;
  }

  public int getEntitiesCarriedCount(EntityPlayer player) {
    int count = 0;
    Entity ridden = player;
    while(ridden.isBeingRidden()) {
      count++;
      ridden = ridden.getPassengers().get(0);
    }

    return count;
  }

  public void matchCarriedEntitiesToCount(EntityLivingBase player, int maxCount) {
    int count = 0;
    // get top rider
    Entity ridden = player;
    while(ridden.isBeingRidden()) {
      ridden = ridden.getPassengers().get(0);
      count++;

      if(count > maxCount) {
        ridden.dismountRidingEntity();
      }
    }

  }

  @Override
  public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
    if(entityIn instanceof EntityLivingBase) {
      if(((EntityLivingBase) entityIn).getItemStackFromSlot(EntityEquipmentSlot.CHEST) == stack && entityIn.isBeingRidden()) {
        ((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(CarryPotionEffect.INSTANCE, 1, 0, true, false));
      }
    }
  }

  public static class CarryPotionEffect extends TinkerPotion {

    public static final CarryPotionEffect INSTANCE = new CarryPotionEffect();

    protected CarryPotionEffect() {
      super(Util.getResource("carry"), false, true);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
      return true; // check every tick
    }

    @Override
    public void performEffect(@Nonnull EntityLivingBase entityLivingBaseIn, int p_76394_2_) {
      ItemStack chestArmor = entityLivingBaseIn.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
      if(chestArmor == null) {
        TinkerGadgets.piggybackPack.matchCarriedEntitiesToCount(entityLivingBaseIn, 0);
      }
      else if(chestArmor.getItem() == TinkerGadgets.piggybackPack) {
        TinkerGadgets.piggybackPack.matchCarriedEntitiesToCount(entityLivingBaseIn, chestArmor.stackSize);
      }
    }
  }
}

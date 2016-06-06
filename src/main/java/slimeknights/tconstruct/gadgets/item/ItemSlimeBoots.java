package slimeknights.tconstruct.gadgets.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import slimeknights.mantle.item.ItemArmorTooltip;
import slimeknights.tconstruct.library.SlimeBounceHandler;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;

public class ItemSlimeBoots extends ItemArmorTooltip {

  // todo: determine if this needs toughness
  public static ArmorMaterial SLIME_MATERIAL = EnumHelper.addArmorMaterial("SLIME", Util.resource("slime"), 0, new int[]{0, 0, 0, 0}, 0, SoundEvents.BLOCK_SLIME_PLACE, 0);

  public ItemSlimeBoots() {
    super(SLIME_MATERIAL, 0, EntityEquipmentSlot.FEET);
    this.setCreativeTab(TinkerRegistry.tabGadgets);
    this.setMaxStackSize(1);
  }

  @Override
  public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType, Entity entity) {
    // can be worn as boots
    return armorType == EntityEquipmentSlot.FEET;
  }

  // equipping with rightclick
  public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
  {
    ItemStack itemstack = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);

    if (itemstack == null)
    {
      player.setItemStackToSlot(EntityEquipmentSlot.FEET, stack.copy());
      stack.stackSize--;
    }

    return stack;
  }

  // RUBBERY BOUNCY BOUNCERY WOOOOO
  @SubscribeEvent
  public void onFall(LivingFallEvent event) {
    EntityLivingBase entity = event.getEntityLiving();
    if(entity == null) {
      return;
    }
    ItemStack feet = entity.getItemStackFromSlot(EntityEquipmentSlot.FEET);
    if(feet == null || feet.getItem() != this) {
      return;
    }

    // thing is wearing slime boots. let's get bouncyyyyy
    if(!entity.isSneaking() && event.getDistance() > 2) {
      event.setDamageMultiplier(0);
      if(entity.worldObj.isRemote) {
        entity.motionY *= -0.9;
        //entity.motionY = event.distance / 15;
        //entity.motionX = entity.posX - entity.lastTickPosX;
        //entity.motionZ = entity.posZ - entity.lastTickPosZ;
        //event.entityLiving.motionY *= -1.2;
        //event.entityLiving.motionY += 0.8;
        event.getEntityLiving().isAirBorne = true;
        event.getEntityLiving().onGround = false;
        double f = 0.91d + 0.04d;
        //System.out.println((entityLiving.worldObj.isRemote ? "client: " : "server: ") + entityLiving.motionX);
        // only slow down half as much when bouncing
        entity.motionX /= f;
        entity.motionZ /= f;
      }
      else {
        event.setCanceled(true); // we don't care about previous cancels, since we just bounceeeee
      }
      entity.playSound(SoundEvents.ENTITY_SLIME_SQUISH, 1f, 1f);
      SlimeBounceHandler.addBounceHandler(entity, entity.motionY);
    }
    else if(!entity.worldObj.isRemote && entity.isSneaking()){
      event.setDamageMultiplier(0.1f);
    }
  }
}

package slimeknights.tconstruct.gadgets.item;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.TinkerCommons;

public class ItemSlimeBoots extends ItemArmor {

  public static ArmorMaterial SLIME_MATERIAL = EnumHelper.addArmorMaterial("SLIME", Util.resource("slime"), 0, new int[]{0, 0, 0, 0}, 0);

  public ItemSlimeBoots() {
    super(SLIME_MATERIAL, 0, 3);
    this.setCreativeTab(TinkerRegistry.tabGadgets);
    this.setMaxStackSize(1);
  }

  @Override
  public boolean isValidArmor(ItemStack stack, int armorType, Entity entity) {
    // can be worn as boots
    return armorType == 3;
  }

  // equipping with rightlcick
  public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
  {
    int slot = 1; // 0 = current item, 1 = feet
    ItemStack itemstack = playerIn.getEquipmentInSlot(slot);

    if (itemstack == null)
    {
      playerIn.setCurrentItemOrArmor(slot, itemStackIn.copy());
      itemStackIn.stackSize--;
    }

    return itemStackIn;
  }

  // RUBBERY BOUNCY BOUNCERY WOOOOO
  @SubscribeEvent
  public void onFall(LivingFallEvent event) {
    EntityLivingBase entity = event.entityLiving;
    if(entity == null) {
      return;
    }
    ItemStack feet = entity.getEquipmentInSlot(1);
    if(feet == null || feet.getItem() != this) {
      return;
    }

    // thing is wearing slime boots. let's get bouncyyyyy
    if(!entity.isSneaking()) {
      event.setCanceled(true); // we don't care about previous cancels, since we just bounceeeee
      //entity.motionY = -(entity.posY - entity.lastTickPosY) * 1.2f;

      entity.motionY = event.distance / 20;
      entity.motionX = entity.posX - entity.lastTickPosX;
      entity.motionZ = entity.posZ - entity.lastTickPosZ;
      //event.entityLiving.motionY *= -1.2;
      //event.entityLiving.motionY += 0.8;
      event.entityLiving.isAirBorne = true;
      entity.setJumping(true);
      entity.onGround = false;

      entity.playSound(Sounds.slime_small, 1f, 1f);
      if(entity instanceof EntityPlayerMP) {
        ((EntityPlayerMP) entity).playerNetServerHandler
            .sendPacket(new S12PacketEntityVelocity(entity));
        TinkerCommons.potionSlimeBounce.apply(entity);
      }
    }
    else {
      event.damageMultiplier = 0.1f;
    }
  }
}

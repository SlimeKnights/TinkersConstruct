package slimeknights.tconstruct.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.FakePlayer;

import slimeknights.tconstruct.TConstruct;

public final class PlayerHelper {
  private PlayerHelper() {}

  public static void spawnItemAtEntity (Entity entity, ItemStack stack, int delay)
  {
    if (!entity.worldObj.isRemote)
    {
      EntityItem entityitem = new EntityItem(entity.worldObj, entity.posX + 0.5D, entity.posY + 0.5D, entity.posZ + 0.5D, stack);
      entityitem.setPickupDelay(delay);
      entity.worldObj.spawnEntityInWorld(entityitem);
    }
  }

  public static void spawnItemAtPlayer (EntityPlayer player, ItemStack stack)
  {
    if (!player.worldObj.isRemote)
    {
      // try to put it into the players inventory
      if(player instanceof FakePlayer || !player.inventory.addItemStackToInventory(stack)) // note that the addItemStackToInventory is not called for fake players
      {
        // drop the rest as an entity
        EntityItem entityitem = new EntityItem(player.worldObj, player.posX + 0.5D, player.posY + 0.5D, player.posZ + 0.5D, stack);
        player.worldObj.spawnEntityInWorld(entityitem);
        if (!(player instanceof FakePlayer))
          entityitem.onCollideWithPlayer(player);
      }
      // if it got picked up, we're playing the sound
      else {
        player.worldObj.playSoundAtEntity(player, "random.pop", 0.2F, ((TConstruct.random.nextFloat() - TConstruct.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
        if(player instanceof EntityPlayerMP)
          player.inventoryContainer.detectAndSendChanges();
      }
    }

  }
}

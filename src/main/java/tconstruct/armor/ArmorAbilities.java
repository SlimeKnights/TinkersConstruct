package tconstruct.armor;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import java.util.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.armor.items.TravelGear;
import tconstruct.armor.player.TPlayerStats;
import tconstruct.library.modifier.IModifyable;

public class ArmorAbilities
{
    //Abilities
    boolean morphed;
    boolean morphLoaded = Loader.isModLoaded("Morph");
    boolean smartmoveLoaded = Loader.isModLoaded("SmartMoving");

    public static List<String> stepBoostedPlayers = new ArrayList();
    //ItemStack prevFeet;
    double prevMotionY;

    @SubscribeEvent
    public void playerTick (TickEvent.PlayerTickEvent event)
    {
        EntityPlayer player = event.player;
        TPlayerStats stats = TPlayerStats.get(player);

        // Wall climb
        if (stats.climbWalls)
        {
            double motionX = player.posX - player.lastTickPosX;
            double motionZ = player.posZ - player.lastTickPosZ;
            double motionY = player.posY - player.lastTickPosY - 0.762;
            if (motionY > 0.0D && (motionX == 0D || motionZ == 0D))
            {
                player.fallDistance = 0.0F;
            }
        }

        //Feet changes
        ItemStack feet = player.getCurrentArmor(0);
        if (feet != null)
        {
            if (feet.getItem() instanceof IModifyable && !player.isSneaking())
            {
                NBTTagCompound tag = feet.getTagCompound().getCompoundTag(((IModifyable) feet.getItem()).getBaseTagName());
                int sole = tag.getInteger("Slimy Soles");
                if (sole > 0)
                {
                    if (!player.isSneaking() && player.onGround && prevMotionY < -0.4)
                        player.motionY = -prevMotionY * (Math.min(0.99, sole * 0.2));
                }
            }
            prevMotionY = player.motionY;
        }
        /* Former step height boost handling 
        if (feet != prevFeet)
        {
            if (prevFeet != null && prevFeet.getItem() instanceof TravelGear)
                player.stepHeight -= 0.6f;
            if (feet != null && feet.getItem() instanceof TravelGear)
                player.stepHeight += 0.6f;
            prevFeet = feet;
        }*/
        boolean stepBoosted = stepBoostedPlayers.contains(player.getGameProfile().getName());
        if (stepBoosted)
            player.stepHeight = 1.1f;
        if (!stepBoosted && feet != null && feet.getItem() instanceof TravelGear)
        {
            stepBoostedPlayers.add(player.getGameProfile().getName());
        }
        else if (stepBoosted && (feet == null || !(feet.getItem() instanceof TravelGear)))
        {
            stepBoostedPlayers.remove(player.getGameProfile().getName());
            player.stepHeight -= 0.6f;
        }
        //TODO: Proper minimap support
        /*ItemStack stack = player.inventory.getStackInSlot(8);
        if (stack != null && stack.getItem() instanceof ItemMap)
        {
            stack.getItem().onUpdate(stack, player.worldObj, player, 8, true);
        }*/

        if (morphLoaded)
        {
            if (morph.api.Api.hasMorph(player.getCommandSenderName(), event.side.isClient()))
            {
                morphed = true;
            }
        }

        if (!player.isPlayerSleeping() && !smartmoveLoaded)
        {
            ItemStack chest = player.getCurrentArmor(2);
            if (chest == null || !(chest.getItem() instanceof IModifyable))
            {
                if (!(morphLoaded && morphed))
                    PlayerAbilityHelper.setEntitySize(player, 0.6F, 1.8F);
            }
            else
            {
                NBTTagCompound tag = chest.getTagCompound().getCompoundTag(((IModifyable) chest.getItem()).getBaseTagName());
                int dodge = tag.getInteger("Perfect Dodge");
                if (dodge > 0)
                {
                    if (!(morphLoaded && morphed))
                        PlayerAbilityHelper.setEntitySize(player, Math.max(0.15F, 0.6F - (dodge * 0.09f)), 1.8F - (dodge * 0.04f));
                }
            }
        }
    }
}

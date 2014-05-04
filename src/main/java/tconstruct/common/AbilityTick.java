package tconstruct.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import tconstruct.TConstruct;
import tconstruct.util.player.TPlayerStats;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;

public class AbilityTick
{

    @SubscribeEvent
    public void tickEnd (WorldTickEvent event)
    {
        for (TPlayerStats stats : TConstruct.playerTracker.getServerStatList().values())
        {
            EntityPlayer player = stats.player.get();
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
            ItemStack stack = player.inventory.getStackInSlot(8);
            if (stack != null && stack.getItem() instanceof ItemMap)
            {
                stack.getItem().onUpdate(stack, player.worldObj, player, 8, true);
            }
        }
    }

}

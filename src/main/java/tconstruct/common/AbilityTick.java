package tconstruct.common;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import tconstruct.TConstruct;
import tconstruct.util.player.TPlayerStats;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class AbilityTick implements ITickHandler
{

    @Override
    public void tickStart (EnumSet<TickType> type, Object... tickData)
    {

    }

    @Override
    public void tickEnd (EnumSet<TickType> type, Object... tickData)
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
        }
    }

    @Override
    public EnumSet<TickType> ticks ()
    {
        return EnumSet.of(TickType.WORLD);
    }

    @Override
    public String getLabel ()
    {
        return null;
    }

}

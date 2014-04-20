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

    int tick = 0;

    @Override
    public void tickEnd (EnumSet<TickType> type, Object... tickData)
    {
        tick++;
        for (TPlayerStats stats : TConstruct.playerTracker.getServerStatList().values())
        {
            EntityPlayer player = stats.player.get();
            if (stats.climbWalls)
            {
                double motionX = player.posX - player.lastTickPosX;
                double motionZ = player.posZ - player.lastTickPosZ;
                double motionY = player.posY - player.lastTickPosY;
                if (motionY > 0.0D && (motionX == 0D || motionZ == 0D))
                {
                    player.fallDistance = 0.0F;
                }
            }
            if (tick == 10)
            {
                tick = 0;
                if (stats.activeGoggles)
                {
                    ItemStack helmet = player.getCurrentItemOrArmor(1);
                    if (helmet.getItem() == TContent.travelGoggles)
                    {
                        player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 15 * 20, 0, true));
                    }
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

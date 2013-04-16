package mods.tinker.tconstruct.player;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class TCommonTickHandler implements ITickHandler
{
    int ticks = 0;

    @Override
    public void tickStart (EnumSet<TickType> type, Object... tickData)
    {

    }

    public void tickEnd (EnumSet<TickType> type, Object... tickData)
    {
        ticks++;
        if (ticks >= 100)
        {
            EntityPlayer player = (EntityPlayer) tickData[0];
            int hp = player.getHealth();
            if (hp < 100 && hp >= 20)
                player.setEntityHealth(hp + 1);
            ticks = 0;
        }
    }

    @Override
    public String getLabel ()
    {
        return null;
    }

    @Override
    public EnumSet<TickType> ticks ()
    {
        return EnumSet.of(TickType.PLAYER);
    }

}

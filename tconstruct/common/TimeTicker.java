package tconstruct.common;

import java.util.EnumSet;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class TimeTicker implements ITickHandler
{
    public static long time = 0;
    public static int tickParts = 0;
    public static int seconds = 0;
    public static int minutes = 0;

    @Override
    public void tickStart (EnumSet<TickType> type, Object... tickData)
    {
        System.out.println("Tick");
        time++;
        tickParts = (int) (time % 20);
        seconds = (int) (time / 20);
        minutes = (int) (time / 1200);
    }

    @Override
    public void tickEnd (EnumSet<TickType> type, Object... tickData)
    {
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

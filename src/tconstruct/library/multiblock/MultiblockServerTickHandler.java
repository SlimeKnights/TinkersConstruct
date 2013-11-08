package tconstruct.library.multiblock;

import java.util.EnumSet;

import net.minecraft.world.World;

import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;

public class MultiblockServerTickHandler implements IScheduledTickHandler
{

    @Override
    public void tickStart (EnumSet<TickType> type, Object... tickData)
    {
        return;
    }

    @Override
    public void tickEnd (EnumSet<TickType> type, Object... tickData)
    {
        if (type.contains(TickType.WORLD))
        {
            World world = (World) tickData[0];
            MultiblockRegistry.tick(world);
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
        return "TConstruct:MultiblockServerTickHandler";
    }

    @Override
    public int nextTickSpacing ()
    {
        return 1;
    }

}

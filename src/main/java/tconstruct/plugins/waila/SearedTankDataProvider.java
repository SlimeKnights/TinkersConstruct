package tconstruct.plugins.waila;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.blocks.logic.LavaTankLogic;

import java.util.List;

public class SearedTankDataProvider implements IWailaDataProvider
{

    @Override
    public ItemStack getWailaStack (IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return null;
    }

    @Override
    public List<String> getWailaHead (ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody (ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        if (accessor.getTileEntity() instanceof LavaTankLogic)
        {
            LavaTankLogic te = (LavaTankLogic) accessor.getTileEntity();
            if (te.containsFluid())
            {
                FluidStack fs = te.tank.getFluid();
                currenttip.add(StatCollector.translateToLocal("tconstruct.waila.liquidtag") + WailaRegistrar.fluidNameHelper(fs));
                currenttip.add(StatCollector.translateToLocal("tconstruct.waila.amounttag") + fs.amount + "/" + te.tank.getCapacity());
            }
            else
            {
                currenttip.add("§o" + StatCollector.translateToLocal("tconstruct.waila.empty")); // "§o" == Italics
            }
        }
        return currenttip;
    }
    
    @Override
    public List<String> getWailaTail (ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return currenttip;
    }
}

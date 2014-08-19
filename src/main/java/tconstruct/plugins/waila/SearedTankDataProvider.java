package tconstruct.plugins.waila;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.SpecialChars;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.smeltery.logic.LavaTankLogic;

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
        if (accessor.getTileEntity() instanceof LavaTankLogic && config.getConfig("tcon.searedtank", true))
        {
            LavaTankLogic te = (LavaTankLogic) accessor.getTileEntity();
            if (te.containsFluid())
            {
                FluidStack fs = te.tank.getFluid();
                currenttip.add("Liquid: " + WailaRegistrar.fluidNameHelper(fs));
                currenttip.add("Amount: " + fs.amount + "/" + te.tank.getCapacity());
            }
            else
            {
                currenttip.add(SpecialChars.ITALIC + "Empty");
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

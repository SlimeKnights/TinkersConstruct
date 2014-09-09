package tconstruct.plugins.waila;

import java.util.List;
import mcp.mobius.waila.api.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.smeltery.logic.CastingBasinLogic;

public class BasinDataProvider implements IWailaDataProvider
{

    @Override
    public ItemStack getWailaStack (IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        if (accessor.getTileEntity() instanceof CastingBasinLogic)
        {
            return ((CastingBasinLogic) accessor.getTileEntity()).getStackInSlot(0);
        }
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
        if (accessor.getTileEntity() instanceof CastingBasinLogic && config.getConfig("tcon.basin", true))
        {
            CastingBasinLogic te = (CastingBasinLogic) accessor.getTileEntity();
            if (te.getFluidAmount() != 0)
            {
                FluidStack fs = te.getFluid();
                currenttip.add("Liquid: " + WailaRegistrar.fluidNameHelper(fs));
                currenttip.add("Amount: " + fs.amount + "/" + te.getCapacity());
            }
            else
            {
                if (te.getStackInSlot(0) != null)
                {
                    currenttip.add("Contains: " + te.getStackInSlot(0).getDisplayName());
                }
                else
                {
                    currenttip.add(SpecialChars.ITALIC + "Empty");
                }
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

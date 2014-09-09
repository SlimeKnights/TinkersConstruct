package tconstruct.plugins.waila;

import java.util.List;
import mcp.mobius.waila.api.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import tconstruct.smeltery.logic.CastingChannelLogic;

public class CastingChannelDataProvider implements IWailaDataProvider
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
        if (accessor.getTileEntity() instanceof CastingChannelLogic)
        {
            CastingChannelLogic te = (CastingChannelLogic) accessor.getTileEntity();
            FluidTankInfo internalTank = te.getTankInfo(null)[0];
            FluidTankInfo northTank = te.getTankInfo(ForgeDirection.NORTH)[0];
            FluidTankInfo southTank = te.getTankInfo(ForgeDirection.SOUTH)[0];
            FluidTankInfo westTank = te.getTankInfo(ForgeDirection.WEST)[0];
            FluidTankInfo eastTank = te.getTankInfo(ForgeDirection.EAST)[0];

            if (internalTank.fluid != null && internalTank.fluid.amount > 0)
            {
                FluidStack fs = internalTank.fluid;
                currenttip.add(StatCollector.translateToLocal("tconstruct.waila.liquidtag") + WailaRegistrar.fluidNameHelper(fs));
                currenttip.add(StatCollector.translateToLocal("tconstruct.waila.amounttag") + fs.amount + "/" + internalTank.capacity);
            }
            else
            {
                currenttip.add(SpecialChars.ITALIC + StatCollector.translateToLocal("tconstruct.waila.empty"));
            }

            currenttip.add(StatCollector.translateToLocal("tconstruct.waila.subtanks"));
            String s1, s2, s3, s4;
            s1 = s2 = s3 = s4 = SpecialChars.ITALIC + StatCollector.translateToLocal("tconstruct.waila.empty");

            if (northTank.fluid != null)
                s1 = northTank.fluid.amount + " / " + northTank.capacity;
            if (southTank.fluid != null)
                s2 = southTank.fluid.amount + " / " + southTank.capacity;
            if (westTank.fluid != null)
                s3 = westTank.fluid.amount + " / " + westTank.capacity;
            if (eastTank.fluid != null)
                s4 = eastTank.fluid.amount + " / " + eastTank.capacity;
            currenttip.add(s1 + "; " + s2 + "; " + s3 + "; " + s4);
        }

        return currenttip;
    }

    @Override
    public List<String> getWailaTail (ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return currenttip;
    }

}

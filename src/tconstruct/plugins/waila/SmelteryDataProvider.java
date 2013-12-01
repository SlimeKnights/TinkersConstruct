package tconstruct.plugins.waila;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.blocks.logic.SmelteryLogic;

import java.util.List;

public class SmelteryDataProvider implements IWailaDataProvider
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
        if (accessor.getTileEntity() instanceof SmelteryLogic)
        {
            SmelteryLogic te = (SmelteryLogic) accessor.getTileEntity();
            if (te.validStructure)
            {
                List<FluidStack> fls = te.moltenMetal;
                if (fls.size() <= 0)
                {
                    currenttip.add("§oEmpty"); // "§o" == Italics
                }
                else
                {
                    for (int i = 0; i < fls.size(); i++)
                    {
                        FluidStack st = fls.get(i);
                        currenttip.add(WailaRegistrar.fluidNameHelper(st) + " (" + st.amount + "mB)");
                    }
                }
            }
            else
            {
                currenttip.add("§oInvalid structure");
            }
        }

        return currenttip;
    }

}

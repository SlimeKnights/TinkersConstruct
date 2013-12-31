package tconstruct.plugins.waila;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.item.ItemStack;
import tconstruct.blocks.logic.CastingTableLogic;

import java.util.List;

public class TableDataProvider implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getTileEntity() instanceof CastingTableLogic)
        {
            CastingTableLogic te = (CastingTableLogic)accessor.getTileEntity();
            return te.getStackInSlot(0);
        }
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getTileEntity() instanceof CastingTableLogic && config.getConfig("tcon.table", true))
        {
            CastingTableLogic te = (CastingTableLogic)accessor.getTileEntity();
            if (te.getStackInSlot(1) != null)
            {
                currenttip.add("Contains: " + te.getStackInSlot(1).getDisplayName());
            }
            if (te.getFluid() != null)
            {
                currenttip.add("Fluid: " + WailaRegistrar.fluidNameHelper(te.getFluid()));
                currenttip.add("Amount: " + te.getFluidAmount() + "/" + te.getCapacity());
            }
        }
        return currenttip;
    }

}

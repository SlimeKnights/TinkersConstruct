package tconstruct.plugins.waila;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.blocks.logic.CastingChannelLogic;

import java.util.List;

public class CastingChannelDataProvider implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getTileEntity() instanceof CastingChannelLogic) {
            CastingChannelLogic te = (CastingChannelLogic)accessor.getTileEntity();
            if (te.liquid != null && te.liquid.amount > 0) {
                FluidStack fs = te.liquid;
                currenttip.add("Liquid: " + Waila.fluidNameHelper(fs));
                currenttip.add("Amount: " + fs.amount + "/" + te.getCapacity());
            } else {
                currenttip.add("§oEmpty"); // "§o" == Italics
            }
        }

        return currenttip;
    }

}

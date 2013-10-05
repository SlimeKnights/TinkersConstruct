package tconstruct.plugins.waila;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.blocks.logic.LavaTankLogic;

import java.util.List;

public class SearedTankDataProvider implements IWailaDataProvider {

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
        if (accessor.getTileEntity() instanceof LavaTankLogic) {
            LavaTankLogic te = (LavaTankLogic)accessor.getTileEntity();
            if (te.containsFluid()) {
                FluidStack fs = te.tank.getFluid();
                currenttip.add("Liquid: " + Waila.fluidNameHelper(fs));
                currenttip.add("Amount: " + fs.amount + "/" + te.tank.getCapacity());
            } else {
                currenttip.add("§oEmpty"); // "§o" == Italics
            }
        }
        return currenttip;
    }
}

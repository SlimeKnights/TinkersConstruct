package tconstruct.plugins.waila;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.item.ItemStack;
import tconstruct.blocks.logic.EssenceExtractorLogic;

import java.util.List;

public class EssenceExtractorDataProvider implements IWailaDataProvider {

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
        if (accessor.getTileEntity() instanceof EssenceExtractorLogic) {
            EssenceExtractorLogic te = (EssenceExtractorLogic)accessor.getTileEntity();
            currenttip.add("Total Essence: " + te.essenceAmount);
            currenttip.add("Stored Levels: " + EssenceExtractorLogic.getEssencelevels(te.essenceAmount));
        }

        return currenttip;
    }

}

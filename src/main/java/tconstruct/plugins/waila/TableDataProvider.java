package tconstruct.plugins.waila;

import java.util.List;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import tconstruct.smeltery.logic.CastingTableLogic;

public class TableDataProvider implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getTileEntity() instanceof CastingTableLogic) {
            CastingTableLogic te = (CastingTableLogic) accessor.getTileEntity();
            return te.getStackInSlot(0);
        }
        return null;
    }

    @Override
    public List<String> getWailaHead(
            ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(
            ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getTileEntity() instanceof CastingTableLogic && config.getConfig("tcon.table", true)) {
            CastingTableLogic te = (CastingTableLogic) accessor.getTileEntity();
            if (te.getStackInSlot(1) != null) {
                currenttip.add(StatCollector.translateToLocal("tconstruct.waila.contains")
                        + te.getStackInSlot(1).getDisplayName());
            }
            if (te.getFluid() != null) {
                currenttip.add(StatCollector.translateToLocal("tconstruct.waila.liquidtag")
                        + WailaRegistrar.fluidNameHelper(te.getFluid()));
                currenttip.add(StatCollector.translateToLocal("tconstruct.waila.amounttag") + te.getFluidAmount() + "/"
                        + te.getCapacity());
                final int progress = te.getProgress();
                currenttip.add(StatCollector.translateToLocal("tconstruct.waila.progress") + progress + "%");
            }
        }
        return currenttip;
    }

    @Override
    public List<String> getWailaTail(
            ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(
            EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
        return tag;
    }
}

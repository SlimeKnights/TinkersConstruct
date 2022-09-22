package tconstruct.plugins.waila;

import java.util.List;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.SpecialChars;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.smeltery.logic.CastingBasinLogic;

public class BasinDataProvider implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getTileEntity() instanceof CastingBasinLogic) {
            return ((CastingBasinLogic) accessor.getTileEntity()).getStackInSlot(0);
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
        if (accessor.getTileEntity() instanceof CastingBasinLogic && config.getConfig("tcon.basin", true)) {
            CastingBasinLogic te = (CastingBasinLogic) accessor.getTileEntity();
            if (te.getFluidAmount() != 0) {
                FluidStack fs = te.getFluid();
                currenttip.add(StatCollector.translateToLocal("tconstruct.waila.liquidtag")
                        + WailaRegistrar.fluidNameHelper(fs));
                currenttip.add(StatCollector.translateToLocal("tconstruct.waila.amounttag") + fs.amount + "/"
                        + te.getCapacity());
                final int progress = te.getProgress();
                currenttip.add(StatCollector.translateToLocal("tconstruct.waila.progress") + progress + "%");
            } else {
                if (te.getStackInSlot(0) != null) {
                    currenttip.add(StatCollector.translateToLocal("tconstruct.waila.contains")
                            + te.getStackInSlot(0).getDisplayName());
                } else {
                    currenttip.add(SpecialChars.ITALIC + StatCollector.translateToLocal("tconstruct.waila.empty"));
                }
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

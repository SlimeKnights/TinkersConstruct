package slimeknights.tconstruct.plugin.waila;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.tileentity.IProgress;

public class ProgressDataProvider implements IWailaDataProvider {

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
    if(config.getConfig(WailaRegistrar.CONFIG_PROGRESS) && accessor.getTileEntity() instanceof IProgress) {
      IProgress te = (IProgress) accessor.getTileEntity();
      float progress = te.getProgress();
      if(progress > 0f) {
        currenttip.add(Util.translateFormatted("gui.waila.casting.progress", Util.dfPercent.format(progress)));
      }
    }
    return currenttip;
  }

  @Override
  public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
    return currenttip;
  }

  @Override
  public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
    return tag;
  }
}

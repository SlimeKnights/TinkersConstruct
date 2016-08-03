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
import slimeknights.tconstruct.smeltery.tileentity.TileCasting;

public class CastingDataProvider implements IWailaDataProvider {

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
    if(config.getConfig(WailaRegistrar.CONFIG_CASTING) && accessor.getTileEntity() instanceof TileCasting) {
      TileCasting te = (TileCasting) accessor.getTileEntity();
      ItemStack output = te.getCurrentResult();
      if(output != null) {
        currenttip.add(Util.translateFormatted("gui.waila.casting.recipe", output.getDisplayName()));
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

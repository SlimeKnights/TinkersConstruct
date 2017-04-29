package slimeknights.tconstruct.plugin.waila;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import slimeknights.tconstruct.library.Util;

public class TankDataProvider implements IWailaDataProvider {

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
    if(config.getConfig(WailaRegistrar.CONFIG_TANK)) {

      TileEntity te = accessor.getTileEntity();

      if(te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
        IFluidHandler fluidHandler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
        assert fluidHandler != null;
        IFluidTankProperties[] fluidHandlerTankProperties = fluidHandler.getTankProperties();
        for(IFluidTankProperties fluidTankProperties : fluidHandlerTankProperties) {
          FluidStack fluidStack = fluidTankProperties.getContents();
          if(fluidStack != null) {
            currenttip.add(Util.translateFormatted("gui.waila.tank.fluid", fluidStack.getLocalizedName()));
            currenttip.add(Util.translateFormatted("gui.waila.tank.amount", fluidStack.amount, fluidTankProperties.getCapacity()));
          }
          else {
            currenttip.add(Util.translate("gui.waila.tank.empty"));
          }
        }
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

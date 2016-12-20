package slimeknights.tconstruct.plugin.theoneprobe;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.smeltery.tileentity.TileCasting;

public class CastingInfoProvider implements IProbeInfoProvider {

  @Override
  public String getID() {
    return Util.getResource("casting").toString();
  }

  @Override
  public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
    TileEntity te = world.getTileEntity(data.getPos());
    if(te instanceof TileCasting) {
      TileCasting casting = (TileCasting) te;
      ItemStack output = casting.getCurrentResult();
      if(output != null) {
        probeInfo.horizontal().text(Util.translateFormatted("gui.waila.casting.recipe", output.getDisplayName()));
      }
    }
  }
}

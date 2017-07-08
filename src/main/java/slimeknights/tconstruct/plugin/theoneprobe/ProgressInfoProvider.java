package slimeknights.tconstruct.plugin.theoneprobe;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.tileentity.IProgress;

public class ProgressInfoProvider implements IProbeInfoProvider {

  @Override
  public String getID() {
    return Util.getResource("progress").toString();
  }

  @Override
  public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
    TileEntity te = world.getTileEntity(data.getPos());
    if(te instanceof IProgress) {
      IProgress progressTe = (IProgress) te;
      float progress = progressTe.getProgress();
      if(progress > 0f) {
        probeInfo.horizontal().progress((int)(progress*100f), 100, probeInfo.defaultProgressStyle().suffix("%"));
      }
    }
  }
}

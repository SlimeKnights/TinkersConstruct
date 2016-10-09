package slimeknights.tconstruct.plugin.theoneprobe;

import com.google.common.base.Function;

import javax.annotation.Nullable;

import mcjty.theoneprobe.api.ITheOneProbe;

public class GetTheOneProbe implements Function<ITheOneProbe, Void> {

  @Nullable
  @Override
  public Void apply(ITheOneProbe probe) {
    probe.registerProvider(new CastingInfoProvider());
    probe.registerProvider(new ProgressInfoProvider());
    return null;
  }
}

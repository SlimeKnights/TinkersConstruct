package slimeknights.tconstruct.test;

import net.minecraft.SharedConstants;
import net.minecraft.WorldVersion;
import net.minecraft.world.level.storage.DataVersion;

import java.util.Date;

public class TestWorldVersion implements WorldVersion {
  public static final TestWorldVersion INSTANCE = new TestWorldVersion();
  private static final DataVersion DATA = new DataVersion(0, "tconstruct_test");

  @Override
  public DataVersion getDataVersion() {
    return DATA;
  }

  @Override
  public String getId() {
    return "tconstruct_test";
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Automated Test";
  }

  @Override
  public String getReleaseTarget() {
    return "1.18.1";
  }

  @Override
  public int getProtocolVersion() {
    return SharedConstants.getProtocolVersion();
  }

  @Override
  public Date getBuildTime() {
    return new Date();
  }

  @Override
  public boolean isStable() {
    return true;
  }
}

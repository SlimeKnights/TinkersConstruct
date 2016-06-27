package slimeknights.tconstruct.plugin.waila;

import mcp.mobius.waila.api.IWailaRegistrar;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.tileentity.IProgress;
import slimeknights.tconstruct.smeltery.tileentity.TileCasting;
import slimeknights.tconstruct.smeltery.tileentity.TileTank;

public class WailaRegistrar {

  static final String CONFIG_TANK = Util.prefix("tank");
  static final String CONFIG_CASTING = Util.prefix("casting");
  static final String CONFIG_PROGRESS = Util.prefix("progress");

  public static void wailaCallback(IWailaRegistrar registrar) {
    // config entries
    registrar.addConfig(TConstruct.modName, CONFIG_TANK, true);
    registrar.addConfig(TConstruct.modName, CONFIG_CASTING, true);
    registrar.addConfig(TConstruct.modName, CONFIG_PROGRESS, true);

    // Casting info
    CastingDataProvider castingDataProvider = new CastingDataProvider();
    registrar.registerBodyProvider(castingDataProvider, TileCasting.class);

    // Fluid Display
    TankDataProvider tankDataProvider = new TankDataProvider();
    registrar.registerBodyProvider(tankDataProvider, TileTank.class);
    registrar.registerBodyProvider(tankDataProvider, TileCasting.class);

    // Progress
    ProgressDataProvider progressDataProvider = new ProgressDataProvider();
    registrar.registerBodyProvider(progressDataProvider, IProgress.class);
  }
}

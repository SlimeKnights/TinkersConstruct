package slimeknights.tconstruct.weapons.ranged;

import com.google.common.eventbus.Subscribe;

import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import org.apache.logging.log4j.Logger;

import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.common.CommonProxy;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.tools.AbstractToolPulse;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.weapons.ranged.item.Arrow;
import slimeknights.tconstruct.weapons.ranged.item.ShortBow;
import slimeknights.tconstruct.weapons.ranged.item.Shuriken;

@Pulse(
    id = TinkerRangedWeapons.PulseId,
    description = "All the melee weapons in one handy package",
    pulsesRequired = TinkerTools.PulseId,
    forced = true)
public class TinkerRangedWeapons extends AbstractToolPulse {

  public static final String PulseId = "TinkerRangedWeapons";
  static final Logger log = Util.getLogger(PulseId);

  @SidedProxy(clientSide = "slimeknights.tconstruct.weapons.ranged.RangedClientProxy", serverSide = "slimeknights.tconstruct.common.CommonProxy")
  public static CommonProxy proxy;

  public static ShortBow shortBow;

  public static Arrow arrow;

  public static ToolCore shuriken;

  // PRE-INITIALIZATION
  @Override
  @Subscribe
  public void preInit(FMLPreInitializationEvent event) {
    super.preInit(event);
    proxy.preInit();
  }

  @Override
  protected void registerTools() {
    shortBow = registerTool(new ShortBow(), "shortbow");

    arrow = registerTool(new Arrow(), "arrow");

    shuriken = registerTool(new Shuriken(), "shuriken");
  }

  // INITIALIZATION
  @Override
  @Subscribe
  public void init(FMLInitializationEvent event) {
    super.init(event);
    proxy.init();
  }

  @Override
  protected void registerToolBuilding() {
    TinkerRegistry.registerToolCrafting(shortBow);

    TinkerRegistry.registerToolCrafting(arrow);

    TinkerRegistry.registerToolForgeCrafting(shuriken);
  }

  // POST-INITIALIZATION
  @Subscribe
  @Override
  public void postInit(FMLPostInitializationEvent event) {
    super.postInit(event);
  }

}

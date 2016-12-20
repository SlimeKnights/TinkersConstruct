package slimeknights.tconstruct.tools.harvest;

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
import slimeknights.tconstruct.tools.tools.Excavator;
import slimeknights.tconstruct.tools.tools.Hammer;
import slimeknights.tconstruct.tools.tools.Hatchet;
import slimeknights.tconstruct.tools.tools.LumberAxe;
import slimeknights.tconstruct.tools.tools.Mattock;
import slimeknights.tconstruct.tools.tools.Pickaxe;
import slimeknights.tconstruct.tools.tools.Scythe;
import slimeknights.tconstruct.tools.tools.Shovel;

@Pulse(
    id = TinkerHarvestTools.PulseId,
    description = "All the melee weapons in one handy package",
    pulsesRequired = TinkerTools.PulseId,
    forced = true)
public class TinkerHarvestTools extends AbstractToolPulse {

  public static final String PulseId = "TinkerHarvestTools";
  static final Logger log = Util.getLogger(PulseId);

  @SidedProxy(clientSide = "slimeknights.tconstruct.tools.harvest.HarvestClientProxy", serverSide = "slimeknights.tconstruct.common.CommonProxy")
  public static CommonProxy proxy;

  public static ToolCore pickaxe;
  public static ToolCore shovel;
  public static ToolCore hatchet;
  public static ToolCore mattock;

  public static ToolCore hammer;
  public static ToolCore excavator;
  public static ToolCore lumberAxe;
  public static ToolCore scythe;

  // PRE-INITIALIZATION
  @Override
  @Subscribe
  public void preInit(FMLPreInitializationEvent event) {
    super.preInit(event);
    proxy.preInit();
  }

  @Override
  protected void registerTools() {
    pickaxe = registerTool(new Pickaxe(), "pickaxe");
    shovel = registerTool(new Shovel(), "shovel");
    hatchet = registerTool(new Hatchet(), "hatchet");
    mattock = registerTool(new Mattock(), "mattock");

    hammer = registerTool(new Hammer(), "hammer");
    excavator = registerTool(new Excavator(), "excavator");
    lumberAxe = registerTool(new LumberAxe(), "lumberaxe");
    scythe = registerTool(new Scythe(), "scythe");
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
    TinkerRegistry.registerToolCrafting(pickaxe);
    TinkerRegistry.registerToolCrafting(shovel);
    TinkerRegistry.registerToolCrafting(hatchet);
    TinkerRegistry.registerToolCrafting(mattock);

    TinkerRegistry.registerToolForgeCrafting(hammer);
    TinkerRegistry.registerToolForgeCrafting(excavator);
    TinkerRegistry.registerToolForgeCrafting(lumberAxe);
    TinkerRegistry.registerToolForgeCrafting(scythe);
  }

  // POST-INITIALIZATION
  @Subscribe
  @Override
  public void postInit(FMLPostInitializationEvent event) {
    super.postInit(event);
    proxy.postInit();
  }
}

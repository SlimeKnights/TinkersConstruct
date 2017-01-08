package slimeknights.tconstruct.tools.ranged;

import com.google.common.eventbus.Subscribe;

import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import org.apache.logging.log4j.Logger;

import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.CommonProxy;
import slimeknights.tconstruct.common.EntityIDs;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.tools.AbstractToolPulse;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.common.entity.EntityArrow;
import slimeknights.tconstruct.tools.common.entity.EntityBolt;
import slimeknights.tconstruct.tools.common.entity.EntityShuriken;
import slimeknights.tconstruct.tools.ranged.item.Arrow;
import slimeknights.tconstruct.tools.ranged.item.Bolt;
import slimeknights.tconstruct.tools.ranged.item.CrossBow;
import slimeknights.tconstruct.tools.ranged.item.LongBow;
import slimeknights.tconstruct.tools.ranged.item.ShortBow;
import slimeknights.tconstruct.tools.ranged.item.Shuriken;

@Pulse(
    id = TinkerRangedWeapons.PulseId,
    description = "All the ranged weapons in one handy package",
    pulsesRequired = TinkerTools.PulseId,
    forced = true)
public class TinkerRangedWeapons extends AbstractToolPulse {

  public static final String PulseId = "TinkerRangedWeapons";
  static final Logger log = Util.getLogger(PulseId);

  @SidedProxy(clientSide = "slimeknights.tconstruct.tools.ranged.RangedClientProxy", serverSide = "slimeknights.tconstruct.common.CommonProxy")
  public static CommonProxy proxy;

  public static ShortBow shortBow;
  public static LongBow longBow;

  public static CrossBow crossBow;

  public static Arrow arrow;
  public static Bolt bolt;

  public static ToolCore shuriken;

  // PRE-INITIALIZATION
  @Override
  @Subscribe
  public void preInit(FMLPreInitializationEvent event) {
    super.preInit(event);

    // entities
    EntityRegistry.registerModEntity(EntityArrow.class, "arrow", EntityIDs.ARROW, TConstruct.instance, 64, 1, false);
    EntityRegistry.registerModEntity(EntityBolt.class, "bolt", EntityIDs.BOLT, TConstruct.instance, 64, 1, false);
    EntityRegistry.registerModEntity(EntityShuriken.class, "shuriken", EntityIDs.SHURIKEN, TConstruct.instance, 64, 1, false);

    proxy.preInit();
  }

  @Override
  protected void registerTools() {
    shortBow = registerTool(new ShortBow(), "shortbow");
    longBow = registerTool(new LongBow(), "longbow");

    crossBow = registerTool(new CrossBow(), "crossbow");

    arrow = registerTool(new Arrow(), "arrow");
    bolt = registerTool(new Bolt(), "bolt");

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
    TinkerRegistry.registerToolForgeCrafting(longBow);

    TinkerRegistry.registerToolCrafting(arrow);

    TinkerRegistry.registerToolForgeCrafting(crossBow);

    TinkerRegistry.registerToolForgeCrafting(bolt);

    TinkerRegistry.registerToolForgeCrafting(shuriken);
  }

  // POST-INITIALIZATION
  @Subscribe
  @Override
  public void postInit(FMLPostInitializationEvent event) {
    super.postInit(event);

    TinkerRegistry.registerTableCasting(BoltCoreCastingRecipe.INSTANCE);
  }

}

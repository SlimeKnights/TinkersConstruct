package slimeknights.tconstruct.tools.ranged;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.Subscribe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

  private static List<Item> DISCOVERED_ARROWS = new ArrayList<>();

  public static List<Item> getDiscoveredArrows() {
    return DISCOVERED_ARROWS;
  }

  @Override
  @SubscribeEvent
  public void registerItems(Register<Item> event) {
    super.registerItems(event);
  }

  @SubscribeEvent
  public void registerEntities(Register<EntityEntry> event) {
    // entities
    EntityRegistry.registerModEntity(Util.getResource("arrow"), EntityArrow.class, "arrow", EntityIDs.ARROW, TConstruct.instance, 64, 1, false);
    EntityRegistry.registerModEntity(Util.getResource("bolt"), EntityBolt.class, "bolt", EntityIDs.BOLT, TConstruct.instance, 64, 1, false);
    EntityRegistry.registerModEntity(Util.getResource("shuriken"), EntityShuriken.class, "shuriken", EntityIDs.SHURIKEN, TConstruct.instance, 64, 1, false);
  }

  @SubscribeEvent
  public void registerModels(ModelRegistryEvent event) {
    proxy.registerModels();
  }

  // PRE-INITIALIZATION
  @Subscribe
  public void preInit(FMLPreInitializationEvent event) {
    proxy.preInit();
  }

  @Override
  protected void registerTools(IForgeRegistry<Item> registry) {
    shortBow = registerTool(registry, new ShortBow(), "shortbow");
    longBow = registerTool(registry, new LongBow(), "longbow");

    crossBow = registerTool(registry, new CrossBow(), "crossbow");

    arrow = registerTool(registry, new Arrow(), "arrow");
    bolt = registerTool(registry, new Bolt(), "bolt");

    shuriken = registerTool(registry, new Shuriken(), "shuriken");
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

    discoverArrows();

    TinkerRegistry.registerTableCasting(BoltCoreCastingRecipe.INSTANCE);
  }

  private void discoverArrows() {
    Iterator<Item> iter = Item.REGISTRY.iterator();
    ImmutableList.Builder<Item> builder = ImmutableList.builder();
    if(arrow != null) {
      builder.add(arrow);
    }
    while(iter.hasNext()) {
      Item item = iter.next();
      // vanilla style arrow
      if(item instanceof ItemArrow) {
        builder.add(item);
      }
    }
    DISCOVERED_ARROWS = builder.build();
  }
}

package slimeknights.tconstruct.tools.melee;

import com.google.common.eventbus.Subscribe;

import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import org.apache.logging.log4j.Logger;

import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.common.CommonProxy;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.tools.AbstractToolPulse;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.melee.item.BattleSign;
import slimeknights.tconstruct.tools.melee.item.BroadSword;
import slimeknights.tconstruct.tools.melee.item.Cleaver;
import slimeknights.tconstruct.tools.melee.item.FryPan;
import slimeknights.tconstruct.tools.melee.item.LongSword;
import slimeknights.tconstruct.tools.melee.item.Rapier;

@Pulse(
    id = TinkerMeleeWeapons.PulseId,
    description = "All the melee weapons in one handy package",
    pulsesRequired = TinkerTools.PulseId,
    forced = true)
public class TinkerMeleeWeapons extends AbstractToolPulse {

  public static final String PulseId = "TinkerMeleeWeapons";
  static final Logger log = Util.getLogger(PulseId);

  @SidedProxy(clientSide = "slimeknights.tconstruct.tools.melee.MeleeClientProxy", serverSide = "slimeknights.tconstruct.common.CommonProxy")
  public static CommonProxy proxy;

  public static ToolCore broadSword;
  public static ToolCore longSword;
  public static ToolCore rapier;
  public static ToolCore cutlass;
  public static ToolCore dagger;
  public static ToolCore fryPan;
  public static ToolCore battleSign;

  public static ToolCore cleaver;
  public static ToolCore battleAxe;

  @Override
  @SubscribeEvent
  public void registerItems(Register<Item> event) {
    super.registerItems(event);
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
    broadSword = registerTool(registry, new BroadSword(), "broadsword");
    longSword = registerTool(registry, new LongSword(), "longsword");
    rapier = registerTool(registry, new Rapier(), "rapier");
    // cutlass
    // dagger
    fryPan = registerTool(registry, new FryPan(), "frypan");
    battleSign = registerTool(registry, new BattleSign(), "battlesign");

    cleaver = registerTool(registry, new Cleaver(), "cleaver");
    //battleAxe = registerTool(new BattleAxe(), "battleaxe");
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
    TinkerRegistry.registerToolCrafting(broadSword);
    TinkerRegistry.registerToolCrafting(longSword);
    TinkerRegistry.registerToolCrafting(rapier);
    TinkerRegistry.registerToolCrafting(fryPan);
    TinkerRegistry.registerToolCrafting(battleSign);

    TinkerRegistry.registerToolForgeCrafting(cleaver);
    //TinkerRegistry.registerToolForgeCrafting(battleAxe);
  }

  // POST-INITIALIZATION
  @Subscribe
  @Override
  public void postInit(FMLPostInitializationEvent event) {
    super.postInit(event);
  }

  @Override
  protected void registerEventHandlers() {
    MinecraftForge.EVENT_BUS.register(battleSign); // battlesign events
  }
}

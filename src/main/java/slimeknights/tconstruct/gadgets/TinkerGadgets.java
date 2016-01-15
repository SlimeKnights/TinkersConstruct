package slimeknights.tconstruct.gadgets;

import com.google.common.eventbus.Subscribe;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

import org.apache.logging.log4j.Logger;

import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.common.CommonProxy;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.gadgets.item.ItemSlimeSling;
import slimeknights.tconstruct.gadgets.item.ItemSlimeBoots;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.world.TinkerWorld;

@Pulse(id = TinkerGadgets.PulseId, description = "All the fun toys")
public class TinkerGadgets extends TinkerPulse {

  public static final String PulseId = "TinkerGadgets";
  static final Logger log = Util.getLogger(PulseId);

  @SidedProxy(clientSide = "slimeknights.tconstruct.gadgets.GadgetClientProxy", serverSide = "slimeknights.tconstruct.common.CommonProxy")
  public static CommonProxy proxy;

  public static ItemSlimeSling slimeSling;
  public static ItemSlimeBoots slimeBoots;

  // PRE-INITIALIZATION
  @Subscribe
  public void preInit(FMLPreInitializationEvent event) {
    slimeSling = registerItem(new ItemSlimeSling(), "slimesling");
    slimeBoots = registerItem(new ItemSlimeBoots(), "slime_boots");

    MinecraftForge.EVENT_BUS.register(slimeBoots);

    proxy.preInit();
  }

  // INITIALIZATION
  @Subscribe
  public void init(FMLInitializationEvent event) {
    registerRecipes();

    proxy.init();
  }

  private void registerRecipes() {
    String ore = "blockSlime";
    if(isWorldLoaded()) {
      ore = "blockSlimeCongealed";
    }

    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(slimeBoots), "   ", "s s", "b b", 's', "slimeball", 'b', ore));
    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(slimeSling), "fbf", "s s", " s ", 'f', Items.string, 's', "slimeball", 'b', ore));
  }


  // POST-INITIALIZATION
  @Subscribe
  public void postInit(FMLPostInitializationEvent event) {
    proxy.postInit();
  }
}

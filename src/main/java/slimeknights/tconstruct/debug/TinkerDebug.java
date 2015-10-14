package slimeknights.tconstruct.debug;

import com.google.common.eventbus.Subscribe;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import org.apache.logging.log4j.Logger;

import mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.IModifier;

// todo: deactivate by default
@Pulse(id=TinkerDebug.PulseId, description = "Debug utilities")
public class TinkerDebug {
  public static final String PulseId = "TinkerDebug";
  static final Logger log = Util.getLogger(PulseId);

  @Subscribe
  public void postInit(FMLPostInitializationEvent event) {
    if(event.getSide().isClient()) {
      ClientCommandHandler.instance.registerCommand(new ReloadResources());
    }
  }

  @Subscribe
  public void serverStart(FMLServerStartingEvent event) {
    event.registerServerCommand(new LocalizationCheckCommand());
    event.registerServerCommand(new DumpMaterialTest());
    event.registerServerCommand(new FindBestTool());
    event.registerServerCommand(new DamageTool());

    sanityCheck();
  }

  public static void sanityCheck() {
    // check all modifiers if they can be applied
    for(IModifier modifier : TinkerRegistry.getAllModifiers()) {
      try {
        modifier.matches(new ItemStack[] {new ItemStack(Items.stick)});
        modifier.matches(new ItemStack[1]);
      } catch(Exception e) {
        log.error("Caught exception in modifier " + modifier.getIdentifier());
        log.error(e);
      }
    }
  }
}

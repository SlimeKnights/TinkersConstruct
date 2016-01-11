package slimeknights.tconstruct.debug;

import com.google.common.eventbus.Subscribe;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import org.apache.logging.log4j.Logger;

import java.util.Set;

import slimeknights.mantle.pulsar.pulse.Pulse;
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
    event.registerServerCommand(new DamageTool());

    ClientCommandHandler.instance.registerCommand(new LocalizationCheckCommand());
    ClientCommandHandler.instance.registerCommand(new DumpMaterialTest());
    ClientCommandHandler.instance.registerCommand(new FindBestTool());
    ClientCommandHandler.instance.registerCommand(new GetToolGrowth());

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

    // check all blocks if all metadatas are supported
    for(ResourceLocation identifier : (Set<ResourceLocation>) Block.blockRegistry.getKeys()) {
      // only our own stuff
      if(!identifier.getResourceDomain().equals(Util.RESOURCE)) {
        continue;
      }

      Block block = (Block)Block.blockRegistry.getObject(identifier);
      for(int i = 0; i < 16; i++) {
        block.getMetaFromState(block.getStateFromMeta(i));
      }
    }

    // same for items
    for(ResourceLocation identifier : (Set<ResourceLocation>) Item.itemRegistry.getKeys()) {
      // only our own stuff
      if(!identifier.getResourceDomain().equals(Util.RESOURCE)) {
        continue;
      }

      Item item = (Item) Item.itemRegistry.getObject(identifier);
      for(int i = 0; i < 0x7FFF; i++) {
        item.getMetadata(i);
      }
    }
  }
}

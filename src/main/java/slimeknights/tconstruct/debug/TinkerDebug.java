package slimeknights.tconstruct.debug;

import com.google.common.eventbus.Subscribe;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

import java.util.Map;

import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.utils.ListUtil;

@Pulse(id = TinkerDebug.PulseId, description = "Debug utilities", defaultEnable = false)
public class TinkerDebug {

  public static final String PulseId = "TinkerDebug";
  static final Logger log = Util.getLogger(PulseId);

  @Subscribe
  public void preInit(FMLPreInitializationEvent event) {
    if(Config.dumpTextureMap) {
      MinecraftForge.EVENT_BUS.register(new TextureDump());
    }
  }

  @SubscribeEvent
  public void testSmelteryIMC(Register<Item> event) {
    if(Config.testIMC) {
      TestIMC.integrateSmeltery();
    }
  }

  @Subscribe
  public void init(FMLInitializationEvent event) {
    if(Config.testIMC) {
      TestIMC.testAll();
    }
  }

  @Subscribe
  public void postInit(FMLPostInitializationEvent event) {
    if(event.getSide().isClient()) {
      ClientCommandHandler.instance.registerCommand(new ReloadResources());
    }
  }

  @Subscribe
  public void serverStart(FMLServerStartingEvent event) {
    event.registerServerCommand(new DamageTool());
    event.registerServerCommand(new TestTool());
    event.registerServerCommand(new GenValidModifiers());
    event.registerServerCommand(new BreakTool());

    if(event.getSide().isClient()) {
      ClientCommandHandler.instance.registerCommand(new LocalizationCheckCommand());
      ClientCommandHandler.instance.registerCommand(new DumpMaterialTest());
      ClientCommandHandler.instance.registerCommand(new FindBestTool());
      ClientCommandHandler.instance.registerCommand(new GetToolGrowth());
      ClientCommandHandler.instance.registerCommand(new CompareVanilla());
      ClientCommandHandler.instance.registerCommand(new ListValidModifiers());
    }

    sanityCheck();
  }

  public static void sanityCheck() {
    // check all modifiers if they can be applied
    for(IModifier modifier : TinkerRegistry.getAllModifiers()) {
      try {
        modifier.matches(ListUtil.getListFrom(new ItemStack(Items.STICK)));
        modifier.matches(NonNullList.withSize(1, ItemStack.EMPTY));
      } catch(Exception e) {
        log.error("Caught exception in modifier " + modifier.getIdentifier(), e);
      }
    }

    // check all blocks if all metadatas are supported
    for(ResourceLocation identifier : Block.REGISTRY.getKeys()) {
      // only our own stuff
      if(!identifier.getResourceDomain().equals(Util.RESOURCE)) {
        continue;
      }

      Block block = Block.REGISTRY.getObject(identifier);
      for(int i = 0; i < 16; i++) {
        try {
          IBlockState state = block.getStateFromMeta(i);
          state.getBlock().getMetaFromState(state);
        } catch(Exception e) {
          log.error("Caught exception when checking block " + identifier + ":" + i, e);
        }
      }
    }

    // same for items
    for(ResourceLocation identifier : Item.REGISTRY.getKeys()) {
      // only our own stuff
      if(!identifier.getResourceDomain().equals(Util.RESOURCE)) {
        continue;
      }

      Item item = Item.REGISTRY.getObject(identifier);
      for(int i = 0; i < 0x7FFF; i++) {
        try {
          item.getMetadata(i);
        } catch(Exception e) {
          log.error("Caught exception when checking item " + identifier + ":" + i, e);
        }
      }
    }

    // check for broken unsavable fluids
    for(Map.Entry<String, Fluid> entry : FluidRegistry.getRegisteredFluids().entrySet()) {
      if(entry.getKey() == null || entry.getKey().isEmpty()) {
        log.error("Fluid " + entry.getValue().getUnlocalizedName() + " has an empty name registered!");
      }
      String name = FluidRegistry.getFluidName(entry.getValue());
      if(name == null || name.isEmpty()) {
        log.error("Fluid " + entry.getValue().getUnlocalizedName() + " is registered with an empty name!");
      }
    }

    log.info("Sanity Check Complete");
  }
}

package slimeknights.tconstruct;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.logging.log4j.Logger;

import java.util.List;

import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.library.MaterialIntegration;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.shared.TinkerFluids;
import slimeknights.tconstruct.tools.TinkerMaterials;

// Takes care of adding all the generic-ish materials
@Pulse(id = TinkerIntegration.PulseId, forced = true)
public class TinkerIntegration extends TinkerPulse {

  public static final String PulseId = "TinkerIntegration";
  static final Logger log = Util.getLogger(PulseId);
  public static List<MaterialIntegration> integrationList = Lists.newLinkedList();

  @Subscribe
  public void preInit(FMLPreInitializationEvent event) {
    integrate(TinkerMaterials.wood);
    integrate(TinkerMaterials.stone);
    integrate(TinkerMaterials.flint);
    integrate(TinkerMaterials.cactus);
    integrate(TinkerMaterials.bone);
    integrate(TinkerMaterials.obsidian, TinkerFluids.obsidian);
    integrate(TinkerMaterials.prismarine);
    integrate(TinkerMaterials.endstone);
    integrate(TinkerMaterials.paper);
    integrate(TinkerMaterials.sponge);
    integrate(TinkerMaterials.firewood);

    integrate(TinkerMaterials.knightslime, TinkerFluids.knightslime, "Knightslime").toolforge();
    integrate(TinkerMaterials.slime, "slimecrystalGreen");
    integrate(TinkerMaterials.blueslime, "slimecrystalBlue");
    integrate(TinkerMaterials.magmaslime, "slimecrystalMagma");

    integrate(TinkerMaterials.iron, TinkerFluids.iron, "Iron").toolforge();
    integrate(TinkerMaterials.pigiron, TinkerFluids.pigIron, "Pigiron").toolforge();
    //integrate(TinkerMaterials.copper, TinkerFluids.copper, "Copper").toolforge();
    //integrate(TinkerMaterials.bronze, TinkerFluids.bronze, "Bronze").toolforge();


    integrate(TinkerMaterials.netherrack);
    integrate(TinkerMaterials.cobalt, TinkerFluids.cobalt, "Cobalt").toolforge();
    integrate(TinkerMaterials.ardite, TinkerFluids.ardite, "Ardite").toolforge();
    integrate(TinkerMaterials.manyullyn, TinkerFluids.manyullyn, "Manyullyn").toolforge();

    // mod integrations
    integrate(TinkerMaterials.copper, TinkerFluids.copper, "Copper").toolforge();
    integrate(TinkerMaterials.bronze, TinkerFluids.bronze, "Bronze").toolforge();
    integrate(TinkerMaterials.lead, TinkerFluids.lead, "Lead").toolforge();
    integrate(TinkerMaterials.silver, TinkerFluids.silver, "Silver").toolforge();
    integrate(TinkerMaterials.electrum, TinkerFluids.electrum, "Electrum").toolforge();
    integrate(TinkerMaterials.steel, TinkerFluids.steel, "Steel").toolforge();

    // non-toolmaterial integration
    integrate(null, TinkerFluids.gold, "Gold").toolforge();
    integrate(null, TinkerFluids.brass, "Brass").toolforge();
    integrate(null, TinkerFluids.tin, "Tin").toolforge();
    integrate(null, TinkerFluids.nickel, "Nickel").toolforge();
    integrate(null, TinkerFluids.zinc, "Zinc").toolforge();
    integrate(null, TinkerFluids.aluminium, "Aluminium").toolforge();

    for(MaterialIntegration integration : integrationList) {
      integration.integrate();
    }

    MinecraftForge.EVENT_BUS.register(this);
  }

  @Subscribe
  public void init(FMLInitializationEvent event) {
    handleIMCs();

    // do we got integration
    for(MaterialIntegration integration : integrationList) {
      // integrate again, some oredicts might not have been present in the previous attempt
      integration.integrateRecipes();
    }
  }

  @SubscribeEvent
  public void onOredictRegister(OreDictionary.OreRegisterEvent event) {
    // the registered ore might be something we integrate and haven't yet
    for(MaterialIntegration integration : integrationList) {
      // calling this multiple time is ok because it does nothing once it was successful
      integration.integrate();
    }
  }

  private void handleIMCs() {
    for(FMLInterModComms.IMCMessage message : FMLInterModComms.fetchRuntimeMessages(TConstruct.instance)) {
      try {
        if(message.key.equals("integrateSmeltery")) {
          NBTTagCompound tag = message.getNBTValue();
          String fluidName = tag.getString("fluid");
          String ore = tag.getString("ore");
          boolean toolforge = tag.getBoolean("toolforge");

          Fluid fluid = FluidRegistry.getFluid(fluidName);

          if(fluid != null && ore != null && !ore.isEmpty()) {
            MaterialIntegration materialIntegration = new MaterialIntegration(null, fluid, ore);
            if(toolforge) {
              materialIntegration.toolforge();
            }
            integrationList.add(materialIntegration);
            materialIntegration.integrate();
          }
        }
      } catch(ClassCastException e) {
        log.error("Got invalid " + message.key + " IMC from " + message.getSender());
      }
    }
  }

  public static MaterialIntegration integrate(Material material) {
    return add(new MaterialIntegration(material));
  }

  public static MaterialIntegration integrate(Material material, Fluid fluid) {
    return add(new MaterialIntegration(material, fluid));
  }

  public static MaterialIntegration integrate(Material material, String oreRequirement) {
    return add(new MaterialIntegration(oreRequirement, material, null, null));
  }

  public static MaterialIntegration integrate(Material material, Fluid fluid, String oreSuffix) {
    return add(new MaterialIntegration(material, fluid, oreSuffix));
  }

  public static MaterialIntegration integrate(Fluid fluid, String oreSuffix) {
    return add(new MaterialIntegration(null, fluid, oreSuffix));
  }

  private static MaterialIntegration add(MaterialIntegration integration) {
    integrationList.add(integration);
    return integration;
  }
}

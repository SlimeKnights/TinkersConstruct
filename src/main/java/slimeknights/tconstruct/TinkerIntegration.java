package slimeknights.tconstruct;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.logging.log4j.Logger;

import java.util.List;

import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.library.MaterialIntegration;
import slimeknights.tconstruct.library.TinkerRegistry;
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
  public static List<NBTTagList> alloys = Lists.newLinkedList();

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

    // alubrass needs  both copper and aluminum
    add(new MaterialIntegration(null, TinkerFluids.alubrass, "Alubrass", "ingotCopper", "ingotAluminum")).toolforge();


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
    integrate(TinkerFluids.gold, "Gold").toolforge();
    integrate(TinkerFluids.brass, "Brass").toolforge();
    integrate(TinkerFluids.tin, "Tin").toolforge();
    integrate(TinkerFluids.nickel, "Nickel").toolforge();
    integrate(TinkerFluids.zinc, "Zinc").toolforge();
    integrate(TinkerFluids.aluminum, "Aluminum").toolforge();

    for(MaterialIntegration integration : integrationList) {
      integration.integrate();
    }

    MinecraftForge.EVENT_BUS.register(this);
  }

  public static boolean isIntegrated(Fluid fluid) {
    String name = FluidRegistry.getFluidName(fluid);
    if(name != null) {
      for(MaterialIntegration integration : integrationList) {
        if(integration.isIntegrated() && integration.fluid != null && name.equals(integration.fluid.getName())) {
          return true;
        }
      }
    }

    return false;
  }

  @Subscribe
  public void init(FMLInitializationEvent event) {
    handleIMCs();

    // do we got integration
    for(MaterialIntegration integration : integrationList) {
      // integrate again, some oredicts might not have been present in the previous attempt
      integration.integrateRecipes();
    }

    handleAlloyIMCs();
  }

  @Subscribe
  public void postInit(FMLPostInitializationEvent event) {
    for(MaterialIntegration integration : integrationList) {
      integration.registerRepresentativeItem();
    }
  }


  @SubscribeEvent
  public void onOredictRegister(OreDictionary.OreRegisterEvent event) {
    // we're only interested in preInit
    if(Loader.instance().hasReachedState(LoaderState.INITIALIZATION)) {
      return;
    }
    // the registered ore might be something we integrate and haven't yet
    for(MaterialIntegration integration : ImmutableList.copyOf(integrationList)) {
      // calling this multiple time is ok because it does nothing once it was successful
      integration.integrate();
    }
  }

  private void handleIMCs() {
    for(FMLInterModComms.IMCMessage message : FMLInterModComms.fetchRuntimeMessages(TConstruct.instance)) {
      try {
        // smeltery melting
        if(message.key.equals("integrateSmeltery")) {
          IMCIntegration.integrateSmeltery(message);
        }
        // smeltery alloys
        else if(message.key.equals("alloy")) {
          IMCIntegration.alloy(message);
        }
        // melting blacklist
        else if(message.key.equals("blacklistMelting")) {
          IMCIntegration.blacklistMelting(message);
        }
        // drying rack integration
        else if(message.key.equals("addDryingRecipe")) {
          IMCIntegration.addDryingRecipe(message);
        }
      } catch(ClassCastException e) {
        log.error("Got invalid " + message.key + " IMC from " + message.getSender());
      }
    }
  }

  private void handleAlloyIMCs() {
    for(NBTTagList taglist : alloys) {
      List<FluidStack> fluids = Lists.newLinkedList();
      for(int i = 0; i < taglist.tagCount(); i++) {
        NBTTagCompound tag = taglist.getCompoundTagAt(i);
        FluidStack fs = FluidStack.loadFluidStackFromNBT(tag);
        if(fs == null) {
          fluids.clear();
          break;
        }
        fluids.add(fs);
      }

      // needs at least 3 fluids
      if(fluids.size() > 2) {
        FluidStack output = fluids.get(0);
        FluidStack[] input = new FluidStack[fluids.size() - 1];
        input = fluids.subList(1, fluids.size()).toArray(input);
        TinkerRegistry.registerAlloy(output, input);
        log.debug("Added integration alloy: " + output.getLocalizedName());
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
    MaterialIntegration materialIntegration = new MaterialIntegration(oreRequirement, material, null, null);
    materialIntegration.setRepresentativeItem(oreRequirement);
    return add(materialIntegration);
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

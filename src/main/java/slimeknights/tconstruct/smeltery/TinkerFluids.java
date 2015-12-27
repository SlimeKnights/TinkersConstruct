package slimeknights.tconstruct.smeltery;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Locale;

import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.common.CommonProxy;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.fluid.FluidMoltenMetal;
import slimeknights.tconstruct.smeltery.block.BlockMolten;

@Pulse(id = TinkerFluids.PulseId)
public class TinkerFluids extends TinkerPulse {

  public static final String PulseId = "TinkerFluids";
  static final Logger log = Util.getLogger(PulseId);

  @SidedProxy(clientSide = "slimeknights.tconstruct.smeltery.FluidsClientProxy", serverSide = "slimeknights.tconstruct.common.CommonProxy")
  public static CommonProxy proxy;

  // The fluids
  public static FluidMoltenMetal obsidian;

  static List<Fluid> fluids = Lists.newLinkedList(); // all fluids registered by tcon

  @Subscribe
  public void preInit(FMLPreInitializationEvent event) {
    obsidian = createFluid("obsidian", 0xFFff00ff);

    // blocks
    registerFluidBlock(obsidian);

    proxy.preInit();
  }

  @Subscribe
  public void init(FMLInitializationEvent event) {
    proxy.init();
  }

  @Subscribe
  public void postInit(FMLPostInitializationEvent event) {
    proxy.postInit();
  }

  private FluidMoltenMetal createFluid(String name, int color) {
    name = name.toLowerCase(Locale.US);

    // create and register the fluid
    FluidMoltenMetal fluid = new FluidMoltenMetal(name, color);
    fluid.setUnlocalizedName(Util.prefix(name));
    FluidRegistry.registerFluid(fluid);

    fluids.add(fluid);

    return fluid;
  }


  // create and register the block
  private BlockMolten registerFluidBlock(FluidMoltenMetal fluid) {
    BlockMolten block = new BlockMolten(fluid);
    return registerBlock(block, "molten_" + fluid.getName()); // molten_foobar prefix
  }
}

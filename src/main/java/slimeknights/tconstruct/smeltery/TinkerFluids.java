package slimeknights.tconstruct.smeltery;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

import net.minecraft.block.Block;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import org.apache.logging.log4j.Logger;

import java.util.List;

import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.common.CommonProxy;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.fluid.FluidMolten;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.smeltery.block.BlockMolten;
import slimeknights.tconstruct.tools.TinkerMaterials;

@Pulse(id = TinkerFluids.PulseId)
public class TinkerFluids extends TinkerPulse {

  public static final String PulseId = "TinkerFluids";
  static final Logger log = Util.getLogger(PulseId);

  @SidedProxy(clientSide = "slimeknights.tconstruct.smeltery.FluidsClientProxy", serverSide = "slimeknights.tconstruct.common.CommonProxy")
  public static CommonProxy proxy;

  // The fluids
  public static FluidMolten obsidian;
  public static FluidMolten iron;
  public static FluidMolten gold;
  public static FluidMolten pigIron;
  public static FluidMolten cobalt;
  public static FluidMolten ardite;
  public static FluidMolten manyullyn;
  public static FluidMolten knightslime;
  public static FluidMolten blood;

  static List<Fluid> fluids = Lists.newLinkedList(); // all fluids registered by tcon

  @Subscribe
  public void preInit(FMLPreInitializationEvent event) {
    obsidian = fluidMetal(TinkerMaterials.obsidian);
    obsidian.setTemperature(1290);

    iron = fluidMetal(TinkerMaterials.iron.getIdentifier(), 0xa81212);
    iron.setTemperature(1038);

    gold = fluidMetal("gold", 0xf6d609);
    gold.setTemperature(664);

    pigIron = fluidMetal(TinkerMaterials.pigiron);
    pigIron.setTemperature(800);

    cobalt = fluidMetal(TinkerMaterials.cobalt);
    cobalt.setTemperature(1150);

    ardite = fluidMetal(TinkerMaterials.ardite);
    ardite.setTemperature(1299);

    manyullyn = fluidMetal(TinkerMaterials.manyullyn);
    manyullyn.setTemperature(1200);

    knightslime = fluidMetal(TinkerMaterials.knightslime);
    knightslime.setTemperature(520);

    blood = fluidLiquid("blood", 0x540000);
    blood.setTemperature(420);
    Block block = new BlockFluidClassic(blood, net.minecraft.block.material.Material.water);
    registerBlock(block, "molten_" + blood.getName()); // molten_foobar prefix

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

  private FluidMolten fluidMetal(Material material) {
    return fluidMetal(material.getIdentifier(), material.materialTextColor);
  }

  private FluidMolten fluidMetal(String name, int color) {
    FluidMolten fluid = FluidMolten.metal(name, color);
    fluid.setUnlocalizedName(Util.prefix(name));
    return registerFluid(fluid, false);
  }

  private FluidMolten fluidLiquid(String name, int color) {
    FluidMolten fluid = FluidMolten.liquid(name, color);
    fluid.setUnlocalizedName(Util.prefix(name));
    return registerFluid(fluid, true);
  }

  private FluidMolten registerFluid(FluidMolten fluid, boolean noBlock) {
    FluidRegistry.registerFluid(fluid);

    fluids.add(fluid);

    if(!noBlock) {
      registerFluidBlock(fluid);
    }

    return fluid;
  }

  // create and register the block
  private BlockMolten registerFluidBlock(FluidMolten fluid) {
    BlockMolten block = new BlockMolten(fluid);
    return registerBlock(block, "molten_" + fluid.getName()); // molten_foobar prefix
  }
}

package slimeknights.tconstruct.shared;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
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
import slimeknights.tconstruct.library.fluid.FluidColored;
import slimeknights.tconstruct.library.fluid.FluidMolten;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.smeltery.block.BlockMolten;
import slimeknights.tconstruct.tools.TinkerMaterials;

@Pulse(id = TinkerFluids.PulseId)
public class TinkerFluids extends TinkerPulse {

  public static final String PulseId = "TinkerFluids";
  static final Logger log = Util.getLogger(PulseId);

  @SidedProxy(clientSide = "slimeknights.tconstruct.shared.FluidsClientProxy", serverSide = "slimeknights.tconstruct.common.CommonProxy")
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
  public static FluidColored blood;
  public static FluidColored milk;

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
    pigIron.setRarity(EnumRarity.EPIC);

    cobalt = fluidMetal(TinkerMaterials.cobalt);
    cobalt.setTemperature(1150);
    cobalt.setRarity(EnumRarity.RARE);

    ardite = fluidMetal(TinkerMaterials.ardite);
    ardite.setTemperature(1299);
    ardite.setRarity(EnumRarity.RARE);

    manyullyn = fluidMetal(TinkerMaterials.manyullyn);
    manyullyn.setTemperature(1200);
    manyullyn.setRarity(EnumRarity.RARE);

    knightslime = fluidMetal(TinkerMaterials.knightslime);
    knightslime.setTemperature(520);
    knightslime.setRarity(EnumRarity.EPIC);

    blood = fluidClassic("blood", 0x540000);
    blood.setTemperature(420);

    milk = fluidMilk("milk", 0xffffff);
    milk.setTemperature(320);

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
    FluidMolten fluid = new FluidMolten(name, color);
    return registerFluid(fluid, false);
  }

  private FluidMolten fluidLiquid(String name, int color) {
    FluidMolten fluid = new FluidMolten(name, color, FluidMolten.ICON_LiquidStill, FluidMolten.ICON_LiquidFlowing);
    return registerFluid(fluid, true);
  }

  private FluidColored fluidClassic(String name, int color) {
    FluidColored fluid = new FluidColored(name, color, FluidColored.ICON_LiquidStill, FluidColored.ICON_LiquidFlowing);
    fluid = registerFluid(fluid, true);

    return fluid;
  }

  private FluidColored fluidMilk(String name, int color) {
    FluidColored fluid = new FluidColored(name, color, FluidColored.ICON_MilkStill, FluidColored.ICON_MilkFlowing);
    fluid = registerFluid(fluid, true);

    return fluid;
  }

  private <T extends Fluid> T registerFluid(T fluid, boolean classicBlock) {
    fluid.setUnlocalizedName(Util.prefix(fluid.getName()));
    FluidRegistry.registerFluid(fluid);

    fluids.add(fluid);

    if(classicBlock) {
      Block block = new BlockFluidClassic(fluid, net.minecraft.block.material.Material.water);
      registerBlock(block, fluid.getName());
    }
    else {
      registerFluidBlock(fluid);
    }

    return fluid;
  }

  // create and register the block
  private BlockMolten registerFluidBlock(Fluid fluid) {
    BlockMolten block = new BlockMolten(fluid);
    return registerBlock(block, "molten_" + fluid.getName()); // molten_foobar prefix
  }
}

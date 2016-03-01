package slimeknights.tconstruct.shared;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
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
import slimeknights.tconstruct.shared.block.BlockLiquidSlime;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.BlockMolten;
import slimeknights.tconstruct.smeltery.block.BlockTinkerFluid;
import slimeknights.tconstruct.tools.TinkerMaterials;

@Pulse(id = TinkerFluids.PulseId, pulsesRequired = TinkerSmeltery.PulseId, forced = true)
public class TinkerFluids extends TinkerPulse {

  public static final String PulseId = "TinkerFluids";
  static final Logger log = Util.getLogger(PulseId);

  @SidedProxy(clientSide = "slimeknights.tconstruct.shared.FluidsClientProxy", serverSide = "slimeknights.tconstruct.common.CommonProxy")
  public static CommonProxy proxy;

  // The fluids. Note that just because they exist doesn't mean that they're registered!
  public static FluidMolten searedStone;
  public static FluidMolten obsidian;
  public static FluidMolten clay;
  public static FluidMolten dirt;
  public static FluidMolten iron;
  public static FluidMolten gold;
  public static FluidMolten pigIron;
  public static FluidMolten cobalt;
  public static FluidMolten ardite;
  public static FluidMolten manyullyn;
  public static FluidMolten knightslime;
  public static FluidMolten emerald;
  public static FluidColored blood;
  public static FluidColored milk;
  public static FluidColored blueslime;
  public static FluidColored purpleSlime;

  // Mod Integration fluids
  public static FluidMolten brass;
  public static FluidMolten copper;
  public static FluidMolten tin;
  public static FluidMolten bronze;
  public static FluidMolten zinc;
  public static FluidMolten lead;
  public static FluidMolten nickel;
  public static FluidMolten silver;
  public static FluidMolten electrum;
  public static FluidMolten steel;
  public static FluidMolten aluminium;

  static List<Fluid> fluids = Lists.newLinkedList(); // all fluids registered by tcon

  static {
    setupFluids();
  }

  public static void setupFluids() {
    // buuuuckeeeeet
    FluidRegistry.enableUniversalBucket();

    // Fluids for integration, getting registered by TinkerIntegration
    iron = fluidMetal(TinkerMaterials.iron.getIdentifier(), 0xa81212);
    iron.setTemperature(769);

    gold = fluidMetal("gold", 0xf6d609);
    gold.setTemperature(532);
    gold.setRarity(EnumRarity.RARE);

    pigIron = fluidMetal(TinkerMaterials.pigiron);
    pigIron.setTemperature(600);
    pigIron.setRarity(EnumRarity.EPIC);

    cobalt = fluidMetal(TinkerMaterials.cobalt);
    cobalt.setTemperature(950);
    cobalt.setRarity(EnumRarity.RARE);

    ardite = fluidMetal(TinkerMaterials.ardite);
    ardite.setTemperature(860);
    ardite.setRarity(EnumRarity.RARE);

    manyullyn = fluidMetal(TinkerMaterials.manyullyn);
    manyullyn.setTemperature(1000);
    manyullyn.setRarity(EnumRarity.RARE);

    knightslime = fluidMetal(TinkerMaterials.knightslime);
    knightslime.setTemperature(520);
    knightslime.setRarity(EnumRarity.EPIC);

    // Mod Integration fluids
    brass = fluidMetal("brass", 0xede38b);
    brass.setTemperature(470);

    copper = fluidMetal(TinkerMaterials.copper);
    copper.setTemperature(542);

    tin = fluidMetal("tin", 0xc1cddc);
    tin.setTemperature(350);

    bronze = fluidMetal(TinkerMaterials.bronze);
    bronze.setTemperature(475);

    zinc = fluidMetal("zinc", 0xd3efe8);
    zinc.setTemperature(375);

    lead = fluidMetal(TinkerMaterials.lead);
    lead.setTemperature(400);

    nickel = fluidMetal("nickel", 0xc8d683);
    nickel.setTemperature(727);

    silver = fluidMetal(TinkerMaterials.silver);
    silver.setTemperature(480);
    silver.setRarity(EnumRarity.RARE);

    electrum = fluidMetal(TinkerMaterials.electrum);
    electrum.setTemperature(500);
    electrum.setRarity(EnumRarity.EPIC);

    steel = fluidMetal(TinkerMaterials.steel);
    steel.setTemperature(681);

    aluminium = fluidMetal("aluminium", 0xefe0d5);
    aluminium.setTemperature(330);
  }

  @Subscribe
  public void preInit(FMLPreInitializationEvent event) {
    if(isSmelteryLoaded()) {
      searedStone = fluidStone("stone", 0x777777);
      searedStone.setTemperature(800);
      registerFluid(searedStone);
      registerMoltenBlock(searedStone);
      FluidRegistry.addBucketForFluid(searedStone);

      obsidian = fluidStone(TinkerMaterials.obsidian.getIdentifier(), 0x411385);
      obsidian.setTemperature(1000);
      registerFluid(obsidian);
      registerMoltenBlock(obsidian);
      FluidRegistry.addBucketForFluid(obsidian);

      clay = fluidStone("clay", 0xc67453);
      clay.setTemperature(700);
      registerFluid(clay);
      registerMoltenBlock(clay);
      FluidRegistry.addBucketForFluid(clay);

      dirt = fluidStone("dirt", 0xa68564);
      dirt.setTemperature(500);
      registerFluid(dirt);
      registerMoltenBlock(dirt);
      FluidRegistry.addBucketForFluid(dirt);

      emerald = fluidMetal("emerald", 0x58e78e);
      emerald.setTemperature(999);
      registerFluid(emerald);
      registerMoltenBlock(emerald);
      FluidRegistry.addBucketForFluid(emerald);

      // blood for the blood god
      blood = fluidClassic("blood", 0x540000);
      blood.setTemperature(420);
      registerFluid(blood);
      registerClassicBlock(blood);
      FluidRegistry.addBucketForFluid(blood);
    }

    milk = fluidMilk("milk", 0xffffff);
    milk.setTemperature(320);
    registerFluid(milk);
    registerClassicBlock(milk);
    FluidContainerRegistry.registerFluidContainer(new FluidStack(milk, FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(Items.milk_bucket), FluidContainerRegistry.EMPTY_BUCKET);

    if(isWorldLoaded()) {
      blueslime = fluidClassic("blueslime", 0xef67f0f5);
      blueslime.setTemperature(310);
      blueslime.setViscosity(1500);
      blueslime.setDensity(1500);
      registerFluid(blueslime);
      registerBlock(new BlockLiquidSlime(blueslime, net.minecraft.block.material.Material.water), blueslime.getName());
      FluidRegistry.addBucketForFluid(blueslime);
    }
    if(isWorldLoaded() || isSmelteryLoaded()) {
      purpleSlime = fluidClassic("purpleslime", 0xefd236ff);
      purpleSlime.setTemperature(370);
      purpleSlime.setViscosity(1600);
      purpleSlime.setDensity(1600);
      registerFluid(purpleSlime);
      registerBlock(new BlockLiquidSlime(purpleSlime, net.minecraft.block.material.Material.water), purpleSlime.getName());
      FluidRegistry.addBucketForFluid(purpleSlime);
    }

    // register fluid buckets for all of the liquids
    // ok we can't register them because fluidcontainerregistry is not NBT sensitive.
/*
    if(TinkerSmeltery.bucket != null) {
      for(Fluid fluid : fluids) {
        if(fluid == milk) {
          continue;
        }
        FluidStack toFill = new FluidStack(fluid, FluidContainerRegistry.BUCKET_VOLUME);
        ItemStack filled = UniversalBucket.getFilledBucket(TinkerSmeltery.bucket, fluid);
        FluidContainerRegistry.registerFluidContainer(toFill, filled, FluidContainerRegistry.EMPTY_BUCKET);
      }
    }
*/
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

  private static FluidMolten fluidMetal(Material material) {
    return fluidMetal(material.getIdentifier(), material.materialTextColor);
  }

  private static FluidMolten fluidMetal(String name, int color) {
    FluidMolten fluid = new FluidMolten(name, color);
    return fluid;
  }

  private static FluidMolten fluidLiquid(String name, int color) {
    FluidMolten fluid = new FluidMolten(name, color, FluidMolten.ICON_LiquidStill, FluidMolten.ICON_LiquidFlowing);
    registerFluid(fluid);
    return fluid;
  }

  private static FluidMolten fluidStone(String name, int color) {
    FluidMolten fluid = new FluidMolten(name, color, FluidColored.ICON_StoneStill, FluidColored.ICON_StoneFlowing);
    fluid = registerFluid(fluid);

    return fluid;
  }

  private static FluidColored fluidClassic(String name, int color) {
    FluidColored fluid = new FluidColored(name, color, FluidColored.ICON_LiquidStill, FluidColored.ICON_LiquidFlowing);
    fluid = registerFluid(fluid);

    return fluid;
  }

  private static FluidColored fluidMilk(String name, int color) {
    FluidColored fluid = new FluidColored(name, color, FluidColored.ICON_MilkStill, FluidColored.ICON_MilkFlowing);
    fluid = registerFluid(fluid);

    return fluid;
  }

  public static <T extends Fluid> T registerFluid(T fluid) {
    fluid.setUnlocalizedName(Util.prefix(fluid.getName()));
    FluidRegistry.registerFluid(fluid);

    fluids.add(fluid);

    return fluid;
  }

  /** Registers a non-burning water based block for the fluid */
  public static BlockFluidBase registerClassicBlock(Fluid fluid) {
    BlockFluidBase block = new BlockTinkerFluid(fluid, net.minecraft.block.material.Material.water);
    return registerBlock(block, fluid.getName());
  }

  /** Registers a hot lava-based block for the fluid, prefix with molten_ */
  public static BlockMolten registerMoltenBlock(Fluid fluid) {
    BlockMolten block = new BlockMolten(fluid);
    return registerBlock(block, "molten_" + fluid.getName()); // molten_foobar prefix
  }
}

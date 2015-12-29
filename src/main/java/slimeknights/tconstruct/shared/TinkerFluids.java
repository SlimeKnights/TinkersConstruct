package slimeknights.tconstruct.shared;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidClassic;
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
import slimeknights.tconstruct.shared.block.BlockBlueSlime;
import slimeknights.tconstruct.smeltery.block.BlockMolten;
import slimeknights.tconstruct.tools.TinkerMaterials;

@Pulse(id = TinkerFluids.PulseId)
public class TinkerFluids extends TinkerPulse {

  public static final String PulseId = "TinkerFluids";
  static final Logger log = Util.getLogger(PulseId);

  @SidedProxy(clientSide = "slimeknights.tconstruct.shared.FluidsClientProxy", serverSide = "slimeknights.tconstruct.common.CommonProxy")
  public static CommonProxy proxy;

  // The fluids
  public static FluidMolten searedStone;
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
  public static FluidColored blueslime;

  static List<Fluid> fluids = Lists.newLinkedList(); // all fluids registered by tcon

  public static Block blockBlueslime;

  @Subscribe
  public void preInit(FMLPreInitializationEvent event) {
    if(isSmelteryLoaded()) {
      searedStone = fluidStone("stone", -1);
      registerFluid(searedStone, true);
      searedStone.setTemperature(800);
      registerFluidBlock(searedStone);

      //obsidian = fluidMetal(TinkerMaterials.obsidian);
      obsidian = fluidStone(TinkerMaterials.obsidian.getIdentifier(), 0x7d24ff);
      obsidian.setTemperature(1000);
      registerFluidBlock(obsidian);

      iron = fluidMetal(TinkerMaterials.iron.getIdentifier(), 0xa81212);
      iron.setTemperature(738);

      gold = fluidMetal("gold", 0xf6d609);
      gold.setTemperature(564);

      pigIron = fluidMetal(TinkerMaterials.pigiron);
      pigIron.setTemperature(600);
      pigIron.setRarity(EnumRarity.EPIC);

      cobalt = fluidMetal(TinkerMaterials.cobalt);
      cobalt.setTemperature(990);
      cobalt.setRarity(EnumRarity.RARE);

      ardite = fluidMetal(TinkerMaterials.ardite);
      ardite.setTemperature(920);
      ardite.setRarity(EnumRarity.RARE);

      manyullyn = fluidMetal(TinkerMaterials.manyullyn);
      manyullyn.setTemperature(1000);
      manyullyn.setRarity(EnumRarity.RARE);

      knightslime = fluidMetal(TinkerMaterials.knightslime);
      knightslime.setTemperature(520);
      knightslime.setRarity(EnumRarity.EPIC);

      blood = fluidClassic("blood", 0x540000);
      blood.setTemperature(420);
      registerDefaultBlock(blood);
    }

    milk = fluidMilk("milk", 0xffffff);
    milk.setTemperature(320);
    FluidContainerRegistry.registerFluidContainer(new FluidStack(milk, FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(Items.milk_bucket), FluidContainerRegistry.EMPTY_BUCKET);

    if(isWorldLoaded()) {
      blueslime = fluidClassic("blueslime", 0xef67f0f5);
      blueslime.setTemperature(310);
      blockBlueslime = registerBlock(new BlockBlueSlime(blueslime, net.minecraft.block.material.Material.water), blueslime.getName());
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

  private FluidMolten fluidMetal(Material material) {
    return fluidMetal(material.getIdentifier(), material.materialTextColor);
  }

  private FluidMolten fluidMetal(String name, int color) {
    FluidMolten fluid = new FluidMolten(name, color);
    return registerFluid(fluid, false);
  }

  private FluidMolten fluidLiquid(String name, int color, boolean withBlock) {
    FluidMolten fluid = new FluidMolten(name, color, FluidMolten.ICON_LiquidStill, FluidMolten.ICON_LiquidFlowing);
    registerFluid(fluid, !withBlock);
    return fluid;
  }

  private FluidMolten fluidStone(String name, int color) {
    FluidMolten fluid = new FluidMolten(name, color, FluidColored.ICON_StoneStill, FluidColored.ICON_StoneFlowing);
    fluid = registerFluid(fluid, true);

    return fluid;
  }

  private FluidColored fluidClassic(String name, int color) {
    FluidColored fluid = new FluidColored(name, color, FluidColored.ICON_LiquidStill, FluidColored.ICON_LiquidFlowing);
    fluid = registerFluid(fluid, true);

    return fluid;
  }

  private FluidColored fluidMilk(String name, int color) {
    FluidColored fluid = new FluidColored(name, color, FluidColored.ICON_MilkStill, FluidColored.ICON_MilkFlowing);
    fluid = registerFluid(fluid, true);
    registerDefaultBlock(fluid);

    return fluid;
  }

  private <T extends Fluid> T registerFluid(T fluid, boolean noBlock) {
    fluid.setUnlocalizedName(Util.prefix(fluid.getName()));
    FluidRegistry.registerFluid(fluid);

    fluids.add(fluid);

    if(!noBlock) {
      registerFluidBlock(fluid);
    }

    return fluid;
  }

  private BlockFluidBase registerDefaultBlock(Fluid fluid) {
    BlockFluidBase block = new BlockFluidClassic(fluid, net.minecraft.block.material.Material.water);
    return registerBlock(block, fluid.getName());
  }

  // create and register the block
  private BlockMolten registerFluidBlock(Fluid fluid) {
    BlockMolten block = new BlockMolten(fluid);
    return registerBlock(block, "molten_" + fluid.getName()); // molten_foobar prefix
  }
}

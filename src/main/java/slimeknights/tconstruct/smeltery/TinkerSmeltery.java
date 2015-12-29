package slimeknights.tconstruct.smeltery;

import com.google.common.eventbus.Subscribe;

import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import org.apache.logging.log4j.Logger;

import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.common.CommonProxy;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;
import slimeknights.tconstruct.library.tools.Pattern;
import slimeknights.tconstruct.shared.TinkerFluids;
import slimeknights.tconstruct.smeltery.block.BlockSeared;
import slimeknights.tconstruct.smeltery.block.BlockSmelteryController;
import slimeknights.tconstruct.smeltery.block.BlockTank;
import slimeknights.tconstruct.smeltery.item.UniversalBucket;
import slimeknights.tconstruct.smeltery.tileentity.TileSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.TileSmelteryComponent;
import slimeknights.tconstruct.smeltery.tileentity.TileTank;

@Pulse(id = TinkerSmeltery.PulseId, description = "The smeltery and items needed for it", defaultEnable = false)
public class TinkerSmeltery extends TinkerPulse {

  public static final String PulseId = "TinkerSmeltery";
  static final Logger log = Util.getLogger(PulseId);

  @SidedProxy(clientSide = "slimeknights.tconstruct.smeltery.SmelteryClientProxy", serverSide = "slimeknights.tconstruct.common.CommonProxy")
  public static CommonProxy proxy;

  // Blocks
  public static BlockSeared searedBlock;
  public static BlockSmelteryController smelteryController;
  public static BlockTank searedTank;

  // Items
  public static Pattern cast;
  public static UniversalBucket bucket;

  // PRE-INITIALIZATION
  @Subscribe
  public void preInit(FMLPreInitializationEvent event) {
    searedBlock = registerEnumBlock(new BlockSeared(), "seared");
    smelteryController = registerBlock(new BlockSmelteryController(), "smeltery_controller");
    searedTank = registerEnumBlock(new BlockTank(), "seared_tank");

    registerTE(TileSmeltery.class, "smeltery_controller");
    registerTE(TileSmelteryComponent.class, "smeltery_component");
    registerTE(TileTank.class, "smeltery_tank");

    cast = registerItem(new Pattern(), "cast");
    cast.setCreativeTab(TinkerRegistry.tabSmeltery);

    bucket = registerItem(new UniversalBucket(), "bucket");
    bucket.setCreativeTab(TinkerRegistry.tabGeneral);

    proxy.preInit();
    MinecraftForge.EVENT_BUS.register(bucket);

    TinkerRegistry.tabSmeltery.setDisplayIcon(new ItemStack(searedTank));
  }

  // INITIALIZATION
  @Subscribe
  public void init(FMLInitializationEvent event) {
    proxy.init();
  }

  // POST-INITIALIZATION
  @Subscribe
  public void postInit(FMLPostInitializationEvent event) {
    registerSmelteryFuel();
    registerMelting();
    registerAlloys();

    proxy.postInit();
  }

  private void registerSmelteryFuel() {
    TinkerRegistry.registerSmelteryFuel(new FluidStack(FluidRegistry.LAVA, 50), 100);
  }

  private void registerMelting() {
    int bucket = FluidContainerRegistry.BUCKET_VOLUME;

    // Water
    Fluid water = FluidRegistry.WATER;
    TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of(Blocks.ice, bucket), water, 305));
    TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of(Blocks.packed_ice, bucket*2), water, 310));
    TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of(Blocks.snow, bucket), water, 305));
    TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of(Items.snowball, bucket/8), water, 301));

    // bloooooood
    TinkerRegistry.registerMelting(Items.rotten_flesh, TinkerFluids.blood, 5);

    // seared stone
    TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of("stone"), TinkerFluids.searedStone, 750));
    TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of("cobblestone"), TinkerFluids.searedStone, 750));

    // obsidian
    TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of("obsidian", Material.VALUE_Ore), TinkerFluids.obsidian, Material.VALUE_Ore, Material.VALUE_Ore));

    TinkerRegistry.registerEntityMelting(EntitySheep.class, new FluidStack(FluidRegistry.LAVA, 1));
  }

  private void registerAlloys() {
    TinkerRegistry.registerAlloy(new FluidStack(TinkerFluids.obsidian, 2),
                                 new FluidStack(FluidRegistry.WATER, 1),
                                 new FluidStack(FluidRegistry.LAVA, 1));
  }
}

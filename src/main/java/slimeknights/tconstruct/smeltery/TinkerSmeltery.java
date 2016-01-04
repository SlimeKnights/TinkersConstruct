package slimeknights.tconstruct.smeltery;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;

import net.minecraft.block.Block;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import slimeknights.mantle.item.ItemBlockMeta;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.common.CommonProxy;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.smeltery.Cast;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerFluids;
import slimeknights.tconstruct.smeltery.block.BlockCasting;
import slimeknights.tconstruct.smeltery.block.BlockFaucet;
import slimeknights.tconstruct.smeltery.block.BlockSeared;
import slimeknights.tconstruct.smeltery.block.BlockSmelteryController;
import slimeknights.tconstruct.smeltery.block.BlockSmelteryIO;
import slimeknights.tconstruct.smeltery.block.BlockTank;
import slimeknights.tconstruct.smeltery.item.CastCustom;
import slimeknights.tconstruct.smeltery.item.UniversalBucket;
import slimeknights.tconstruct.smeltery.tileentity.TileCastingBasin;
import slimeknights.tconstruct.smeltery.tileentity.TileCastingTable;
import slimeknights.tconstruct.smeltery.tileentity.TileDrain;
import slimeknights.tconstruct.smeltery.tileentity.TileFaucet;
import slimeknights.tconstruct.smeltery.tileentity.TileSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.TileSmelteryComponent;
import slimeknights.tconstruct.smeltery.tileentity.TileTank;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.BlockSlime;

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
  public static BlockFaucet faucet;
  public static BlockCasting castingBlock;
  public static BlockSmelteryIO smelteryIO;

  // Items
  public static Cast cast;
  public static CastCustom castCustom;
  public static UniversalBucket bucket;

  private static Map<Fluid, Set<Pair<List<ItemStack>, Integer>>> knownOreFluids = Maps.newHashMap();

  public static ImmutableSet<Block> validSmelteryBlocks;

  // PRE-INITIALIZATION
  @Subscribe
  public void preInit(FMLPreInitializationEvent event) {
    searedBlock = registerEnumBlock(new BlockSeared(), "seared");
    smelteryController = registerBlock(new BlockSmelteryController(), "smeltery_controller");
    searedTank = registerEnumBlock(new BlockTank(), "seared_tank");
    faucet = registerBlock(new BlockFaucet(), "faucet");
    castingBlock = registerBlock(new BlockCasting(), ItemBlockMeta.class,"casting");
    smelteryIO = registerEnumBlock(new BlockSmelteryIO(), "smeltery_io");

    ItemBlockMeta.setMappingProperty(castingBlock, BlockCasting.TYPE);

    registerTE(TileSmeltery.class, "smeltery_controller");
    registerTE(TileSmelteryComponent.class, "smeltery_component");
    registerTE(TileTank.class, "tank");
    registerTE(TileFaucet.class, "faucet");
    registerTE(TileCastingTable.class, "casting_table");
    registerTE(TileCastingBasin.class, "casting_basin");
    registerTE(TileDrain.class, "smeltery_drain");

    cast = registerItem(new Cast(), "cast");
    castCustom = registerItem(new CastCustom(), "cast_custom");
    castCustom.addMeta(0, "ingot", Material.VALUE_Ingot);
    castCustom.addMeta(1, "nugget", Material.VALUE_Nugget);
    castCustom.addMeta(2, "gem", Material.VALUE_Gem);

    bucket = registerItem(new UniversalBucket(), "bucket");
    bucket.setCreativeTab(TinkerRegistry.tabGeneral);

    proxy.preInit();
    MinecraftForge.EVENT_BUS.register(bucket);

    TinkerRegistry.tabSmeltery.setDisplayIcon(new ItemStack(searedTank));

    ImmutableSet.Builder<Block> builder = ImmutableSet.builder();
    builder.add(searedBlock);
    builder.add(searedTank);
    builder.add(smelteryIO);

    validSmelteryBlocks = builder.build();
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

    registerRecipeOredictMelting();

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

    // purple slime
    TinkerRegistry.registerMelting(TinkerCommons.matSlimeBallPurple, TinkerFluids.purpleSlime, Material.VALUE_SlimeBall);
    if(TinkerWorld.slimeBlockCongealed != null) {
      ItemStack slimeblock = new ItemStack(TinkerWorld.slimeBlockCongealed, 1, BlockSlime.SlimeType.PURPLE.meta);
      TinkerRegistry.registerMelting(slimeblock, TinkerFluids.purpleSlime, Material.VALUE_SlimeBall*4);
      slimeblock = new ItemStack(TinkerWorld.slimeBlock, 1, BlockSlime.SlimeType.PURPLE.meta);
      TinkerRegistry.registerMelting(slimeblock, TinkerFluids.purpleSlime, Material.VALUE_SlimeBall*9);
    }

    // seared stone, takes as long as a full block to melt, but gives less
    TinkerRegistry.registerMelting(MeltingRecipe.forAmount(RecipeMatch.of("stone", Material.VALUE_SearedMaterial),
                                                           TinkerFluids.searedStone, Material.VALUE_Ore));
    TinkerRegistry.registerMelting(MeltingRecipe.forAmount(RecipeMatch.of("cobblestone", Material.VALUE_SearedMaterial),
                                                           TinkerFluids.searedStone, Material.VALUE_Ore));

    // obsidian
    TinkerRegistry.registerMelting(MeltingRecipe.forAmount(RecipeMatch.of("obsidian", Material.VALUE_Ore),
                                                           TinkerFluids.obsidian, Material.VALUE_Ore));

    TinkerRegistry.registerEntityMelting(EntitySheep.class, new FluidStack(TinkerFluids.blood, 5));

    registerOredictMelting(TinkerFluids.gold, "Gold");
  }

  private void registerAlloys() {
    // 1 bucket lava + 1 bucket water = 2 buckets obsidian = 1 block obsidian
    // 1000 + 1000 = 2000
    TinkerRegistry.registerAlloy(new FluidStack(TinkerFluids.obsidian, 2),
                                 new FluidStack(FluidRegistry.WATER, 1),
                                 new FluidStack(FluidRegistry.LAVA, 1));

    // 1 iron ingot + 1 purple slime ball + seared stone in molten form = 1 knightslime ingot
    // 144 + 250 + 288 = 144
    TinkerRegistry.registerAlloy(new FluidStack(TinkerFluids.knightslime, 72),
                                 new FluidStack(TinkerFluids.iron, 72),
                                 new FluidStack(TinkerFluids.purpleSlime, 125),
                                 new FluidStack(TinkerFluids.searedStone, 144));
  }

  /**
   * Registers melting for all directly supported pre- and suffixes of the ore.
   * E.g. "Iron" -> "ingotIron", "blockIron", "oreIron",
   */
  public static void registerOredictMelting(Fluid fluid, String ore) {
    ImmutableSet.Builder<Pair<List<ItemStack>, Integer>> builder = ImmutableSet.builder();
    Pair<String, Integer> nuggetOre = Pair.of("nugget" + ore, Material.VALUE_Nugget);
    Pair<String, Integer> ingotOre = Pair.of("ingot" + ore, Material.VALUE_Ingot);
    Pair<String, Integer> blockOre = Pair.of("block" + ore, Material.VALUE_Block);
    Pair<String, Integer> oreOre = Pair.of("ore" + ore, Material.VALUE_Ore);
    Set<Pair<String, Integer>> knownOres = ImmutableSet.of(nuggetOre, ingotOre, blockOre, oreOre);


    // register oredicts
    for(Pair<String, Integer> pair : knownOres) {
      TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of(pair.getLeft(), pair.getRight()), fluid));
      builder.add(Pair.of(OreDictionary.getOres(pair.getLeft()), pair.getRight()));
    }

    // used for recipe detection
    knownOreFluids.put(fluid, builder.build());
  }

  // take all fluids we registered oredicts for and scan all recipies for oredict-recipies that we can apply this to
  private static void registerRecipeOredictMelting() {
    // we go through all recipies, and if it's an ore recipe we go through its contents and check if it
    // only consists of one of our known oredict entries
    for(IRecipe irecipe : CraftingManager.getInstance().getRecipeList()) {
      // recipe already has a melting recipe?
      if(TinkerRegistry.getMelting(irecipe.getRecipeOutput()) != null) {
        continue;
      }

      List<Object> inputs;
      if(irecipe instanceof ShapelessOreRecipe) {
        inputs = ((ShapelessOreRecipe) irecipe).getInput();
      }
      else if(irecipe instanceof ShapedOreRecipe) {
        inputs = Arrays.asList(((ShapedOreRecipe) irecipe).getInput());
      }
      else if(irecipe instanceof ShapelessRecipes) {
        inputs = Lists.<Object>newLinkedList(((ShapelessRecipes) irecipe).recipeItems);
      }
      else if(irecipe instanceof ShapedRecipes) {
        inputs = Arrays.asList((Object[])((ShapedRecipes) irecipe).recipeItems);
      }
      else {
        // not an ore recipe, stop here because we can't handle it
        continue;
      }

      // this map holds how much of which fluid is known of the recipe
      // if an recipe contains an itemstack that can't be mapped to a fluid calculation is aborted
      Map<Fluid, Integer> known = Maps.newHashMap();
      for(Object o : inputs) {
        // can contain nulls because of shapedrecipe
        if(o == null) {
          continue;
        }
        boolean found = false;
        for(Map.Entry<Fluid, Set<Pair<List<ItemStack>, Integer>>> entry : knownOreFluids.entrySet()) {
          // check if it's a known oredict (all oredict lists are equal if they match the same oredict)
          // OR if it's an itemstack contained in one of our oredicts
          for(Pair<List<ItemStack>, Integer> pair : entry.getValue()) {
            if(o == pair.getLeft() || (o instanceof  ItemStack && pair.getLeft().contains(o))) {
              // matches! Update fluid amount known
              Integer amount = known.get(entry.getKey()); // what we found for the liquid so far
              if(amount == null) {
                // nothing is what we found so far.
                amount = 0;
              }
              amount += pair.getRight();
              known.put(entry.getKey(), amount);
              found = true;
              break;
            }
          }
          if(found) {
            break;
          }
        }
        // not a recipe we can process, contains an item that can't melt
        if(!found) {
          known.clear();
          break;
        }
      }

      // add a melting recipe for it
      // we only support single-liquid recipies currently :I
      if(known.keySet().size() == 1) {
        Fluid fluid = known.keySet().iterator().next();
        ItemStack output = irecipe.getRecipeOutput().copy();
        int amount = known.get(fluid)/output.stackSize;
        output.stackSize = 1;
        TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.ofNBT(output, amount), fluid));
        log.trace("Added automatic melting recipe for %s (%d %s)", irecipe.getRecipeOutput().toString(), amount, fluid.getName());
      }
    }
  }
}

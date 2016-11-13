package slimeknights.tconstruct.tools;

import com.google.common.eventbus.Subscribe;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.apache.logging.log4j.Logger;

import slimeknights.mantle.item.ItemBlockMeta;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.CommonProxy;
import slimeknights.tconstruct.common.EntityIDs;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.IndestructibleEntityItem;
import slimeknights.tconstruct.library.tools.DualToolHarvestUtils;
import slimeknights.tconstruct.library.tools.Pattern;
import slimeknights.tconstruct.library.tools.Shard;
import slimeknights.tconstruct.library.tools.ToolPart;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.BlockSlime;
import slimeknights.tconstruct.shared.block.BlockTable;
import slimeknights.tconstruct.shared.tileentity.TileTable;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.common.RepairRecipe;
import slimeknights.tconstruct.tools.common.TableRecipe;
import slimeknights.tconstruct.tools.common.block.BlockToolForge;
import slimeknights.tconstruct.tools.common.block.BlockToolTable;
import slimeknights.tconstruct.tools.common.item.ItemBlockTable;
import slimeknights.tconstruct.tools.common.item.SharpeningKit;
import slimeknights.tconstruct.tools.common.tileentity.TileCraftingStation;
import slimeknights.tconstruct.tools.common.tileentity.TilePartBuilder;
import slimeknights.tconstruct.tools.common.tileentity.TilePartChest;
import slimeknights.tconstruct.tools.common.tileentity.TilePatternChest;
import slimeknights.tconstruct.tools.common.tileentity.TileStencilTable;
import slimeknights.tconstruct.tools.common.tileentity.TileToolForge;
import slimeknights.tconstruct.tools.common.tileentity.TileToolStation;
import slimeknights.tconstruct.tools.ranged.item.BoltCore;

@Pulse(id = TinkerTools.PulseId, description = "All the tools and everything related to it.")
public class TinkerTools extends AbstractToolPulse {

  public static final String PulseId = "TinkerTools";
  static final Logger log = Util.getLogger(PulseId);

  @SidedProxy(clientSide = "slimeknights.tconstruct.tools.ToolClientProxy", serverSide = "slimeknights.tconstruct.common.CommonProxy")
  public static CommonProxy proxy;

  // Blocks
  public static BlockToolTable toolTables;
  public static BlockToolForge toolForge;

  // General Items
  public static Pattern pattern;
  public static Shard shard;
  public static SharpeningKit sharpeningKit;

  // Tool Parts
  public static ToolPart pickHead;
  public static ToolPart shovelHead;
  public static ToolPart axeHead;
  public static ToolPart broadAxeHead;
  public static ToolPart swordBlade;
  public static ToolPart largeSwordBlade;
  public static ToolPart hammerHead;
  public static ToolPart excavatorHead;
  public static ToolPart scytheHead;
  public static ToolPart panHead;
  public static ToolPart signHead;

  public static ToolPart toolRod;
  public static ToolPart toughToolRod;
  public static ToolPart binding;
  public static ToolPart toughBinding;
  public static ToolPart wideGuard;
  public static ToolPart handGuard;
  public static ToolPart crossGuard;
  public static ToolPart largePlate;

  public static ToolPart knifeBlade;

  public static ToolPart bowLimb;
  public static ToolPart bowString;

  public static ToolPart arrowHead;
  public static ToolPart arrowShaft;
  public static ToolPart fletching;
  public static BoltCore boltCore;

  // PRE-INITIALIZATION
  @Override
  @Subscribe
  public void preInit(FMLPreInitializationEvent event) {
    // register items
    pattern = registerItem(new Pattern(), "pattern");

    shard = registerItem(new Shard(), "shard");
    sharpeningKit = (SharpeningKit) registerToolPart(new SharpeningKit(), "sharpening_kit");
    sharpeningKit.setCreativeTab(TinkerRegistry.tabParts);
    TinkerRegistry.registerToolPart(sharpeningKit);
    TinkerRegistry.registerToolPart(shard);

    super.preInit(event);

    // register blocks
    toolTables = registerBlock(new ItemBlockTable(new BlockToolTable()), "tooltables");
    toolForge = registerBlock(new ItemBlockTable(new BlockToolForge()), "toolforge");

    ItemBlockMeta.setMappingProperty(toolTables, BlockToolTable.TABLES);

    registerTE(TileTable.class, "table");
    registerTE(TileCraftingStation.class, "craftingstation");
    registerTE(TileStencilTable.class, "stenciltable");
    registerTE(TilePartBuilder.class, "partbuilder");
    registerTE(TilePatternChest.class, "patternchest");
    registerTE(TilePartChest.class, "partchest");
    registerTE(TileToolStation.class, "toolstation");
    registerTE(TileToolForge.class, "toolforge");

    // register entities

    EntityRegistry.registerModEntity(IndestructibleEntityItem.class, "Indestructible Item", EntityIDs.INDESTRUCTIBLE_ITEM, TConstruct.instance, 32, 5, true);

    proxy.preInit();

    // set shard
    TinkerRegistry.setShardItem(shard);

    TinkerRegistry.registerStencilTableCrafting(Pattern.setTagForPart(new ItemStack(pattern), sharpeningKit));
    TinkerRegistry.registerStencilTableCrafting(Pattern.setTagForPart(new ItemStack(pattern), shard));
  }

  @Override
  protected void registerToolParts() {
    // The order the items are registered in represents the order in the stencil table GUI too
    pickHead = registerToolPart(new ToolPart(Material.VALUE_Ingot * 2), "pick_head");
    shovelHead = registerToolPart(new ToolPart(Material.VALUE_Ingot * 2), "shovel_head");
    axeHead = registerToolPart(new ToolPart(Material.VALUE_Ingot * 2), "axe_head");
    broadAxeHead = registerToolPart(new ToolPart(Material.VALUE_Ingot * 8), "broad_axe_head");
    swordBlade = registerToolPart(new ToolPart(Material.VALUE_Ingot * 2), "sword_blade");
    largeSwordBlade = registerToolPart(new ToolPart(Material.VALUE_Ingot * 8), "large_sword_blade");
    hammerHead = registerToolPart(new ToolPart(Material.VALUE_Ingot * 8), "hammer_head");
    excavatorHead = registerToolPart(new ToolPart(Material.VALUE_Ingot * 8), "excavator_head");
    scytheHead = registerToolPart(new ToolPart(Material.VALUE_Ingot * 8), "scythe_head");
    panHead = registerToolPart(new ToolPart(Material.VALUE_Ingot * 3), "pan_head");
    signHead = registerToolPart(new ToolPart(Material.VALUE_Ingot * 3), "sign_head");

    toolRod = registerToolPart(new ToolPart(Material.VALUE_Ingot), "tool_rod");
    toughToolRod = registerToolPart(new ToolPart(Material.VALUE_Ingot * 3), "tough_tool_rod");
    binding = registerToolPart(new ToolPart(Material.VALUE_Ingot), "binding");
    toughBinding = registerToolPart(new ToolPart(Material.VALUE_Ingot * 3), "tough_binding");

    wideGuard = registerToolPart(new ToolPart(Material.VALUE_Ingot), "wide_guard");
    handGuard = registerToolPart(new ToolPart(Material.VALUE_Ingot), "hand_guard");
    crossGuard = registerToolPart(new ToolPart(Material.VALUE_Ingot), "cross_guard");

    largePlate = registerToolPart(new ToolPart(Material.VALUE_Ingot * 8), "large_plate");

    knifeBlade = registerToolPart(new ToolPart(Material.VALUE_Ingot), "knife_blade");

    bowLimb = registerToolPart(new ToolPart(Material.VALUE_Ingot * 3), "bow_limb");
    bowString = registerToolPart(new ToolPart(Material.VALUE_Ingot), "bow_string");

    arrowHead = registerToolPart(new ToolPart(Material.VALUE_Ingot * 2), "arrow_head");
    arrowShaft = registerToolPart(new ToolPart(Material.VALUE_Ingot * 2), "arrow_shaft");
    fletching = registerToolPart(new ToolPart(Material.VALUE_Ingot * 2), "fletching");
    boltCore = (BoltCore)registerToolPart(new BoltCore(Material.VALUE_Ingot * 2), "bolt_core", null);
    toolparts.remove(boltCore);
  }

  // INITIALIZATION
  @Override
  @Subscribe
  public void init(FMLInitializationEvent event) {
    super.init(event);

    proxy.init();
  }

  @Override
  protected void registerRecipies() {
    // Pattern
    ItemStack pattern = new ItemStack(TinkerTools.pattern, 4);
    GameRegistry.addRecipe(new ShapedOreRecipe(pattern, "PS", "SP", 'P', "plankWood", 'S', "stickWood"));
    GameRegistry.addRecipe(new ShapedOreRecipe(pattern, "SP", "PS", 'P', "plankWood", 'S', "stickWood"));
    pattern.stackSize = 1;

    // pattern book recipe
    GameRegistry.addShapelessRecipe(new ItemStack(Items.BOOK), Items.PAPER, Items.PAPER, Items.PAPER, Items.STRING, pattern, pattern);

    // Crafting Station
    GameRegistry.addRecipe(
        new ShapelessOreRecipe(new ItemStack(toolTables, 1, BlockToolTable.TableTypes.CraftingStation.meta),
                               "workbench"));
    // Stencil Table
    GameRegistry.addRecipe(
        new TableRecipe(OreDictionary.getOres("plankWood"), toolTables, BlockToolTable.TableTypes.StencilTable.meta,
                        "P", "B", 'P', pattern, 'B', "plankWood"));
    GameRegistry.addRecipe(BlockTable.createItemstack(toolTables, BlockToolTable.TableTypes.StencilTable.meta, TinkerCommons.blockFirewood, TinkerCommons.lavawood.getMetadata()),
                           "P", "B", 'P', pattern, 'B', TinkerCommons.lavawood);
    GameRegistry.addRecipe(BlockTable.createItemstack(toolTables, BlockToolTable.TableTypes.StencilTable.meta, Blocks.RAIL, 0),
                           "P", "B", 'P', pattern, 'B', Blocks.RAIL);
    GameRegistry.addRecipe(BlockTable.createItemstack(toolTables, BlockToolTable.TableTypes.StencilTable.meta, Blocks.MELON_BLOCK, 0),
                           "P", "B", 'P', pattern, 'B', Blocks.MELON_BLOCK);

    // Part Builder
    GameRegistry.addRecipe(
        new TableRecipe(OreDictionary.getOres("logWood"), toolTables, BlockToolTable.TableTypes.PartBuilder.meta, "P",
                        "B", 'P', pattern, 'B', "logWood"));
    GameRegistry.addRecipe(BlockTable.createItemstack(toolTables, BlockToolTable.TableTypes.PartBuilder.meta, TinkerCommons.blockFirewood, TinkerCommons.firewood.getMetadata()),
                           "P", "B", 'P', pattern, 'B', TinkerCommons.firewood);
    GameRegistry.addRecipe(BlockTable.createItemstack(toolTables, BlockToolTable.TableTypes.PartBuilder.meta, Blocks.GOLDEN_RAIL, 0),
                           "P", "B", 'P', pattern, 'B', Blocks.GOLDEN_RAIL);
    GameRegistry.addRecipe(BlockTable.createItemstack(toolTables, BlockToolTable.TableTypes.PartBuilder.meta, Blocks.CACTUS, 0),
                           "P", "B", 'P', pattern, 'B', Blocks.CACTUS);

    // Pattern Chest
    ItemStack patternChest = new ItemStack(toolTables, 1, BlockToolTable.TableTypes.PatternChest.meta);
    ItemStack partChest = new ItemStack(toolTables, 1, BlockToolTable.TableTypes.PartChest.meta);
    GameRegistry.addRecipe(new ShapedOreRecipe(patternChest,
                                               "P", "B", 'P', pattern, 'B', "chestWood"));
    GameRegistry.addRecipe(new ShapedOreRecipe(patternChest,
                                               "BBB",
                                               "BPB",
                                               "BBB", 'P', pattern, 'B', "plankWood"));
    // Part Chest
    GameRegistry.addRecipe(new ShapedOreRecipe(partChest,
                                               " P ",
                                               "SCS",
                                               " B ", 'C', "chestWood", 'S', "stickWood", 'B', "plankWood", 'P', pattern));

    // Tool Station
    GameRegistry.addRecipe(
        new ShapedOreRecipe(new ItemStack(toolTables, 1, BlockToolTable.TableTypes.ToolStation.meta),
                            "P", "B", 'P', pattern, 'B', "workbench"));

    // Materials
    ItemStack slimeBallBlue = TinkerCommons.matSlimeBallBlue;
    ItemStack slimeBallPurple = TinkerCommons.matSlimeBallPurple;
    ItemStack slimeBallMagma = TinkerCommons.matSlimeBallMagma;
    ItemStack knightSlime = TinkerCommons.ingotKnightSlime;
    // blue slimeball has a recipe if world isn't present
    if(!isWorldLoaded()) {
      GameRegistry.addRecipe(new ShapelessOreRecipe(slimeBallBlue, Items.SLIME_BALL, "dyeBlue"));
      GameRegistry.addRecipe(new ShapelessOreRecipe(slimeBallPurple, Items.SLIME_BALL, "dyePurple"));
      GameRegistry.addRecipe(new ShapelessOreRecipe(slimeBallMagma, Items.SLIME_BALL, "dyeOrange"));
    }
    if(!isSmelteryLoaded()) {
      GameRegistry.addRecipe(new ShapelessOreRecipe(knightSlime, slimeBallPurple, "ingotIron", "stone"));
      // extra utility recipe if both are not loaded
      if(!isWorldLoaded()) {
        GameRegistry.addRecipe(new ShapelessOreRecipe(knightSlime, Items.SLIME_BALL, "dyePurple", "ingotIron", "stone"));
      }

      // cobalt ardite manyullyn
      GameRegistry.addSmelting(TinkerCommons.oreArdite, TinkerCommons.ingotArdite, 1.0f);
      GameRegistry.addSmelting(TinkerCommons.oreCobalt, TinkerCommons.ingotCobalt, 1.0f);

      GameRegistry.addShapelessRecipe(TinkerCommons.ingotManyullyn, TinkerCommons.ingotCobalt, TinkerCommons.ingotArdite, Blocks.COAL_BLOCK);
      GameRegistry.addShapelessRecipe(TinkerCommons.nuggetManyullyn, TinkerCommons.nuggetCobalt, TinkerCommons.nuggetArdite, Items.COAL);

      // pigiron
      ItemStack pigiron = TinkerCommons.ingotPigIron.copy();
      pigiron.stackSize = 3;
      GameRegistry.addRecipe(new ShapelessOreRecipe(pigiron, "ingotIron", "ingotIron", "ingotIron", Items.PORKCHOP, Items.PORKCHOP, Items.PORKCHOP, "gemEmerald"));
    }

    // Expander items for the Harvest-Width/Height modifier
    GameRegistry.addRecipe(new ShapedOreRecipe(TinkerCommons.matExpanderW,
                                               " L ",
                                               "PSP",
                                               " L ",
                                               'L', "gemLapis",
                                               'P', Blocks.PISTON,
                                               'S', slimeBallPurple));

    GameRegistry.addRecipe(new ShapedOreRecipe(TinkerCommons.matExpanderH,
                                               " P ",
                                               "LSL",
                                               " P ",
                                               'L', "gemLapis",
                                               'P', Blocks.PISTON,
                                               'S', slimeBallPurple));

    // silky cloth/jewel
    GameRegistry.addRecipe(new ShapedOreRecipe(TinkerCommons.matSilkyCloth,
                                               "CCC",
                                               "CGC",
                                               "CCC",
                                               'C', Items.STRING,
                                               'G', "ingotGold"));
    GameRegistry.addRecipe(new ShapedOreRecipe(TinkerCommons.matSilkyJewel,
                                               " C ",
                                               "CEC",
                                               " C ",
                                               'C', TinkerCommons.matSilkyCloth,
                                               'E', "gemEmerald"));

    // Reinforcement item
    String goldThing = "ingotGold";
    if(TinkerSmeltery.cast != null) {
      goldThing = "cast";
    }
    GameRegistry.addRecipe(new ShapedOreRecipe(TinkerCommons.matReinforcement,
                                               "OOO",
                                               "OPO",
                                               "OOO",
                                               'O', "obsidian",
                                               'P', goldThing));

    // Moss
    GameRegistry.addRecipe(new ShapedOreRecipe(TinkerCommons.matMoss, "xxx", "xxx", "xxx", 'x', "blockMossy"));

    // Slimy Mud
    GameRegistry.addRecipe(new ShapelessOreRecipe(TinkerCommons.slimyMudGreen, Items.SLIME_BALL, Items.SLIME_BALL, Items.SLIME_BALL, Items.SLIME_BALL, "sand", "dirt"));
    GameRegistry.addRecipe(new ShapelessOreRecipe(TinkerCommons.slimyMudBlue, slimeBallBlue, slimeBallBlue, slimeBallBlue, slimeBallBlue, "sand", "dirt"));
    GameRegistry.addRecipe(new ShapelessOreRecipe(TinkerCommons.slimyMudMagma, slimeBallMagma, Items.MAGMA_CREAM, slimeBallMagma, Items.MAGMA_CREAM, Blocks.SOUL_SAND, Blocks.NETHERRACK));

    // recipies using congealed slime blocks
    ItemStack congealed = new ItemStack(TinkerCommons.blockSlimeCongealed, 0, BlockSlime.SlimeType.GREEN.meta);
    GameRegistry.addRecipe(new ShapelessOreRecipe(TinkerCommons.slimyMudGreen, congealed, "sand", "dirt"));
    congealed = new ItemStack(TinkerCommons.blockSlimeCongealed, 0, BlockSlime.SlimeType.BLUE.meta);
    GameRegistry.addRecipe(new ShapelessOreRecipe(TinkerCommons.slimyMudBlue, congealed, "sand", "dirt"));

    // Slime crystals
    FurnaceRecipes.instance().addSmeltingRecipe(TinkerCommons.slimyMudGreen, TinkerCommons.matSlimeCrystalGreen, 0);
    FurnaceRecipes.instance().addSmeltingRecipe(TinkerCommons.slimyMudBlue, TinkerCommons.matSlimeCrystalBlue, 0);
    FurnaceRecipes.instance().addSmeltingRecipe(TinkerCommons.slimyMudMagma, TinkerCommons.matSlimeCrystalMagma, 0);

    // lavawood if needed
    if(!isSmelteryLoaded()) {
      GameRegistry.addRecipe(new ShapedOreRecipe(TinkerCommons.lavawood,
                                                 " B ", "BWB", " B ",
                                                 'B', "plankWood",
                                                 'W', Items.LAVA_BUCKET));
    }


    CraftingManager.getInstance().addRecipe(new RepairRecipe());
  }

  public static void registerToolForgeBlock(String oredict) {
    if(toolForge != null) {
      toolForge.baseBlocks.add(oredict);
      registerToolForgeRecipe(oredict);
    }
  }

  private static void registerToolForgeRecipe(String oredict) {
    Block brick = TinkerSmeltery.searedBlock;
    if(brick == null) {
      brick = Blocks.STONEBRICK;
    }
    GameRegistry
        .addRecipe(new TableRecipe(OreDictionary.getOres(oredict), toolForge, 0,
                                   "BBB",
                                   "MTM",
                                   "M M",
                                   'B', brick,
                                   'M', oredict,
                                   'T', new ItemStack(toolTables, 1, BlockToolTable.TableTypes.ToolStation.meta)));
  }

  // POST-INITIALIZATION
  @Override
  @Subscribe
  public void postInit(FMLPostInitializationEvent event) {
    super.postInit(event);

    proxy.postInit();
  }

  @Override
  protected void registerEventHandlers() {
    // prevents tools from despawning
    MinecraftForge.EVENT_BUS.register(IndestructibleEntityItem.EventHandler.instance);
    MinecraftForge.EVENT_BUS.register(new TraitEvents());
    MinecraftForge.EVENT_BUS.register(new ToolEvents());
    MinecraftForge.EVENT_BUS.register(DualToolHarvestUtils.INSTANCE);
  }
}

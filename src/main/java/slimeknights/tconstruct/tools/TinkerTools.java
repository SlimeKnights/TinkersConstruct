package slimeknights.tconstruct.tools;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.apache.logging.log4j.Logger;

import java.util.List;

import slimeknights.mantle.item.ItemBlockMeta;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.common.CommonProxy;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.ToolMaterialStats;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.Pattern;
import slimeknights.tconstruct.library.tools.Shard;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolPart;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.BlockTable;
import slimeknights.tconstruct.shared.tileentity.TileTable;
import slimeknights.tconstruct.tools.block.BlockSlimeSand;
import slimeknights.tconstruct.tools.block.BlockToolForge;
import slimeknights.tconstruct.tools.block.BlockToolTable;
import slimeknights.tconstruct.tools.item.BattleAxe;
import slimeknights.tconstruct.tools.item.BattleSign;
import slimeknights.tconstruct.tools.item.BroadSword;
import slimeknights.tconstruct.tools.item.Cleaver;
import slimeknights.tconstruct.tools.item.Excavator;
import slimeknights.tconstruct.tools.item.FryPan;
import slimeknights.tconstruct.tools.item.Hammer;
import slimeknights.tconstruct.tools.item.Hatchet;
import slimeknights.tconstruct.tools.item.ItemBlockTable;
import slimeknights.tconstruct.tools.item.LongSword;
import slimeknights.tconstruct.tools.item.LumberAxe;
import slimeknights.tconstruct.tools.item.Mattock;
import slimeknights.tconstruct.tools.item.Pickaxe;
import slimeknights.tconstruct.tools.item.Scythe;
import slimeknights.tconstruct.tools.item.Shovel;
import slimeknights.tconstruct.tools.modifiers.ModDiamond;
import slimeknights.tconstruct.tools.modifiers.ModFortify;
import slimeknights.tconstruct.tools.modifiers.ModHarvestSize;
import slimeknights.tconstruct.tools.modifiers.ModHaste;
import slimeknights.tconstruct.tools.modifiers.ModLuck;
import slimeknights.tconstruct.tools.modifiers.ModSharpness;
import slimeknights.tconstruct.tools.tileentity.TileCraftingStation;
import slimeknights.tconstruct.tools.tileentity.TilePartBuilder;
import slimeknights.tconstruct.tools.tileentity.TilePatternChest;
import slimeknights.tconstruct.tools.tileentity.TileStencilTable;
import slimeknights.tconstruct.tools.tileentity.TileToolForge;
import slimeknights.tconstruct.tools.tileentity.TileToolStation;

@Pulse(id = TinkerTools.PulseId, description = "All the tools and everything related to it.")
public class TinkerTools extends TinkerPulse {

  public static final String PulseId = "TinkerTools";
  static final Logger log = Util.getLogger(PulseId);

  @SidedProxy(clientSide = "slimeknights.tconstruct.tools.ToolClientProxy", serverSide = "slimeknights.tconstruct.common.CommonProxy")
  public static CommonProxy proxy;

  // Blocks
  public static BlockToolTable toolTables;
  public static BlockToolForge toolForge;
  public static BlockSlimeSand slimeSand;

  // General Items
  public static Pattern pattern;
  public static Shard shard;
  public static Item materials;

  // Tools
  public static ToolCore pickaxe;
  public static ToolCore shovel;
  public static ToolCore hatchet;
  public static ToolCore mattock;
  public static ToolCore broadSword;
  public static ToolCore longSword;
  public static ToolCore rapier;
  public static ToolCore cutlass;
  public static ToolCore dagger;
  public static ToolCore fryPan;
  public static ToolCore battleSign;

  public static ToolCore hammer;
  public static ToolCore excavator;
  public static ToolCore lumberAxe;
  public static ToolCore cleaver;
  public static ToolCore battleAxe;
  public static ToolCore scythe;

  // Tool Parts
  public static ToolPart pickHead;
  public static ToolPart shovelHead;
  public static ToolPart axeHead;
  public static ToolPart broadAxeHead;
  public static ToolPart swordBlade;
  public static ToolPart largeSwordBlade;
  public static ToolPart hammerHead;
  public static ToolPart excavatorHead;
  public static ToolPart panHead;
  public static ToolPart signHead;

  public static ToolPart toolRod;
  public static ToolPart toughToolRod;
  public static ToolPart binding;
  public static ToolPart toughBinding;
  public static ToolPart wideGuard;
  public static ToolPart largePlate;

  // Modifiers
  public static Modifier modDiamond;
  public static Modifier modHaste;
  public static Modifier modHarvestWidth;
  public static Modifier modHarvestHeight;
  public static Modifier modLuck;
  public static Modifier modSharpness;

  public static List<Modifier> fortifyMods;

  // Helper stuff
  static List<ToolCore> tools = Lists.newLinkedList(); // contains all tools registered in this pulse
  static List<ToolPart> toolparts = Lists.newLinkedList(); // ^ all toolparts

  // PRE-INITIALIZATION
  @Subscribe
  public void preInit(FMLPreInitializationEvent event) {
    // register items
    pattern = registerItem(new Pattern(), "pattern");

    shard = registerItem(new Shard(), "shard");

    registerToolParts();
    registerTools();
    registerModifiers();

    // register blocks
    toolTables = registerBlock(new BlockToolTable(), ItemBlockTable.class, "tooltables");
    toolForge = registerBlock(new BlockToolForge(), ItemBlockTable.class, "toolforge");
    slimeSand = registerBlock(new BlockSlimeSand(), ItemBlockMeta.class, "slimesand");

    ItemBlockMeta.setMappingProperty(toolTables, BlockToolTable.TABLES);
    ItemBlockMeta.setMappingProperty(slimeSand, BlockSlimeSand.TYPE);

    registerTE(TileTable.class, "table");
    registerTE(TileCraftingStation.class, "craftingstation");
    registerTE(TileStencilTable.class, "stenciltable");
    registerTE(TilePartBuilder.class, "partbuilder");
    registerTE(TilePatternChest.class, "patternchest");
    registerTE(TileToolStation.class, "toolstation");
    registerTE(TileToolForge.class, "toolforge");

    oredict();

    proxy.preInit();

    // set shard
    TinkerRegistry.setShardItem(shard);
  }

  private void registerToolParts() {
    // The order the items are registered in represents the order in the stencil table GUI too
    pickHead = registerToolPart(new ToolPart(Material.VALUE_Ingot*2), "pick_head");
    shovelHead = registerToolPart(new ToolPart(Material.VALUE_Ingot*2), "shovel_head");
    axeHead = registerToolPart(new ToolPart(Material.VALUE_Ingot*2), "axe_head");
    broadAxeHead = registerToolPart(new ToolPart(Material.VALUE_Ingot * 8), "broad_axe_head");
    swordBlade = registerToolPart(new ToolPart(Material.VALUE_Ingot*2), "sword_blade");
    largeSwordBlade = registerToolPart(new ToolPart(Material.VALUE_Ingot * 8), "large_sword_blade");
    hammerHead = registerToolPart(new ToolPart(Material.VALUE_Ingot * 8), "hammer_head");
    excavatorHead = registerToolPart(new ToolPart(Material.VALUE_Ingot * 8), "excavator_head");
    panHead = registerToolPart(new ToolPart(Material.VALUE_Ingot), "pan_head");
    signHead = registerToolPart(new ToolPart(Material.VALUE_Ingot), "sign_head");

    toolRod = registerToolPart(new ToolPart(Material.VALUE_Shard), "tool_rod");
    toughToolRod = registerToolPart(new ToolPart(Material.VALUE_Ingot * 3), "tough_tool_rod");
    binding = registerToolPart(new ToolPart(Material.VALUE_Shard), "binding");
    toughBinding = registerToolPart(new ToolPart(Material.VALUE_Ingot * 3), "tough_binding");

    wideGuard = registerToolPart(new ToolPart(Material.VALUE_Shard), "wide_guard");

    largePlate = registerToolPart(new ToolPart(Material.VALUE_Shard * 8), "large_plate");
  }

  private void registerTools() {
    pickaxe = registerTool(new Pickaxe(), "pickaxe");
    shovel = registerTool(new Shovel(), "shovel");
    hatchet = registerTool(new Hatchet(), "hatchet");
    mattock = registerTool(new Mattock(), "mattock");
    broadSword = registerTool(new BroadSword(), "broadsword");
    longSword = registerTool(new LongSword(), "longsword");
    // rapier
    // cutlass
    // dagger
    fryPan = registerTool(new FryPan(), "frypan");
    battleSign = registerTool(new BattleSign(), "battlesign");

    hammer = registerTool(new Hammer(), "hammer");
    excavator = registerTool(new Excavator(), "excavator");
    lumberAxe = registerTool(new LumberAxe(), "lumberaxe");
    cleaver = registerTool(new Cleaver(), "cleaver");
    battleAxe = registerTool(new BattleAxe(), "battleaxe");
    scythe = registerTool(new Scythe(), "scythe");
  }

  private void registerModifiers() {
    // create the modifiers and add their items
    modDiamond = new ModDiamond();
    modDiamond.addItem("gemDiamond");

    modHaste = new ModHaste(50);
    modHaste.addItem("dustRedstone");
    modHaste.addItem("blockRedstone", 1, 9);

    modHarvestWidth = new ModHarvestSize("width");
    modHarvestWidth.addItem(TinkerCommons.matExpanderW, 1, 1);

    modHarvestHeight = new ModHarvestSize("height");
    modHarvestHeight.addItem(TinkerCommons.matExpanderH, 1, 1);

    modLuck = new ModLuck();
    modLuck.addItem("gemLapis");
    modLuck.addItem("blockLapis", 1, 9);

    modSharpness = new ModSharpness(24);
    modSharpness.addItem("gemQuartz");
    modSharpness.addItem("blockQuartz", 1, 4);
  }

  private void oredict() {

  }

  // INITIALIZATION
  @Subscribe
  public void init(FMLInitializationEvent event) {
    registerToolBuilding();
    registerRecipies();

    proxy.init();
  }

  private void registerToolBuilding() {
    TinkerRegistry.registerToolCrafting(pickaxe);
    TinkerRegistry.registerToolCrafting(shovel);
    TinkerRegistry.registerToolCrafting(hatchet);
    TinkerRegistry.registerToolCrafting(mattock);
    TinkerRegistry.registerToolCrafting(broadSword);
    TinkerRegistry.registerToolCrafting(longSword);
    TinkerRegistry.registerToolCrafting(fryPan);
    TinkerRegistry.registerToolCrafting(battleSign);

    TinkerRegistry.registerToolForgeCrafting(hammer);
    TinkerRegistry.registerToolForgeCrafting(excavator);
    TinkerRegistry.registerToolForgeCrafting(lumberAxe);
    TinkerRegistry.registerToolForgeCrafting(cleaver);
    TinkerRegistry.registerToolForgeCrafting(battleAxe);
    TinkerRegistry.registerToolForgeCrafting(scythe);
  }

  private void registerRecipies() {
    // Pattern
    ItemStack pattern = new ItemStack(TinkerTools.pattern, 4);
    GameRegistry.addRecipe(new ShapedOreRecipe(pattern, "PS", "SP", 'P', "plankWood", 'S', "stickWood"));
    GameRegistry.addRecipe(new ShapedOreRecipe(pattern, "SP", "PS", 'P', "plankWood", 'S', "stickWood"));
    pattern.stackSize = 1;

    // Crafting Station
    GameRegistry.addRecipe(
        new ShapelessOreRecipe(new ItemStack(toolTables, 1, BlockToolTable.TableTypes.CraftingStation.meta),
                               "workbench"));
    // Stencil Table
    GameRegistry.addRecipe(
        new TableRecipe(OreDictionary.getOres("plankWood"), toolTables, BlockToolTable.TableTypes.StencilTable.meta,
                        "P", "B", 'P', pattern, 'B', "plankWood"));
    GameRegistry.addRecipe(BlockTable
                               .createItemstack(toolTables, BlockToolTable.TableTypes.StencilTable.meta, Blocks.rail, 0),
                           "P", "B", 'P', pattern, 'B', Blocks.rail);
    GameRegistry.addRecipe(BlockTable
                               .createItemstack(toolTables, BlockToolTable.TableTypes.StencilTable.meta, Blocks.melon_block, 0),
                           "P", "B", 'P', pattern, 'B', Blocks.melon_block);

    // Part Builder
    GameRegistry.addRecipe(
        new TableRecipe(OreDictionary.getOres("logWood"), toolTables, BlockToolTable.TableTypes.PartBuilder.meta, "P",
                        "B", 'P', pattern, 'B', "logWood"));
    GameRegistry.addRecipe(BlockTable
                               .createItemstack(toolTables, BlockToolTable.TableTypes.PartBuilder.meta, Blocks.golden_rail, 0),
                           "P", "B", 'P', pattern, 'B', Blocks.rail);
    GameRegistry.addRecipe(BlockTable
                               .createItemstack(toolTables, BlockToolTable.TableTypes.PartBuilder.meta, Blocks.cactus, 0),
                           "P", "B", 'P', pattern, 'B', Blocks.cactus);

    // Pattern Chest
    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(toolTables, 1, BlockToolTable.TableTypes.PatternChest.meta),
                                               "P", "B", 'P', pattern, 'B', "chestWood"));

    // Tool Station
    GameRegistry.addRecipe(
        new ShapedOreRecipe(new ItemStack(toolTables, 1, BlockToolTable.TableTypes.ToolStation.meta),
                            "P", "B", 'P', pattern, 'B', "workbench"));
    // Tool Forge
    registerToolForgeBlock("blockIron");
    registerToolForgeBlock("blockGold");

    // Materials
    ItemStack slimeBallBlue = TinkerCommons.matSlimeBallBlue;
    ItemStack slimeBallPurple = TinkerCommons.matSlimeBallPurple;
    ItemStack knightSlime = TinkerCommons.ingotKnightSlime;
    // blue slimeball has a recipe if world isn't present
    if(!isWorldLoaded()) {
      GameRegistry.addRecipe(new ShapelessOreRecipe(slimeBallBlue, Items.slime_ball, "dyeBlue"));
      GameRegistry.addRecipe(new ShapelessOreRecipe(slimeBallPurple, Items.slime_ball, "dyePurple"));
    }
    if(!isSmelteryLoaded()) {
      GameRegistry.addRecipe(new ShapelessOreRecipe(knightSlime, slimeBallPurple, "ingotIron", "stone"));
      // extra utility recipe if both are not loaded
      if(!isWorldLoaded()) {
        GameRegistry.addRecipe(new ShapelessOreRecipe(knightSlime, Items.slime_ball, "dyePurple", "ingotIron", "stone"));
      }

      GameRegistry.addShapelessRecipe(TinkerCommons.ingotManyullyn, TinkerCommons.ingotCobalt, TinkerCommons.ingotArdite, Blocks.coal_block);
      GameRegistry.addShapelessRecipe(TinkerCommons.nuggetManyullyn, TinkerCommons.nuggetCobalt, TinkerCommons.nuggetArdite, Items.coal);
    }

    // Expander items for the Harvest-Width/Height modifier
    GameRegistry.addRecipe(new ShapedOreRecipe(TinkerCommons.matExpanderW,
                                               " L ",
                                               "PSP",
                                               " L ",
                                               'L', "gemLapis",
                                               'P', Blocks.piston,
                                               'S', slimeBallPurple));

    GameRegistry.addRecipe(new ShapedOreRecipe(TinkerCommons.matExpanderH,
                                               " P ",
                                               "LSL",
                                               " P ",
                                               'L', "gemLapis",
                                               'P', Blocks.piston,
                                               'S', slimeBallPurple));

    // Slime Sand
    GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(slimeSand, 1, 0), Items.slime_ball, Items.slime_ball, Items.slime_ball, Items.slime_ball, "sand", "dirt"));
    GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(slimeSand, 1, 1), slimeBallBlue, slimeBallBlue, slimeBallBlue, slimeBallBlue, "sand", "dirt"));

    // Slime crystals
    FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(slimeSand, 1, 0), TinkerCommons.matSlimeCrystal, 0);
    FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(slimeSand, 1, 1), TinkerCommons.matSlimeCrystalBlue, 0);
  }

  public static void registerToolForgeBlock(String oredict) {
    toolForge.baseBlocks.add(oredict);
    registerToolForgeRecipe(oredict);
  }

  private static void registerToolForgeRecipe(String oredict) {
    // todo: change recipe to brick vs. smeltery-bricks wether smeltery pulse is active
    GameRegistry
        .addRecipe(new TableRecipe(OreDictionary.getOres(oredict), toolForge, 0,
                                   "BBB",
                                   "MTM",
                                   "M M",
                                   'B', Blocks.stonebrick,
                                   'M', oredict,
                                   'T', new ItemStack(toolTables, 1, BlockToolTable.TableTypes.ToolStation.meta)));
  }

  // POST-INITIALIZATION
  @Subscribe
  public void postInit(FMLPostInitializationEvent event) {
    registerFortifyModifiers();

    proxy.postInit();

    MinecraftForge.EVENT_BUS.register(new TraitEvents());
    MinecraftForge.EVENT_BUS.register(new ToolEvents());
    MinecraftForge.EVENT_BUS.register(battleSign); // battlesign events
  }

  private void registerFortifyModifiers() {
    fortifyMods = Lists.newArrayList();
    for(Material mat : TinkerRegistry.getAllMaterials()) {
      if(mat.hasStats(ToolMaterialStats.TYPE)) {
        fortifyMods.add(new ModFortify(mat));
      }
    }
  }

  private static <T extends ToolCore> T registerTool(T item, String unlocName) {
    tools.add(item);
    return registerItem(item, unlocName);
  }

  private ToolPart registerToolPart(ToolPart part, String name) {
    ToolPart ret = registerItem(part, name);

    ItemStack stencil = new ItemStack(pattern);
    Pattern.setTagForPart(stencil, part);
    TinkerRegistry.registerStencilTableCrafting(stencil);

    toolparts.add(ret);

    return ret;
  }
}

package slimeknights.tconstruct.tools;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.apache.logging.log4j.Logger;

import java.util.List;

import mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.shared.block.BlockTable;
import slimeknights.mantle.item.ItemBlockMeta;
import slimeknights.tconstruct.shared.tileentity.TileTable;
import slimeknights.tconstruct.common.CommonProxy;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.ToolMaterialStats;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.Pattern;
import slimeknights.tconstruct.library.tools.Shard;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolPart;
import slimeknights.tconstruct.shared.TinkerCommons;
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
import slimeknights.tconstruct.tools.item.Shovel;
import slimeknights.tconstruct.tools.modifiers.ModDiamond;
import slimeknights.tconstruct.tools.modifiers.ModFortify;
import slimeknights.tconstruct.tools.modifiers.ModHarvestSize;
import slimeknights.tconstruct.tools.modifiers.ModHaste;
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
  public static ToolPart hammerHead;

  public static ToolPart toolRod;
  public static ToolPart toughToolRod;
  public static ToolPart binding;
  public static ToolPart toughBinding;
  public static ToolPart wideGuard;
  public static ToolPart largePlate;

  // Modifiers
  public static IModifier diamondMod;
  public static IModifier fortifyMod;
  public static IModifier redstoneMod;
  public static IModifier harvestWidth;
  public static IModifier harvestHeight;

  // Helper stuff
  static List<ToolCore> tools = Lists.newLinkedList(); // contains all tools registered in this pulse
  static List<ToolPart> toolparts = Lists.newLinkedList(); // ^ all toolparts

  // PRE-INITIALIZATION
  @Subscribe
  public void preInit(FMLPreInitializationEvent event) {
    // register items
    pattern = registerItem(new Pattern(), "Pattern");

    shard = registerItem(new Shard(), "Shard");

    registerToolParts();
    registerTools();
    registerModifiers();

    // register blocks
    toolTables = registerBlock(new BlockToolTable(), ItemBlockTable.class, "ToolTables");
    toolForge = registerBlock(new BlockToolForge(), ItemBlockTable.class, "ToolForge");
    slimeSand = registerBlock(new BlockSlimeSand(), ItemBlockMeta.class, "SlimeSand");

    ItemBlockMeta.setMappingProperty(toolTables, BlockToolTable.TABLES);
    ItemBlockMeta.setMappingProperty(slimeSand, BlockSlimeSand.TYPE);

    registerTE(TileTable.class, "Table");
    registerTE(TileCraftingStation.class, "CraftingStation");
    registerTE(TileStencilTable.class, "StencilTable");
    registerTE(TilePartBuilder.class, "PartBuilder");
    registerTE(TilePatternChest.class, "PatternChest");
    registerTE(TileToolStation.class, "ToolStation");
    registerTE(TileToolForge.class, "ToolForge");

    oredict();

    proxy.preInit();

    // set shard
    TinkerRegistry.setShardItem(shard);
  }

  private void registerToolParts() {
    // The order the items are registered in represents the order in the stencil table GUI too
    pickHead = registerToolPart(new ToolPart(Material.VALUE_Ingot*2), "PickHead");
    shovelHead = registerToolPart(new ToolPart(Material.VALUE_Ingot*2), "ShovelHead");
    axeHead = registerToolPart(new ToolPart(Material.VALUE_Ingot*2), "AxeHead");
    broadAxeHead = registerToolPart(new ToolPart(Material.VALUE_Ingot * 8), "BroadAxeHead");
    swordBlade = registerToolPart(new ToolPart(Material.VALUE_Ingot*2), "SwordBlade");
    hammerHead = registerToolPart(new ToolPart(Material.VALUE_Ingot * 8), "HammerHead");

    toolRod = registerToolPart(new ToolPart(Material.VALUE_Shard), "ToolRod");
    toughToolRod = registerToolPart(new ToolPart(Material.VALUE_Ingot * 3), "ToughToolRod");
    binding = registerToolPart(new ToolPart(Material.VALUE_Shard), "Binding");
    toughBinding = registerToolPart(new ToolPart(Material.VALUE_Ingot * 3), "ToughBinding");

    wideGuard = registerToolPart(new ToolPart(Material.VALUE_Shard), "WideGuard");

    largePlate = registerToolPart(new ToolPart(Material.VALUE_Shard * 8), "LargePlate");
  }

  private void registerTools() {
    pickaxe = registerTool(new Pickaxe(), "Pickaxe");
    shovel = registerTool(new Shovel(), "Shovel");
    hatchet = registerTool(new Hatchet(), "Hatchet");
    mattock = registerTool(new Mattock(), "Mattock");
    broadSword = registerTool(new BroadSword(), "BroadSword");
    longSword = registerTool(new LongSword(), "LongSword");
    // rapier
    // cutlass
    // dagger
    fryPan = registerTool(new FryPan(), "FryPan");
    battleSign = registerTool(new BattleSign(), "BattleSign");

    hammer = registerTool(new Hammer(), "Hammer");
    excavator = registerTool(new Excavator(), "Excavator");
    lumberAxe = registerTool(new LumberAxe(), "LumberAxe");
    cleaver = registerTool(new Cleaver(), "Cleaver");
    battleAxe = registerTool(new BattleAxe(), "BattleAxe");
    // scythe
  }

  private void registerModifiers() {
    diamondMod = new ModDiamond();
    redstoneMod = new ModHaste(50);

    Modifier harvestWidth = new ModHarvestSize("width");
    Modifier harvestHeight = new ModHarvestSize("height");

    // todo: proper items
    harvestWidth.addItem(Items.arrow, 1, 1);
    harvestHeight.addItem(Blocks.ladder, 1);

    TinkerTools.harvestWidth = harvestWidth;
    TinkerTools.harvestHeight = harvestHeight;

    // todo: fix
    fortifyMod = new Modifier("Fortify") {

      @Override
      public void updateNBT(NBTTagCompound modifierTag) {

      }

      @Override
      public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {

      }


      @SideOnly(Side.CLIENT)
      @Override
      public boolean hasTexturePerMaterial() {
        return true;
      }
    };
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

    TinkerRegistry.registerToolForgeCrafting(hammer);
  }

  private void registerRecipies() {
    // Pattern
    ItemStack pattern = new ItemStack(TinkerTools.pattern);
    GameRegistry.addRecipe(new ShapedOreRecipe(pattern, "PS", "SP", 'P', "plankWood", 'S', "stickWood"));
    GameRegistry.addRecipe(new ShapedOreRecipe(pattern, "SP", "PS", 'P', "plankWood", 'S', "stickWood"));

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


    ItemStack slimeBallBlue = TinkerCommons.matSlimeBallBlue;
    // blue slimeball has a recipe if world isn't present
    if(!isWorldLoaded()) {
      GameRegistry.addRecipe(new ShapelessOreRecipe(slimeBallBlue, Items.slime_ball, "dyeBlue"));
    }

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
    proxy.postInit();

    registerFortifyModifiers();

    MinecraftForge.EVENT_BUS.register(new TraitEvents());
    MinecraftForge.EVENT_BUS.register(new ToolEvents());
    MinecraftForge.EVENT_BUS.register(battleSign); // battlesign events
  }

  private void registerFortifyModifiers() {
    for(Material mat : TinkerRegistry.getAllMaterials()) {
      if(mat.hasStats(ToolMaterialStats.TYPE)) {
        new ModFortify(mat);
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

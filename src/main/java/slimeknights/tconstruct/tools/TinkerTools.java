package slimeknights.tconstruct.tools;

import com.google.common.collect.Lists;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
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

import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.mantle.block.BlockTable;
import slimeknights.mantle.item.ItemBlockMeta;
import slimeknights.mantle.tileentity.TileTable;
import slimeknights.tconstruct.library.tinkering.MaterialItem;
import slimeknights.tconstruct.library.tools.ToolPart;
import slimeknights.tconstruct.tools.block.BlockToolTable;
import slimeknights.tconstruct.tools.debug.TempToolModifying;
import slimeknights.tconstruct.tools.item.Hatchet;
import slimeknights.tconstruct.tools.item.Pickaxe;
import slimeknights.tconstruct.tools.item.BroadSword;
import slimeknights.tconstruct.tools.tileentity.TileCraftingStation;
import slimeknights.tconstruct.common.CommonProxy;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.tools.block.BlockToolForge;
import slimeknights.tconstruct.tools.debug.TempToolCrafting;
import slimeknights.tconstruct.library.tools.Pattern;
import slimeknights.tconstruct.tools.item.Shard;
import slimeknights.tconstruct.tools.modifiers.ModDiamond;
import slimeknights.tconstruct.tools.modifiers.ModHaste;
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

  // General Items
  public static Pattern pattern;
  public static MaterialItem shard;

  // Tools
  public static ToolCore pickaxe;
  public static ToolCore hatchet;
  public static ToolCore broadSword;

  // Tool Parts
  public static ToolPart pickHead;
  public static ToolPart axeHead;
  public static ToolPart swordBlade;

  public static ToolPart toolRod;
  public static ToolPart binding;
  public static ToolPart wideGuard;
  public static ToolPart largePlate;

  // Modifiers
  public static IModifier diamondMod;
  public static IModifier fortifyMod;
  public static IModifier redstoneMod;

  // Helper stuff
  static List<ToolCore> tools = Lists.newLinkedList(); // contains all tools registered in this pulse
  static List<ToolPart> toolparts = Lists.newLinkedList(); // ^ all toolparts

  // PRE-INITIALIZATION
  @Handler
  public void preInit(FMLPreInitializationEvent event) {
    // register items
    pattern = registerItem(new Pattern(), "Pattern");

    shard = registerItem(new Shard(), "Shard");

    registerToolParts();
    registerTools();
    registerModifiers();

    // register blocks
    toolTables = registerBlock(new BlockToolTable(), ItemBlockMeta.class, "ToolTables");
    registerTE(TileTable.class, "Table");
    registerTE(TileCraftingStation.class, "CraftingStation");
    registerTE(TileStencilTable.class, "StencilTable");
    registerTE(TilePartBuilder.class, "PartBuilder");
    registerTE(TilePatternChest.class, "PatternChest");
    registerTE(TileToolStation.class, "ToolStation");
    registerTE(TileToolForge.class, "ToolForge");

    toolForge = registerBlock(new BlockToolForge(), ItemBlockMeta.class, "ToolForge");

    proxy.preInit();


    // debug things
    GameRegistry.addRecipe(new TempToolCrafting());
    GameRegistry.addRecipe(new TempToolModifying());
  }

  private void registerToolParts() {
    // The order the items are registered in represents the order in the stencil table GUI too
    pickHead = registerToolPart(new ToolPart(TinkerMaterials.VALUE_Ingot*2), "PickHead");
    axeHead = registerToolPart(new ToolPart(TinkerMaterials.VALUE_Ingot*2), "AxeHead");
    swordBlade = registerToolPart(new ToolPart(TinkerMaterials.VALUE_Ingot*2), "SwordBlade");

    toolRod = registerToolPart(new ToolPart(TinkerMaterials.VALUE_Shard), "ToolRod");
    binding = registerToolPart(new ToolPart(TinkerMaterials.VALUE_Shard), "Binding");

    wideGuard = registerToolPart(new ToolPart(TinkerMaterials.VALUE_Shard), "WideGuard");

    largePlate = registerToolPart(new ToolPart(TinkerMaterials.VALUE_Shard*8), "LargePlate");
  }

  private void registerTools() {
    pickaxe = registerTool(new Pickaxe(), "Pickaxe");
    hatchet = registerTool(new Hatchet(), "Hatchet");
    broadSword = registerTool(new BroadSword(), "BroadSword");
  }

  private void registerModifiers() {
    diamondMod = new ModDiamond();
    redstoneMod = new ModHaste(50);

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

  // INITIALIZATION
  @Handler
  public void init(FMLInitializationEvent event) {
    registerToolBuilding();
    registerRecipies();

    proxy.init();
  }

  private void registerToolBuilding() {
    TinkerRegistry.registerToolCrafting(pickaxe);
  }

  private void registerRecipies() {
    // todo: remove debug recipe stuff
    ItemStack pattern = new ItemStack(TinkerTools.pattern);

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
    GameRegistry.addRecipe(BlockTable.createItemstack(toolTables, BlockToolTable.TableTypes.StencilTable.meta, Blocks.melon_block, 0),
                           "P", "B", 'P', pattern, 'B', Blocks.melon_block);

    // Part Builder
    GameRegistry.addRecipe(
        new TableRecipe(OreDictionary.getOres("logWood"), toolTables, BlockToolTable.TableTypes.PartBuilder.meta, "P",
                        "B", 'P', pattern, 'B', "logWood"));
    GameRegistry.addRecipe(BlockTable.createItemstack(toolTables, BlockToolTable.TableTypes.PartBuilder.meta, Blocks.golden_rail, 0),
                           "P", "B", 'P', pattern, 'B', Blocks.rail);
    GameRegistry.addRecipe(BlockTable.createItemstack(toolTables, BlockToolTable.TableTypes.PartBuilder.meta, Blocks.cactus, 0),
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
  @Handler
  public void postInit(FMLPostInitializationEvent event) {
    proxy.postInit();

    MinecraftForge.EVENT_BUS.register(new TraitEvents());
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

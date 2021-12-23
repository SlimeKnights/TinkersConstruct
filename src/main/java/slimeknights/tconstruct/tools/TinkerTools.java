package slimeknights.tconstruct.tools;

import com.google.common.eventbus.Subscribe;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Locale;

import org.apache.logging.log4j.Logger;

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
import slimeknights.tconstruct.shared.tileentity.TileTable;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.common.RepairRecipe;
import slimeknights.tconstruct.tools.common.TableRecipeFactory.TableRecipe;
import slimeknights.tconstruct.tools.common.block.BlockToolForge;
import slimeknights.tconstruct.tools.common.block.BlockToolTable;
import slimeknights.tconstruct.tools.common.block.BlockToolTable.TableTypes;
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
  public static ToolPart kamaHead;
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

  @SubscribeEvent
  public void registerBlocks(Register<Block> event) {
    IForgeRegistry<Block> registry = event.getRegistry();

    // register blocks
    toolTables = registerBlock(registry, new BlockToolTable(), "tooltables");
    toolForge = registerBlock(registry, new BlockToolForge(), "toolforge");

    registerTE(TileTable.class, "table");
    registerTE(TileCraftingStation.class, "craftingstation");
    registerTE(TileStencilTable.class, "stenciltable");
    registerTE(TilePartBuilder.class, "partbuilder");
    registerTE(TilePatternChest.class, "patternchest");
    registerTE(TilePartChest.class, "partchest");
    registerTE(TileToolStation.class, "toolstation");
    registerTE(TileToolForge.class, "toolforge");

  }

  @Override
  @SubscribeEvent
  public void registerItems(Register<Item> event) {
    IForgeRegistry<Item> registry = event.getRegistry();

    // register items
    pattern = registerItem(registry, new Pattern(), "pattern");

    shard = registerItem(registry, new Shard(), "shard");
    sharpeningKit = (SharpeningKit) registerToolPart(registry, new SharpeningKit(), "sharpening_kit");
    sharpeningKit.setCreativeTab(TinkerRegistry.tabParts);
    TinkerRegistry.registerToolPart(sharpeningKit);
    TinkerRegistry.registerToolPart(shard);

    super.registerItems(event);

    // register blocks
    toolTables = registerItemBlockProp(registry, new ItemBlockTable(toolTables), BlockToolTable.TABLES);
    toolForge = registerItemBlock(registry, new ItemBlockTable(toolForge));

    // set shard
    TinkerRegistry.setShardItem(shard);

    TinkerRegistry.registerStencilTableCrafting(Pattern.setTagForPart(new ItemStack(pattern), sharpeningKit));
    TinkerRegistry.registerStencilTableCrafting(Pattern.setTagForPart(new ItemStack(pattern), shard));
  }

  @SubscribeEvent
  public void registerEntities(Register<EntityEntry> event) {
    // register entities
    EntityRegistry.registerModEntity(Util.getResource("indestructible"), IndestructibleEntityItem.class, "Indestructible Item", EntityIDs.INDESTRUCTIBLE_ITEM, TConstruct.instance, 32, 5, true);
  }

  @SubscribeEvent
  public void registerRecipes(Register<IRecipe> event) {
    IForgeRegistry<IRecipe> registry = event.getRegistry();

    registry.register(new RepairRecipe());
  }

  @SubscribeEvent
  public void registerModels(ModelRegistryEvent event) {
    proxy.registerModels();
  }

  // PRE-INITIALIZATION
  @Subscribe
  public void preInit(FMLPreInitializationEvent event) {
    proxy.preInit();
  }

  @Override
  protected void registerToolParts(IForgeRegistry<Item> registry) {
    // The order the items are registered in represents the order in the stencil table GUI too
    pickHead = registerToolPart(registry, new ToolPart(Material.VALUE_Ingot * 2), "pick_head");
    shovelHead = registerToolPart(registry, new ToolPart(Material.VALUE_Ingot * 2), "shovel_head");
    axeHead = registerToolPart(registry, new ToolPart(Material.VALUE_Ingot * 2), "axe_head");
    broadAxeHead = registerToolPart(registry, new ToolPart(Material.VALUE_Ingot * 8), "broad_axe_head");
    swordBlade = registerToolPart(registry, new ToolPart(Material.VALUE_Ingot * 2), "sword_blade");
    largeSwordBlade = registerToolPart(registry, new ToolPart(Material.VALUE_Ingot * 8), "large_sword_blade");
    hammerHead = registerToolPart(registry, new ToolPart(Material.VALUE_Ingot * 8), "hammer_head");
    excavatorHead = registerToolPart(registry, new ToolPart(Material.VALUE_Ingot * 8), "excavator_head");
    kamaHead = registerToolPart(registry, new ToolPart(Material.VALUE_Ingot * 2), "kama_head");
    scytheHead = registerToolPart(registry, new ToolPart(Material.VALUE_Ingot * 8), "scythe_head");
    panHead = registerToolPart(registry, new ToolPart(Material.VALUE_Ingot * 3), "pan_head");
    signHead = registerToolPart(registry, new ToolPart(Material.VALUE_Ingot * 3), "sign_head");

    toolRod = registerToolPart(registry, new ToolPart(Material.VALUE_Ingot), "tool_rod");
    toughToolRod = registerToolPart(registry, new ToolPart(Material.VALUE_Ingot * 3), "tough_tool_rod");
    binding = registerToolPart(registry, new ToolPart(Material.VALUE_Ingot), "binding");
    toughBinding = registerToolPart(registry, new ToolPart(Material.VALUE_Ingot * 3), "tough_binding");

    wideGuard = registerToolPart(registry, new ToolPart(Material.VALUE_Ingot), "wide_guard");
    handGuard = registerToolPart(registry, new ToolPart(Material.VALUE_Ingot), "hand_guard");
    crossGuard = registerToolPart(registry, new ToolPart(Material.VALUE_Ingot), "cross_guard");

    largePlate = registerToolPart(registry, new ToolPart(Material.VALUE_Ingot * 8), "large_plate");

    knifeBlade = registerToolPart(registry, new ToolPart(Material.VALUE_Ingot), "knife_blade");

    bowLimb = registerToolPart(registry, new ToolPart(Material.VALUE_Ingot * 3), "bow_limb");
    bowString = registerToolPart(registry, new ToolPart(Material.VALUE_Ingot), "bow_string");

    arrowHead = registerToolPart(registry, new ToolPart(Material.VALUE_Ingot * 2), "arrow_head");
    arrowShaft = registerToolPart(registry, new ToolPart(Material.VALUE_Ingot * 2), "arrow_shaft");
    fletching = registerToolPart(registry, new ToolPart(Material.VALUE_Ingot * 2), "fletching");
    boltCore = (BoltCore) registerToolPart(registry, new BoltCore(Material.VALUE_Ingot * 2), "bolt_core", null);
    toolparts.remove(boltCore);
  }

  // INITIALIZATION
  @Override
  @Subscribe
  public void init(FMLInitializationEvent event) {
    super.init(event);

    registerSmeltingRecipes();
    proxy.init();
  }

  protected void registerSmeltingRecipes() {
    // cobalt ardite manyullyn
    GameRegistry.addSmelting(TinkerCommons.oreArdite, TinkerCommons.ingotArdite, 1.0f);
    GameRegistry.addSmelting(TinkerCommons.oreCobalt, TinkerCommons.ingotCobalt, 1.0f);

    // Slime crystals
    GameRegistry.addSmelting(TinkerCommons.slimyMudGreen, TinkerCommons.matSlimeCrystalGreen, 0.75f);
    GameRegistry.addSmelting(TinkerCommons.slimyMudBlue, TinkerCommons.matSlimeCrystalBlue, 0.75f);
    GameRegistry.addSmelting(TinkerCommons.slimyMudMagma, TinkerCommons.matSlimeCrystalMagma, 0.75f);
  }

  /**
   * Adds a block to the tool Forge
   * @param registry IForgeRegistry to register the recipe
   * @param oredict oredict string for the block to add
   */
  public static void registerToolForgeBlock(IForgeRegistry<IRecipe> registry, String oredict) {
    if(toolForge != null) {
      toolForge.baseBlocks.add(oredict);
      registerToolForgeRecipe(registry, oredict);
    }
  }

  private static void registerToolForgeRecipe(IForgeRegistry<IRecipe> registry, String oredict) {
    // determine the brick we will use
    Block brick = TinkerSmeltery.searedBlock;
    if(brick == null) {
      brick = Blocks.STONEBRICK;
    }

    // create the recipe
    TableRecipe recipe = new TableRecipe(Util.getResource("tool_forge"), new OreIngredient(oredict), new ItemStack(toolForge),
        CraftingHelper.parseShaped("BBB", "MTM", "M M",
            'B', brick,
            'M', oredict,
            'T', new ItemStack(TinkerTools.toolTables, 1, TableTypes.ToolStation.meta)));

    // recipe location is tconstruct:tools/forge/<ore>, for example tconstruct:tools/forge/blockiron
    recipe.setRegistryName(Util.getResource("tools/forge/" + oredict.toLowerCase(Locale.US)));

    registry.register(recipe);
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

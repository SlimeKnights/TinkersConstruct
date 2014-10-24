package tconstruct.tools;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.registry.*;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;
import mantle.pulsar.pulse.*;
import mantle.utils.RecipeRemover;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.*;
import tconstruct.TConstruct;
import tconstruct.achievements.items.CraftAchievementItem;
import tconstruct.common.itemblocks.MetadataItemBlock;
import tconstruct.items.tools.*;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.client.TConstructClientRegistry;
import tconstruct.library.crafting.*;
import tconstruct.library.tools.*;
import tconstruct.library.util.IPattern;
import tconstruct.modifiers.tools.*;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.tools.blocks.*;
import tconstruct.tools.itemblocks.*;
import tconstruct.tools.items.*;
import tconstruct.tools.logic.*;
import tconstruct.util.ItemHelper;
import tconstruct.util.config.PHConstruct;
import tconstruct.weaponry.TinkerWeaponry;
import tconstruct.world.TinkerWorld;
import tconstruct.world.blocks.SoilBlock;
import tconstruct.world.itemblocks.CraftedSoilItemBlock;

import static net.minecraft.util.EnumChatFormatting.*;

@ObjectHolder(TConstruct.modID)
@Pulse(id = "Tinkers' Tools", description = "The main core of the mod! All of the tools, the tables, and the patterns are here.")
public class TinkerTools
{
    /* Proxies for sides, used for graphics processing */
    @SidedProxy(clientSide = "tconstruct.tools.ToolProxyClient", serverSide = "tconstruct.tools.ToolProxyCommon")
    public static ToolProxyCommon proxy;

    // backwards compatibility
    public static Item bowstring;
    public static DynamicToolPart arrowhead;
    public static Item fletching;
    public static ToolCore shortbow;
    public static ToolCore arrow;

    // Crafting blocks
    public static Block toolStationWood;
    public static Block toolStationStone;
    public static Block toolForge;
    public static Block craftingStationWood;
    public static Block craftingSlabWood;
    public static Block furnaceSlab;
    public static Block heldItemBlock;
    public static Block battlesignBlock;

    // Tool parts
    public static Item binding;
    public static Item toughBinding;
    public static Item toughRod;
    public static Item largePlate;
    public static Item pickaxeHead;
    public static Item shovelHead;
    public static Item hatchetHead;
    public static Item frypanHead;
    public static Item signHead;
    public static Item chiselHead;
    public static Item scytheBlade;
    public static Item broadAxeHead;
    public static Item excavatorHead;
    public static Item hammerHead;
    public static Item swordBlade;
    public static Item largeSwordBlade;
    public static Item knifeBlade;
    public static Item wideGuard;

    // Patterns and other materials
    public static Item blankPattern;
    public static Item materials; //TODO: Untwine this item
    public static Item toolRod;
    public static Item toolShard;
    public static Item titleIcon;

    // Tools
    public static ToolCore pickaxe;
    public static ToolCore shovel;
    public static ToolCore hatchet;
    public static ToolCore broadsword;
    public static ToolCore longsword;
    public static ToolCore rapier;
    public static ToolCore dagger;
    public static ToolCore cutlass;
    public static ToolCore frypan;
    public static ToolCore battlesign;
    public static ToolCore chisel;
    public static ToolCore mattock;
    public static ToolCore scythe;
    public static ToolCore lumberaxe;
    public static ToolCore cleaver;
    public static ToolCore hammer;
    public static ToolCore battleaxe;
    public static Item potionLauncher;
    public static Item handGuard;
    public static Item crossbar;
    public static Item fullGuard;
    public static Block craftedSoil; //TODO: Untwine this
    public static Block multiBrick;
    public static Block multiBrickFancy;
    // Tool modifiers
    public static ModFlux modFlux;
    public static ModLapis modLapis;
    public static ModAttack modAttack;
    public static Item[] patternOutputs;
    public static Item woodPattern;
    public static Item manualBook;
    public static ToolCore excavator;
    public static Item creativeModifier;

    // recipe stuff
    public static boolean thaumcraftAvailable;

    @Handler
    public void preInit (FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new TinkerToolEvents());

        //Blocks
        TinkerTools.toolStationWood = new ToolStationBlock(Material.wood).setBlockName("ToolStation");
        TinkerTools.toolForge = new ToolForgeBlock(Material.iron).setBlockName("ToolForge");
        TinkerTools.craftingStationWood = new CraftingStationBlock(Material.wood).setBlockName("CraftingStation");
        TinkerTools.craftingSlabWood = new CraftingSlab(Material.wood).setBlockName("CraftingSlab");
        TinkerTools.furnaceSlab = new FurnaceSlab(Material.rock).setBlockName("FurnaceSlab");

        TinkerTools.heldItemBlock = new EquipBlock(Material.wood).setBlockName("Frypan");
        TinkerTools.battlesignBlock = new BattlesignBlock(Material.wood).setBlockName("Battlesign");

        TinkerTools.craftedSoil = new SoilBlock().setLightOpacity(0).setBlockName("TConstruct.Soil");
        TinkerTools.craftedSoil.stepSound = Block.soundTypeGravel;

        GameRegistry.registerBlock(TinkerTools.toolStationWood, ToolStationItemBlock.class, "ToolStationBlock");
        GameRegistry.registerTileEntity(ToolStationLogic.class, "ToolStation");
        GameRegistry.registerTileEntity(PartBuilderLogic.class, "PartCrafter");
        GameRegistry.registerTileEntity(PatternChestLogic.class, "PatternHolder");
        GameRegistry.registerTileEntity(StencilTableLogic.class, "PatternShaper");
        GameRegistry.registerBlock(TinkerTools.toolForge, MetadataItemBlock.class, "ToolForgeBlock");
        GameRegistry.registerTileEntity(ToolForgeLogic.class, "ToolForge");
        GameRegistry.registerBlock(TinkerTools.craftingStationWood, "CraftingStation");
        GameRegistry.registerTileEntity(CraftingStationLogic.class, "CraftingStation");
        GameRegistry.registerBlock(TinkerTools.craftingSlabWood, CraftingSlabItemBlock.class, "CraftingSlab");
        GameRegistry.registerBlock(TinkerTools.furnaceSlab, "FurnaceSlab");
        GameRegistry.registerTileEntity(FurnaceLogic.class, "TConstruct.Furnace");
        GameRegistry.registerBlock(TinkerTools.heldItemBlock, "HeldItemBlock");
        GameRegistry.registerTileEntity(FrypanLogic.class, "FrypanLogic");
        GameRegistry.registerBlock(TinkerTools.battlesignBlock, "BattleSignBlock");
        GameRegistry.registerTileEntity(BattlesignLogic.class, "BattlesignLogic");

        GameRegistry.registerBlock(TinkerTools.craftedSoil, CraftedSoilItemBlock.class, "CraftedSoil");

        //Items
        TinkerTools.titleIcon = new TitleIcon().setUnlocalizedName("tconstruct.titleicon");
        GameRegistry.registerItem(TinkerTools.titleIcon, "titleIcon");
        String[] blanks = new String[] { "blank_pattern", "blank_cast", "blank_cast" };
        TinkerTools.blankPattern = new CraftAchievementItem(blanks, blanks, "materials/", "tinker", TConstructRegistry.materialTab, "tconstruct.pattern").setUnlocalizedName("tconstruct.Pattern");
        GameRegistry.registerItem(TinkerTools.blankPattern, "blankPattern");

        TinkerTools.materials = new MaterialItem().setUnlocalizedName("tconstruct.Materials");
        TinkerTools.toolRod = new DynamicToolPart("_rod", "ToolRod");
        TinkerTools.toolShard = new ToolShard("_chunk", "ToolShard");
        TinkerTools.woodPattern = new Pattern("pattern_", "materials/").setUnlocalizedName("tconstruct.Pattern");
        GameRegistry.registerItem(TinkerTools.materials, "materials");
        GameRegistry.registerItem(TinkerTools.woodPattern, "woodPattern");
        TConstructRegistry.addItemToDirectory("blankPattern", TinkerTools.blankPattern);
        TConstructRegistry.addItemToDirectory("woodPattern", TinkerTools.woodPattern);

        String[] patternTypes = { "ingot", "toolRod", "pickaxeHead", "shovelHead", "hatchetHead", "swordBlade", "wideGuard", "handGuard", "crossbar", "binding", "frypanHead", "signHead", "knifeBlade", "chiselHead", "toughRod", "toughBinding", "largePlate", "broadAxeHead", "scytheHead", "excavatorHead", "largeBlade", "hammerHead", "fullGuard", "bowString", "fletching", "arrowHead" };

        for (int i = 1; i < patternTypes.length; i++)
        {
            TConstructRegistry.addItemStackToDirectory(patternTypes[i] + "Pattern", new ItemStack(TinkerTools.woodPattern, 1, i));
        }

        TinkerTools.manualBook = new Manual();
        GameRegistry.registerItem(TinkerTools.manualBook, "manualBook");

        TinkerTools.pickaxe = new Pickaxe();
        TinkerTools.shovel = new Shovel();
        TinkerTools.hatchet = new Hatchet();
        TinkerTools.broadsword = new Broadsword();
        TinkerTools.longsword = new Longsword();
        TinkerTools.rapier = new Rapier();
        TinkerTools.dagger = new Dagger();
        TinkerTools.cutlass = new Cutlass();

        TinkerTools.frypan = new FryingPan();
        TinkerTools.battlesign = new BattleSign();
        TinkerTools.mattock = new Mattock();
        TinkerTools.chisel = new Chisel();

        TinkerTools.lumberaxe = new LumberAxe();
        TinkerTools.cleaver = new Cleaver();
        TinkerTools.scythe = new Scythe();
        TinkerTools.excavator = new Excavator();
        TinkerTools.hammer = new Hammer();
        TinkerTools.battleaxe = new Battleaxe();

        Item[] tools = { TinkerTools.pickaxe, TinkerTools.shovel, TinkerTools.hatchet, TinkerTools.broadsword, TinkerTools.longsword, TinkerTools.rapier, TinkerTools.dagger, TinkerTools.cutlass, TinkerTools.frypan, TinkerTools.battlesign, TinkerTools.mattock, TinkerTools.chisel, TinkerTools.lumberaxe, TinkerTools.cleaver, TinkerTools.scythe, TinkerTools.excavator, TinkerTools.hammer, TinkerTools.battleaxe};
        String[] toolStrings = { "pickaxe", "shovel", "hatchet", "broadsword", "longsword", "rapier", "dagger", "cutlass", "frypan", "battlesign", "mattock", "chisel", "lumberaxe", "cleaver", "scythe", "excavator", "hammer", "battleaxe"};

        for (int i = 0; i < tools.length; i++)
        {
            GameRegistry.registerItem(tools[i], toolStrings[i]); // 1.7 compat
            TConstructRegistry.addItemToDirectory(toolStrings[i], tools[i]);
        }

        TinkerTools.potionLauncher = new PotionLauncher().setUnlocalizedName("tconstruct.PotionLauncher");
        GameRegistry.registerItem(TinkerTools.potionLauncher, "potionLauncher");

        //TinkerTools.pickaxeHead = new ToolPart("_pickaxe_head", "PickHead").setUnlocalizedName("tconstruct.PickaxeHead");
        TinkerTools.pickaxeHead = new DynamicToolPart("_pickaxe_head", "PickaxeHead");
        TinkerTools.shovelHead = new DynamicToolPart("_shovel_head", "ShovelHead");
        TinkerTools.hatchetHead = new DynamicToolPart("_axe_head", "AxeHead");
        TinkerTools.binding = new DynamicToolPart("_binding", "Binding");
        TinkerTools.toughBinding = new DynamicToolPart("_toughbind", "ToughBinding");
        TinkerTools.toughRod = new DynamicToolPart("_toughrod", "ToughRod");
        TinkerTools.largePlate = new DynamicToolPart("_largeplate", "LargePlate");

        TinkerTools.swordBlade = new DynamicToolPart("_sword_blade", "SwordBlade");
        TinkerTools.wideGuard = new DynamicToolPart("_large_guard", "LargeGuard");
        TinkerTools.handGuard = new DynamicToolPart("_medium_guard", "MediumGuard");
        TinkerTools.crossbar = new DynamicToolPart("_crossbar", "Crossbar");
        TinkerTools.knifeBlade = new DynamicToolPart("_knife_blade", "KnifeBlade");
        TinkerTools.fullGuard = new DynamicToolPart("_full_guard", "FullGuard").hide();

        TinkerTools.frypanHead = new DynamicToolPart("_frypan_head", "FrypanHead");
        TinkerTools.signHead = new DynamicToolPart("_battlesign_head", "SignHead");
        TinkerTools.chiselHead = new DynamicToolPart("_chisel_head", "ChiselHead");

        TinkerTools.scytheBlade = new DynamicToolPart("_scythe_head", "ScytheHead");
        TinkerTools.broadAxeHead = new DynamicToolPart("_lumberaxe_head", "LumberAxeHead");
        TinkerTools.excavatorHead = new DynamicToolPart("_excavator_head", "ExcavatorHead");
        TinkerTools.largeSwordBlade = new DynamicToolPart("_large_sword_blade", "LargeSwordBlade");
        TinkerTools.hammerHead = new DynamicToolPart("_hammer_head", "HammerHead");



        Item[] toolParts = { TinkerTools.toolRod, TinkerTools.toolShard, TinkerTools.pickaxeHead, TinkerTools.shovelHead, TinkerTools.hatchetHead, TinkerTools.binding, TinkerTools.toughBinding, TinkerTools.toughRod, TinkerTools.largePlate, TinkerTools.swordBlade, TinkerTools.wideGuard, TinkerTools.handGuard, TinkerTools.crossbar, TinkerTools.knifeBlade, TinkerTools.fullGuard, TinkerTools.frypanHead, TinkerTools.signHead, TinkerTools.chiselHead, TinkerTools.scytheBlade, TinkerTools.broadAxeHead, TinkerTools.excavatorHead, TinkerTools.largeSwordBlade, TinkerTools.hammerHead};
        String[] toolPartStrings = { "toolRod", "toolShard", "pickaxeHead", "shovelHead", "hatchetHead", "binding", "toughBinding", "toughRod", "heavyPlate", "swordBlade", "wideGuard", "handGuard", "crossbar", "knifeBlade", "fullGuard", "frypanHead", "signHead", "chiselHead", "scytheBlade", "broadAxeHead", "excavatorHead", "largeSwordBlade", "hammerHead" };

        for (int i = 0; i < toolParts.length; i++)
        {
            GameRegistry.registerItem(toolParts[i], toolPartStrings[i]); // 1.7
                                                                         // compat
            TConstructRegistry.addItemToDirectory(toolPartStrings[i], toolParts[i]);
        }

        TinkerTools.creativeModifier = new CreativeModifier().setUnlocalizedName("tconstruct.modifier.creative");
        GameRegistry.registerItem(TinkerTools.creativeModifier, "creativeModifier");

        String[] materialStrings = { "paperStack", "greenSlimeCrystal", "searedBrick", "ingotCobalt", "ingotArdite", "ingotManyullyn", "mossBall", "lavaCrystal", "necroticBone", "ingotCopper", "ingotTin", "ingotAluminum", "rawAluminum", "ingotBronze", "ingotAluminumBrass", "ingotAlumite", "ingotSteel", "blueSlimeCrystal", "ingotObsidian", "nuggetIron", "nuggetCopper", "nuggetTin", "nuggetAluminum", "nuggetSilver", "nuggetAluminumBrass", "silkyCloth", "silkyJewel", "nuggetObsidian", "nuggetCobalt", "nuggetArdite", "nuggetManyullyn", "nuggetBronze", "nuggetAlumite", "nuggetSteel", "ingotPigIron", "nuggetPigIron", "glueball" };

        for (int i = 0; i < materialStrings.length; i++)
        {
            TConstructRegistry.addItemStackToDirectory(materialStrings[i], new ItemStack(TinkerTools.materials, 1, i));
        }

        registerMaterials();

        registerStencils();

        // this array is only used to register the remaining pattern-part-interactions
        TinkerTools.patternOutputs = new Item[] { TinkerTools.toolRod, TinkerTools.pickaxeHead, TinkerTools.shovelHead, TinkerTools.hatchetHead, TinkerTools.swordBlade, TinkerTools.wideGuard, TinkerTools.handGuard, TinkerTools.crossbar, TinkerTools.binding, TinkerTools.frypanHead, TinkerTools.signHead, TinkerTools.knifeBlade, TinkerTools.chiselHead, TinkerTools.toughRod, TinkerTools.toughBinding, TinkerTools.largePlate, TinkerTools.broadAxeHead, TinkerTools.scytheBlade, TinkerTools.excavatorHead, TinkerTools.largeSwordBlade, TinkerTools.hammerHead, TinkerTools.fullGuard, null, null, TinkerWeaponry.arrowhead, null };

        //Moved temporarily to deal with AE2 Quartz
        TinkerTools.modFlux = new ModFlux();
        ModifyBuilder.registerModifier(TinkerTools.modFlux);

        ItemStack lapisItem = new ItemStack(Items.dye, 1, 4);
        ItemStack lapisBlock = new ItemStack(Blocks.lapis_block);
        TinkerTools.modLapis = new ModLapis(10, new ItemStack[] { lapisItem, lapisBlock }, new int[] { 1, 9 });
        ModifyBuilder.registerModifier(TinkerTools.modLapis);

        TinkerTools.modAttack = new ModAttack("Quartz", 11, new ItemStack[] { new ItemStack(Items.quartz), new ItemStack(Blocks.quartz_block, 1, Short.MAX_VALUE) }, new int[] { 1, 4 });
        ModifyBuilder.registerModifier(TinkerTools.modAttack);
    }

    void setupToolTabs ()
    {
        TConstructRegistry.materialTab.init(new ItemStack(TinkerTools.manualBook, 1, 0));
        TConstructRegistry.partTab.init(new ItemStack(TinkerTools.titleIcon, 1, 255));
        TConstructRegistry.blockTab.init(new ItemStack(TinkerTools.toolStationWood));
        ItemStack tool = new ItemStack(TinkerTools.longsword, 1, 0);

        NBTTagCompound compound = new NBTTagCompound();
        compound.setTag("InfiTool", new NBTTagCompound());
        compound.getCompoundTag("InfiTool").setInteger("RenderHead", 2);
        compound.getCompoundTag("InfiTool").setInteger("RenderHandle", 0);
        compound.getCompoundTag("InfiTool").setInteger("RenderAccessory", 10);
        tool.setTagCompound(compound);

        TConstructRegistry.toolTab.init(tool);
    }

    //@Override
    public int getBurnTime (ItemStack fuel)
    {
        if (fuel.getItem() == TinkerTools.materials && fuel.getItemDamage() == 7)
            return 26400;
        return 0;
    }

    @Handler
    public void init (FMLInitializationEvent event)
    {
        addPartMapping();
        addRecipesForToolBuilder();
        addRecipesForChisel();
        craftingTableRecipes();
        setupToolTabs();
        proxy.initialize();
    }

    @Handler
    public void postInit (FMLPostInitializationEvent evt)
    {
        vanillaToolRecipes();
        modIntegration();
    }

    private void addPartMapping ()
    {
        /* Tools */

        int[] nonMetals = { 0, 1, 3, 4, 5, 6, 7, 8, 9, 17 };

        if (PHConstruct.craftMetalTools)
        {
            for (int mat = 0; mat < 18; mat++)
            {
                for (int meta = 0; meta < TinkerTools.patternOutputs.length; meta++)
                {
                    if (TinkerTools.patternOutputs[meta] != null)
                        TConstructRegistry.addPartMapping(TinkerTools.woodPattern, meta + 1, mat, new ItemStack(TinkerTools.patternOutputs[meta], 1, mat));
                }
            }
        }
        else
        {
            for (int mat = 0; mat < nonMetals.length; mat++)
            {
                for (int meta = 0; meta < TinkerTools.patternOutputs.length; meta++)
                {
                    if (TinkerTools.patternOutputs[meta] != null)
                        TConstructRegistry.addPartMapping(TinkerTools.woodPattern, meta + 1, nonMetals[mat], new ItemStack(TinkerTools.patternOutputs[meta], 1, nonMetals[mat]));
                }
            }
        }

        registerPatternMaterial("plankWood", 2, "Wood");
        registerPatternMaterial("stickWood", 1, "Wood");
        registerPatternMaterial("slabWood", 1, "Wood");
        registerPatternMaterial("compressedCobblestone1x", 18, "Stone");
    }

    private void addRecipesForToolBuilder ()
    {
        ToolBuilder tb = ToolBuilder.instance;
        tb.addNormalToolRecipe(TinkerTools.pickaxe, TinkerTools.pickaxeHead, TinkerTools.toolRod, TinkerTools.binding);
        tb.addNormalToolRecipe(TinkerTools.broadsword, TinkerTools.swordBlade, TinkerTools.toolRod, TinkerTools.wideGuard);
        tb.addNormalToolRecipe(TinkerTools.hatchet, TinkerTools.hatchetHead, TinkerTools.toolRod);
        tb.addNormalToolRecipe(TinkerTools.shovel, TinkerTools.shovelHead, TinkerTools.toolRod);
        tb.addNormalToolRecipe(TinkerTools.longsword, TinkerTools.swordBlade, TinkerTools.toolRod, TinkerTools.handGuard);
        tb.addNormalToolRecipe(TinkerTools.rapier, TinkerTools.swordBlade, TinkerTools.toolRod, TinkerTools.crossbar);
        tb.addNormalToolRecipe(TinkerTools.frypan, TinkerTools.frypanHead, TinkerTools.toolRod);
        tb.addNormalToolRecipe(TinkerTools.battlesign, TinkerTools.signHead, TinkerTools.toolRod);
        tb.addNormalToolRecipe(TinkerTools.mattock, TinkerTools.hatchetHead, TinkerTools.toolRod, TinkerTools.shovelHead);
        tb.addNormalToolRecipe(TinkerTools.dagger, TinkerTools.knifeBlade, TinkerTools.toolRod, TinkerTools.crossbar);
        tb.addNormalToolRecipe(TinkerTools.cutlass, TinkerTools.swordBlade, TinkerTools.toolRod, TinkerTools.fullGuard);
        tb.addNormalToolRecipe(TinkerTools.chisel, TinkerTools.chiselHead, TinkerTools.toolRod);

        tb.addNormalToolRecipe(TinkerTools.scythe, TinkerTools.scytheBlade, TinkerTools.toughRod, TinkerTools.toughBinding, TinkerTools.toughRod);
        tb.addNormalToolRecipe(TinkerTools.lumberaxe, TinkerTools.broadAxeHead, TinkerTools.toughRod, TinkerTools.largePlate, TinkerTools.toughBinding);
        tb.addNormalToolRecipe(TinkerTools.cleaver, TinkerTools.largeSwordBlade, TinkerTools.toughRod, TinkerTools.largePlate, TinkerTools.toughRod);
        tb.addNormalToolRecipe(TinkerTools.excavator, TinkerTools.excavatorHead, TinkerTools.toughRod, TinkerTools.largePlate, TinkerTools.toughBinding);
        tb.addNormalToolRecipe(TinkerTools.hammer, TinkerTools.hammerHead, TinkerTools.toughRod, TinkerTools.largePlate, TinkerTools.largePlate);
        tb.addNormalToolRecipe(TinkerTools.battleaxe, TinkerTools.broadAxeHead, TinkerTools.toughRod, TinkerTools.broadAxeHead, TinkerTools.toughBinding);

        ItemStack diamond = new ItemStack(Items.diamond);
        ModifyBuilder.registerModifier(new ModToolRepair());
        ModifyBuilder.registerModifier(new ModDurability(new ItemStack[] { diamond }, 0, 500, 0f, 3, "Diamond", "\u00a7b" + StatCollector.translateToLocal("modifier.tool.diamond"), "\u00a7b"));
        ModifyBuilder.registerModifier(new ModDurability(new ItemStack[] { new ItemStack(Items.emerald) }, 1, 0, 0.5f, 2, "Emerald", "\u00a72" + StatCollector.translateToLocal("modifier.tool.emerald"), "\u00a72"));

        ItemStack redstoneItem = new ItemStack(Items.redstone);
        ItemStack redstoneBlock = new ItemStack(Blocks.redstone_block);
        ModifyBuilder.registerModifier(new ModRedstone(2, new ItemStack[] { redstoneItem, redstoneBlock }, new int[] { 1, 9 }));

        ModifyBuilder.registerModifier(new ModInteger(new ItemStack[] { new ItemStack(TinkerTools.materials, 1, 6) }, 4, "Moss", 3, "\u00a72", StatCollector.translateToLocal("modifier.tool.moss")));
        ItemStack blazePowder = new ItemStack(Items.blaze_powder);
        ModifyBuilder.registerModifier(new ModBlaze(7, new ItemStack[] { blazePowder }, new int[] { 1 }));
        ModifyBuilder.registerModifier(new ModAutoSmelt(new ItemStack[] { new ItemStack(TinkerTools.materials, 1, 7) }, 6, "Lava", "\u00a74", StatCollector.translateToLocal("modifier.tool.lava")));
        ModifyBuilder.registerModifier(new ModInteger(new ItemStack[] { new ItemStack(TinkerTools.materials, 1, 8) }, 8, "Necrotic", 1, "\u00a78", StatCollector.translateToLocal("modifier.tool.necro")));

        ModifyBuilder.registerModifier(new ModExtraModifier(new ItemStack[] { diamond, new ItemStack(Blocks.gold_block) }, "Tier1Free"));
        ModifyBuilder.registerModifier(new ModExtraModifier(new ItemStack[] { new ItemStack(Blocks.diamond_block), new ItemStack(Items.golden_apple, 1, 1) }, "Tier1.5Free"));
        ModifyBuilder.registerModifier(new ModExtraModifier(new ItemStack[] { new ItemStack(Items.nether_star) }, "Tier2Free"));
        ModifyBuilder.registerModifier(new ModCreativeToolModifier(new ItemStack[] { new ItemStack(TinkerTools.creativeModifier) }));

        ItemStack silkyJewel = new ItemStack(TinkerTools.materials, 1, 26);
        ModifyBuilder.registerModifier(new ModButtertouch(new ItemStack[] { silkyJewel }, 12));

        ItemStack piston = new ItemStack(Blocks.piston);
        ModifyBuilder.registerModifier(new ModPiston(3, new ItemStack[] { piston }, new int[] { 1 }));

        ModifyBuilder.registerModifier(new ModInteger(new ItemStack[] { new ItemStack(Blocks.obsidian), new ItemStack(Items.ender_pearl) }, 13, "Beheading", 1, "\u00a7d", "Beheading"));

        ItemStack holySoil = new ItemStack(TinkerTools.craftedSoil, 1, 4);
        ModifyBuilder.registerModifier(new ModSmite("Smite", 14, new ItemStack[] { holySoil }, new int[] { 1 }));

        ItemStack spidereyeball = new ItemStack(Items.fermented_spider_eye);
        ModifyBuilder.registerModifier(new ModAntiSpider("ModAntiSpider", 15, new ItemStack[] { spidereyeball }, new int[] { 1 }));

        ItemStack obsidianPlate = new ItemStack(TinkerTools.largePlate, 1, 6);
        ModifyBuilder.registerModifier(new ModReinforced(new ItemStack[] { obsidianPlate }, 16, 1));

        TConstructRegistry.registerActiveToolMod(new TActiveOmniMod());
    }

    private void addRecipesForChisel ()
    {
        /* Detailing */
        Detailing chiseling = TConstructRegistry.getChiselDetailing();
        chiseling.addDetailing(Blocks.stone, 0, Blocks.stonebrick, 0, TinkerTools.chisel);
        chiseling.addDetailing(TinkerSmeltery.speedBlock, 0, TinkerSmeltery.speedBlock, 1, TinkerTools.chisel);
        chiseling.addDetailing(TinkerSmeltery.speedBlock, 2, TinkerSmeltery.speedBlock, 3, TinkerTools.chisel);
        chiseling.addDetailing(TinkerSmeltery.speedBlock, 3, TinkerSmeltery.speedBlock, 4, TinkerTools.chisel);
        chiseling.addDetailing(TinkerSmeltery.speedBlock, 4, TinkerSmeltery.speedBlock, 5, TinkerTools.chisel);
        chiseling.addDetailing(TinkerSmeltery.speedBlock, 5, TinkerSmeltery.speedBlock, 6, TinkerTools.chisel);

        chiseling.addDetailing(Blocks.obsidian, 0, TinkerTools.multiBrick, 0, TinkerTools.chisel);
        chiseling.addDetailing(Blocks.sandstone, 0, Blocks.sandstone, 2, TinkerTools.chisel);
        chiseling.addDetailing(Blocks.sandstone, 2, Blocks.sandstone, 1, TinkerTools.chisel);
        chiseling.addDetailing(Blocks.sandstone, 1, TinkerTools.multiBrick, 1, TinkerTools.chisel);
        // chiseling.addDetailing(Block.netherrack, 0, TRepo.multiBrick, 2,
        // TRepo.chisel);
        // chiseling.addDetailing(Block.stone_refined, 0, TRepo.multiBrick, 3,
        // TRepo.chisel);
        chiseling.addDetailing(Items.iron_ingot, 0, TinkerTools.multiBrick, 4, TinkerTools.chisel);
        chiseling.addDetailing(Items.gold_ingot, 0, TinkerTools.multiBrick, 5, TinkerTools.chisel);
        chiseling.addDetailing(Items.dye, 4, TinkerTools.multiBrick, 6, TinkerTools.chisel);
        chiseling.addDetailing(Items.diamond, 0, TinkerTools.multiBrick, 7, TinkerTools.chisel);
        chiseling.addDetailing(Items.redstone, 0, TinkerTools.multiBrick, 8, TinkerTools.chisel);
        chiseling.addDetailing(Items.bone, 0, TinkerTools.multiBrick, 9, TinkerTools.chisel);
        chiseling.addDetailing(Items.slime_ball, 0, TinkerTools.multiBrick, 10, TinkerTools.chisel);
        chiseling.addDetailing(TinkerWorld.strangeFood, 0, TinkerTools.multiBrick, 11, TinkerTools.chisel);
        chiseling.addDetailing(Blocks.end_stone, 0, TinkerTools.multiBrick, 12, TinkerTools.chisel);
        chiseling.addDetailing(TinkerTools.materials, 18, TinkerTools.multiBrick, 13, TinkerTools.chisel);

        // adding multiBrick / multiBrickFanxy meta 0-13 to list
        for (int sc = 0; sc < 14; sc++)
        {
            chiseling.addDetailing(TinkerTools.multiBrick, sc, TinkerTools.multiBrickFancy, sc, TinkerTools.chisel);
        }

        chiseling.addDetailing(Blocks.stonebrick, 0, TinkerTools.multiBrickFancy, 15, TinkerTools.chisel);
        chiseling.addDetailing(TinkerTools.multiBrickFancy, 15, TinkerTools.multiBrickFancy, 14, TinkerTools.chisel);
        chiseling.addDetailing(TinkerTools.multiBrickFancy, 14, Blocks.stonebrick, 3, TinkerTools.chisel);
        /*
         * chiseling.addDetailing(TRepo.multiBrick, 14, TRepo.multiBrickFancy,
         * 14, TRepo.chisel); chiseling.addDetailing(TRepo.multiBrick, 15,
         * TRepo.multiBrickFancy, 15, TRepo.chisel);
         */

        chiseling.addDetailing(TinkerSmeltery.smeltery, 4, TinkerSmeltery.smeltery, 6, TinkerTools.chisel);
        chiseling.addDetailing(TinkerSmeltery.smeltery, 6, TinkerSmeltery.smeltery, 11, TinkerTools.chisel);
        chiseling.addDetailing(TinkerSmeltery.smeltery, 11, TinkerSmeltery.smeltery, 2, TinkerTools.chisel);
        chiseling.addDetailing(TinkerSmeltery.smeltery, 2, TinkerSmeltery.smeltery, 8, TinkerTools.chisel);
        chiseling.addDetailing(TinkerSmeltery.smeltery, 8, TinkerSmeltery.smeltery, 9, TinkerTools.chisel);
        chiseling.addDetailing(TinkerSmeltery.smeltery, 9, TinkerSmeltery.smeltery, 10, TinkerTools.chisel);
    }

    public void vanillaToolRecipes ()
    {
        if (PHConstruct.removeVanillaToolRecipes)
        {
            RecipeRemover.removeAnyRecipe(new ItemStack(Items.wooden_pickaxe));
            RecipeRemover.removeAnyRecipe(new ItemStack(Items.wooden_axe));
            RecipeRemover.removeAnyRecipe(new ItemStack(Items.wooden_shovel));
            RecipeRemover.removeAnyRecipe(new ItemStack(Items.wooden_hoe));
            RecipeRemover.removeAnyRecipe(new ItemStack(Items.wooden_sword));

            RecipeRemover.removeAnyRecipe(new ItemStack(Items.stone_pickaxe));
            RecipeRemover.removeAnyRecipe(new ItemStack(Items.stone_axe));
            RecipeRemover.removeAnyRecipe(new ItemStack(Items.stone_shovel));
            RecipeRemover.removeAnyRecipe(new ItemStack(Items.stone_hoe));
            RecipeRemover.removeAnyRecipe(new ItemStack(Items.stone_sword));

            RecipeRemover.removeAnyRecipe(new ItemStack(Items.iron_pickaxe));
            RecipeRemover.removeAnyRecipe(new ItemStack(Items.iron_axe));
            RecipeRemover.removeAnyRecipe(new ItemStack(Items.iron_shovel));
            RecipeRemover.removeAnyRecipe(new ItemStack(Items.iron_hoe));
            RecipeRemover.removeAnyRecipe(new ItemStack(Items.iron_sword));

            RecipeRemover.removeAnyRecipe(new ItemStack(Items.diamond_pickaxe));
            RecipeRemover.removeAnyRecipe(new ItemStack(Items.diamond_axe));
            RecipeRemover.removeAnyRecipe(new ItemStack(Items.diamond_shovel));
            RecipeRemover.removeAnyRecipe(new ItemStack(Items.diamond_hoe));
            RecipeRemover.removeAnyRecipe(new ItemStack(Items.diamond_sword));

            RecipeRemover.removeAnyRecipe(new ItemStack(Items.golden_pickaxe));
            RecipeRemover.removeAnyRecipe(new ItemStack(Items.golden_axe));
            RecipeRemover.removeAnyRecipe(new ItemStack(Items.golden_shovel));
            RecipeRemover.removeAnyRecipe(new ItemStack(Items.golden_hoe));
            RecipeRemover.removeAnyRecipe(new ItemStack(Items.golden_sword));
        }

        if (PHConstruct.labotimizeVanillaTools)
        {
            Items.wooden_pickaxe.setMaxDamage(1);
            Items.wooden_axe.setMaxDamage(1);
            Items.wooden_shovel.setMaxDamage(1);
            Items.wooden_hoe.setMaxDamage(1);
            Items.wooden_sword.setMaxDamage(1);

            Items.stone_pickaxe.setMaxDamage(1);
            Items.stone_axe.setMaxDamage(1);
            Items.stone_shovel.setMaxDamage(1);
            Items.stone_hoe.setMaxDamage(1);
            Items.stone_sword.setMaxDamage(1);

            Items.iron_pickaxe.setMaxDamage(1);
            Items.iron_axe.setMaxDamage(1);
            Items.iron_shovel.setMaxDamage(1);
            Items.iron_hoe.setMaxDamage(1);
            Items.iron_sword.setMaxDamage(1);

            Items.diamond_pickaxe.setMaxDamage(1);
            Items.diamond_axe.setMaxDamage(1);
            Items.diamond_shovel.setMaxDamage(1);
            Items.diamond_hoe.setMaxDamage(1);
            Items.diamond_sword.setMaxDamage(1);

            Items.golden_pickaxe.setMaxDamage(1);
            Items.golden_axe.setMaxDamage(1);
            Items.golden_shovel.setMaxDamage(1);
            Items.golden_hoe.setMaxDamage(1);
            Items.golden_sword.setMaxDamage(1);
        }
    }

    public static void registerPatternMaterial (String oreName, int value, String materialName)
    {
        for (ItemStack ore : OreDictionary.getOres(oreName))
        {
            PatternBuilder.instance.registerMaterial(ore, value, materialName);
        }
    }

    private void craftingTableRecipes ()
    {
        String[] patBlock = { "###", "###", "###" };
        String[] patSurround = { "###", "#m#", "###" };

        Object[] toolForgeBlocks = { "blockIron", "blockGold", Blocks.diamond_block, Blocks.emerald_block, "blockCobalt", "blockArdite", "blockManyullyn", "blockCopper", "blockBronze", "blockTin", "blockAluminum", "blockAluminumBrass", "blockAlumite", "blockSteel" };

        // ToolForge Recipes (Metal Version)
        for (int sc = 0; sc < toolForgeBlocks.length; sc++)
        {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerTools.toolForge, 1, sc), "bbb", "msm", "m m", 'b', new ItemStack(TinkerSmeltery.smeltery, 1, 2), 's', new ItemStack(TinkerTools.toolStationWood, 1, 0), 'm', toolForgeBlocks[sc]));
            // adding slab version recipe
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerTools.craftingSlabWood, 1, 5), "bbb", "msm", "m m", 'b', new ItemStack(TinkerSmeltery.smeltery, 1, 2), 's', new ItemStack(TinkerTools.craftingSlabWood, 1, 1), 'm', toolForgeBlocks[sc]));
        }

        // ToolStation Recipes (Wooden Version)
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerTools.toolStationWood, 1, 0), "p", "w", 'p', new ItemStack(TinkerTools.blankPattern, 1, 0), 'w', "crafterWood"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerTools.toolStationWood, 1, 0), "p", "w", 'p', new ItemStack(TinkerTools.blankPattern, 1, 0), 'w', "craftingTableWood"));
        GameRegistry.addRecipe(new ItemStack(TinkerTools.toolStationWood, 1, 0), "p", "w", 'p', new ItemStack(TinkerTools.blankPattern, 1, 0), 'w', new ItemStack(TinkerTools.craftingStationWood, 1, 0));
        GameRegistry.addRecipe(new ItemStack(TinkerTools.toolStationWood, 1, 0), "p", "w", 'p', new ItemStack(TinkerTools.blankPattern, 1, 0), 'w', new ItemStack(TinkerTools.craftingSlabWood, 1, 0));
        GameRegistry.addRecipe(new ItemStack(TinkerTools.toolStationWood, 1, 2), "p", "w", 'p', new ItemStack(TinkerTools.blankPattern, 1, 0), 'w', new ItemStack(Blocks.log, 1, 1));
        GameRegistry.addRecipe(new ItemStack(TinkerTools.toolStationWood, 1, 3), "p", "w", 'p', new ItemStack(TinkerTools.blankPattern, 1, 0), 'w', new ItemStack(Blocks.log, 1, 2));
        GameRegistry.addRecipe(new ItemStack(TinkerTools.toolStationWood, 1, 4), "p", "w", 'p', new ItemStack(TinkerTools.blankPattern, 1, 0), 'w', new ItemStack(Blocks.log, 1, 3));
        GameRegistry.addRecipe(new ItemStack(TinkerTools.toolStationWood, 1, 5), "p", "w", 'p', new ItemStack(TinkerTools.blankPattern, 1, 0), 'w', Blocks.chest);
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerTools.toolStationWood, 1, 1), "p", "w", 'p', new ItemStack(TinkerTools.blankPattern, 1, 0), 'w', "logWood"));
        GameRegistry.addRecipe(new ItemStack(TinkerTools.toolStationWood, 1, 10), "p", "w", 'p', new ItemStack(TinkerTools.blankPattern, 1, 0), 'w', new ItemStack(Blocks.planks, 1, 0));
        GameRegistry.addRecipe(new ItemStack(TinkerTools.toolStationWood, 1, 11), "p", "w", 'p', new ItemStack(TinkerTools.blankPattern, 1, 0), 'w', new ItemStack(Blocks.planks, 1, 1));
        GameRegistry.addRecipe(new ItemStack(TinkerTools.toolStationWood, 1, 12), "p", "w", 'p', new ItemStack(TinkerTools.blankPattern, 1, 0), 'w', new ItemStack(Blocks.planks, 1, 2));
        GameRegistry.addRecipe(new ItemStack(TinkerTools.toolStationWood, 1, 13), "p", "w", 'p', new ItemStack(TinkerTools.blankPattern, 1, 0), 'w', new ItemStack(Blocks.planks, 1, 3));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerTools.toolStationWood, 1, 10), "p", "w", 'p', new ItemStack(TinkerTools.blankPattern, 1, 0), 'w', "plankWood"));
        GameRegistry.addRecipe(new ItemStack(TinkerTools.furnaceSlab, 1, 0), "###", "# #", "###", '#', new ItemStack(Blocks.stone_slab, 1, 3));

        // Blank Pattern Recipe
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerTools.blankPattern, 4, 0), "ps", "sp", 'p', "plankWood", 's', "stickWood"));
        // Manual Book Recipes
        GameRegistry.addRecipe(new ItemStack(TinkerTools.manualBook), "wp", 'w', new ItemStack(TinkerTools.blankPattern, 1, 0), 'p', Items.paper);
        GameRegistry.addShapelessRecipe(new ItemStack(TinkerTools.manualBook, 2, 0), new ItemStack(TinkerTools.manualBook, 1, 0), Items.book);
        GameRegistry.addShapelessRecipe(new ItemStack(TinkerTools.manualBook, 1, 1), new ItemStack(TinkerTools.manualBook, 1, 0));
        GameRegistry.addShapelessRecipe(new ItemStack(TinkerTools.manualBook, 2, 1), new ItemStack(TinkerTools.manualBook, 1, 1), Items.book);
        GameRegistry.addShapelessRecipe(new ItemStack(TinkerTools.manualBook, 1, 2), new ItemStack(TinkerTools.manualBook, 1, 1));
        GameRegistry.addShapelessRecipe(new ItemStack(TinkerTools.manualBook, 2, 2), new ItemStack(TinkerTools.manualBook, 1, 2), Items.book);
        GameRegistry.addShapelessRecipe(new ItemStack(TinkerTools.manualBook, 1, 3), new ItemStack(TinkerTools.manualBook, 1, 2));
        // alternative Vanilla Book Recipe
        GameRegistry.addShapelessRecipe(new ItemStack(Items.book), Items.paper, Items.paper, Items.paper, Items.string, TinkerTools.blankPattern, TinkerTools.blankPattern);
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.name_tag), "P~ ", "~O ", "  ~", '~', Items.string, 'P', Items.paper, 'O', "slimeball"));

        // Paperstack Recipe
        GameRegistry.addRecipe(new ItemStack(TinkerTools.materials, 1, 0), "pp", "pp", 'p', Items.paper);
        // Mossball Recipe
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerTools.materials, 1, 6), patBlock, '#', "stoneMossy"));
        // LavaCrystal Recipes -Auto-smelt
        GameRegistry.addRecipe(new ItemStack(TinkerTools.materials, 1, 7), "xcx", "cbc", "xcx", 'b', Items.lava_bucket, 'c', Items.fire_charge, 'x', Items.blaze_rod);
        GameRegistry.addRecipe(new ItemStack(TinkerTools.materials, 1, 7), "xcx", "cbc", "xcx", 'b', Items.lava_bucket, 'x', Items.fire_charge, 'c', Items.blaze_rod);
        // Slimy sand Recipes
        GameRegistry.addShapelessRecipe(new ItemStack(TinkerTools.craftedSoil, 1, 0), Items.slime_ball, Items.slime_ball, Items.slime_ball, Items.slime_ball, Blocks.sand, Blocks.dirt);
        GameRegistry.addShapelessRecipe(new ItemStack(TinkerTools.craftedSoil, 1, 2), TinkerWorld.strangeFood, TinkerWorld.strangeFood, TinkerWorld.strangeFood, TinkerWorld.strangeFood, Blocks.sand, Blocks.dirt);
        // Grout Recipes
        GameRegistry.addShapelessRecipe(new ItemStack(TinkerTools.craftedSoil, 2, 1), Items.clay_ball, Blocks.sand, Blocks.gravel);
        GameRegistry.addShapelessRecipe(new ItemStack(TinkerTools.craftedSoil, 8, 1), new ItemStack(Blocks.clay, 1, Short.MAX_VALUE), Blocks.sand, Blocks.sand, Blocks.sand, Blocks.sand, Blocks.gravel, Blocks.gravel, Blocks.gravel, Blocks.gravel);
        GameRegistry.addShapelessRecipe(new ItemStack(TinkerTools.craftedSoil, 2, 6), Items.nether_wart, Blocks.soul_sand, Blocks.gravel);
        // Graveyard Soil Recipes
        GameRegistry.addShapelessRecipe(new ItemStack(TinkerTools.craftedSoil, 1, 3), Blocks.dirt, Items.rotten_flesh, new ItemStack(Items.dye, 1, 15));
        // Silky Cloth Recipes
        GameRegistry.addRecipe(new ItemStack(TinkerTools.materials, 1, 25), patSurround, 'm', new ItemStack(TinkerTools.materials, 1, 24), '#', new ItemStack(Items.string));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerTools.materials, 1, 25), patSurround, 'm', "nuggetGold", '#', new ItemStack(Items.string)));
        // Silky Jewel Recipes
        GameRegistry.addRecipe(new ItemStack(TinkerTools.materials, 1, 26), " c ", "cec", " c ", 'c', new ItemStack(TinkerTools.materials, 1, 25), 'e', new ItemStack(Items.emerald));

        // Advanced WorkBench Recipes
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerTools.craftingStationWood, 1, 0), "b", 'b', "crafterWood"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerTools.craftingStationWood, 1, 0), "b", 'b', "craftingTableWood"));
        // Slab crafters
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerTools.craftingSlabWood, 6, 0), "bbb", 'b', "crafterWood"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerTools.craftingSlabWood, 6, 0), "bbb", 'b', "craftingTableWood"));
        GameRegistry.addRecipe(new ItemStack(TinkerTools.craftingSlabWood, 1, 0), "b", 'b', new ItemStack(TinkerTools.craftingStationWood, 1, 0));
        GameRegistry.addRecipe(new ItemStack(TinkerTools.craftingSlabWood, 1, 1), "b", 'b', new ItemStack(TinkerTools.toolStationWood, 1, 0));
        GameRegistry.addRecipe(new ItemStack(TinkerTools.craftingSlabWood, 1, 2), "b", 'b', new ItemStack(TinkerTools.toolStationWood, 1, 1));
        GameRegistry.addRecipe(new ItemStack(TinkerTools.craftingSlabWood, 1, 2), "b", 'b', new ItemStack(TinkerTools.toolStationWood, 1, 2));
        GameRegistry.addRecipe(new ItemStack(TinkerTools.craftingSlabWood, 1, 2), "b", 'b', new ItemStack(TinkerTools.toolStationWood, 1, 3));
        GameRegistry.addRecipe(new ItemStack(TinkerTools.craftingSlabWood, 1, 2), "b", 'b', new ItemStack(TinkerTools.toolStationWood, 1, 4));
        GameRegistry.addRecipe(new ItemStack(TinkerTools.craftingSlabWood, 1, 4), "b", 'b', new ItemStack(TinkerTools.toolStationWood, 1, 5));
        GameRegistry.addRecipe(new ItemStack(TinkerTools.craftingSlabWood, 1, 3), "b", 'b', new ItemStack(TinkerTools.toolStationWood, 1, 10));
        GameRegistry.addRecipe(new ItemStack(TinkerTools.craftingSlabWood, 1, 3), "b", 'b', new ItemStack(TinkerTools.toolStationWood, 1, 11));
        GameRegistry.addRecipe(new ItemStack(TinkerTools.craftingSlabWood, 1, 3), "b", 'b', new ItemStack(TinkerTools.toolStationWood, 1, 12));
        GameRegistry.addRecipe(new ItemStack(TinkerTools.craftingSlabWood, 1, 3), "b", 'b', new ItemStack(TinkerTools.toolStationWood, 1, 13));
        GameRegistry.addRecipe(new ItemStack(TinkerTools.craftingSlabWood, 1, 5), "b", 'b', new ItemStack(TinkerTools.toolForge, 1, Short.MAX_VALUE));

        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TinkerTools.materials, 1, 41), "dustArdite", "dustCobalt"));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TinkerTools.materials, 4, 42), "dustAluminium", "dustAluminium", "dustAluminium", "dustCopper"));
    }

    private void modIntegration ()
    {
        /* TE3 Flux */
        ItemStack batHardened = GameRegistry.findItemStack("ThermalExpansion", "capacitorHardened", 1);
        if (batHardened != null)
        {
            TinkerTools.modFlux.batteries.add(batHardened);
        }
        ItemStack basicCell = GameRegistry.findItemStack("ThermalExpansion", "cellBasic", 1);
        if (basicCell != null)
        {
            TinkerTools.modFlux.batteries.add(basicCell);
        }

        ItemStack ironpick = ToolBuilder.instance.buildTool(new ItemStack(TinkerTools.pickaxeHead, 1, 6), new ItemStack(TinkerTools.toolRod, 1, 2), new ItemStack(TinkerTools.binding, 1, 6), "");
        if (batHardened != null)
            TConstructClientRegistry.registerManualModifier("fluxmod", ironpick.copy(), (ItemStack) batHardened);
        if (basicCell != null)
            TConstructClientRegistry.registerManualModifier("fluxmod2", ironpick.copy(), (ItemStack) basicCell);

        /* Thaumcraft */
        Object obj = ItemHelper.getStaticItem("itemResource", "thaumcraft.common.config.ConfigItems");
        if (obj != null)
        {
            TConstruct.logger.info("Thaumcraft detected. Adding thaumium tools.");
            TinkerTools.thaumcraftAvailable = true;
            TConstructClientRegistry.addMaterialRenderMapping(MaterialID.Thaumium, "tinker", "thaumium", true);
            TConstructRegistry.addToolMaterial(MaterialID.Thaumium, "Thaumium", 3, 400, 700, 2, 1.3F, 0, 0f, "\u00A75", 0x51437c);
            PatternBuilder.instance.registerFullMaterial(new ItemStack((Item) obj, 1, 2), 2, "Thaumium", new ItemStack(TinkerTools.toolShard, 1, 31), new ItemStack(TinkerTools.toolRod, 1, 31), 31);
            for (int meta = 0; meta < TinkerTools.patternOutputs.length; meta++)
            {
                if (TinkerTools.patternOutputs[meta] != null)
                    TConstructRegistry.addPartMapping(TinkerTools.woodPattern, meta + 1, 31, new ItemStack(TinkerTools.patternOutputs[meta], 1, 31));
            }

            TConstructRegistry.addBowstringMaterial(1, 2, new ItemStack((Item) obj, 1, 7), new ItemStack(TinkerWeaponry.bowstring, 1, 1), 1F, 1F, 0.9f, 0x63bcd9);
            TConstructRegistry.addBowMaterial(31, 40, 1.2f);
            TConstructRegistry.addArrowMaterial(31, 1.8F, 0.5F);

            TConstructRegistry.addDefaultToolPartMaterial(MaterialID.Thaumium);
            TConstructRegistry.addDefaultShardMaterial(MaterialID.Thaumium);
        }
        else
        {
            TConstruct.logger.warn("Thaumcraft not detected.");
        }

        if (Loader.isModLoaded("Natura"))
        {
            try
            {
                Object plantItem = ItemHelper.getStaticItem("plantItem", "mods.natura.common.NContent");
                TConstructRegistry.addBowstringMaterial(2, 2, new ItemStack((Item) plantItem, 1, 7), new ItemStack(TinkerWeaponry.bowstring, 1, 2), 1.2F, 0.8F, 1.3f, 0xd3414f);
            }
            catch (Exception e)
            {
            } // No need to handle
        }
    }

    void registerMaterials ()
    {
        // Tool Materials: id, name, harvestlevel, durability, speed, damage, handlemodifier, reinforced, shoddy, style color, primary color for block use
        TConstructRegistry.addToolMaterial(MaterialID.Wood, "Wood", 1, 97, 350, 0, 1.0F, 0, 0f, YELLOW.toString(), 0x755821);
        TConstructRegistry.addToolMaterial(MaterialID.Stone, "Stone", 1, 131, 400, 1, 0.5F, 0, 1f, GRAY.toString(), 0x7F7F7F);
        TConstructRegistry.addToolMaterial(MaterialID.Iron, "Iron", 2, 250, 600, 2, 1.3F, 1, 0f, WHITE.toString(), 0xDADADA);
        TConstructRegistry.addToolMaterial(MaterialID.Flint, "Flint", 1, 171, 525, 2, 0.7F, 0, 0f, DARK_GRAY.toString(), 0x484848);
        TConstructRegistry.addToolMaterial(MaterialID.Cactus, "Cactus", 1, 150, 500, 2, 1.0F, 0, -1f, DARK_GREEN.toString(), 0x12690b);
        TConstructRegistry.addToolMaterial(MaterialID.Bone, "Bone", 1, 200, 400, 1, 1.0F, 0, 0f, YELLOW.toString(), 0xEDEBCA);
        TConstructRegistry.addToolMaterial(MaterialID.Obsidian, "Obsidian", 3, 89, 700, 2, 0.8F, 3, 0f, LIGHT_PURPLE.toString(), 0xaa7ff5);
        TConstructRegistry.addToolMaterial(MaterialID.Netherrack, "Netherrack", 2, 131, 400, 1, 1.2F, 0, 1f, DARK_RED.toString(), 0x833238);
        TConstructRegistry.addToolMaterial(MaterialID.Slime, "Slime", 0, 500, 150, 0, 1.5F, 0, 0f, GREEN.toString(), 0x6EB065);
        TConstructRegistry.addToolMaterial(MaterialID.Paper, "Paper", 0, 30, 200, 0, 0.3F, 0, 0f, WHITE.toString(), 0xFFFFFF);
        TConstructRegistry.addToolMaterial(MaterialID.Cobalt, "Cobalt", 4, 800, 1400, 3, 1.75F, 2, 0f, DARK_AQUA.toString(), 0x2376DD);
        TConstructRegistry.addToolMaterial(MaterialID.Ardite, "Ardite", 4, 500, 800, 3, 2.0F, 0, 2f, DARK_RED.toString(), 0xA53000);
        TConstructRegistry.addToolMaterial(MaterialID.Manyullyn, "Manyullyn", 5, 1200, 900, 4, 2.5F, 0, 0f, DARK_PURPLE.toString(), 0x7338A5);
        TConstructRegistry.addToolMaterial(MaterialID.Copper, "Copper", 1, 180, 500, 2, 1.15F, 0, 0f, RED.toString(), 0xCC6410);
        TConstructRegistry.addToolMaterial(MaterialID.Bronze, "Bronze", 2, 550, 800, 2, 1.3F, 1, 0f, GOLD.toString(), 0xCA9956);
        TConstructRegistry.addToolMaterial(MaterialID.Alumite, "Alumite", 4, 700, 800, 3, 1.3F, 2, 0f, LIGHT_PURPLE.toString(), 0xffa7e9);
        TConstructRegistry.addToolMaterial(MaterialID.Steel, "Steel", 4, 750, 1000, 4, 1.3F, 2, 0f, GRAY.toString(), 0xA0A0A0);
        TConstructRegistry.addToolMaterial(MaterialID.BlueSlime, "BlueSlime", 0, 1200, 150, 0, 2.0F, 0, 0f, AQUA.toString(), 0x66AEB0);
        TConstructRegistry.addToolMaterial(MaterialID.PigIron, "PigIron", 3, 250, 600, 2, 1.3F, 1, 0f, RED.toString(), 0xF0A8A4);

        // Register all the materials for default toolparts
        TConstructRegistry.addDefaultToolPartMaterial(MaterialID.Wood);
        TConstructRegistry.addDefaultToolPartMaterial(MaterialID.Stone);
        TConstructRegistry.addDefaultToolPartMaterial(MaterialID.Iron);
        TConstructRegistry.addDefaultToolPartMaterial(MaterialID.Flint);
        TConstructRegistry.addDefaultToolPartMaterial(MaterialID.Cactus);
        TConstructRegistry.addDefaultToolPartMaterial(MaterialID.Bone);
        TConstructRegistry.addDefaultToolPartMaterial(MaterialID.Obsidian);
        TConstructRegistry.addDefaultToolPartMaterial(MaterialID.Netherrack);
        TConstructRegistry.addDefaultToolPartMaterial(MaterialID.Slime);
        TConstructRegistry.addDefaultToolPartMaterial(MaterialID.Paper);
        TConstructRegistry.addDefaultToolPartMaterial(MaterialID.Cobalt);
        TConstructRegistry.addDefaultToolPartMaterial(MaterialID.Ardite);
        TConstructRegistry.addDefaultToolPartMaterial(MaterialID.Manyullyn);
        TConstructRegistry.addDefaultToolPartMaterial(MaterialID.Copper);
        TConstructRegistry.addDefaultToolPartMaterial(MaterialID.Bronze);
        TConstructRegistry.addDefaultToolPartMaterial(MaterialID.Alumite);
        TConstructRegistry.addDefaultToolPartMaterial(MaterialID.Steel);
        TConstructRegistry.addDefaultToolPartMaterial(MaterialID.BlueSlime );
        TConstructRegistry.addDefaultToolPartMaterial(MaterialID.PigIron);

        // Register all the materials for default toolparts
        //TConstructRegistry.addDefaultShardMaterial(MaterialID.Wood);
        TConstructRegistry.addDefaultShardMaterial(MaterialID.Stone);
        TConstructRegistry.addDefaultShardMaterial(MaterialID.Flint);
        TConstructRegistry.addDefaultShardMaterial(MaterialID.Cactus);
        //TConstructRegistry.addDefaultShardMaterial(MaterialID.Bone);
        TConstructRegistry.addDefaultShardMaterial(MaterialID.Obsidian);
        TConstructRegistry.addDefaultShardMaterial(MaterialID.Netherrack);
        TConstructRegistry.addDefaultShardMaterial(MaterialID.Slime);
        //TConstructRegistry.addDefaultShardMaterial(MaterialID.Paper);
        TConstructRegistry.addDefaultShardMaterial(MaterialID.BlueSlime );

        if(PHConstruct.craftMetalTools)
        {
            TConstructRegistry.addDefaultShardMaterial(MaterialID.Iron);
            TConstructRegistry.addDefaultShardMaterial(MaterialID.Cobalt);
            TConstructRegistry.addDefaultShardMaterial(MaterialID.Ardite);
            TConstructRegistry.addDefaultShardMaterial(MaterialID.Manyullyn);
            TConstructRegistry.addDefaultShardMaterial(MaterialID.Copper);
            TConstructRegistry.addDefaultShardMaterial(MaterialID.Bronze);
            TConstructRegistry.addDefaultShardMaterial(MaterialID.Alumite);
            TConstructRegistry.addDefaultShardMaterial(MaterialID.Steel);
            TConstructRegistry.addDefaultShardMaterial(MaterialID.PigIron);
        }

        PatternBuilder pb = PatternBuilder.instance;
        if (PHConstruct.enableTWood)
            pb.registerFullMaterial(Blocks.planks, 2, "Wood", new ItemStack(Items.stick), new ItemStack(Items.stick), 0);
        else
            pb.registerMaterialSet("Wood", new ItemStack(Items.stick, 2), new ItemStack(Items.stick), 0);
        if (PHConstruct.enableTStone)
        {
            pb.registerFullMaterial(Blocks.stone, 2, "Stone", new ItemStack(TinkerTools.toolShard, 1, 1), new ItemStack(TinkerTools.toolRod, 1, 1), 1);
            pb.registerMaterial(Blocks.cobblestone, 2, "Stone");
        }
        else
            pb.registerMaterialSet("Stone", new ItemStack(TinkerTools.toolShard, 1, 1), new ItemStack(TinkerTools.toolRod, 1, 1), 0);
        pb.registerFullMaterial(Items.iron_ingot, 2, "Iron", new ItemStack(TinkerTools.toolShard, 1, 2), new ItemStack(TinkerTools.toolRod, 1, 2), 2);
        if (PHConstruct.enableTFlint)
            pb.registerFullMaterial(Items.flint, 2, "Flint", new ItemStack(TinkerTools.toolShard, 1, 3), new ItemStack(TinkerTools.toolRod, 1, 3), 3);
        else
            pb.registerMaterialSet("Flint", new ItemStack(TinkerTools.toolShard, 1, 3), new ItemStack(TinkerTools.toolRod, 1, 3), 3);
        if (PHConstruct.enableTCactus)
            pb.registerFullMaterial(Blocks.cactus, 2, "Cactus", new ItemStack(TinkerTools.toolShard, 1, 4), new ItemStack(TinkerTools.toolRod, 1, 4), 4);
        else
            pb.registerMaterialSet("Cactus", new ItemStack(TinkerTools.toolShard, 1, 4), new ItemStack(TinkerTools.toolRod, 1, 4), 4);
        if (PHConstruct.enableTBone)
            pb.registerFullMaterial(Items.bone, 2, "Bone", new ItemStack(Items.dye, 1, 15), new ItemStack(Items.bone), 5);
        else
            pb.registerMaterialSet("Bone", new ItemStack(Items.dye, 1, 15), new ItemStack(Items.bone), 5);
        pb.registerFullMaterial(Blocks.obsidian, 2, "Obsidian", new ItemStack(TinkerTools.toolShard, 1, 6), new ItemStack(TinkerTools.toolRod, 1, 6), 6);
        pb.registerMaterial(new ItemStack(materials, 1, 18), 2, "Obsidian");
        if (PHConstruct.enableTNetherrack)
            pb.registerFullMaterial(Blocks.netherrack, 2, "Netherrack", new ItemStack(TinkerTools.toolShard, 1, 7), new ItemStack(TinkerTools.toolRod, 1, 7), 7);
        else
            pb.registerMaterialSet("Netherrack", new ItemStack(TinkerTools.toolShard, 1, 7), new ItemStack(TinkerTools.toolRod, 1, 7), 7);
        if (PHConstruct.enableTSlime)
            pb.registerFullMaterial(new ItemStack(materials, 1, 1), 2, "Slime", new ItemStack(toolShard, 1, 8), new ItemStack(toolRod, 1, 8), 8);
        else
            pb.registerMaterialSet("Slime", new ItemStack(TinkerTools.toolShard, 1, 8), new ItemStack(TinkerTools.toolRod, 1, 17), 8);
        if (PHConstruct.enableTPaper)
            pb.registerFullMaterial(new ItemStack(materials, 1, 0), 2, "Paper", new ItemStack(Items.paper, 2), new ItemStack(toolRod, 1, 9), 9);
        else
            pb.registerMaterialSet("Paper", new ItemStack(Items.paper, 2), new ItemStack(TinkerTools.toolRod, 1, 9), 9);
        pb.registerMaterialSet("Cobalt", new ItemStack(toolShard, 1, 10), new ItemStack(toolRod, 1, 10), 10);
        pb.registerMaterialSet("Ardite", new ItemStack(toolShard, 1, 11), new ItemStack(toolRod, 1, 11), 11);
        pb.registerMaterialSet("Manyullyn", new ItemStack(toolShard, 1, 12), new ItemStack(toolRod, 1, 12), 12);
        pb.registerMaterialSet("Copper", new ItemStack(toolShard, 1, 13), new ItemStack(toolRod, 1, 13), 13);
        pb.registerMaterialSet("Bronze", new ItemStack(toolShard, 1, 14), new ItemStack(toolRod, 1, 14), 14);
        pb.registerMaterialSet("Alumite", new ItemStack(toolShard, 1, 15), new ItemStack(toolRod, 1, 15), 15);
        pb.registerMaterialSet("Steel", new ItemStack(toolShard, 1, 16), new ItemStack(toolRod, 1, 16), 16);
        if (PHConstruct.enableTBlueSlime)
            pb.registerFullMaterial(new ItemStack(materials, 1, 17), 2, "BlueSlime", new ItemStack(toolShard, 1, 17), new ItemStack(toolRod, 1, 17), 17);
        else
            pb.registerMaterialSet("BlueSlime", new ItemStack(TinkerTools.toolShard, 1, 17), new ItemStack(TinkerTools.toolRod, 1, 17), 17);
        pb.registerFullMaterial(new ItemStack(materials, 1, 34), 2, "PigIron", new ItemStack(toolShard, 1, 18), new ItemStack(toolRod, 1, 18), 18);

        pb.addToolPattern((IPattern) TinkerTools.woodPattern);
    }

    private void registerStencils ()
    {
        StencilBuilder.registerBlankStencil(new ItemStack(TinkerTools.blankPattern));

        // we register this manually because we want that specific order
        StencilBuilder.registerStencil(0, TinkerTools.woodPattern, 1); // tool rod
        StencilBuilder.registerStencil(1, TinkerTools.woodPattern, 9); // binding
        StencilBuilder.registerStencil(2, TinkerTools.woodPattern, 14); // large tool rod
        StencilBuilder.registerStencil(3, TinkerTools.woodPattern, 15); // large binding

        StencilBuilder.registerStencil(4, TinkerTools.woodPattern, 2); // pickaxe head
        StencilBuilder.registerStencil(5, TinkerTools.woodPattern, 3); // shovel head
        StencilBuilder.registerStencil(6, TinkerTools.woodPattern, 4); // hatchet head
        StencilBuilder.registerStencil(7, TinkerTools.woodPattern, 18); // scythe

        StencilBuilder.registerStencil(8, TinkerTools.woodPattern, 21); // hammer head
        StencilBuilder.registerStencil(9, TinkerTools.woodPattern, 19); // excavator head
        StencilBuilder.registerStencil(10, TinkerTools.woodPattern, 17); // lumberaxe head
        StencilBuilder.registerStencil(11, TinkerTools.woodPattern, 16); // large plate

        StencilBuilder.registerStencil(12, TinkerTools.woodPattern, 10); // frying pan
        StencilBuilder.registerStencil(13, TinkerTools.woodPattern, 11); // battlesign
        StencilBuilder.registerStencil(14, TinkerTools.woodPattern, 13); // chisel

        StencilBuilder.registerStencil(15, TinkerTools.woodPattern, 12); // knifeblade
        StencilBuilder.registerStencil(16, TinkerTools.woodPattern, 5); // swordblade
        StencilBuilder.registerStencil(17, TinkerTools.woodPattern, 20); // cleaver blade

        StencilBuilder.registerStencil(18, TinkerTools.woodPattern, 8); // crossbar
        StencilBuilder.registerStencil(19, TinkerTools.woodPattern, 7); // small guard
        StencilBuilder.registerStencil(20, TinkerTools.woodPattern, 6); // wide guard
    }

    public static final class MaterialID
    {
        public static final int Wood = 0;
        public static final int Stone = 1;
        public static final int Iron = 2;
        public static final int Flint = 3;
        public static final int Cactus = 4;
        public static final int Bone = 5;
        public static final int Obsidian = 6;
        public static final int Netherrack = 7;
        public static final int Slime = 8;
        public static final int Paper = 9;
        public static final int Cobalt = 10;
        public static final int Ardite = 11;
        public static final int Manyullyn = 12; // I'd still call it Manymetal --boni
        public static final int Copper = 13;
        public static final int Bronze = 14;
        public static final int Alumite = 15;
        public static final int Steel = 16;
        public static final int BlueSlime = 17;
        public static final int PigIron = 18;

        public static final int Thaumium = 31;
    }
}

package mods.tinker.tconstruct;

import java.util.ArrayList;
import java.util.List;

import mods.tinker.common.IPattern;
import mods.tinker.common.RecipeRemover;
import mods.tinker.common.fancyitem.FancyEntityItem;
import mods.tinker.tconstruct.blocks.EquipBlock;
import mods.tinker.tconstruct.blocks.LavaTankBlock;
import mods.tinker.tconstruct.blocks.LiquidMetalFlowing;
import mods.tinker.tconstruct.blocks.LiquidMetalStill;
import mods.tinker.tconstruct.blocks.MetalOre;
import mods.tinker.tconstruct.blocks.SearedBlock;
import mods.tinker.tconstruct.blocks.SmelteryBlock;
import mods.tinker.tconstruct.blocks.TConstructBlock;
import mods.tinker.tconstruct.blocks.ToolStationBlock;
import mods.tinker.tconstruct.crafting.LiquidCasting;
import mods.tinker.tconstruct.crafting.PatternBuilder;
import mods.tinker.tconstruct.crafting.Smeltery;
import mods.tinker.tconstruct.crafting.ToolBuilder;
import mods.tinker.tconstruct.entity.BlueSlime;
import mods.tinker.tconstruct.entity.CartEntity;
import mods.tinker.tconstruct.entity.Crystal;
import mods.tinker.tconstruct.entity.MetalSlime;
import mods.tinker.tconstruct.entity.Skyla;
import mods.tinker.tconstruct.entity.UnstableCreeper;
import mods.tinker.tconstruct.items.CraftedSoilItemBlock;
import mods.tinker.tconstruct.items.CraftingItem;
import mods.tinker.tconstruct.items.FilledBucket;
import mods.tinker.tconstruct.items.LavaTankItemBlock;
import mods.tinker.tconstruct.items.LiquidItemBlock;
import mods.tinker.tconstruct.items.MetalItemBlock;
import mods.tinker.tconstruct.items.MetalOreItemBlock;
import mods.tinker.tconstruct.items.MetalPattern;
import mods.tinker.tconstruct.items.Pattern;
import mods.tinker.tconstruct.items.PatternManual;
import mods.tinker.tconstruct.items.SearedTableItemBlock;
import mods.tinker.tconstruct.items.SmelteryItemBlock;
import mods.tinker.tconstruct.items.StrangeFood;
import mods.tinker.tconstruct.items.TitleIcon;
import mods.tinker.tconstruct.items.ToolPart;
import mods.tinker.tconstruct.items.ToolShard;
import mods.tinker.tconstruct.items.ToolStationItemBlock;
import mods.tinker.tconstruct.library.TConstructRegistry;
import mods.tinker.tconstruct.library.ToolCore;
import mods.tinker.tconstruct.logic.CastingTableLogic;
import mods.tinker.tconstruct.logic.FaucetLogic;
import mods.tinker.tconstruct.logic.FrypanLogic;
import mods.tinker.tconstruct.logic.LavaTankLogic;
import mods.tinker.tconstruct.logic.LiquidTextureLogic;
import mods.tinker.tconstruct.logic.MultiServantLogic;
import mods.tinker.tconstruct.logic.PartCrafterLogic;
import mods.tinker.tconstruct.logic.PatternChestLogic;
import mods.tinker.tconstruct.logic.PatternShaperLogic;
import mods.tinker.tconstruct.logic.SmelteryDrainLogic;
import mods.tinker.tconstruct.logic.SmelteryLogic;
import mods.tinker.tconstruct.logic.ToolStationLogic;
import mods.tinker.tconstruct.modifiers.ModBlaze;
import mods.tinker.tconstruct.modifiers.ModBoolean;
import mods.tinker.tconstruct.modifiers.ModDurability;
import mods.tinker.tconstruct.modifiers.ModElectric;
import mods.tinker.tconstruct.modifiers.ModInteger;
import mods.tinker.tconstruct.modifiers.ModLapisBase;
import mods.tinker.tconstruct.modifiers.ModLapisIncrease;
import mods.tinker.tconstruct.modifiers.ModRedstone;
import mods.tinker.tconstruct.modifiers.ModRepair;
import mods.tinker.tconstruct.tools.Axe;
import mods.tinker.tconstruct.tools.BattleSign;
import mods.tinker.tconstruct.tools.Broadsword;
import mods.tinker.tconstruct.tools.FryingPan;
import mods.tinker.tconstruct.tools.Longsword;
import mods.tinker.tconstruct.tools.Mattock;
import mods.tinker.tconstruct.tools.Pickaxe;
import mods.tinker.tconstruct.tools.Rapier;
import mods.tinker.tconstruct.tools.Shovel;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.liquids.LiquidContainerData;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class TContent implements IFuelHandler
{
	//Patterns and other materials
	public static Item blankPattern;
	public static Item materials;
	public static Item toolRod;
	public static Item toolShard;
	public static Item woodPattern;
	public static Item metalPattern;

	public static Item manualBook;
	public static Item buckets;
	public static Item titleIcon;
	
	public static Item strangeFood;
	//public static Item stonePattern;
	//public static Item netherPattern;

	//Tools
	public static ToolCore pickaxe;
	public static ToolCore shovel;
	public static ToolCore axe;
	public static ToolCore broadsword;
	public static ToolCore longsword;
	public static ToolCore rapier;

	public static ToolCore frypan;
	public static ToolCore battlesign;
	//public static ToolCore longbow;

	public static ToolCore mattock;
	public static ToolCore lumberaxe;

	//Tool parts
	public static Item pickaxeHead;
	public static Item shovelHead;
	public static Item axeHead;
	public static Item swordBlade;
	public static Item largeGuard;
	public static Item medGuard;
	public static Item crossbar;
	public static Item binding;

	public static Item frypanHead;
	public static Item signHead;

	public static Item lumberHead;

	//Crafting blocks
	public static Block woodCrafter;
	public static Block heldItemBlock;
	public static Block craftedSoil;

	public static Block smeltery;
	public static Block lavaTank;
	public static Block searedBlock;
	public static Block oreSlag;
	public static Block metalBlock;

	//Traps
	public static Block landmine;

	//Liquids
	public static Block liquidMetalFlowing;
	public static Block liquidMetalStill;
	public static Material liquidMetal;

	//public static Block axle;

	//Tool modifiers
	public static ModElectric modE;

	public TContent()
	{
		createEntities();
		registerBlocks();
		registerItems();
		registerMaterials();
		addToolRecipes();
		addSmelteryRecipes();
		addCraftingRecipes();
		setupToolTabs();
		GameRegistry.registerFuelHandler(this);
	}

	void createEntities ()
	{
		EntityRegistry.registerModEntity(FancyEntityItem.class, "Fancy Item", 0, TConstruct.instance, 32, 5, true);
		EntityRegistry.registerModEntity(CartEntity.class, "Small Wagon", 1, TConstruct.instance, 32, 5, true);
		EntityRegistry.registerModEntity(Crystal.class, "Crystal", 2, TConstruct.instance, 32, 5, true);

		EntityRegistry.registerModEntity(Skyla.class, "Skyla", 10, TConstruct.instance, 32, 5, true);
		EntityRegistry.registerModEntity(UnstableCreeper.class, "UnstableCreeper", 11, TConstruct.instance, 64, 5, true);
		EntityRegistry.registerModEntity(BlueSlime.class, "EdibleSlime", 12, TConstruct.instance, 64, 5, true);
		EntityRegistry.registerModEntity(MetalSlime.class, "MetalSlime", 13, TConstruct.instance, 64, 5, true);

		BiomeGenBase[] overworldBiomes = new BiomeGenBase[] { BiomeGenBase.ocean, BiomeGenBase.plains, BiomeGenBase.desert, BiomeGenBase.extremeHills, BiomeGenBase.forest, BiomeGenBase.taiga,
				BiomeGenBase.swampland, BiomeGenBase.river, BiomeGenBase.frozenOcean, BiomeGenBase.frozenRiver, BiomeGenBase.icePlains, BiomeGenBase.iceMountains, BiomeGenBase.beach,
				BiomeGenBase.desertHills, BiomeGenBase.forestHills, BiomeGenBase.taigaHills, BiomeGenBase.extremeHillsEdge, BiomeGenBase.jungle, BiomeGenBase.jungleHills };
		EntityRegistry.addSpawn(UnstableCreeper.class, 8, 4, 6, EnumCreatureType.monster, overworldBiomes);
		EntityRegistry.addSpawn(BlueSlime.class, 10, 4, 4, EnumCreatureType.monster, overworldBiomes);
		//EntityRegistry.addSpawn(MetalSlime.class, 1, 4, 4, EnumCreatureType.monster, overworldBiomes);
	}

	void registerBlocks ()
	{
		//Tool Station
		woodCrafter = new ToolStationBlock(PHConstruct.woodCrafter, Material.wood);
		GameRegistry.registerBlock(woodCrafter, ToolStationItemBlock.class, "ToolStationBlock");
		GameRegistry.registerTileEntity(ToolStationLogic.class, "ToolStation");
		GameRegistry.registerTileEntity(PartCrafterLogic.class, "PartCrafter");
		GameRegistry.registerTileEntity(PatternChestLogic.class, "PatternHolder");
		GameRegistry.registerTileEntity(PatternShaperLogic.class, "PatternShaper");

		heldItemBlock = new EquipBlock(PHConstruct.heldItemBlock, Material.wood);
		GameRegistry.registerBlock(heldItemBlock, "HeldItemBlock");
		GameRegistry.registerTileEntity(FrypanLogic.class, "FrypanLogic");

		String[] soilTypes = new String[] { "slimesand", "grout" };
		craftedSoil = new TConstructBlock(PHConstruct.craftedSoil, Material.sand, 3.0F, soilTypes);
		craftedSoil.stepSound = Block.soundGravelFootstep;
		GameRegistry.registerBlock(craftedSoil, CraftedSoilItemBlock.class, "CraftedSoil");

		String[] metalTypes = new String[] { "compressed_cobalt", "compressed_ardite", "compressed_manyullyn", "compressed_copper", "compressed_bronze", "compressed_tin", "compressed_aluminum",
				"compressed_alubrass", "compressed_alumite", "compressed_steel" };
		metalBlock = new TConstructBlock(PHConstruct.metalBlock, Material.iron, 10.0F, metalTypes);
		metalBlock.stepSound = Block.soundMetalFootstep;
		GameRegistry.registerBlock(metalBlock, MetalItemBlock.class, "MetalBlock");

		//Smeltery
		smeltery = new SmelteryBlock(PHConstruct.smeltery).setUnlocalizedName("Smeltery");
		GameRegistry.registerBlock(smeltery, SmelteryItemBlock.class, "Smeltery");
		GameRegistry.registerTileEntity(SmelteryLogic.class, "TConstruct.Smeltery");
		GameRegistry.registerTileEntity(SmelteryDrainLogic.class, "TConstruct.SmelteryDrain");
		GameRegistry.registerTileEntity(MultiServantLogic.class, "TConstruct.Servants");

		lavaTank = new LavaTankBlock(PHConstruct.lavaTank).setUnlocalizedName("LavaTank");
		lavaTank.setStepSound(Block.soundGlassFootstep);
		GameRegistry.registerBlock(lavaTank, LavaTankItemBlock.class, "LavaTank");
		GameRegistry.registerTileEntity(LavaTankLogic.class, "TConstruct.LavaTank");

		searedBlock = new SearedBlock(PHConstruct.searedTable).setUnlocalizedName("SearedBlock");
		GameRegistry.registerBlock(searedBlock, SearedTableItemBlock.class, "SearedBlock");
		GameRegistry.registerTileEntity(CastingTableLogic.class, "CastingTable");
		GameRegistry.registerTileEntity(FaucetLogic.class, "Faucet");

		String[] oreTypes = new String[] { "nether_slag", "nether_cobalt", "nether_ardite", "ore_copper", "ore_tin", "ore_aluminum", "ore_slag" };
		oreSlag = new MetalOre(PHConstruct.oreSlag, Material.iron, 10.0F, oreTypes);
		GameRegistry.registerBlock(oreSlag, MetalOreItemBlock.class, "SearedBrick");
		//MinecraftForge.setBlockHarvestLevel(oreSlag, 0, "pickaxe", 0);
		MinecraftForge.setBlockHarvestLevel(oreSlag, 1, "pickaxe", 4);
		MinecraftForge.setBlockHarvestLevel(oreSlag, 2, "pickaxe", 4);
		MinecraftForge.setBlockHarvestLevel(oreSlag, 3, "pickaxe", 1);
		MinecraftForge.setBlockHarvestLevel(oreSlag, 4, "pickaxe", 1);
		MinecraftForge.setBlockHarvestLevel(oreSlag, 5, "pickaxe", 1);

		//Traps
		/*landmine = new Landmine(PHConstruct.landmine, 0, EnumMobType.mobs, Material.cactus).setUnlocalizedName("landmine");
		GameRegistry.registerBlock(landmine, "landmine");*/

		//Liquids
		liquidMetal = new MaterialLiquid(MapColor.tntColor);
		liquidMetalFlowing = new LiquidMetalFlowing(PHConstruct.metalFlowing).setUnlocalizedName("liquid.metalFlow");
		liquidMetalStill = new LiquidMetalStill(PHConstruct.metalStill).setUnlocalizedName("liquid.metalStill");
		GameRegistry.registerBlock(liquidMetalFlowing, LiquidItemBlock.class, "metalFlow");
		GameRegistry.registerBlock(liquidMetalStill, LiquidItemBlock.class, "metalStill");
		GameRegistry.registerTileEntity(LiquidTextureLogic.class, "LiquidTexture");
	}

	void registerItems ()
	{
		titleIcon = new TitleIcon(PHConstruct.uselessItem).setUnlocalizedName("tconstruct.titleicon");
		String[] blanks = new String[] { "blank_pattern", "blank_cast" };
		blankPattern = new CraftingItem(PHConstruct.blankPattern, blanks, blanks, "materials/").setUnlocalizedName("tconstruct.Pattern");
		
		String[] craftingMaterials = new String[] { "PaperStack", "SlimeCrystal", "SearedBrick", "CobaltIngot", "ArditeIngot", "ManyullynIngot", "Mossball", "LavaCrystal", "NecroticBone",
				"CopperIngot", "TinIngot", "AluminumIngot", "RawAluminum", "BronzeIngot", "AlBrassIngot", "AlumiteIngot", "SteelIngot" };
		String[] craftingTextures = new String[] { "material_paperstack", "material_slimecrystal", "material_searedbrick", "material_cobaltingot", "material_arditeingot", "material_manyullyningot",
				"material_mossball", "material_lavacrystal", "material_necroticbone", "material_copperingot", "material_tiningot", "material_aluminumingot", "material_aluminumraw",
				"material_bronzeingot", "material_alubrassingot", "material_alumiteingot", "material_steelingot" };
		
		materials = new CraftingItem(PHConstruct.materials, craftingMaterials, craftingTextures, "materials/").setUnlocalizedName("tconstruct.Materials");
		toolRod = new ToolPart(PHConstruct.toolRod, "ToolRod", "_rod").setUnlocalizedName("tconstruct.ToolRod");
		toolShard = new ToolShard(PHConstruct.toolShard, "ToolShard", "_chunk").setUnlocalizedName("tconstruct.ToolShard");
		woodPattern = new Pattern(PHConstruct.woodPattern, "WoodPattern", "pattern_", "materials/").setUnlocalizedName("tconstruct.Pattern");
		metalPattern = new MetalPattern(PHConstruct.metalPattern, "MetalPattern", "cast_", "materials/").setUnlocalizedName("tconstruct.MetalPattern");
		//stonePattern = new Pattern(PHTools.stonePattern, 64, patternTexture).setUnlocalizedName("tconstruct.Pattern");
		//netherPattern = new Pattern(PHTools.netherPattern, 128, patternTexture).setUnlocalizedName("tconstruct.Pattern");

		manualBook = new PatternManual(PHConstruct.manual);
		buckets = new FilledBucket(PHConstruct.buckets);

		pickaxe = new Pickaxe(PHConstruct.pickaxe);
		shovel = new Shovel(PHConstruct.shovel);
		axe = new Axe(PHConstruct.axe);
		broadsword = new Broadsword(PHConstruct.broadsword);
		longsword = new Longsword(PHConstruct.longsword);
		rapier = new Rapier(PHConstruct.rapier);

		frypan = new FryingPan(PHConstruct.frypan);
		battlesign = new BattleSign(PHConstruct.battlesign);
		//longbow = new RangedWeapon(PHConstruct.longbow);
		mattock = new Mattock(PHConstruct.mattock);
		//lumberaxe = new LumberAxe(PHConstruct.lumberaxe, lumberaxeTexture);

		pickaxeHead = new ToolPart(PHConstruct.pickaxeHead, "PickaxeHead", "_pickaxe_head").setUnlocalizedName("tconstruct.PickaxeHead");
		shovelHead = new ToolPart(PHConstruct.shovelHead, "ShovelHead", "_shovel_head").setUnlocalizedName("tconstruct.ShovelHead");
		axeHead = new ToolPart(PHConstruct.axeHead, "AxeHead", "_axe_head").setUnlocalizedName("tconstruct.AxeHead");
		swordBlade = new ToolPart(PHConstruct.swordBlade, "SwordBlade", "_sword_blade").setUnlocalizedName("tconstruct.SwordBlade");
		largeGuard = new ToolPart(PHConstruct.largeGuard, "LargeGuard", "_large_guard").setUnlocalizedName("tconstruct.LargeGuard");
		medGuard = new ToolPart(PHConstruct.medGuard, "MediumGuard", "_medium_guard").setUnlocalizedName("tconstruct.MediumGuard");
		crossbar = new ToolPart(PHConstruct.crossbar, "Crossbar", "_crossbar").setUnlocalizedName("tconstruct.Crossbar");
		binding = new ToolPart(PHConstruct.binding, "Binding", "_binding").setUnlocalizedName("tconstruct.Binding");

		frypanHead = new ToolPart(PHConstruct.frypanHead, "FrypanHead", "_frypan_head").setUnlocalizedName("tconstruct.FrypanHead");
		signHead = new ToolPart(PHConstruct.signHead, "SignHead", "_battlesign_head").setUnlocalizedName("tconstruct.SignHead");

		strangeFood = new StrangeFood(PHConstruct.slimefood).setUnlocalizedName("tconstruct.strangefood");
		//lumberHead = new ToolPart(PHConstruct.lumberHead, 0, broadheads).setUnlocalizedName("tconstruct.LumberHead");
		Item.doorWood.setMaxStackSize(16);
		Item.doorSteel.setMaxStackSize(16);
		Item.snowball.setMaxStackSize(64);
		Item.boat.setMaxStackSize(16);
		Item.minecartEmpty.setMaxStackSize(3);
		Item.minecartCrate.setMaxStackSize(3);
		Item.minecartPowered.setMaxStackSize(3);
	}

	void registerMaterials ()
	{
		TConstructRegistry.addToolMaterial(0, "Wood", 1, 0, 59, 200, 0, 1.0F, 0, 0f);
		TConstructRegistry.addToolMaterial(1, "Stone", 1, 1, 131, 400, 1, 0.5F, 0, 1f);
		TConstructRegistry.addToolMaterial(2, "Iron", 1, 2, 250, 600, 2, 1.3F, 1, 0f);
		TConstructRegistry.addToolMaterial(3, "Flint", 1, 1, 171, 525, 2, 0.7F, 0, 1f);
		TConstructRegistry.addToolMaterial(4, "Cactus", 1, 1, 150, 500, 2, 1.0F, 0, -1f);
		TConstructRegistry.addToolMaterial(5, "Bone", 1, 1, 200, 400, 1, 1.0F, 0, 0f);
		TConstructRegistry.addToolMaterial(6, "Obsidian", 1, 3, 89, 700, 2, 0.8F, 3, 0f);
		TConstructRegistry.addToolMaterial(7, "Netherrack", 1, 2, 131, 400, 1, 1.2F, 0, 1f);
		TConstructRegistry.addToolMaterial(8, "Slime", 1, 3, 1500, 150, 0, 5.0F, 0, 0f);
		TConstructRegistry.addToolMaterial(9, "Paper", 1, 0, 30, 200, 0, 0.3F, 0, 0f);
		TConstructRegistry.addToolMaterial(10, "Cobalt", 2, 4, 800, 800, 3, 1.75F, 2, 0f);
		TConstructRegistry.addToolMaterial(11, "Ardite", 2, 4, 600, 800, 3, 2.0F, 0, 0f);
		TConstructRegistry.addToolMaterial(12, "Manyullyn", 2, 5, 1200, 1000, 4, 2.5F, 0, 0f);
		TConstructRegistry.addToolMaterial(13, "Copper", 1, 1, 180, 500, 2, 1.15F, 0, 0f);
		TConstructRegistry.addToolMaterial(14, "Bronze", 1, 2, 250, 600, 2, 1.3F, 1, 0f);
		TConstructRegistry.addToolMaterial(15, "Alumite", 2, 4, 550, 800, 3, 1.3F, 2, 0f);
		TConstructRegistry.addToolMaterial(16, "Steel", 2, 3, 750, 800, 3, 1.3F, 2, 0f);

		//Thaumcraft
		TConstructRegistry.addToolMaterial(21, "Thaumium", 2, 2, 250, 600, 2, 1.3F, 1, 0f);
		
		//Metallurgy
		TConstructRegistry.addToolMaterial(22, "Heptazion", 2, 2, 300, 800, 1, 1.0F, 0, 0f);
		TConstructRegistry.addToolMaterial(23, "Damascus Steel", 2, 3, 500, 600, 2, 1.35F, 1, 0f);
		TConstructRegistry.addToolMaterial(24, "Angmallen", 2, 2, 300, 800, 2, 0.8F, 0, 0f);

		TConstructRegistry.addToolMaterial(25, "Promethium", 1, 1, 200, 400, 1, 1.0F, 0, 0.5f);
		TConstructRegistry.addToolMaterial(26, "Deep Iron", 1, 2, 250, 600, 2, 1.3F, 1, 0f);
		TConstructRegistry.addToolMaterial(27, "Oureclase", 2, 3, 750, 800, 2, 1.2F, 0, 0f);
		TConstructRegistry.addToolMaterial(28, "Aredrite", 2, 3, 1000, 400, 2, 1.5F, 0, 1.0f);
		TConstructRegistry.addToolMaterial(29, "Astral Silver", 1, 4, 35, 1200, 1, 0.5F, 0, 0f);
		TConstructRegistry.addToolMaterial(30, "Carmot", 1, 4, 50, 1200, 1, 0.5F, 0, 0f);
		TConstructRegistry.addToolMaterial(31, "Mithril", 2, 4, 1000, 900, 3, 1.25F, 3, 0f);
		TConstructRegistry.addToolMaterial(32, "Orichalcum", 2, 5, 1350, 900, 3, 1.25F, 0, 0f);
		TConstructRegistry.addToolMaterial(33, "Adamantine", 3, 6, 1550, 1000, 4, 1.5F, 1, 0f);
		TConstructRegistry.addToolMaterial(34, "Atlarus", 3, 6, 1750, 1000, 4, 1.6F, 2, 0f);

		TConstructRegistry.addToolMaterial(35, "Black Steel", 2, 2, 500, 800, 2, 1.3F, 2, 0f);
		TConstructRegistry.addToolMaterial(36, "Quicksilver", 2, 4, 1100, 1400, 3, 1.0F, 1, 0f);
		TConstructRegistry.addToolMaterial(37, "Haderoth", 2, 4, 1250, 1200, 3, 1.0F, 2, 0f);
		TConstructRegistry.addToolMaterial(38, "Celenegil", 3, 5, 1600, 1400, 3, 1.0F, 2, 0f);
		TConstructRegistry.addToolMaterial(39, "Tartarite", 3, 7, 3000, 1400, 5, 1.6667F, 4, 0f);

		PatternBuilder pb = PatternBuilder.instance;
		if (PHConstruct.enableTWood)
			pb.registerFullMaterial(Block.planks, 2, "Wood", new ItemStack(Item.stick, 2), new ItemStack(Item.stick), 0);
		else
			pb.registerMaterialSet("Wood", new ItemStack(Item.stick, 2), new ItemStack(Item.stick), 0);
		if (PHConstruct.enableTStone)
		{
			pb.registerFullMaterial(Block.stone, 2, "Stone", 1);
			pb.registerMaterial(Block.cobblestone, 2, "Stone");
		}
		//else
		//pb.registerMaterialSet("Stone", new ItemStack(TContent.toolShard, 1, 1), new ItemStack(TContent.toolRod, 1, 1), 1);
		pb.registerFullMaterial(Item.ingotIron, 2, "Iron", 2);
		pb.registerFullMaterial(Item.flint, 2, "Flint", 3);
		if (PHConstruct.enableTCactus)
			pb.registerFullMaterial(Block.cactus, 2, "Cactus", 4);
		else
			pb.registerMaterialSet("Cactus", new ItemStack(TContent.toolShard, 1, 4), new ItemStack(TContent.toolRod, 1, 4), 4);
		if (PHConstruct.enableTBone)
			pb.registerFullMaterial(Item.bone, 2, "Bone", new ItemStack(Item.dyePowder, 1, 15), new ItemStack(Item.bone), 5);
		else
			pb.registerMaterialSet("Bone", new ItemStack(Item.dyePowder, 1, 15), new ItemStack(Item.bone), 5);
		pb.registerFullMaterial(Block.obsidian, 2, "Obsidian", 6);
		pb.registerFullMaterial(Block.netherrack, 2, "Netherrack", 7);
		pb.registerFullMaterial(new ItemStack(materials, 1, 1), 2, "Slime", new ItemStack(toolShard, 1, 8), new ItemStack(toolRod, 1, 8), 8);
		pb.registerFullMaterial(new ItemStack(materials, 1, 0), 2, "Paper", new ItemStack(Item.paper, 2), new ItemStack(toolRod, 1, 9), 9);
		pb.registerMaterialSet("Cobalt", new ItemStack(toolShard, 1, 10), new ItemStack(toolRod, 1, 10), 10);
		pb.registerMaterialSet("Ardite", new ItemStack(toolShard, 1, 11), new ItemStack(toolRod, 1, 11), 11);
		pb.registerMaterialSet("Manyullyn", new ItemStack(toolShard, 1, 12), new ItemStack(toolRod, 1, 12), 12);
		pb.registerMaterialSet("Copper", new ItemStack(toolShard, 1, 13), new ItemStack(toolRod, 1, 13), 13);
		pb.registerMaterialSet("Bronze", new ItemStack(toolShard, 1, 14), new ItemStack(toolRod, 1, 14), 14);
		pb.registerMaterialSet("Alumite", new ItemStack(toolShard, 1, 15), new ItemStack(toolRod, 1, 15), 15);
		pb.registerMaterialSet("Steel", new ItemStack(toolShard, 1, 16), new ItemStack(toolRod, 1, 16), 16);

		pb.addToolPattern((IPattern) woodPattern);
	}

	public static Item[] patternOutputs;
	public static LiquidStack[] liquids;

	void addToolRecipes ()
	{
		List<ItemStack> removeTools = new ArrayList<ItemStack>();
		if (PHConstruct.disableWoodTools)
		{
			removeTools.add(new ItemStack(Item.pickaxeWood));
			removeTools.add(new ItemStack(Item.axeWood));
			removeTools.add(new ItemStack(Item.shovelWood));
			removeTools.add(new ItemStack(Item.swordWood));
			removeTools.add(new ItemStack(Item.hoeWood));
		}
		if (PHConstruct.disableStoneTools)
		{
			removeTools.add(new ItemStack(Item.pickaxeStone));
			removeTools.add(new ItemStack(Item.axeStone));
			removeTools.add(new ItemStack(Item.shovelStone));
			removeTools.add(new ItemStack(Item.swordStone));
			removeTools.add(new ItemStack(Item.hoeStone));
		}
		if (PHConstruct.disableIronTools)
		{
			removeTools.add(new ItemStack(Item.pickaxeSteel));
			removeTools.add(new ItemStack(Item.axeSteel));
			removeTools.add(new ItemStack(Item.shovelSteel));
			removeTools.add(new ItemStack(Item.swordSteel));
			removeTools.add(new ItemStack(Item.hoeSteel));
		}
		if (PHConstruct.disableDiamondTools)
		{
			removeTools.add(new ItemStack(Item.pickaxeDiamond));
			removeTools.add(new ItemStack(Item.axeDiamond));
			removeTools.add(new ItemStack(Item.shovelDiamond));
			removeTools.add(new ItemStack(Item.swordDiamond));
			removeTools.add(new ItemStack(Item.hoeDiamond));
		}
		if (PHConstruct.disableGoldTools)
		{
			removeTools.add(new ItemStack(Item.pickaxeGold));
			removeTools.add(new ItemStack(Item.axeGold));
			removeTools.add(new ItemStack(Item.shovelGold));
			removeTools.add(new ItemStack(Item.swordGold));
			removeTools.add(new ItemStack(Item.hoeGold));
		}

		RecipeRemover.removeShapedRecipes(removeTools);

		patternOutputs = new Item[] { toolRod, pickaxeHead, shovelHead, axeHead, swordBlade, largeGuard, medGuard, crossbar, binding, frypanHead, signHead };

		ToolBuilder tb = ToolBuilder.instance;
		tb.addToolRecipe(pickaxe, pickaxeHead, binding);
		tb.addToolRecipe(broadsword, swordBlade, largeGuard);
		tb.addToolRecipe(axe, axeHead);
		tb.addToolRecipe(shovel, shovelHead);
		tb.addToolRecipe(longsword, swordBlade, medGuard);
		tb.addToolRecipe(rapier, swordBlade, crossbar);
		tb.addToolRecipe(frypan, frypanHead);
		tb.addToolRecipe(battlesign, signHead);
		tb.addToolRecipe(mattock, axeHead, shovelHead);
		//tb.addToolRecipe(longbow, toolRod, toolRod);
		//tb.addToolRecipe(lumberaxe, lumberHead);

		tb.registerToolMod(new ModRepair());
		tb.registerToolMod(new ModDurability(new ItemStack[] { new ItemStack(Item.diamond) }, 0, 500, 0f, 3, "Diamond", "\u00a7bDurability +500", "\u00a7b"));
		tb.registerToolMod(new ModDurability(new ItemStack[] { new ItemStack(Item.emerald) }, 1, 0, 0.5f, 2, "Emerald", "\u00a72Durability +50%", "\u00a72"));
		modE = new ModElectric();
		tb.registerToolMod(modE);
		tb.registerToolMod(new ModRedstone(new ItemStack[] { new ItemStack(Item.redstone) }, 2, 1));
		tb.registerToolMod(new ModRedstone(new ItemStack[] { new ItemStack(Item.redstone), new ItemStack(Item.redstone) }, 2, 2));
		tb.registerToolMod(new ModLapisIncrease(new ItemStack[] { new ItemStack(Item.dyePowder, 1, 4) }, 10, 1));
		tb.registerToolMod(new ModLapisIncrease(new ItemStack[] { new ItemStack(Item.dyePowder, 1, 4), new ItemStack(Item.dyePowder, 1, 4) }, 10, 2));
		tb.registerToolMod(new ModLapisIncrease(new ItemStack[] { new ItemStack(Block.blockLapis) }, 10, 9));
		tb.registerToolMod(new ModLapisIncrease(new ItemStack[] { new ItemStack(Item.dyePowder, 1, 4), new ItemStack(Block.blockLapis) }, 10, 10));
		tb.registerToolMod(new ModLapisIncrease(new ItemStack[] { new ItemStack(Block.blockLapis), new ItemStack(Block.blockLapis) }, 10, 18));
		tb.registerToolMod(new ModLapisBase(new ItemStack[] { new ItemStack(Block.blockLapis), new ItemStack(Block.blockLapis) }, 10));
		tb.registerToolMod(new ModInteger(new ItemStack[] { new ItemStack(materials, 1, 6) }, 4, "Moss", 3, "\u00a72", "Auto-Repair"));
		tb.registerToolMod(new ModBlaze(new ItemStack[] { new ItemStack(Item.blazePowder) }, 7, 1));
		tb.registerToolMod(new ModBlaze(new ItemStack[] { new ItemStack(Item.blazePowder), new ItemStack(Item.blazePowder) }, 7, 2));
		tb.registerToolMod(new ModBoolean(new ItemStack[] { new ItemStack(materials, 1, 7) }, 6, "Lava", "\u00a74", "Auto-Smelt"));
		tb.registerToolMod(new ModInteger(new ItemStack[] { new ItemStack(materials, 1, 8) }, 8, "Necrotic", 1, "\u00a78", "Life Steal"));

		ItemStack ingotcast = new ItemStack(metalPattern, 1, 0);

		LiquidCasting lc = LiquidCasting.instance;
		//Blank
		lc.addCastingRecipe(new ItemStack(blankPattern, 1, 1), new LiquidStack(liquidMetalStill.blockID, 1, 8), 50);

		//Ingots
		lc.addCastingRecipe(new ItemStack(Item.ingotIron), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 0), ingotcast, 75); //Iron
		lc.addCastingRecipe(new ItemStack(Item.ingotGold), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 1), ingotcast, 75); //gold
		lc.addCastingRecipe(new ItemStack(materials, 1, 9), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 2), ingotcast, 75); //copper
		lc.addCastingRecipe(new ItemStack(materials, 1, 10), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 3), ingotcast, 75); //tin
		lc.addCastingRecipe(new ItemStack(materials, 1, 11), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 4), ingotcast, 75); //aluminum
		lc.addCastingRecipe(new ItemStack(materials, 1, 3), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 5), ingotcast, 75); //cobalt
		lc.addCastingRecipe(new ItemStack(materials, 1, 4), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 6), ingotcast, 75); //ardite
		lc.addCastingRecipe(new ItemStack(materials, 1, 13), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 7), ingotcast, 75); //bronze
		lc.addCastingRecipe(new ItemStack(materials, 1, 14), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 8), ingotcast, 75); //albrass
		lc.addCastingRecipe(new ItemStack(materials, 1, 5), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 9), ingotcast, 75); //manyullyn
		lc.addCastingRecipe(new ItemStack(materials, 1, 15), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 10), ingotcast, 75); //alumite
		// obsidian
		lc.addCastingRecipe(new ItemStack(materials, 1, 16), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 12), ingotcast, 75); //steel
		
		//Buckets
		ItemStack bucket = new ItemStack(Item.bucketEmpty);
		lc.addCastingRecipe(new ItemStack(buckets, 1, 0), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue*9, 0), bucket, 10); //Iron
		lc.addCastingRecipe(new ItemStack(buckets, 1, 1), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue*9, 1), bucket, 10); //gold
		lc.addCastingRecipe(new ItemStack(buckets, 1, 2), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue*9, 2), bucket, 10); //copper
		lc.addCastingRecipe(new ItemStack(buckets, 1, 3), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue*9, 3), bucket, 10); //tin
		lc.addCastingRecipe(new ItemStack(buckets, 1, 4), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue*9, 4), bucket, 10); //aluminum
		lc.addCastingRecipe(new ItemStack(buckets, 1, 5), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue*9, 5), bucket, 10); //cobalt
		lc.addCastingRecipe(new ItemStack(buckets, 1, 6), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue*9, 6), bucket, 10); //ardite
		lc.addCastingRecipe(new ItemStack(buckets, 1, 7), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue*9, 7), bucket, 10); //bronze
		lc.addCastingRecipe(new ItemStack(buckets, 1, 8), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue*9, 8), bucket, 10); //albrass
		lc.addCastingRecipe(new ItemStack(buckets, 1, 9), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue*9, 9), bucket, 10); //manyullyn
		lc.addCastingRecipe(new ItemStack(buckets, 1, 10), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue*9, 10), bucket, 10); //alumite
		lc.addCastingRecipe(new ItemStack(buckets, 1, 11), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue*9, 11), bucket, 10);// obsidian
		lc.addCastingRecipe(new ItemStack(buckets, 1, 12), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue*9, 12), bucket, 10); //steel

		liquids = new LiquidStack[] { new LiquidStack(liquidMetalStill.blockID, 1, 0), new LiquidStack(liquidMetalStill.blockID, 1, 2), new LiquidStack(liquidMetalStill.blockID, 1, 5),
				new LiquidStack(liquidMetalStill.blockID, 1, 6), new LiquidStack(liquidMetalStill.blockID, 1, 9), new LiquidStack(liquidMetalStill.blockID, 1, 7),
				new LiquidStack(liquidMetalStill.blockID, 1, 10), new LiquidStack(liquidMetalStill.blockID, 1, 12) };
		int[] liquidDamage = new int[] { 2, 13, 10, 11, 12, 14, 15, 16 };

		for (int iter = 0; iter < patternOutputs.length; iter++)
		{
			ItemStack cast = new ItemStack(metalPattern, 1, iter + 1);
			for (int iterTwo = 0; iterTwo < liquids.length; iterTwo++)
			{
				lc.addCastingRecipe(new ItemStack(patternOutputs[iter], 1, liquidDamage[iterTwo]), liquids[iterTwo], cast, 50);
			}
		}
	}

	void addSmelteryRecipes ()
	{
		//Ore
		Smeltery.addMelting(Block.oreIron, 0, 600, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 0));
		Smeltery.addMelting(Block.oreGold, 0, 550, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 1));

		//Items
		Smeltery.addMelting(new ItemStack(Item.ingotIron, 4), Block.blockSteel.blockID, 0, 500, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 0));
		Smeltery.addMelting(new ItemStack(Item.ingotGold, 4), Block.blockGold.blockID, 0, 450, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 1));
		Smeltery.addMelting(new ItemStack(Item.goldNugget, 4), Block.blockGold.blockID, 0, 450, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue / 9, 1));

		//Blocks
		Smeltery.addMelting(Block.blockSteel, 0, 600, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 0));
		Smeltery.addMelting(Block.blockGold, 0, 550, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 1));
		Smeltery.addMelting(Block.obsidian, 0, 800, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 11));

		//Alloys
		Smeltery.addAlloyMixing(new LiquidStack(liquidMetalStill.blockID, 2, 7), new LiquidStack(liquidMetalStill.blockID, 3, 2), new LiquidStack(liquidMetalStill.blockID, 1, 3)); //Bronze
		Smeltery.addAlloyMixing(new LiquidStack(liquidMetalStill.blockID, 2, 8), new LiquidStack(liquidMetalStill.blockID, 3, 4), new LiquidStack(liquidMetalStill.blockID, 1, 2)); //Aluminum Brass
		Smeltery.addAlloyMixing(new LiquidStack(liquidMetalStill.blockID, 2, 9), new LiquidStack(liquidMetalStill.blockID, 1, 5), new LiquidStack(liquidMetalStill.blockID, 1, 6)); //Manyullyn
		Smeltery.addAlloyMixing(new LiquidStack(liquidMetalStill.blockID, 3, 10), new LiquidStack(liquidMetalStill.blockID, 5, 4), new LiquidStack(liquidMetalStill.blockID, 2, 0), new LiquidStack(
				liquidMetalStill.blockID, 2, 11)); //Alumite
	}

	void addCraftingRecipes ()
	{
		/*GameRegistry.addRecipe(new ItemStack(woodCrafter, 1, 0), "c", 'c', Block.workbench);
		GameRegistry.addRecipe(new ItemStack(woodCrafter, 1, 1), "cc", 'c', Block.workbench);*/
		GameRegistry.addRecipe(new ItemStack(woodCrafter, 1, 0), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', Block.workbench);
		GameRegistry.addRecipe(new ItemStack(woodCrafter, 1, 1), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', new ItemStack(Block.wood, 1, 0));
		GameRegistry.addRecipe(new ItemStack(woodCrafter, 1, 2), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', new ItemStack(Block.wood, 1, 1));
		GameRegistry.addRecipe(new ItemStack(woodCrafter, 1, 3), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', new ItemStack(Block.wood, 1, 2));
		GameRegistry.addRecipe(new ItemStack(woodCrafter, 1, 4), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', new ItemStack(Block.wood, 1, 3));
		GameRegistry.addRecipe(new ItemStack(woodCrafter, 1, 5), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', Block.chest);
		GameRegistry.addRecipe(new ItemStack(woodCrafter, 1, 10), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', new ItemStack(Block.planks, 1, 0));
		GameRegistry.addRecipe(new ItemStack(woodCrafter, 1, 11), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', new ItemStack(Block.planks, 1, 1));
		GameRegistry.addRecipe(new ItemStack(woodCrafter, 1, 12), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', new ItemStack(Block.planks, 1, 2));
		GameRegistry.addRecipe(new ItemStack(woodCrafter, 1, 13), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', new ItemStack(Block.planks, 1, 3));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(woodCrafter, 1, 1), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', "logWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(woodCrafter, 1, 10), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', "plankWood"));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blankPattern, 1, 0), "ps", "sp", 'p', "plankWood", 's', Item.stick));
		GameRegistry.addRecipe(new ItemStack(manualBook), "wp", 'w', new ItemStack(blankPattern, 1, 0), 'p', Item.paper);

		GameRegistry.addRecipe(new ItemStack(materials, 1, 0), "pp", "pp", 'p', Item.paper); //Paper stack
		GameRegistry.addRecipe(new ItemStack(materials, 1, 6), "ppp", "ppp", "ppp", 'p', Block.cobblestoneMossy); //Moss ball
		GameRegistry.addRecipe(new ItemStack(materials, 1, 7), "xcx", "cbc", "xcx", 'b', Item.bucketLava, 'c', Item.coal, 'x', Block.netherrack); //Auto-smelt
		GameRegistry.addShapelessRecipe(new ItemStack(materials, 1, 8), Item.bone, Item.rottenFlesh, Item.chickenRaw, Item.beefRaw, Item.porkRaw, Item.fishRaw); //Necrotic bone
		GameRegistry.addShapelessRecipe(new ItemStack(craftedSoil, 1, 0), Item.slimeBall, Item.slimeBall, Item.slimeBall, Item.slimeBall, Block.sand, Block.dirt); //Slimy sand
		GameRegistry.addShapelessRecipe(new ItemStack(craftedSoil, 1, 1), Item.clay, Block.sand, Block.gravel); //Grout, Add stone dust?

		FurnaceRecipes.smelting().addSmelting(craftedSoil.blockID, 0, new ItemStack(materials, 1, 1), 2f); //Slime
		FurnaceRecipes.smelting().addSmelting(craftedSoil.blockID, 1, new ItemStack(materials, 1, 2), 2f); //Seared brick item
		//GameRegistry.addRecipe(new ItemStack(oreSlag, 1, 0), "pp", "pp", 'p', new ItemStack(materials, 1, 2)); //Seared brick block

		FurnaceRecipes.smelting().addSmelting(oreSlag.blockID, 1, new ItemStack(materials, 1, 3), 3f);
		FurnaceRecipes.smelting().addSmelting(oreSlag.blockID, 2, new ItemStack(materials, 1, 4), 3f);
		FurnaceRecipes.smelting().addSmelting(oreSlag.blockID, 3, new ItemStack(materials, 1, 9), 0.5f);
		FurnaceRecipes.smelting().addSmelting(oreSlag.blockID, 4, new ItemStack(materials, 1, 10), 0.5f);
		FurnaceRecipes.smelting().addSmelting(oreSlag.blockID, 5, new ItemStack(materials, 1, 12), 0.5f);

		//Smeltery
		ItemStack searedBrick = new ItemStack(materials, 1, 2);
		GameRegistry.addRecipe(new ItemStack(smeltery, 1, 0), "bbb", "b b", "bbb", 'b', searedBrick);
		GameRegistry.addRecipe(new ItemStack(smeltery, 1, 1), "b b", "b b", "bbb", 'b', searedBrick);
		GameRegistry.addRecipe(new ItemStack(smeltery, 1, 2), "bb", "bb", 'b', searedBrick);

		GameRegistry.addRecipe(new ItemStack(lavaTank, 1, 0), "bbb", "bgb", "bbb", 'b', searedBrick, 'g', Block.glass);
		GameRegistry.addRecipe(new ItemStack(lavaTank, 1, 1), "bgb", "ggg", "bgb", 'b', searedBrick, 'g', Block.glass);
		GameRegistry.addRecipe(new ItemStack(lavaTank, 1, 2), "bgb", "bgb", "bgb", 'b', searedBrick, 'g', Block.glass);

		GameRegistry.addRecipe(new ItemStack(searedBlock, 1, 0), "bbb", "b b", "b b", 'b', searedBrick);
		GameRegistry.addRecipe(new ItemStack(searedBlock, 1, 1), "b b", " b ", 'b', searedBrick);
	}

	void setupToolTabs ()
	{
		TConstruct.materialTab.init(new ItemStack(titleIcon));
		TConstruct.blockTab.init(new ItemStack(woodCrafter));
		ItemStack tool = new ItemStack(longsword, 1, 0);

		NBTTagCompound compound = new NBTTagCompound();
		compound.setCompoundTag("InfiTool", new NBTTagCompound());
		compound.getCompoundTag("InfiTool").setInteger("RenderHead", 2);
		compound.getCompoundTag("InfiTool").setInteger("RenderHandle", 0);
		compound.getCompoundTag("InfiTool").setInteger("RenderAccessory", 10);
		tool.setTagCompound(compound);

		TConstruct.toolTab.init(tool);
	}

	public void oreRegistry ()
	{
		OreDictionary.registerOre("oreCobalt", new ItemStack(oreSlag, 1, 1));
		OreDictionary.registerOre("oreArdite", new ItemStack(oreSlag, 1, 2));
		OreDictionary.registerOre("oreCopper", new ItemStack(oreSlag, 1, 3));
		OreDictionary.registerOre("oreTin", new ItemStack(oreSlag, 1, 4));
		OreDictionary.registerOre("oreNaturalAluminum", new ItemStack(oreSlag, 1, 5));

		OreDictionary.registerOre("ingotCobalt", new ItemStack(materials, 1, 3));
		OreDictionary.registerOre("ingotArdite", new ItemStack(materials, 1, 4));
		OreDictionary.registerOre("ingotManyullyn", new ItemStack(materials, 1, 5));
		OreDictionary.registerOre("ingotCopper", new ItemStack(materials, 1, 9));
		OreDictionary.registerOre("ingotTin", new ItemStack(materials, 1, 10));
		OreDictionary.registerOre("ingotAluminum", new ItemStack(materials, 1, 11));
		OreDictionary.registerOre("naturalAluminum", new ItemStack(materials, 1, 12));
		OreDictionary.registerOre("ingotBronze", new ItemStack(materials, 1, 13));
		OreDictionary.registerOre("ingotAluminumBrass", new ItemStack(materials, 1, 14));
		OreDictionary.registerOre("ingotAlumite", new ItemStack(materials, 1, 15));
		OreDictionary.registerOre("ingotSteel", new ItemStack(materials, 1, 16));

		OreDictionary.registerOre("blockCobalt", new ItemStack(metalBlock, 1, 0));
		OreDictionary.registerOre("blockArdite", new ItemStack(metalBlock, 1, 1));
		OreDictionary.registerOre("blockManyullyn", new ItemStack(metalBlock, 1, 2));
		OreDictionary.registerOre("blockCopper", new ItemStack(metalBlock, 1, 3));
		OreDictionary.registerOre("blockBronze", new ItemStack(metalBlock, 1, 4));
		OreDictionary.registerOre("blockTin", new ItemStack(metalBlock, 1, 5));
		OreDictionary.registerOre("blockNaturalAluminum", new ItemStack(metalBlock, 1, 6));
		OreDictionary.registerOre("blockAluminumBrass", new ItemStack(metalBlock, 1, 7));
		OreDictionary.registerOre("blockAlumite", new ItemStack(metalBlock, 1, 8));
		OreDictionary.registerOre("blockSteel", new ItemStack(metalBlock, 1, 9));

		String[] liquidNames = new String[] { "Iron", "Gold", "Copper", "Tin", "Aluminum", "Cobalt", "Ardite", "Bronze", "Brass", "Manyullyn", "Alumite", "Obsidian", "Steel" };
		for (int iter = 0; iter < liquidNames.length; iter++)
		{
			LiquidStack liquidstack = new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue*9, iter);
			LiquidDictionary.getOrCreateLiquid("Molten " + liquidNames[iter], liquidstack);
			LiquidContainerRegistry.registerLiquid(new LiquidContainerData(liquidstack, new ItemStack(buckets, 1, iter), new ItemStack(Item.bucketEmpty)));
		}
	}

	public void modIntegration ()
	{
		/* IC2 */
		ItemStack reBattery = ic2.api.Items.getItem("reBattery");
		if (reBattery != null)
			modE.batteries.add(reBattery);
		ItemStack chargedReBattery = ic2.api.Items.getItem("chargedReBattery");
		if (chargedReBattery != null)
			modE.batteries.add(new ItemStack(chargedReBattery.getItem(), 1, -1));
		ItemStack electronicCircuit = ic2.api.Items.getItem("electronicCircuit");
		if (electronicCircuit != null)
			modE.circuits.add(electronicCircuit);

		/* Thaumcraft */
		//Object obj = getItem("itemResource", "thaumcraft.common.Config");
	}

	public static Object getItem (String name, String classPackage)
	{
		try
		{
			Class c = Class.forName(classPackage);
			Object ret = c.getField(name);
			if (ret != null && (ret instanceof ItemStack || ret instanceof Item))
				return ret;
			return null;
		}
		catch (Exception e)
		{
			System.out.println("[TConstruct] Could not find item for " + name);
			return null;
		}
	}

	/*public static String blockTexture = "/tinkertextures/ConstructBlocks.png";
	public static String blankSprite = "/tinkertextures/blanksprite.png";
	public static String liquidTexture = "/tinkertextures/LiquidWhite.png";

	public static String craftingTexture = "/tinkertextures/materials.png";
	public static String patternTexture = "/tinkertextures/patterns.png";
	public static String baseHeads = "/tinkertextures/tools/baseheads.png";
	public static String baseAccessories = "/tinkertextures/tools/baseaccessories.png";
	public static String swordparts = "/tinkertextures/tools/swordparts.png";
	public static String jokeparts = "/tinkertextures/tools/jokeparts.png";
	public static String broadheads = "/tinkertextures/tools/broadheads.png";

	public static String pickaxeTexture = "/tinkertextures/tools/pickaxe/";
	public static String broadswordTexture = "/tinkertextures/tools/broadsword/";
	public static String shovelTexture = "/tinkertextures/tools/shovel/";
	public static String axeTexture = "/tinkertextures/tools/axe/";
	public static String longswordTexture = "/tinkertextures/tools/longsword/";
	public static String rapierTexture = "/tinkertextures/tools/rapier/";
	public static String frypanTexture = "/tinkertextures/tools/frypan/";
	public static String signTexture = "/tinkertextures/tools/battlesign/";
	public static String bowTexture = "/tinkertextures/tools/bow/";
	public static String mattockTexture = "/tinkertextures/tools/mattock/";
	public static String lumberaxeTexture = "/tinkertextures/tools/lumberaxe/";*/

	@Override
	public int getBurnTime (ItemStack fuel)
	{
		if (fuel.itemID == materials.itemID && fuel.getItemDamage() == 7)
			return 26400;
		return 0;
	}
}
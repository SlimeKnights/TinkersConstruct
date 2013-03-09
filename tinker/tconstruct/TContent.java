package tinker.tconstruct;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.liquids.LiquidContainerData;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import tinker.common.IPattern;
import tinker.common.fancyitem.FancyEntityItem;
import tinker.tconstruct.blocks.*;
import tinker.tconstruct.blocks.liquids.*;
import tinker.tconstruct.client.gui.ToolGuiElement;
import tinker.tconstruct.crafting.*;
import tinker.tconstruct.entity.*;
import tinker.tconstruct.items.*;
import tinker.tconstruct.logic.*;
import tinker.tconstruct.modifiers.*;
import tinker.tconstruct.tools.*;
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
	}
	
	void registerBlocks()
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
		GameRegistry.registerTileEntity(tinker.tconstruct.logic.FrypanLogic.class, "FrypanLogic");
		
		craftedSoil = new TConstructBlock(PHConstruct.craftedSoil, 96, Material.sand, 3.0F, 2);
		craftedSoil.stepSound = Block.soundGravelFootstep;
		GameRegistry.registerBlock(craftedSoil, tinker.tconstruct.items.CraftedSoilItemBlock.class, "CraftedSoil");
		
		metalBlock = new TConstructBlock(PHConstruct.metalBlock, 128, Material.iron, 10.0F, 10);
		metalBlock.stepSound = Block.soundMetalFootstep;
		GameRegistry.registerBlock(metalBlock, tinker.tconstruct.items.MetalItemBlock.class, "MetalBlock");

		//Smeltery
		smeltery = new SmelteryBlock(PHConstruct.smeltery).setBlockName("Smeltery");
		GameRegistry.registerBlock(smeltery, SmelteryItemBlock.class, "Smeltery");
		GameRegistry.registerTileEntity(SmelteryLogic.class, "TConstruct.Smeltery");
		GameRegistry.registerTileEntity(SmelteryDrainLogic.class, "TConstruct.SmelteryDrain");
		GameRegistry.registerTileEntity(MultiServantLogic.class, "TConstruct.Servants");
		
		lavaTank = new LavaTankBlock(PHConstruct.lavaTank).setBlockName("LavaTank");
		lavaTank.setStepSound(Block.soundGlassFootstep);
		GameRegistry.registerBlock(lavaTank, LavaTankItemBlock.class, "LavaTank");
		GameRegistry.registerTileEntity(tinker.tconstruct.logic.LavaTankLogic.class, "TConstruct.LavaTank");
		
		searedBlock = new SearedBlock(PHConstruct.searedTable).setBlockName("SearedBlock");
		GameRegistry.registerBlock(searedBlock, SearedTableItemBlock.class, "SearedBlock");
		GameRegistry.registerTileEntity(CastingTableLogic.class, "CastingTable");
		GameRegistry.registerTileEntity(FaucetLogic.class, "Faucet");

		oreSlag = new MetalOre(PHConstruct.oreSlag, 80, Material.iron, 10.0F, 6);
		GameRegistry.registerBlock(oreSlag, tinker.tconstruct.items.MetalOreItemBlock.class, "SearedBrick");
		MinecraftForge.setBlockHarvestLevel(oreSlag, 0, "pickaxe", 2);
		MinecraftForge.setBlockHarvestLevel(oreSlag, 1, "pickaxe", 4);
		MinecraftForge.setBlockHarvestLevel(oreSlag, 2, "pickaxe", 4);
		MinecraftForge.setBlockHarvestLevel(oreSlag, 3, "pickaxe", 1);
		MinecraftForge.setBlockHarvestLevel(oreSlag, 4, "pickaxe", 1);
		MinecraftForge.setBlockHarvestLevel(oreSlag, 5, "pickaxe", 1);
		
		//Traps
		/*landmine = new Landmine(PHConstruct.landmine, 0, EnumMobType.mobs, Material.cactus).setBlockName("landmine");
		GameRegistry.registerBlock(landmine, "landmine");*/

		//Liquids
		liquidMetal = new MaterialLiquid(MapColor.tntColor);
		liquidMetalFlowing = new LiquidMetalFlowing(PHConstruct.metalFlowing).setBlockName("liquid.metalFlow");
		liquidMetalStill = new LiquidMetalStill(PHConstruct.metalStill).setBlockName("liquid.metalStill");
		GameRegistry.registerBlock(liquidMetalFlowing, LiquidItemBlock.class, "metalFlow");
		GameRegistry.registerBlock(liquidMetalStill, LiquidItemBlock.class, "metalStill");
		GameRegistry.registerTileEntity(LiquidTextureLogic.class, "LiquidTexture");
	}

	void registerItems ()
	{
		blankPattern = new BlankPattern(PHConstruct.blankPattern, 96, craftingTexture).setItemName("tconstruct.blankpattern");
		materials = new Materials(PHConstruct.materials, 128, craftingTexture).setItemName("tconstruct.Materials");
		toolRod = new ToolPart(PHConstruct.toolRod, 0, craftingTexture).setItemName("tconstruct.ToolRod");
		toolShard = new ToolShard(PHConstruct.toolShard, 64, craftingTexture).setItemName("tconstruct.ToolShard");
		woodPattern = new Pattern(PHConstruct.woodPattern, 0, patternTexture).setItemName("tconstruct.Pattern");
		metalPattern = new MetalPattern(PHConstruct.metalPattern, 64, patternTexture).setItemName("tconstruct.MetalPattern");
		//stonePattern = new Pattern(PHTools.stonePattern, 64, patternTexture).setItemName("tconstruct.Pattern");
		//netherPattern = new Pattern(PHTools.netherPattern, 128, patternTexture).setItemName("tconstruct.Pattern");

		manualBook = new PatternManual(PHConstruct.manual);
		buckets = new FilledBucket(PHConstruct.buckets);

		pickaxe = new Pickaxe(PHConstruct.pickaxe, pickaxeTexture);
		shovel = new Shovel(PHConstruct.shovel, shovelTexture);
		axe = new Axe(PHConstruct.axe, axeTexture);
		broadsword = new Broadsword(PHConstruct.broadsword, broadswordTexture);
		longsword = new Longsword(PHConstruct.longsword, longswordTexture);
		rapier = new Rapier(PHConstruct.rapier, rapierTexture);

		frypan = new FryingPan(PHConstruct.frypan, frypanTexture);
		battlesign = new BattleSign(PHConstruct.battlesign, signTexture);
		//longbow = new RangedWeapon(PHConstruct.longbow, bowTexture);
		mattock = new Mattock(PHConstruct.mattock, mattockTexture);
		//lumberaxe = new LumberAxe(PHConstruct.lumberaxe, lumberaxeTexture);

		pickaxeHead = new ToolPart(PHConstruct.pickaxeHead, 0, baseHeads).setItemName("tconstruct.PickaxeHead");
		shovelHead = new ToolPart(PHConstruct.shovelHead, 64, baseHeads).setItemName("tconstruct.ShovelHead");
		axeHead = new ToolPart(PHConstruct.axeHead, 128, baseHeads).setItemName("tconstruct.AxeHead");
		swordBlade = new ToolPart(PHConstruct.swordBlade, 0, swordparts).setItemName("tconstruct.SwordBlade");
		largeGuard = new ToolPart(PHConstruct.largeGuard, 64, swordparts).setItemName("tconstruct.LargeGuard");
		medGuard = new ToolPart(PHConstruct.medGuard, 128, swordparts).setItemName("tconstruct.MediumGuard");
		crossbar = new ToolPart(PHConstruct.crossbar, 192, swordparts).setItemName("tconstruct.Crossbar");
		binding = new ToolPart(PHConstruct.binding, 0, baseAccessories).setItemName("tconstruct.Binding");

		frypanHead = new ToolPart(PHConstruct.frypanHead, 0, jokeparts).setItemName("tconstruct.FrypanHead");
		signHead = new ToolPart(PHConstruct.signHead, 64, jokeparts).setItemName("tconstruct.SignHead");

		//lumberHead = new ToolPart(PHConstruct.lumberHead, 0, broadheads).setItemName("tconstruct.LumberHead");
	}

	

	void registerMaterials ()
	{
		TConstructRegistry.addToolMaterial(0, "Wood", 1, 0, 59, 200, 0, 1.0F, 0, 0f);
		TConstructRegistry.addToolMaterial(1, "Stone", 1, 1, 131, 400, 1, 0.5F, 0, 1f);
		TConstructRegistry.addToolMaterial(2, "Iron", 1, 2, 250, 600, 2, 1.3F, 1, 0f);
		TConstructRegistry.addToolMaterial(3, "Flint", 1, 1, 171, 525, 2, 0.7F, 0, 1f);
		TConstructRegistry.addToolMaterial(4, "Cactus", 1, 1, 150, 500, 2, 1.0F, 0, -1f);
		TConstructRegistry.addToolMaterial(5, "Bone", 1, 1, 200, 500, 2, 1.0F, 0, 0f);
		TConstructRegistry.addToolMaterial(6, "Obsidian", 1, 3, 89, 700, 2, 0.8F, 3, 0f);
		TConstructRegistry.addToolMaterial(7, "Netherrack", 1, 2, 131, 400, 1, 1.2F, 0, 1f);
		TConstructRegistry.addToolMaterial(8, "Slime", 1, 3, 1500, 150, 0, 5.0F, 0, 0f);
		TConstructRegistry.addToolMaterial(9, "Paper", 1, 0, 131, 200, 0, 0.1F, 0, 0f);
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
		pb.registerFullMaterial(Block.planks, 2, "Wood", new ItemStack(Item.stick, 2), new ItemStack(Item.stick), 0);
		pb.registerFullMaterial(Block.stone, 2, "Stone", 1);
		pb.registerMaterial(Block.cobblestone, 2, "Stone");
		pb.registerFullMaterial(Item.ingotIron, 2, "Iron", 2);
		pb.registerFullMaterial(Item.flint, 2, "Flint", 3);
		pb.registerFullMaterial(Block.cactus, 2, "Cactus", 4);
		pb.registerFullMaterial(Item.bone, 2, "Bone", new ItemStack(Item.dyePowder, 1, 15), new ItemStack(Item.bone), 5);
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
		lc.addCastingRecipe(new ItemStack(Item.ingotIron), new LiquidStack(liquidMetalStill.blockID, 1, 0), ingotcast, 50); //Iron
		lc.addCastingRecipe(new ItemStack(Item.ingotGold), new LiquidStack(liquidMetalStill.blockID, 1, 1), ingotcast, 50); //gold
		lc.addCastingRecipe(new ItemStack(materials, 1, 9), new LiquidStack(liquidMetalStill.blockID, 1, 2), ingotcast, 50); //copper
		lc.addCastingRecipe(new ItemStack(materials, 1, 10), new LiquidStack(liquidMetalStill.blockID, 1, 3), ingotcast, 50); //tin
		lc.addCastingRecipe(new ItemStack(materials, 1, 11), new LiquidStack(liquidMetalStill.blockID, 1, 4), ingotcast, 50); //aluminum
		lc.addCastingRecipe(new ItemStack(materials, 1, 3), new LiquidStack(liquidMetalStill.blockID, 1, 5), ingotcast, 50); //cobalt
		lc.addCastingRecipe(new ItemStack(materials, 1, 4), new LiquidStack(liquidMetalStill.blockID, 1, 6), ingotcast, 50); //ardite
		lc.addCastingRecipe(new ItemStack(materials, 1, 13), new LiquidStack(liquidMetalStill.blockID, 1, 7), ingotcast, 50); //bronze
		lc.addCastingRecipe(new ItemStack(materials, 1, 14), new LiquidStack(liquidMetalStill.blockID, 1, 8), ingotcast, 50); //albrass
		lc.addCastingRecipe(new ItemStack(materials, 1, 5), new LiquidStack(liquidMetalStill.blockID, 1, 9), ingotcast, 50); //manyullyn
		lc.addCastingRecipe(new ItemStack(materials, 1, 15), new LiquidStack(liquidMetalStill.blockID, 1, 10), ingotcast, 50); //alumite
		// obsidian
		lc.addCastingRecipe(new ItemStack(materials, 1, 16), new LiquidStack(liquidMetalStill.blockID, 1, 12), ingotcast, 50); //steel

		liquids = new LiquidStack[] {
				new LiquidStack(liquidMetalStill.blockID, 1, 0),
				new LiquidStack(liquidMetalStill.blockID, 1, 2),
				new LiquidStack(liquidMetalStill.blockID, 1, 5),
				new LiquidStack(liquidMetalStill.blockID, 1, 6),
				new LiquidStack(liquidMetalStill.blockID, 1, 9),
				new LiquidStack(liquidMetalStill.blockID, 1, 7),
				new LiquidStack(liquidMetalStill.blockID, 1, 10),
				new LiquidStack(liquidMetalStill.blockID, 1, 12)
		};
		int[] liquidDamage = new int[] {
			2, 13, 10, 11, 12, 14, 15, 16
		};
		
		for (int iter = 0; iter < patternOutputs.length; iter++)
		{
			ItemStack cast = new ItemStack(metalPattern, 1, iter+1);
			for (int iterTwo = 0; iterTwo < liquids.length; iterTwo++)
			{
				lc.addCastingRecipe(new ItemStack(patternOutputs[iter], 1, liquidDamage[iterTwo]), liquids[iterTwo], cast, 50);
			}
		}
	}
	
	void addSmelteryRecipes()
	{
		//Ore
		Smeltery.addMelting(Block.oreIron, 0, 600, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue*2, 0));
		Smeltery.addMelting(Block.oreGold, 0, 550, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue*2, 1));
		
		//Items
		Smeltery.addMelting(new ItemStack(Item.ingotIron, 4), Block.blockSteel.blockID, 0, 500, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 0));
		Smeltery.addMelting(new ItemStack(Item.ingotGold, 4), Block.blockGold.blockID, 0, 450, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 1));
		Smeltery.addMelting(new ItemStack(Item.goldNugget, 4), Block.blockGold.blockID, 0, 450, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue/9, 1));
		
		//Blocks
		Smeltery.addMelting(Block.blockSteel, 0, 600, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue*9, 0));
		Smeltery.addMelting(Block.blockGold, 0, 550, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue*9, 1));
		Smeltery.addMelting(Block.obsidian, 0, 800, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 11));
		
		//Alloys
		Smeltery.addAlloyMixing(new LiquidStack(liquidMetalStill.blockID, 4, 7), new LiquidStack(liquidMetalStill.blockID, 3, 2), new LiquidStack(liquidMetalStill.blockID, 1, 3)); //Bronze
		Smeltery.addAlloyMixing(new LiquidStack(liquidMetalStill.blockID, 4, 8), new LiquidStack(liquidMetalStill.blockID, 3, 4), new LiquidStack(liquidMetalStill.blockID, 1, 2)); //Aluminum Brass
		Smeltery.addAlloyMixing(new LiquidStack(liquidMetalStill.blockID, 2, 9), new LiquidStack(liquidMetalStill.blockID, 1, 5), new LiquidStack(liquidMetalStill.blockID, 1, 6)); //Manyullyn
		Smeltery.addAlloyMixing(new LiquidStack(liquidMetalStill.blockID, 9, 10), new LiquidStack(liquidMetalStill.blockID, 5, 4), 
				new LiquidStack(liquidMetalStill.blockID, 2, 0), new LiquidStack(liquidMetalStill.blockID, 2, 11)); //Alumite
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
		TConstruct.materialTab.init(new ItemStack(TContent.woodPattern, 1, 255));
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
			LiquidStack liquidstack = new LiquidStack(liquidMetalStill.blockID, LiquidContainerRegistry.BUCKET_VOLUME, iter);
			LiquidDictionary.getOrCreateLiquid("Molten "+liquidNames[iter], liquidstack);
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

	public static String blockTexture = "/tinkertextures/ConstructBlocks.png";
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
	public static String lumberaxeTexture = "/tinkertextures/tools/lumberaxe/";

	@Override
	public int getBurnTime (ItemStack fuel)
	{
		if (fuel.itemID == materials.itemID && fuel.getItemDamage() == 7)
			return 26400;
		return 0;
	}
}
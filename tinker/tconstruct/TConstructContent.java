package tinker.tconstruct;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.ShapedOreRecipe;
import tinker.tconstruct.blocks.EquipBlock;
import tinker.tconstruct.blocks.TConstructBlock;
import tinker.tconstruct.blocks.ToolStationBlock;
import tinker.tconstruct.crafting.PatternBuilder;
import tinker.tconstruct.crafting.ToolBuilder;
import tinker.tconstruct.items.CraftingItem;
import tinker.tconstruct.items.Materials;
import tinker.tconstruct.items.Pattern;
import tinker.tconstruct.items.ToolPart;
import tinker.tconstruct.modifiers.ModBlaze;
import tinker.tconstruct.modifiers.ModBoolean;
import tinker.tconstruct.modifiers.ModDurability;
import tinker.tconstruct.modifiers.ModElectric;
import tinker.tconstruct.modifiers.ModInteger;
import tinker.tconstruct.modifiers.ModLapisBase;
import tinker.tconstruct.modifiers.ModLapisIncrease;
import tinker.tconstruct.modifiers.ModRedstone;
import tinker.tconstruct.modifiers.ModRepair;
import tinker.tconstruct.tools.Axe;
import tinker.tconstruct.tools.BattleSign;
import tinker.tconstruct.tools.Broadsword;
import tinker.tconstruct.tools.FryingPan;
import tinker.tconstruct.tools.Longsword;
import tinker.tconstruct.tools.LumberAxe;
import tinker.tconstruct.tools.Mattock;
import tinker.tconstruct.tools.Pickaxe;
import tinker.tconstruct.tools.Rapier;
import tinker.tconstruct.tools.Shovel;
import tinker.tconstruct.tools.ToolCore;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class TConstructContent
	implements IFuelHandler
{
	//Patterns and other materials
	public static Item blankPattern;
	public static Item materials;
	public static Item toolRod;
	public static Item toolShard;
	public static Item woodPattern;
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
	public static Block smeltery;
	
	public static Block heldItemBlock;
	public static Block ores;
	public static Block lavaTank;
	public static Block craftedSoil;
	public static Block searedBrick;
	//public static Block axle;
	
	//Tool modifiers
	public static ModElectric modE;
	
	public TConstructContent()
	{
		createEntities();
		createItems();
		registerMaterials();
		addToolRecipes();
		addCraftingRecipes();
		setupToolTabs();
		GameRegistry.registerFuelHandler(this);
	}
	
	void createEntities()
	{
		EntityRegistry.registerModEntity(tinker.tconstruct.entity.EntityWagon.class, "Small Wagon", 0, TConstruct.instance, 32, 5, true);
	}
	
	void createItems ()
	{
		woodCrafter = new ToolStationBlock(PHConstruct.woodCrafter, Material.wood);
		GameRegistry.registerBlock(woodCrafter, tinker.tconstruct.blocks.ToolStationItemBlock.class, "ToolStationBlock");
		GameRegistry.registerTileEntity(tinker.tconstruct.logic.ToolStationLogic.class, "ToolStation");
		GameRegistry.registerTileEntity(tinker.tconstruct.logic.PartCrafterLogic.class, "PartCrafter");
		GameRegistry.registerTileEntity(tinker.tconstruct.logic.PatternChestLogic.class, "PatternHolder");
		GameRegistry.registerTileEntity(tinker.tconstruct.logic.PatternShaperLogic.class, "PatternShaper");
		
		heldItemBlock = new EquipBlock(PHConstruct.heldItemBlock, Material.wood);
		GameRegistry.registerBlock(heldItemBlock, "HeldItemBlock");
		GameRegistry.registerTileEntity(tinker.tconstruct.logic.FrypanLogic.class, "FrypanLogic");
		
		//ores = new TBaseOre(PHTools.ores, 48);
		//GameRegistry.registerBlock(ores, tinker.toolconstruct.blocks.TBaseOreItem.class, "TConstruct.ores");
		
		//lavaTank = new LavaTankBlock(PHConstruct.lavaTank);
		//GameRegistry.registerBlock(lavaTank, "LavaTank");
		//GameRegistry.registerTileEntity(tinker.tconstruct.logic.LavaTankLogic.class, "TConstruct.LavaTank");
		
		//smeltery = new SmelteryBlock(PHConstruct.smeltery);
		//GameRegistry.registerBlock(smeltery, "Smeltery");
		//GameRegistry.registerTileEntity(tinker.tconstruct.logic.SmelteryLogic.class, "TConstruct.Smeltery");
		
		craftedSoil = new TConstructBlock(PHConstruct.craftedSoil, 96, Material.sand, 3.0F, 2);
		craftedSoil.stepSound = Block.soundGravelFootstep;
		GameRegistry.registerBlock(craftedSoil, tinker.tconstruct.blocks.CraftedSoilItemBlock.class, "CraftedSoil");
		
		searedBrick = new TConstructBlock(PHConstruct.searedBrick, 80, Material.iron, 10.0F, 1);
		GameRegistry.registerBlock(searedBrick, tinker.tconstruct.blocks.SearedBrickItemBlock.class, "SearedBrick");
		MinecraftForge.setBlockHarvestLevel(searedBrick, 0, "pickaxe", 2);

		blankPattern = new CraftingItem(PHConstruct.blankPattern, 96, craftingTexture).setItemName("tconstruct.BlankPattern");
		materials = new Materials(PHConstruct.materials, 128, craftingTexture).setItemName("tconstruct.Materials");
		toolRod = new ToolPart(PHConstruct.toolRod, 0, craftingTexture).setItemName("tconstruct.ToolRod");
		toolShard = new ToolPart(PHConstruct.toolShard, 64, craftingTexture).setItemName("tconstruct.ToolShard");
		woodPattern = new Pattern(PHConstruct.woodPattern, 0, patternTexture).setItemName("tconstruct.Pattern");
		//stonePattern = new Pattern(PHTools.stonePattern, 64, patternTexture).setItemName("tconstruct.Pattern");
		//netherPattern = new Pattern(PHTools.netherPattern, 128, patternTexture).setItemName("tconstruct.Pattern");
		
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
		lumberaxe = new LumberAxe(PHConstruct.lumberaxe, lumberaxeTexture);
				
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
	
	public static Item[] patternOutputs;
	
	void registerMaterials ()
	{
		PatternBuilder pb = PatternBuilder.instance;
		pb.registerFullMaterial(Block.planks, 2, "wood", new ItemStack(Item.stick), new ItemStack(Item.stick), 0);
		pb.registerFullMaterial(Block.stone, 2, "stone", 1);
		pb.registerMaterial(Block.cobblestone, 2, "stone");
		pb.registerFullMaterial(Item.ingotIron, 2, "iron", 2);
		pb.registerFullMaterial(Item.flint, 2, "flint", 3);
		pb.registerFullMaterial(Block.cactus, 2, "cactus", 4);
		pb.registerFullMaterial(Item.bone, 2, "bone", new ItemStack(Item.dyePowder, 1, 15), new ItemStack(Item.bone), 5);
		pb.registerFullMaterial(Block.obsidian, 2, "obsidian", 6);
		pb.registerFullMaterial(Block.netherrack, 2, "netherrack", 7);
		pb.registerFullMaterial(new ItemStack(materials, 1, 1), 2, "slime", new ItemStack(toolShard, 1, 8), new ItemStack(toolRod, 1, 8), 8);
		pb.registerFullMaterial(new ItemStack(materials, 1, 0), 2, "paper", new ItemStack(Item.paper), new ItemStack(toolRod, 1, 9), 9);
		pb.registerMaterialSet("copper", new ItemStack(toolShard, 1, 13), new ItemStack(toolRod, 1, 13), 13);
		pb.registerMaterialSet("bronze", new ItemStack(toolShard, 1, 14), new ItemStack(toolRod, 1, 14), 14);
		
		patternOutputs = new Item[] { toolRod, pickaxeHead, shovelHead, axeHead, swordBlade, largeGuard, medGuard, crossbar, binding, frypanHead, signHead };
		for (int iter = 0; iter < patternOutputs.length; iter++)
		{
			pb.addToolPattern(new ItemStack(woodPattern, 1, iter+1), patternOutputs[iter]);
			//pb.addToolPattern(new ItemStack(stonePattern, 1, iter+1), items[iter]);
			//pb.addToolPattern(new ItemStack(netherPattern, 1, iter+1), items[iter]);
		}
	}
	
	void addToolRecipes ()
	{
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
		tb.registerToolMod(new ModDurability(new ItemStack[] {new ItemStack(Item.diamond)}, 0, 500, 0f, 3, "Diamond", "\u00a7bDurability +500", "\u00a7b"));
		tb.registerToolMod(new ModDurability(new ItemStack[] {new ItemStack(Item.emerald)}, 1, 0, 0.5f, 2, "Emerald", "\u00a72Durability +50%", "\u00a72"));
		modE = new ModElectric();
		tb.registerToolMod(modE);
		tb.registerToolMod(new ModRedstone(new ItemStack[] {new ItemStack(Item.redstone)}, 2, 1));
		tb.registerToolMod(new ModRedstone(new ItemStack[] { new ItemStack(Item.redstone), new ItemStack(Item.redstone) }, 2, 2));
		tb.registerToolMod(new ModLapisIncrease(new ItemStack[] {new ItemStack(Item.dyePowder, 1, 4)}, 10, 1));
		tb.registerToolMod(new ModLapisIncrease(new ItemStack[] {new ItemStack(Item.dyePowder, 1, 4), new ItemStack(Item.dyePowder, 1, 4)}, 10, 2));
		tb.registerToolMod(new ModLapisIncrease(new ItemStack[] {new ItemStack(Block.blockLapis)}, 10, 9));
		tb.registerToolMod(new ModLapisIncrease(new ItemStack[] {new ItemStack(Item.dyePowder, 1, 4), new ItemStack(Block.blockLapis)}, 10, 10));
		tb.registerToolMod(new ModLapisIncrease(new ItemStack[] {new ItemStack(Block.blockLapis), new ItemStack(Block.blockLapis)}, 10, 18));
		tb.registerToolMod(new ModLapisBase(new ItemStack[] {new ItemStack(Block.blockLapis), new ItemStack(Block.blockLapis)}, 10));
		tb.registerToolMod(new ModInteger(new ItemStack[] {new ItemStack(materials, 1, 6)}, 4, "Moss", 3, "\u00a72", "Auto-Repair"));
		tb.registerToolMod(new ModBlaze(new ItemStack[] {new ItemStack(Item.blazePowder)}, 7, 1));
		tb.registerToolMod(new ModBlaze(new ItemStack[] {new ItemStack(Item.blazePowder), new ItemStack(Item.blazePowder)}, 7, 2));
		tb.registerToolMod(new ModBoolean(new ItemStack[] {new ItemStack(materials, 1, 7)}, 6, "Lava", "\u00a74", "Auto-Smelt"));
		tb.registerToolMod(new ModInteger(new ItemStack[] {new ItemStack(materials, 1, 8)}, 8, "Necrotic", 1, "\u00a78", "Life Steal"));
		
		ItemStack reBattery = ic2.api.Items.getItem("reBattery");
		if (reBattery != null)
			modE.batteries.add(reBattery);
		ItemStack chargedReBattery = ic2.api.Items.getItem("chargedReBattery");
		if (chargedReBattery != null)
			modE.batteries.add(new ItemStack(chargedReBattery.getItem(), 1, -1));
		ItemStack electronicCircuit = ic2.api.Items.getItem("electronicCircuit");
		if (electronicCircuit != null)
			modE.circuits.add(electronicCircuit);
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
		GameRegistry.addRecipe( new ShapedOreRecipe(new ItemStack(woodCrafter, 1, 1), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', "logWood"));
		GameRegistry.addRecipe( new ShapedOreRecipe(new ItemStack(woodCrafter, 1, 10), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', "plankWood"));
		
		GameRegistry.addRecipe( new ShapedOreRecipe(new ItemStack(blankPattern, 1, 0), "ps", "sp", 'p', "plankWood", 's', Item.stick));
		/*GameRegistry.addRecipe(new ItemStack(stonePattern, 1, 0), "ps", "sp", 'p', Block.cobblestone, 's', new ItemStack(toolRod, 1, 1));
		GameRegistry.addRecipe(new ItemStack(stonePattern, 1, 0), "ps", "sp", 'p', Block.stone, 's', new ItemStack(toolRod, 1, 1));
		GameRegistry.addRecipe(new ItemStack(netherPattern, 1, 0), "ps", "sp", 'p', Block.netherrack, 's', new ItemStack(toolRod, 1, 7));*/
		
		GameRegistry.addRecipe(new ItemStack(materials, 1, 0), "pp", "pp", 'p', Item.paper); //Paper stack
		GameRegistry.addRecipe(new ItemStack(materials, 1, 6), "ppp", "ppp", "ppp", 'p', Block.cobblestoneMossy); //Moss ball
		GameRegistry.addRecipe(new ItemStack(materials, 1, 7), "xcx", "cbc", "xcx", 'b', Item.bucketLava, 'c', Item.coal, 'x', Block.netherrack); //Auto-smelt
		GameRegistry.addShapelessRecipe(new ItemStack(materials, 1, 8), Item.bone, Item.rottenFlesh, Item.chickenRaw, Item.beefRaw, Item.porkRaw, Item.fishRaw); //Necrotic bone
		GameRegistry.addShapelessRecipe(new ItemStack(craftedSoil, 1, 0), Item.slimeBall, Item.slimeBall, Item.slimeBall, Item.slimeBall, Block.sand, Block.dirt); //Slimy sand
		GameRegistry.addShapelessRecipe(new ItemStack(craftedSoil, 1, 1), Item.clay, Block.sand, Block.gravel); //Grout, Add stone dust?
		
		FurnaceRecipes.smelting().addSmelting(craftedSoil.blockID, 0, new ItemStack(materials, 1, 1), 2f); //Slime
		FurnaceRecipes.smelting().addSmelting(craftedSoil.blockID, 1, new ItemStack(materials, 1, 2), 2f); //Seared brick item
		GameRegistry.addRecipe(new ItemStack(searedBrick, 1, 0), "pp", "pp", 'p', new ItemStack(materials, 1, 2)); //Seared brick block
		
		/*for (int i = 0; i < 12; i++)
		{
			GameRegistry.addRecipe(new ItemStack(TConstructContent.woodPattern, 1, i + 1), "s", 's', new ItemStack(TConstructContent.woodPattern, 1, i));
		}*/
	}
	
	void setupToolTabs ()
	{
		TConstruct.materialTab.init(new ItemStack(TConstructContent.woodPattern, 1, 255));
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
	
	public static String blockTexture = "/tinkertextures/ConstructBlocks.png";
	
	public static String craftingTexture = "/tinkertextures/materials.png";
	public static String patternTexture = "/tinkertextures/tools/patterns.png";
	public static String baseHeads = "/tinkertextures/tools/baseheads.png";
	public static String baseAccessories = "/tinkertextures/tools/baseaccessories.png";
	public static String swordparts = "/tinkertextures/tools/swordparts.png";
	public static String jokeparts = "/tinkertextures/tools/jokeparts.png";
	public static String broadheads = "/tinkertextures/tools/broadheads.png";

	public static String pickaxeTexture = "/tinkertextures/tools/pickaxes.png";
	public static String broadswordTexture = "/tinkertextures/tools/swordbroad.png";
	public static String shovelTexture = "/tinkertextures/tools/shovels.png";
	public static String axeTexture = "/tinkertextures/tools/axes.png";
	public static String longswordTexture = "/tinkertextures/tools/swordlong.png";
	public static String rapierTexture = "/tinkertextures/tools/swordrapier.png";
	public static String frypanTexture = "/tinkertextures/tools/frypans.png";
	public static String signTexture = "/tinkertextures/tools/battlesigns.png";
	public static String bowTexture = "/tinkertextures/tools/bows.png";
	public static String mattockTexture = "/tinkertextures/tools/mattock.png";
	public static String lumberaxeTexture = "/tinkertextures/tools/lumberaxe.png";

	@Override
	public int getBurnTime (ItemStack fuel)
	{
		if (fuel.itemID == materials.itemID && fuel.getItemDamage() == 7)
			return 26400;
		return 0;
	}
}
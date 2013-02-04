package tinker.tconstruct.client;


import java.io.File;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import org.w3c.dom.Document;

import tinker.tconstruct.TConstruct;
import tinker.tconstruct.TContent;
import tinker.tconstruct.TConstructRegistry;
import tinker.tconstruct.TProxyCommon;
import tinker.tconstruct.tools.ToolCore;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class TProxyClient extends TProxyCommon
{
	public static SmallFontRenderer smallFontRenderer;
	/* Registers any rendering code. */
	public void registerRenderer() 
	{
		Minecraft mc = Minecraft.getMinecraft();
		smallFontRenderer = new SmallFontRenderer(mc.gameSettings, "/font/default.png", mc.renderEngine, false);
		RenderingRegistry.registerBlockHandler(new TableRender());
		//RenderingRegistry.registerBlockHandler(new TankRender());
		RenderingRegistry.registerBlockHandler(new FrypanRender());
		//RenderingRegistry.registerBlockHandler(new AxleRender());
		
		MinecraftForgeClient.preloadTexture(TContent.blockTexture);
		IItemRenderer render = new SuperCustomToolRenderer();
		for (ToolCore tool : TConstructRegistry.tools)
		{
			MinecraftForgeClient.registerItemRenderer(tool.itemID, render);
		}
		
		RenderingRegistry.registerEntityRenderingHandler(tinker.tconstruct.entity.CartEntity.class, new CartRender());
	}
	
	
	/* Ties an internal name to a visible one. */
	public void addNames() 
	{
		//LanguageRegistry.addName(TConstructContent.lavaTank, "Lava Tank");
		LanguageRegistry.instance().addStringLocalization("itemGroup.TConstructTools", "TConstruct Tools");
		LanguageRegistry.instance().addStringLocalization("itemGroup.TConstructMaterials", "TConstruct Materials");
		LanguageRegistry.instance().addStringLocalization("itemGroup.TConstructBlocks", "TConstruct Blocks");
		
		LanguageRegistry.instance().addStringLocalization("crafters.ToolStation", "Tool Station");
		LanguageRegistry.instance().addStringLocalization("crafters.PartBuilder", "Part Builder");
		LanguageRegistry.instance().addStringLocalization("crafters.PatternShaper", "Pattern Shaper");
		LanguageRegistry.instance().addStringLocalization("inventory.PatternChest", "Pattern Chest");
		LanguageRegistry.instance().addStringLocalization("crafters.Smeltery", "Smeltery");
		LanguageRegistry.instance().addStringLocalization("crafters.Frypan", "Frying Pan");
		
		LanguageRegistry.instance().addStringLocalization("ToolStation.Crafter.name", "Tool Station");
		LanguageRegistry.instance().addStringLocalization("ToolStation.Parts.name", "Part Builder");
		LanguageRegistry.instance().addStringLocalization("ToolStation.PatternChest.name", "Pattern Chest");
		LanguageRegistry.instance().addStringLocalization("ToolStation.PatternShaper.name", "Stencil Table");
		
		LanguageRegistry.instance().addStringLocalization("CraftedSoil.Slime.name", "Slimy Mud");
		LanguageRegistry.instance().addStringLocalization("CraftedSoil.Grout.name", "Grout");
		LanguageRegistry.instance().addStringLocalization("SearedBrick.Brick.name", "Seared Bricks");
		
		for (int mat = 0; mat < materialTypes.length; mat++)
		{
			for (int type = 0; type < toolMaterialNames.length; type++)
			{
				String internalName = new StringBuilder().append("item.tconstruct.").append(materialTypes[mat]).append(".").append(toolMaterialNames[type]).append(".name").toString();
				String visibleName = new StringBuilder().append(toolMaterialNames[type]).append(materialNames[mat]).toString();
				LanguageRegistry.instance().addStringLocalization(internalName, "en_US", visibleName);
			}			
		}
		
		for (int i = 0; i < shardNames.length; i++)
		{
			String internalName = "item.tconstruct.ToolShard."+toolMaterialNames[i]+".name";
			String visibleName = shardNames[i];
			LanguageRegistry.instance().addStringLocalization(internalName, "en_US", visibleName);
		}
		
		for (int i = 0; i < materialItemNames.length; i++)
		{
			String internalName = "item.tconstruct.Materials."+materialItemInternalNames[i]+".name";
			String visibleName = materialItemNames[i];
			LanguageRegistry.instance().addStringLocalization(internalName, "en_US", visibleName);
		}
		
		for (int i = 0; i < patterns.length; i++)
		{
			String internalName = "item.tconstruct.Pattern."+patterns[i]+".name";
			String visibleName = patternNames[i]+" Pattern";
			LanguageRegistry.instance().addStringLocalization(internalName, "en_US", visibleName);
		}
		
		//LanguageRegistry.addName(TConstructContent.smeltery, "Smeltery");
		LanguageRegistry.addName(TContent.manualBook, "Tinker's Log");
		LanguageRegistry.addName(TContent.blankPattern, "Blank Pattern");
		LanguageRegistry.addName(TContent.pickaxe, "Pickaxe");
		LanguageRegistry.addName(TContent.shovel, "Shovel");
		LanguageRegistry.addName(TContent.axe, "Axe");
		LanguageRegistry.addName(TContent.broadsword, "Broadsword");
		LanguageRegistry.addName(TContent.longsword, "Longsword");
		LanguageRegistry.addName(TContent.rapier, "Rapier");
		LanguageRegistry.addName(TContent.frypan, "Frying Pan");
		LanguageRegistry.addName(TContent.battlesign, "Battlesign");
		LanguageRegistry.addName(TContent.mattock, "Mattock");
		LanguageRegistry.addName(TContent.lumberaxe, "Lumber Axe");
	}
	

	public static final String[] shardNames = new String[] { 
		"Wood", "Stone Shard", "Iron Chunk", "Flint Shard", "Cactus Shard", "Bone", "Obsidian Shard", "Netherrack Shard", "Slime Crystal Fragment", "Paper", 
		"Cobalt Chunk", "Ardite Chunk", "Manyullyn Chunk", "Copper Chunk", "Bronze Chunk" };
	
	public static final String[] materialItemInternalNames = new String[] { 
		"PaperStack", "SlimeCrystal", "SearedBrick", "CobaltIngot", "ArditeIngot", "ManyullynIngot", "Mossball", "LavaCrystal", "NecroticBone" };
	
	public static final String[] materialItemNames = new String[] { 
		"Paper Stack", "Slime Crystal", "Seared Brick", "Cobalt Ingot", "Ardite Ingot", "Manyullyn Ingot", "Ball of Moss", "Lava Crystal", "Necrotic Bone" };
	
	public static final String[] toolMaterialNames = new String[] { 
		"Wood", "Stone", "Iron", "Flint", "Cactus", "Bone", "Obsidian", "Netherrack", "Slime", "Paper", "Cobalt", "Ardite", "Manyullyn", "Copper", "Bronze" };
	
	public static final String[] materialTypes = new String[] {
		"ToolRod", "PickaxeHead", "ShovelHead", "AxeHead", "SwordBlade", "LargeGuard", "MediumGuard", "Crossbar", "Binding", "FrypanHead", "SignHead", "LumberHead" };
	
	public static final String[] materialNames = new String[] {
		" Rod", " Pickaxe Head", " Shovel Head", " Axe Head", " Sword Blade", " Wide Guard", " Hand Guard", " Crossbar", " Binding", " Pan", " Board", " Broad Axe Head" };
	
	public static final String[] patterns = new String[] {
		"blank", "rod", "pickaxe", "shovel", "axe", "blade", "largeguard", "medguard", "crossbar", "binding", "frypan", "sign", "lumber" };
	
	public static final String[] patternNames = new String[] {
		"Blank", "Tool Rod", "Pickaxe Head", "Shovel Head", "Axe Head", "Sword Blade", "Wide Guard", "Hand Guard", "Crossbar", "Tool Binding", "Pan", "Board", "Broad Axe Head" };

	public static Document diary;
	public static Document volume1;
	
	public void readManuals ()
	{
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		diary = readManual("/manuals/diary.xml", dbFactory);
		volume1 = readManual("/manuals/materials.xml", dbFactory);
	}
	
	Document readManual(String location, DocumentBuilderFactory dbFactory)
	{
		try
		{
			InputStream stream = TConstruct.class.getResourceAsStream(location);
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(stream);
			doc.getDocumentElement().normalize();
			return doc;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static Document getManualFromStack(ItemStack stack)
	{
		switch (stack.getItemDamage())
		{
		case 0: return diary;
		case 1: return volume1;
		}
		
		return null;
	}
	
	@Override
	public File getLocation()
	{
		return Minecraft.getMinecraftDir();
	}
}

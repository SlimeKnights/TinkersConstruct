package tinker.tconstruct.client;

import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import org.w3c.dom.Document;

import tinker.common.fancyitem.FancyEntityItem;
import tinker.common.fancyitem.FancyItemRender;
import tinker.tconstruct.*;
import tinker.tconstruct.client.entityrender.*;
import tinker.tconstruct.client.liquidrender.*;
import tinker.tconstruct.entity.*;
import tinker.tconstruct.logic.*;
import tinker.tconstruct.tools.*;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class TProxyClient extends TProxyCommon
{
	public static SmallFontRenderer smallFontRenderer;

	/* Registers any rendering code. */
	public void registerRenderer ()
	{
		Minecraft mc = Minecraft.getMinecraft();
		smallFontRenderer = new SmallFontRenderer(mc.gameSettings, "/font/default.png", mc.renderEngine, false);
		RenderingRegistry.registerBlockHandler(new TableRender());
		RenderingRegistry.registerBlockHandler(new FrypanRender());
		RenderingRegistry.registerBlockHandler(new SmelteryRender());
		RenderingRegistry.registerBlockHandler(new TankRender());
		RenderingRegistry.registerBlockHandler(new SearedRender());
		RenderingRegistry.registerBlockHandler(new RenderLiquidMetal());
		//RenderingRegistry.registerBlockHandler(new AxleRender());

		RenderEngine renderEngine = FMLClientHandler.instance().getClient().renderEngine;
		renderEngine.registerTextureFX(new LiquidIronFX());
		renderEngine.registerTextureFX(new LiquidIronFlowFX());
		renderEngine.registerTextureFX(new LiquidGoldFX());
		renderEngine.registerTextureFX(new LiquidGoldFlowFX());
		renderEngine.registerTextureFX(new LiquidCopperFX());
		renderEngine.registerTextureFX(new LiquidCopperFlowFX());
		renderEngine.registerTextureFX(new LiquidTinFX());
		renderEngine.registerTextureFX(new LiquidTinFlowFX());
		renderEngine.registerTextureFX(new LiquidAluminumFX());
		renderEngine.registerTextureFX(new LiquidAluminumFlowFX());
		renderEngine.registerTextureFX(new LiquidCobaltFX());
		renderEngine.registerTextureFX(new LiquidCobaltFlowFX());
		renderEngine.registerTextureFX(new LiquidArditeFX());
		renderEngine.registerTextureFX(new LiquidArditeFlowFX());
		renderEngine.registerTextureFX(new LiquidBronzeFX());
		renderEngine.registerTextureFX(new LiquidBronzeFlowFX());
		renderEngine.registerTextureFX(new LiquidAlBrassFX());
		renderEngine.registerTextureFX(new LiquidAlBrassFlowFX());
		renderEngine.registerTextureFX(new LiquidManyullynFX());
		renderEngine.registerTextureFX(new LiquidManyullynFlowFX());
		renderEngine.registerTextureFX(new LiquidAlumiteFX());
		renderEngine.registerTextureFX(new LiquidAlumiteFlowFX());
		renderEngine.registerTextureFX(new LiquidObsidianFX());
		renderEngine.registerTextureFX(new LiquidObsidianFlowFX());
		renderEngine.registerTextureFX(new LiquidSteelFX());
		renderEngine.registerTextureFX(new LiquidSteelFlowFX());

		//Tools
		MinecraftForgeClient.preloadTexture(TContent.blockTexture);
		IItemRenderer render = new SuperCustomToolRenderer();
		for (ToolCore tool : TConstructRegistry.tools)
		{
			MinecraftForgeClient.registerItemRenderer(tool.itemID, render);
		}

		//Special Renderers
		ClientRegistry.bindTileEntitySpecialRenderer(CastingTableLogic.class, new CastingTableSpecialRenderer());

		//Entities
		RenderingRegistry.registerEntityRenderingHandler(CartEntity.class, new CartRender());
		RenderingRegistry.registerEntityRenderingHandler(Skyla.class, new SkylaRender());
		RenderingRegistry.registerEntityRenderingHandler(FancyEntityItem.class, new FancyItemRender());
		//RenderingRegistry.registerEntityRenderingHandler(net.minecraft.entity.player.EntityPlayer.class, new PlayerArmorRender()); // <-- Works, woo!
	}

	/* Ties an internal name to a visible one. */
	public void addNames ()
	{
		//LanguageRegistry.addName(TContent.lavaTank, "Lava Tank");
		/*LanguageRegistry.instance().addStringLocalization("itemGroup.TConstructTools", "TConstruct Tools");
		LanguageRegistry.instance().addStringLocalization("itemGroup.TConstructMaterials", "TConstruct Materials");
		LanguageRegistry.instance().addStringLocalization("itemGroup.TConstructBlocks", "TConstruct Blocks");*/

		String langDir = "/tinkerlang/";
		String[] langFiles =
			{ "en_US.xml" };

		for (String langFile : langFiles)
		{
			try
			{
				LanguageRegistry.instance().loadLocalization(langDir + langFile, langFile.substring(langFile.lastIndexOf('/') + 1, langFile.lastIndexOf('.')), true);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		/*LanguageRegistry.instance().addStringLocalization("crafters.ToolStation", "Tool Station");
		LanguageRegistry.instance().addStringLocalization("crafters.PartBuilder", "Part Builder");
		LanguageRegistry.instance().addStringLocalization("crafters.PatternShaper", "Pattern Shaper");
		LanguageRegistry.instance().addStringLocalization("inventory.PatternChest", "Pattern Chest");
		LanguageRegistry.instance().addStringLocalization("crafters.Smeltery", "Smeltery");
		LanguageRegistry.instance().addStringLocalization("crafters.Frypan", "Frying Pan");

		LanguageRegistry.instance().addStringLocalization("ToolStation.Crafter.name", "Tool Station");
		LanguageRegistry.instance().addStringLocalization("ToolStation.Parts.name", "Part Builder");
		LanguageRegistry.instance().addStringLocalization("ToolStation.PatternChest.name", "Pattern Chest");
		LanguageRegistry.instance().addStringLocalization("ToolStation.PatternShaper.name", "Stencil Table");
		LanguageRegistry.instance().addStringLocalization("ToolStation.CastingTable.name", "Casting Table");

		LanguageRegistry.instance().addStringLocalization("CraftedSoil.Slime.name", "Slimy Mud");
		LanguageRegistry.instance().addStringLocalization("CraftedSoil.Grout.name", "Grout");

		LanguageRegistry.instance().addStringLocalization("MetalOre.NetherSlag.name", "Netherack Slag");
		LanguageRegistry.instance().addStringLocalization("MetalOre.Cobalt.name", "Cobalt Ore");
		LanguageRegistry.instance().addStringLocalization("MetalOre.Ardite.name", "Ardite Ore");
		LanguageRegistry.instance().addStringLocalization("MetalOre.Copper.name", "Copper Ore");
		LanguageRegistry.instance().addStringLocalization("MetalOre.Tin.name", "Tin Ore");
		LanguageRegistry.instance().addStringLocalization("MetalOre.Aluminum.name", "Aluminum Ore");
		LanguageRegistry.instance().addStringLocalization("MetalOre.Slag.name", "Stone Slag");

		LanguageRegistry.instance().addStringLocalization("Smeltery.Controller.name", "Smeltery");
		LanguageRegistry.instance().addStringLocalization("Smeltery.Drain.name", "Smeltery Drain");
		LanguageRegistry.instance().addStringLocalization("Smeltery.Brick.name", "Seared Bricks");
		LanguageRegistry.instance().addStringLocalization("Smeltery.Gague.name", "Seared Glass");
		LanguageRegistry.instance().addStringLocalization("Smeltery.Window.name", "Seared Window");*/

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
			String internalName = "item.tconstruct.ToolShard." + toolMaterialNames[i] + ".name";
			String visibleName = shardNames[i];
			LanguageRegistry.instance().addStringLocalization(internalName, "en_US", visibleName);
		}

		for (int i = 0; i < materialItemNames.length; i++)
		{
			String internalName = "item.tconstruct.Materials." + materialItemInternalNames[i] + ".name";
			String visibleName = materialItemNames[i];
			LanguageRegistry.instance().addStringLocalization(internalName, "en_US", visibleName);
		}

		for (int i = 0; i < patterns.length; i++)
		{
			String internalName = "item.tconstruct.Pattern." + patterns[i] + ".name";
			String visibleName = patternNames[i] + " Pattern";
			LanguageRegistry.instance().addStringLocalization(internalName, "en_US", visibleName);
		}

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
		//LanguageRegistry.addName(TContent.lumberaxe, "Lumber Axe");

		LanguageRegistry.addName(TContent.ironFlowing, "Liquid Iron");
		LanguageRegistry.addName(TContent.ironStill, "Liquid Iron");
		LanguageRegistry.addName(TContent.goldFlowing, "Liquid Gold");
		LanguageRegistry.addName(TContent.goldStill, "Liquid Gold");
		LanguageRegistry.addName(TContent.copperFlowing, "Liquid Copper");
		LanguageRegistry.addName(TContent.copperStill, "Liquid Copper");
		LanguageRegistry.addName(TContent.tinFlowing, "Liquid Tin");
		LanguageRegistry.addName(TContent.tinStill, "Liquid Tin");
		LanguageRegistry.addName(TContent.aluminumFlowing, "Liquid Aluminum");
		LanguageRegistry.addName(TContent.aluminumStill, "Liquid Aluminum");
		LanguageRegistry.addName(TContent.cobaltFlowing, "Liquid Cobalt");
		LanguageRegistry.addName(TContent.cobaltStill, "Liquid Cobalt");
		LanguageRegistry.addName(TContent.arditeFlowing, "Liquid Ardite");
		LanguageRegistry.addName(TContent.arditeStill, "Liquid Ardite");

		LanguageRegistry.addName(TContent.bronzeFlowing, "Liquid Bronze");
		LanguageRegistry.addName(TContent.bronzeStill, "Liquid Bronze");
		LanguageRegistry.addName(TContent.alBrassFlowing, "Liquid Brass");
		LanguageRegistry.addName(TContent.alBrassStill, "Liquid Brass");
		LanguageRegistry.addName(TContent.alumiteFlowing, "Liquid Alumite");
		LanguageRegistry.addName(TContent.alumiteStill, "Liquid Alumite");
		LanguageRegistry.addName(TContent.manyullynFlowing, "Liquid Manyullyn");
		LanguageRegistry.addName(TContent.manyullynStill, "Liquid Manyullyn");
		LanguageRegistry.addName(TContent.obsidianFlowing, "Liquid Obsidian");
		LanguageRegistry.addName(TContent.obsidianStill, "Liquid Obsidian");
		LanguageRegistry.addName(TContent.steelFlowing, "Liquid Steel");
		LanguageRegistry.addName(TContent.steelStill, "Liquid Steel");
	}

	public static final String[] shardNames = new String[] { "Wood", "Stone Shard", "Iron Chunk", "Flint Shard", "Cactus Shard", "Bone", "Obsidian Shard", "Netherrack Shard", "Slime Crystal Fragment", "Paper", "Cobalt Chunk", "Ardite Chunk", "Manyullyn Chunk", "Copper Chunk", "Bronze Chunk" };

	public static final String[] materialItemInternalNames = new String[] { "PaperStack", "SlimeCrystal", "SearedBrick", "CobaltIngot", "ArditeIngot", "ManyullynIngot", "Mossball", "LavaCrystal", "NecroticBone", "CopperIngot", "TinIngot", "AluminumIngot", "RawAluminum" };

	public static final String[] materialItemNames = new String[] { "Paper Stack", "Slime Crystal", "Seared Brick", "Cobalt Ingot", "Ardite Ingot", "Manyullyn Ingot", "Ball of Moss", "Lava Crystal", "Necrotic Bone", "Copper Ingot", "Tin Ingot", "Aluminum Ingot", "Raw Aluminum" };

	public static final String[] toolMaterialNames = new String[] { "Wood", "Stone", "Iron", "Flint", "Cactus", "Bone", "Obsidian", "Netherrack", "Slime", "Paper", "Cobalt", "Ardite", "Manyullyn", "Copper", "Bronze" };

	public static final String[] materialTypes = new String[] { "ToolRod", "PickaxeHead", "ShovelHead", "AxeHead", "SwordBlade", "LargeGuard", "MediumGuard", "Crossbar", "Binding", "FrypanHead", "SignHead", "LumberHead" };

	public static final String[] materialNames = new String[] { " Rod", " Pickaxe Head", " Shovel Head", " Axe Head", " Sword Blade", " Wide Guard", " Hand Guard", " Crossbar", " Binding", " Pan", " Board", " Broad Axe Head" };

	public static final String[] patterns = new String[] { "blank", "rod", "pickaxe", "shovel", "axe", "blade", "largeguard", "medguard", "crossbar", "binding", "frypan", "sign", "lumber" };

	public static final String[] patternNames = new String[] { "Blank", "Tool Rod", "Pickaxe Head", "Shovel Head", "Axe Head", "Sword Blade", "Wide Guard", "Hand Guard", "Crossbar", "Tool Binding", "Pan", "Board", "Broad Axe Head" };

	public static Document diary;
	public static Document volume1;

	public void readManuals ()
	{
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		diary = readManual("/manuals/diary.xml", dbFactory);
		volume1 = readManual("/manuals/materials.xml", dbFactory);
	}

	Document readManual (String location, DocumentBuilderFactory dbFactory)
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

	public static Document getManualFromStack (ItemStack stack)
	{
		switch (stack.getItemDamage())
		{
		case 0:
			return diary;
		case 1:
			return volume1;
		}

		return null;
	}

	@Override
	public File getLocation ()
	{
		return Minecraft.getMinecraftDir();
	}
}

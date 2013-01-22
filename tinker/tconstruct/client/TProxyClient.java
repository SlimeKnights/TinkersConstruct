package tinker.tconstruct.client;


import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.MinecraftForgeClient;
import tinker.tconstruct.TConstructContent;
import tinker.tconstruct.TProxyCommon;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class TProxyClient extends TProxyCommon
{
	/* Registers any rendering code. */
	public void registerRenderer() 
	{
		RenderingRegistry.registerBlockHandler(new TableRender());
		RenderingRegistry.registerBlockHandler(new TankRender());
		RenderingRegistry.registerBlockHandler(new FrypanRender());
		
		MinecraftForgeClient.preloadTexture(TConstructContent.craftingTexture);
		MinecraftForgeClient.preloadTexture(TConstructContent.baseHeads);
		MinecraftForgeClient.preloadTexture(TConstructContent.baseAccessories);
		MinecraftForgeClient.preloadTexture(TConstructContent.patternTexture);
		MinecraftForgeClient.preloadTexture(TConstructContent.swordparts);
		MinecraftForgeClient.preloadTexture(TConstructContent.jokeparts);
		
		MinecraftForgeClient.preloadTexture(TConstructContent.pickaxeTexture);
		MinecraftForgeClient.preloadTexture(TConstructContent.broadswordTexture);
		MinecraftForgeClient.preloadTexture(TConstructContent.shovelTexture);
		MinecraftForgeClient.preloadTexture(TConstructContent.axeTexture);
		MinecraftForgeClient.preloadTexture(TConstructContent.longswordTexture);
		MinecraftForgeClient.preloadTexture(TConstructContent.rapierTexture);
		
		MinecraftForgeClient.preloadTexture(TConstructContent.frypanTexture);
		MinecraftForgeClient.preloadTexture(TConstructContent.signTexture);
		MinecraftForgeClient.preloadTexture(TConstructContent.blockTexture);
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
		LanguageRegistry.instance().addStringLocalization("ToolStation.Parts.name", "Part Crafting");
		LanguageRegistry.instance().addStringLocalization("ToolStation.PatternChest.name", "Pattern Chest");
		LanguageRegistry.instance().addStringLocalization("ToolStation.PatternShaper.name", "Pattern Shaper");
		
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
		LanguageRegistry.addName(TConstructContent.blankPattern, "Blank Pattern");
		LanguageRegistry.addName(TConstructContent.pickaxe, "Pickaxe");
		LanguageRegistry.addName(TConstructContent.shovel, "Shovel");
		LanguageRegistry.addName(TConstructContent.axe, "Axe");
		LanguageRegistry.addName(TConstructContent.broadsword, "Broadsword");
		LanguageRegistry.addName(TConstructContent.longsword, "Longsword");
		LanguageRegistry.addName(TConstructContent.rapier, "Rapier");
		LanguageRegistry.addName(TConstructContent.frypan, "Frying Pan");
		LanguageRegistry.addName(TConstructContent.battlesign, "Battlesign");
		LanguageRegistry.addName(TConstructContent.mattock, "Mattock");
		LanguageRegistry.addName(TConstructContent.lumberaxe, "Lumber Axe");
	}
	
	@Override
	public File getLocation()
	{
		return Minecraft.getMinecraftDir();
	}
	
	public static final String[] materialItemInternalNames = new String[] { 
		"PaperStack", "SlimeCrystal", "SearedBrick", "CobaltIngot", "ArditeIngot", "ManyullynIngot", "Mossball", "LavaCrystal", "NecroticBone" };
	
	public static final String[] materialItemNames = new String[] { 
		"Paper Stack", "Slime Crystal", "Seared Brick", "Cobalt Ingot", "Ardite Ingot", "Manyullyn Ingot", "Ball of Moss", "Lava Crystal", "Necrotic Bone" };
	
	public static final String[] toolMaterialNames = new String[] { 
		"Wood", "Stone", "Iron", "Flint", "Cactus", "Bone", "Obsidian", "Netherrack", "Slime", "Paper", "Cobalt", "Ardite", "Manyullyn" };
	
	public static final String[] materialTypes = new String[] {
		"ToolRod", "PickaxeHead", "ToolShard", "ShovelHead", "AxeHead", "SwordBlade", "LargeGuard", "MediumGuard", "Crossbar", "Binding", "FrypanHead", "SignHead", "LumberHead" };
	
	public static final String[] materialNames = new String[] {
		" Rod", " Pickaxe Head", " Shard", " Shovel Head", " Axe Head", " Sword Blade", " Wide Guard", " Hand Guard", " Crossbar", " Binding", " Pan", " Board", " Broad Axe Head" };
	
	public static final String[] patterns = new String[] {
		"blank", "rod", "pickaxe", "shovel", "axe", "blade", "largeguard", "medguard", "crossbar", "binding", "frypan", "sign", "lumber" };
	
	public static final String[] patternNames = new String[] {
		"Blank", "Tool Rod", "Pickaxe Head", "Shovel Head", "Axe Head", "Sword Blade", "Wide Guard", "Hand Guard", "Crossbar", "Tool Binding", "Pan", "Board", "Broad Axe Head" };
}

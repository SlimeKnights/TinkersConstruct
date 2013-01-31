package tinker.tconstruct;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;
import tinker.tconstruct.client.gui.ToolGuiElement;
import tinker.tconstruct.crafting.PatternBuilder;
import tinker.tconstruct.tools.ToolCore;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;

/** TConstruct, the tool mod.
 * Craft your tools with style, then modify until the original is gone!
 * @author: mDiyo
 */

@Mod(modid = "TConstruct", name = "TConstruct", version = "mc1.4.7_1.0.7")
@NetworkMod(serverSideRequired = false, clientSideRequired = true, channels={"TConstruct"}, packetHandler = tinker.tconstruct.TConstructPacketHandler.class)
public class TConstruct 
{
	/* Instance of this mod, used for grabbing prototype fields */
	@Instance("TConstruct")
	public static TConstruct instance;
	/* Proxies for sides, used for graphics processing */
	@SidedProxy(clientSide = "tinker.tconstruct.client.TProxyClient", serverSide = "tinker.tconstruct.TProxyCommon")
	public static TProxyCommon proxy;
	
	@PreInit
	public void preInit(FMLPreInitializationEvent evt)
	{
		MinecraftForge.EVENT_BUS.register(this);
		PHConstruct.initProps();
		materialTab = new TabTools("TConstructMaterials");
		toolTab = new TabTools("TConstructTools");
		blockTab = new TabTools("TConstructBlocks");
		content = new TConstructContent();
		
		NetworkRegistry.instance().registerGuiHandler(instance, new TConstructGuiHandler());
	}
	
	@Init
	public void load(FMLInitializationEvent evt) 
	{
		//GameRegistry.registerWorldGenerator(new TBaseWorldGenerator());
	}
	
	@PostInit
	public void postInit(FMLPostInitializationEvent evt) 
	{
		proxy.addNames();
		proxy.registerRenderer();
		content.modIntegration();
		
		ArrayList<ItemStack> copperIngots = OreDictionary.getOres("ingotCopper");
		for (ItemStack copper : copperIngots)
			PatternBuilder.instance.registerMaterial(copper, 2, "Copper");
		
		ArrayList<ItemStack> bronzeIngots = OreDictionary.getOres("ingotBronze");
		for (ItemStack bronze : bronzeIngots)
			PatternBuilder.instance.registerMaterial(bronze, 2, "Bronze");
	}
	
	@ForgeSubscribe
    public void registerOre(OreRegisterEvent evt)
	{
		if (evt.Name == "battery")
			content.modE.batteries.add(evt.Ore);
		
		if (evt.Name == "basicCircuit")
			content.modE.circuits.add(evt.Ore);
		
		if (evt.Name == "ingotCopper")
			PatternBuilder.instance.registerMaterial(evt.Ore, 2, "Copper");
		
		if (evt.Name == "ingotBronze")
			PatternBuilder.instance.registerMaterial(evt.Ore, 2, "Bronze");
	}	
	
	public static TConstructContent content;
	
	public static Random tRand = new Random();
	public static TabTools toolTab;
	public static TabTools materialTab;
	public static TabTools blockTab;
}

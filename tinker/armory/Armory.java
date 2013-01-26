package tinker.armory;

import tinker.armory.content.ArmorStandItem;
import tinker.armory.content.Toolrack;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

/**
 * Tinkering with the Armory
 * Every good castle has a place to put its tools
 * Armor stands are entites!
 * @author: mDiyo
 */

@Mod(modid = "Armory", name = "Tinker's Armory", version = "1.4.7_A3")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class Armory 
{
	/* Proxies for sides, used for client-only processing */
	@SidedProxy(clientSide = "tinker.armory.client.ArmoryProxyClient", serverSide = "tinker.armory.ArmoryProxyCommon")
	public static ArmoryProxyCommon proxy;
	
	/* Instance of this mod, used for grabbing prototype fields */
	@Instance("Armory")
	public static Armory instance;
	
	@PreInit
	public void preInit(FMLPreInitializationEvent evt)
	{
		PHArmory.initProps();
		stoneRack = new Toolrack(PHArmory.rackBlock, Material.rock);
		GameRegistry.registerBlock(stoneRack, tinker.armory.content.ToolrackItem.class, "ToolRack");
		GameRegistry.registerTileEntity(tinker.armory.content.ToolrackLogic.class, "InfiToolrack");
		/*armorStand = new ArmorStand(1501, Material.rock);
		GameRegistry.registerBlock(armorStand, mDiyo.inficraft.armory.ArmorStandItem.class);
		GameRegistry.registerTileEntity(mDiyo.inficraft.armory.ArmorStandLogic.class, "InfiArmorStand");*/
		
		armorStandItem = new ArmorStandItem(PHArmory.armorItem).setItemName("armorstand");
		LanguageRegistry.instance().addName(armorStandItem, "Armor Stand");
		
		EntityRegistry.registerModEntity(tinker.armory.content.ArmorStandEntity.class, "Armor Stand", 0, this, 32, 5, true);
		NetworkRegistry.instance().registerGuiHandler(instance, new ArmoryGuiHandler());
		
		GameRegistry.addRecipe(new ItemStack(Armory.instance.armorStandItem, 1, 0), " c ", "csc", " b ", 's', Item.stick, 'c', Block.cobblestone, 'b', Block.stoneSingleSlab);
	}

	@Init
	public void init(FMLInitializationEvent evt) 
	{		
		proxy.registerRenderer();
		proxy.addNames();
		proxy.addRecipes();
	}
	
	/* Prototype fields, used elsewhere */
	public static Block stoneRack;
	public static Block woodRack;
	public static Block armorStand;
	public static Block pedestal;
	
	public static Item armorStandItem;
	
	public static String texture = "/tinkertextures/armory.png";
}

package common.darkknight.jewelrycraft.item;

import net.minecraft.item.Item;

import common.darkknight.jewelrycraft.JewelrycraftMod;
import common.darkknight.jewelrycraft.config.ConfigHandler;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class ItemList
{
    public static Item     thiefGloves;
    public static Item     shadowIngot;
    public static Item     molds;
    public static Item     clayMolds;
    public static ItemRing     ring;
    
    private static boolean isInitialized = false;
    
    public static void preInit(FMLPreInitializationEvent e)
    {
        if (!isInitialized)
        {
            thiefGloves = new ItemThiefGloves(ConfigHandler.idThiefGloves - 256).setUnlocalizedName("Jewelrycraft.thiefGloves").setTextureName("jewelrycraft:thiefGloves").setCreativeTab(JewelrycraftMod.jewelrycraft);
            shadowIngot = new Item(ConfigHandler.idShadowIngot - 256).setUnlocalizedName("Jewelrycraft.ingotShadow").setTextureName("jewelrycraft:ingotShadow").setCreativeTab(JewelrycraftMod.jewelrycraft);
            molds = new ItemMolds(ConfigHandler.idMolds - 256).setUnlocalizedName("Jewelrycraft.mold").setTextureName("Mold").setCreativeTab(JewelrycraftMod.jewelrycraft);
            clayMolds = new ItemClayMolds(ConfigHandler.idClayMolds - 256).setUnlocalizedName("Jewelrycraft.mold").setTextureName("Mold").setCreativeTab(JewelrycraftMod.jewelrycraft);
            ring = (ItemRing) new ItemRing(ConfigHandler.idRing - 256).setUnlocalizedName("Jewelrycraft.ring").setTextureName("jewelrycraft:ring");
            
            GameRegistry.registerItem(thiefGloves, "thiefGloves");
            GameRegistry.registerItem(shadowIngot, "shadowIngot");
            GameRegistry.registerItem(molds, "molds");
            GameRegistry.registerItem(clayMolds, "clayMolds");
            GameRegistry.registerItem(ring, "ring");
            
            isInitialized = true;
        }
    }
}

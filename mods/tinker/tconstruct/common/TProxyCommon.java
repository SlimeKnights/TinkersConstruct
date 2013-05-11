package mods.tinker.tconstruct.common;

import java.io.File;

import mods.tinker.tconstruct.TConstruct;
import mods.tinker.tconstruct.inventory.ArmorExtendedContainer;
import mods.tinker.tconstruct.library.blocks.InventoryLogic;
import mods.tinker.tconstruct.util.player.TPlayerStats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.LanguageRegistry;

/**
 * Common proxy class for InfiTools
 */

public class TProxyCommon implements IGuiHandler
{
    public static int stationGuiID = 0;
    public static int partGuiID = 1;
    public static int pchestGuiID = 2;
    public static int pshaperGuiID = 3;
    public static int frypanGuiID = 4;

    public static int smelteryGuiID = 7;
    public static int armorGuiID = 101;
    public static int manualGuiID = -1;
    
    @Override
    public Object getServerGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (ID < 0)
            return null;

        else if (ID <= 100)
        {
            TileEntity tile = world.getBlockTileEntity(x, y, z);
            if (tile != null && tile instanceof InventoryLogic)
            {
                return ((InventoryLogic) tile).getGuiContainer(player.inventory, world, x, y, z);
            }
        }
        else
        {
            if (ID == armorGuiID)
            {
                TPlayerStats stats = TConstruct.playerTracker.getPlayerStats(player.username);
                return new ArmorExtendedContainer(player.inventory, stats.armor);
            }
        }
        return null;
    }

    @Override
    public Object getClientGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        // TODO Auto-generated method stub
        return null;
    }

    
    public void registerTickHandler()
    {
        
    }
	/* Registers any rendering code. Does nothing server-side */
	public void registerRenderer() {}
    /* Ties an internal name to a visible one. */
    public void addNames ()
    {

        String langDir = "/mods/tinker/resources/lang/";
        String[] langFiles = { "en_US.xml" };

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

        /*for (int i = 0; i < materialItemNames.length; i++)
        {
            String internalName = "item.tconstruct.Materials." + materialItemInternalNames[i] + ".name";
            String visibleName = materialItemNames[i];
            LanguageRegistry.instance().addStringLocalization(internalName, "en_US", visibleName);
        }*/

        for (int i = 0; i < patterns.length; i++)
        {
            String internalName = "item.tconstruct.Pattern." + patterns[i] + ".name";
            String visibleName = patternNames[i] + " Pattern";
            LanguageRegistry.instance().addStringLocalization(internalName, "en_US", visibleName);
            internalName = "item.tconstruct.MetalPattern." + patterns[i] + ".name";
            visibleName = patternNames[i] + " Cast";
            LanguageRegistry.instance().addStringLocalization(internalName, "en_US", visibleName);
        }

        //LanguageRegistry.addName(TContent.manualBook, "Tinker's Log");

        //LanguageRegistry.instance().addStringLocalization("entity.TConstruct.UnstableCreeper.name", "en_US", "Nitro Creeper");
        //LanguageRegistry.instance().addStringLocalization("entity.TConstruct.EdibleSlime.name", "en_US", "Blue Slime");
        //LanguageRegistry.instance().addStringLocalization("entity.TConstruct.MetalSlime.name", "en_US", "Metal Slime");
        //LanguageRegistry.instance().addStringLocalization("item.tconstruct.diary.diary.name", "en_US", "Tinker's Log");
        LanguageRegistry.instance().addStringLocalization("item.tconstruct.Pattern.blank_pattern.name", "en_US", "Blank Pattern");
        LanguageRegistry.instance().addStringLocalization("item.tconstruct.Pattern.blank_cast.name", "en_US", "Blank Cast");
        //LanguageRegistry.addName(TContent.blankPattern, "Blank Pattern");
        LanguageRegistry.addName(TContent.pickaxe, "Pickaxe");
        LanguageRegistry.addName(TContent.shovel, "Shovel");
        LanguageRegistry.addName(TContent.axe, "Hatchet");
        LanguageRegistry.addName(TContent.broadsword, "Broadsword");
        LanguageRegistry.addName(TContent.longsword, "Longsword");
        LanguageRegistry.addName(TContent.rapier, "Rapier");
        LanguageRegistry.addName(TContent.frypan, "Frying Pan");
        LanguageRegistry.addName(TContent.battlesign, "Battlesign");
        LanguageRegistry.addName(TContent.mattock, "Mattock");
        LanguageRegistry.addName(TContent.potionLauncher, "Potion Launcher");
        LanguageRegistry.addName(TContent.dagger, "Dagger");
        LanguageRegistry.addName(TContent.chisel, "Chisel");
        //LanguageRegistry.addName(TContent.lumberaxe, "Lumber Axe");

    }
	
	public void readManuals() {}
	
	public void registerKeys() {}
	
	public File getLocation()
	{
		return new File(".");
	}

	public void spawnParticle (String slimeParticle, double xPos, double yPos, double zPos, double velX, double velY, double velZ) {}
	


    public static final String[] shardNames = new String[] { "Wood", "Stone Shard", "Iron Chunk", "Flint Shard", "Cactus Shard", "Bone", "Obsidian Shard", "Netherrack Shard",
            "Slime Crystal Fragment", "Paper", "Cobalt Chunk", "Ardite Chunk", "Manyullyn Chunk", "Copper Chunk", "Bronze Chunk", "Alumite Chunk", "Steel Chunk", "Slime Crystal Fragment" };

    /*public static final String[] materialItemInternalNames = new String[] {
            "IronNugget",
            "CopperNugget", "TinNugget", "AluminumNugget", "SilverNugget" };

    public static final String[] materialItemNames = new String[] { 
            "Iron Nugget",
            "Copper Nugget", "Tin Nugget", "Aluminum Nugget", "Silver Nugget" };*/

    public static final String[] toolMaterialNames = new String[] { "Wood", "Stone", "Iron", "Flint", "Cactus", "Bone", "Obsidian", "Netherrack", "Slime", "Paper", "Cobalt", "Ardite", "Manyullyn",
            "Copper", "Bronze", "Alumite", "Steel", "Blue Slime" };

    public static final String[] materialTypes = new String[] { "ToolRod", "PickaxeHead", "ShovelHead", "AxeHead", "SwordBlade", "LargeGuard", "MediumGuard", "Crossbar", "Binding", "FrypanHead",
            "SignHead", "LumberHead", "KnifeBlade", "ChiselHead" };

    public static final String[] materialNames = new String[] { " Rod", " Pickaxe Head", " Shovel Head", " Axe Head", " Sword Blade", " Wide Guard", " Hand Guard", " Crossbar", " Binding", " Pan",
            " Board", " Broad Axe Head", " Knife Blade", " Chisel Head" };

    public static final String[] patterns = new String[] { "ingot", "rod", "pickaxe", "shovel", "axe", "swordblade", "largeguard", "mediumguard", "crossbar", "binding", "frypan", "sign", "knifeblade", "chisel" };

    public static final String[] patternNames = new String[] { "Ingot", "Tool Rod", "Pickaxe Head", "Shovel Head", "Axe Head", "Sword Blade", "Wide Guard", "Hand Guard", "Crossbar", "Tool Binding",
            "Pan", "Board", "Knife Blade", "Chisel Head" };
}

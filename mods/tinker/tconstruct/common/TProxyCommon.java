package mods.tinker.tconstruct.common;

import mods.tinker.tconstruct.TConstruct;
import mods.tinker.tconstruct.entity.Gardeslime;
import mods.tinker.tconstruct.inventory.ArmorExtendedContainer;
import mods.tinker.tconstruct.inventory.KnapsackContainer;
import mods.tinker.tconstruct.inventory.MiniGardyContainer;
import mods.tinker.tconstruct.landmine.inventory.ContainerLandmine;
import mods.tinker.tconstruct.landmine.tileentity.TileEntityLandmine;
import mods.tinker.tconstruct.library.blocks.InventoryLogic;
import mods.tinker.tconstruct.util.player.TPlayerStats;
import net.minecraft.entity.Entity;
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
    public static int toolStationID = 0;
    public static int partBuilderID = 1;
    public static int patternChestID = 2;
    public static int stencilTableID = 3;
    public static int frypanGuiID = 4;
    public static int toolForgeID = 5;
    public static int smelteryGuiID = 7;
    public static int glowstoneAggregatorID = 8;
    public static int drawbridgeID = 9;
    public static int landmineID = 10;
    public static int craftingStationID = 11;

    public static int inventoryGui = 100;
    public static int armorGuiID = 101;
    public static int knapsackGuiID = 102;
    
    public static int miniGardyGui = 131;

    public static int manualGuiID = -1;

    @Override
    public Object getServerGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (ID < 0)
            return null;
        
        if (ID == landmineID){
            return new ContainerLandmine(player, (TileEntityLandmine)world.getBlockTileEntity(x, y, z));
        }

        else if (ID < 100)
        {
            TileEntity tile = world.getBlockTileEntity(x, y, z);
            if (tile != null && tile instanceof InventoryLogic)
            {
                return ((InventoryLogic) tile).getGuiContainer(player.inventory, world, x, y, z);
            }
        }
        else
        {
            if (ID == inventoryGui)
            {
                //GuiInventory inv = new GuiInventory(player);
                return player.inventoryContainer;
            }
            if (ID == armorGuiID)
            {
                TPlayerStats stats = TConstruct.playerTracker.getPlayerStats(player.username);
                return new ArmorExtendedContainer(player.inventory, stats.armor);
            }
            if (ID == knapsackGuiID)
            {
                TPlayerStats stats = TConstruct.playerTracker.getPlayerStats(player.username);
                return new KnapsackContainer(player.inventory, stats.knapsack);
            }
            if (ID == miniGardyGui)
            {
                for (Object o : world.loadedEntityList)
                {
                    Entity entity = (Entity) o;
                    if (entity.entityId == x)
                    {
                        return new MiniGardyContainer(player.inventory, (Gardeslime) entity);
                    }
                }
                return null;
            }
        }
        return null;
    }

    @Override
    public Object getClientGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        return null;
    }

    public void registerTickHandler ()
    {
        //TickRegistry.registerTickHandler(new TimeTicker(), Side.SERVER);
    }

    /* Registers any rendering code. Does nothing server-side */
    public void registerRenderer ()
    {
    }

    /* Ties an internal name to a visible one. */
    public void addNames ()
    {

        String langDir = "/mods/tinker/resources/lang/";
        String[] langFiles = { "en_US.xml", "de_DE.xml", "ru_RU.xml", "zh_CN.xml" };

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
        LanguageRegistry.addName(TContent.hatchet, "Hatchet");
        LanguageRegistry.addName(TContent.broadsword, "Broadsword");
        LanguageRegistry.addName(TContent.longsword, "Longsword");
        LanguageRegistry.addName(TContent.rapier, "Rapier");
        LanguageRegistry.addName(TContent.frypan, "Frying Pan");
        LanguageRegistry.addName(TContent.battlesign, "Battlesign");
        LanguageRegistry.addName(TContent.mattock, "Mattock");
        LanguageRegistry.addName(TContent.potionLauncher, "Potion Launcher");
        LanguageRegistry.addName(TContent.dagger, "Dagger");
        LanguageRegistry.addName(TContent.chisel, "Chisel");
        LanguageRegistry.addName(TContent.scythe, "Scythe");
        LanguageRegistry.addName(TContent.lumberaxe, "Lumber Axe");
        LanguageRegistry.addName(TContent.cleaver, "Cleaver");
        LanguageRegistry.addName(TContent.excavator, "Excavator");
        LanguageRegistry.addName(TContent.hammer, "Hammer");
        LanguageRegistry.addName(TContent.battleaxe, "Battleaxe");
        LanguageRegistry.addName(TContent.cutlass, "Cutlass");
        LanguageRegistry.addName(TContent.aggregator, "Glowstone Aggregator");
        LanguageRegistry.addName(TContent.lightCrystalBase, "Lightstone Crystal");
        LanguageRegistry.addName(TContent.knapsack, "Knapsack");
        LanguageRegistry.addName(TContent.shortbow, "Shortbow");
        LanguageRegistry.addName(TContent.arrow, "Arrow");
        LanguageRegistry.addName(TContent.meatBlock, "Hambone");

    }

    public void readManuals ()
    {
    }

    public void registerKeys ()
    {
    }

    public void spawnParticle (String slimeParticle, double xPos, double yPos, double zPos, double velX, double velY, double velZ)
    {
    }

    public static final String[] shardNames = new String[] { "Wood", "Stone Shard", "Iron Chunk", "Flint Shard", "Cactus Shard", "Bone", "Obsidian Shard", "Netherrack Shard",
            "Slime Crystal Fragment", "Paper", "Cobalt Chunk", "Ardite Chunk", "Manyullyn Chunk", "Copper Chunk", "Bronze Chunk", "Alumite Chunk", "Steel Chunk", "Slime Crystal Fragment" };

    /*public static final String[] materialItemInternalNames = new String[] {
            "IronNugget",
            "CopperNugget", "TinNugget", "AluminumNugget", "SilverNugget" };

    public static final String[] materialItemNames = new String[] { 
            "Iron Nugget",
            "Copper Nugget", "Tin Nugget", "Aluminum Nugget", "Silver Nugget" };*/

    public static final String[] toolMaterialNames = new String[] { "Wood", "Stone", "Iron", "Flint", "Cactus", "Bone", "Obsidian", "Netherrack", "Slime", "Paper", "Cobalt", "Ardite", "Manyullyn",
            "Copper", "Bronze", "Alumite", "Steel", "Blue Slime", "", "", "", "", "", "", "", "", "", "", "", "", "", "Thaumium" };

    public static final String[] materialTypes = new String[] { "ToolRod", "PickaxeHead", "ShovelHead", "AxeHead", "SwordBlade", "LargeGuard", "MediumGuard", "Crossbar", "Binding", "FrypanHead",
            "SignHead", "LumberHead", "KnifeBlade", "ChiselHead", "ScytheBlade", "LumberHead", "ThickRod", "ThickBinding", "LargeSwordBlade", "LargePlate", "ExcavatorHead", "HammerHead", "FullGuard",
            "Bowstring", "Fletching", "Arrowhead" };

    public static final String[] materialNames = new String[] { " Rod", " Pickaxe Head", " Shovel Head", " Axe Head", " Sword Blade", " Wide Guard", " Hand Guard", " Crossbar", " Binding", " Pan",
            " Board", " Broad Axe Head", " Knife Blade", " Chisel Head", " Scythe Blade", " Broad Axe Head", " Tough Tool Rod", " Tough Binding", " Large Sword Blade", " Large Plate",
            " Excavator Head", " Hammer Head", " Full Guard", " Bowstring", " Fletching", " Arrowhead" };

    public static final String[] patterns = new String[] { "ingot", "rod", "pickaxe", "shovel", "axe", "swordblade", "largeguard", "mediumguard", "crossbar", "binding", "frypan", "sign",
            "knifeblade", "chisel", "largerod", "toughbinding", "largeplate", "broadaxe", "scythe", "excavator", "largeblade", "hammerhead", "fullguard", "bowstring", "fletching", "arrowhead" };

    public static final String[] patternNames = new String[] { "Ingot", "Tool Rod", "Pickaxe Head", "Shovel Head", "Axe Head", "Sword Blade", "Wide Guard", "Hand Guard", "Crossbar", "Tool Binding",
            "Pan", "Board", "Knife Blade", "Chisel Head", "Tough Tool Rod", "Tough Binding", "Large Plate", "Broad Axe Head", "Scythe Head", "Broad Shovel Head", "Large Blade", "Hammer Head",
            "Full Guard", "Bowstring", "Fletching", "Arrowhead" };
}

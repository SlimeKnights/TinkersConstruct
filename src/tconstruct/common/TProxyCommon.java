package tconstruct.common;

import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tconstruct.TConstruct;
import tconstruct.blocks.logic.TileEntityLandmine;
import tconstruct.inventory.*;
import tconstruct.library.blocks.InventoryLogic;
import tconstruct.util.player.TPlayerStats;

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
    public static int furnaceID = 8;
    public static int drawbridgeID = 9;
    public static int landmineID = 10;
    public static int craftingStationID = 11;
    public static int advDrawbridgeID = 12;
    public static int inventoryGui = 100;
    public static int armorGuiID = 101;
    public static int knapsackGuiID = 102;

    public static int manualGuiID = -1;

    @Override
    public Object getServerGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (ID < 0)
            return null;

        if (ID == landmineID)
        {
            return new ContainerLandmine(player, (TileEntityLandmine) world.getBlockTileEntity(x, y, z));
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
        // As of 1.6.x, this is now handled by Minecraft itself for the most part.

        LanguageRegistry.addName(TContent.potionLauncher, "Potion Launcher");
        LanguageRegistry.addName(TContent.knapsack, "Knapsack");

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

    public static final String[] patterns = new String[] { "ingot", "rod", "pickaxe", "shovel", "axe", "swordblade", "largeguard", "mediumguard", "crossbar", "binding", "frypan", "sign",
            "knifeblade", "chisel", "largerod", "toughbinding", "largeplate", "broadaxe", "scythe", "excavator", "largeblade", "hammerhead", "fullguard", "bowstring", "fletching", "arrowhead" };

    public void postInit ()
    {

    }
}

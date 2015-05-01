package tconstruct.armor;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import tconstruct.TConstruct;
import tconstruct.armor.inventory.*;
import tconstruct.armor.player.TPlayerStats;
import tconstruct.common.TProxyCommon;

public class ArmorProxyCommon implements IGuiHandler
{
    public static final int inventoryGui = 100;
    public static final int armorGuiID = 101;
    public static final int knapsackGuiID = 102;

    public void preInit() {}

    public void initialize ()
    {
        registerGuiHandler();
    }

    protected void registerGuiHandler ()
    {
        TProxyCommon.registerServerGuiHandler(inventoryGui, this);
        TProxyCommon.registerServerGuiHandler(armorGuiID, this);
        TProxyCommon.registerServerGuiHandler(knapsackGuiID, this);
    }

    @Override
    public Object getServerGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (ID == ArmorProxyCommon.inventoryGui)
        {
            // GuiInventory inv = new GuiInventory(player);
            return player.inventoryContainer;
        }
        if (ID == ArmorProxyCommon.armorGuiID)
        {
            TPlayerStats stats = TPlayerStats.get(player);
            return new ArmorExtendedContainer(player.inventory, stats.armor);
        }
        if (ID == ArmorProxyCommon.knapsackGuiID)
        {
            TPlayerStats stats = TPlayerStats.get(player);
            return new KnapsackContainer(player.inventory, stats.knapsack);
        }

        return null;
    }

    @Override
    public Object getClientGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void registerTickHandler ()
    {

    }

    public void registerKeys ()
    {

    }
    
    public void updatePlayerStats(TPlayerStats stats)
    {
        
    }
    
    public void dumpTPlayerStats(TPlayerStats stats)
    {
        TConstruct.logger.debug("~~~~~~~~~~~~~~~~~ STATS ~~~~~~~~~~~~~~~~~");
        TConstruct.logger.debug("Player: " + stats.player.get().getCommandSenderName());
        TConstruct.logger.debug("Level: " + stats.level);
        TConstruct.logger.debug("BonusHealth: " + stats.bonusHealth);
        TConstruct.logger.debug("Damage: " + stats.damage);
        TConstruct.logger.debug("Hunger: " + stats.hunger);
        TConstruct.logger.debug("Prev Dim: " + stats.previousDimension);
        TConstruct.logger.debug("Climb Walls: " + stats.climbWalls);
        TConstruct.logger.debug("Activate Goggles: " + stats.activeGoggles);
        TConstruct.logger.debug("Beginer Manual: " + stats.beginnerManual);
        TConstruct.logger.debug("Material Manual: " + stats.materialManual);
        TConstruct.logger.debug("Smeltery Manual: " + stats.smelteryManual);
        TConstruct.logger.debug("Weponary Manual: " + stats.weaponryManual);
        TConstruct.logger.debug("BattleSign Bonus: " + stats.battlesignBonus);
        TConstruct.logger.debug("Derp Level: " + stats.derpLevel);
        int i = 0;
        for (ItemStack stack : stats.armor.inventory)
        {
            if (stack != null)
            {
                TConstruct.logger.debug("Armor Slot: " + i + " Contains: " + stack.getDisplayName());   
            }
            i++;
        }
        i = 0;
        for (ItemStack stack : stats.knapsack.inventory)
        {
            if (stack != null)
            {
                TConstruct.logger.debug("Knapsack Slot: " + i + " Contains: " + stack.getDisplayName());
            }
            i++;
        }
        TConstruct.logger.debug("~~~~~~~~~~~~~~~~~ STATS ~~~~~~~~~~~~~~~~~");
    }
}

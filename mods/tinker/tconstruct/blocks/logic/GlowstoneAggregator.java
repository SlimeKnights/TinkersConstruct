package mods.tinker.tconstruct.blocks.logic;

import mods.tinker.tconstruct.inventory.AggregatorContainer;
import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class GlowstoneAggregator extends AggregatorLogic
{
    short currentTime;
    short maxTime = 20 * 60 * 5;
    public int currentLightLevel;

    public GlowstoneAggregator()
    {
        super(3);
    }

    @Override
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        return new AggregatorContainer(inventoryplayer, this);
    }

    @Override
    protected String getDefaultName ()
    {
        return "aggregator.glowstone";
    }

    @Override
    public void updateEntity ()
    {
        if (worldObj.isRemote)
            return;
        if (inventory[2] == null || inventory[2].stackSize < this.getInventoryStackLimit())
        {
            if (inventory[0] != null && inventory[0].itemID == Block.cobblestone.blockID)
            {
                currentLightLevel = worldObj.getSavedLightValue(EnumSkyBlock.Sky, xCoord, yCoord, zCoord) - worldObj.skylightSubtracted;
                if (currentLightLevel > 12)
                {
                    currentTime++;
                    if (currentTime >= 20)
                    {
                        currentTime = 0;
                        inventory[0].stackSize--;
                        if (inventory[0].stackSize < 1)
                            inventory[0] = null;
                        
                        if (inventory[2] != null)
                            inventory[2].stackSize++;
                        else
                            inventory[2] = new ItemStack(Block.glowStone);
                    }
                }
                /*
                if (this.currentTime % 60 == 0)
                    System.out.println("Light: " + lightLevel);
                currentTime++;
                if (currentTime >= maxTime)
                {
                    currentTime = 0;
                }*/
            }
        }
    }
}

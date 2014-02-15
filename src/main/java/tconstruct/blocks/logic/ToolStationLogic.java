package tconstruct.blocks.logic;

import tconstruct.inventory.ToolStationContainer;
import tconstruct.library.crafting.ToolBuilder;
import mantle.blocks.abstracts.InventoryLogic;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/* Simple class for storing items in the block
 */

public class ToolStationLogic extends InventoryLogic implements ISidedInventory
{
    public ItemStack previousTool;
    public String toolName;

    public ToolStationLogic()
    {
        super(4);
        toolName = "";
    }

    public ToolStationLogic(int slots)
    {
        super(slots);
        toolName = "";
    }

    public boolean canDropInventorySlot (int slot)
    {
        if (slot == 0)
            return false;
        return true;
    }

    @Override
    public ItemStack getStackInSlotOnClosing (int var1)
    {
        return null;
    }

    @Override
    public String getDefaultName ()
    {
        return "crafters.ToolStation";
    }

    @Override
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        return new ToolStationContainer(inventoryplayer, this);
    }

    public void markDirty ()
    {
        buildTool(toolName);
        if (this.worldObj != null)
        {
            this.blockMetadata = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
            this.worldObj.markTileEntityChunkModified(this.xCoord, this.yCoord, this.zCoord, this);
        }
    }

    public void buildTool (String name)
    {
        toolName = name;
        ItemStack tool = ToolBuilder.instance.buildTool(inventory[1], inventory[2], inventory[3], name);
        if (inventory[0] == null)
            inventory[0] = tool;
        else
        {
            NBTTagCompound tags = inventory[0].getTagCompound();
            if (!tags.getCompoundTag("InfiTool").hasKey("Built"))
            {
                inventory[0] = tool;
            }
        }
    }

    public void setToolname (String name)
    {
        toolName = name;
        buildTool(name);
    }

    public boolean canUpdate ()
    {
        return false;
    }

    @Override
    public int[] getAccessibleSlotsFromSide (int side)
    {
        return new int[0];
    }

    @Override
    public boolean canInsertItem (int i, ItemStack itemstack, int j)
    {
        return false;
    }

    @Override
    public boolean canExtractItem (int i, ItemStack itemstack, int j)
    {
        return false;
    }

    @Override
    public String getInventoryName ()
    {
        return null;
    }

    @Override
    public boolean hasCustomInventoryName ()
    {
        return false;
    }

    @Override
    public void openInventory ()
    {
    }

    @Override
    public void closeInventory ()
    {
    }
}
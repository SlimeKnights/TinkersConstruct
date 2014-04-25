package tconstruct.blocks.logic;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import tconstruct.inventory.ToolStationContainer;
import tconstruct.library.IModifyable;
import tconstruct.library.blocks.InventoryLogic;
import tconstruct.library.crafting.ModifyBuilder;
import tconstruct.library.crafting.ToolBuilder;

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

    public void onInventoryChanged ()
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
        ItemStack output = null;
        if (inventory[1] != null)
        {
            if (inventory[1].getItem() instanceof IModifyable) //Modify item
            {
                if (inventory[2] == null && inventory[3] == null)
                    output = inventory[1].copy();
                else
                {
                    output = ModifyBuilder.instance.modifyItem(inventory[1], new ItemStack[] { inventory[2], inventory[3] });
                }
            }
            else
            //Build new item
            {
                toolName = name;
                ItemStack tool = ToolBuilder.instance.buildTool(inventory[1], inventory[2], inventory[3], name);
                if (inventory[0] == null)
                    output = tool;
                else if (tool != null)
                {
                    NBTTagCompound tags = tool.getTagCompound();
                    if (!tags.getCompoundTag(((IModifyable) tool.getItem()).getBaseTag()).hasKey("Built"))
                    {
                        output = tool;
                    }
                }
            }
            if (!name.equals("")) //Name item
            {
                ItemStack temp = inventory[1].copy();
                if (output != null)
                    temp = output;

                if (temp != null)
                {
                    NBTTagCompound tags = temp.getTagCompound();
                    if (tags == null)
                    {
                        tags = new NBTTagCompound();
                        temp.setTagCompound(tags);
                    }

                    if (!(tags.hasKey("display")))
                    {
                        NBTTagCompound display = new NBTTagCompound();
                        String dName = temp.getItem() instanceof IModifyable ? "\u00A7f" + name : name;
                        display.setString("Name", dName);
                        tags.setCompoundTag("display", display);
                        output = temp;
                    }
                }

            }
        }
        inventory[0] = output;
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
}

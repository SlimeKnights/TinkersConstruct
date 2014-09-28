package tconstruct.tools.logic;

import mantle.blocks.abstracts.InventoryLogic;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import tconstruct.library.crafting.*;
import tconstruct.library.modifier.IModifyable;
import tconstruct.tools.inventory.ToolStationContainer;

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

    @Override
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

    @Override
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
                    if (!tags.getCompoundTag(((IModifyable) tool.getItem()).getBaseTagName()).hasKey("Built"))
                    {
                        output = tool;
                    }
                }
            }
            if (!name.equals("")) //Name item
                output = tryRenameTool(output, name);
        }
        inventory[0] = output;
    }

    public void setToolname (String name)
    {
        toolName = name;
        buildTool(name);
    }

    protected ItemStack tryRenameTool (ItemStack output, String name)
    {
        ItemStack temp;
        if (output != null)
            temp = output;
        else
            temp = inventory[1].copy();

        if (temp == null)
            return null; // output as well as inventory is null :(

        NBTTagCompound tags = temp.getTagCompound();
        if (tags == null)
        {
            tags = new NBTTagCompound();
            temp.setTagCompound(tags);
        }

        NBTTagCompound display = null;
        if (!(tags.hasKey("display")))
            display = new NBTTagCompound();
        else if (tags.getCompoundTag("display").hasKey("Name"))
            display = tags.getCompoundTag("display");

        if (display == null)
            return output;
        if (display.hasKey("Name") && !display.getString("Name").equals("\u00A7f" + ToolBuilder.defaultToolName(temp)))
            // no default name anymore
            return output;

        String dName = temp.getItem() instanceof IModifyable ? "\u00A7f" + name : name;
        display.setString("Name", dName);
        tags.setTag("display", display);
        temp.setRepairCost(2);
        output = temp;

        return output;
    }

    @Override
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
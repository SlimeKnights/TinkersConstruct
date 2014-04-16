package tconstruct.blocks.logic;

import tconstruct.inventory.ToolForgeContainer;
import tconstruct.library.IModifyable;
import tconstruct.library.crafting.ModifyBuilder;
import tconstruct.library.crafting.ToolBuilder;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/* Simple class for storing items in the block
 */

public class ToolForgeLogic extends ToolStationLogic implements ISidedInventory
{
    ItemStack previousTool;
    String toolName;

    public ToolForgeLogic()
    {
        super(6);
        toolName = "";
    }

    @Override
    public String getDefaultName ()
    {
        return "crafters.ToolForge";
    }

    @Override
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        return new ToolForgeContainer(inventoryplayer, this);
    }

    /*public void buildTool (String name)
    {
        toolName = name;
        ItemStack tool = ToolBuilder.instance.buildTool(inventory[1], inventory[2], inventory[3], inventory[4], name);
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
    }*/
    
    public void buildTool (String name)
    {
        
        if (inventory[1] != null && inventory[1].getItem() instanceof IModifyable)
        {
            ItemStack output = ModifyBuilder.instance.modifyItem(inventory[1], new ItemStack[] {inventory[2], inventory[3], inventory[4]});
            if (output != null)
                inventory[0] = output;
        }
        else
        {
            toolName = name;
            ItemStack tool = ToolBuilder.instance.buildTool(inventory[1], inventory[2], inventory[3], inventory[4], name);
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
    }
}

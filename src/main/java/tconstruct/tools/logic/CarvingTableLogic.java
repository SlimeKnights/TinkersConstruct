package tconstruct.tools.logic;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import mantle.blocks.abstracts.InventoryLogic;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tconstruct.TConstruct;
import tconstruct.library.crafting.PatternBuilder;
import tconstruct.library.util.IPattern;
import tconstruct.tools.inventory.CarvingTableContainer;
import tconstruct.tools.inventory.PartCrafterChestContainer;
import tconstruct.tools.inventory.PartCrafterContainer;
import tconstruct.tools.items.Pattern;
import tconstruct.util.network.CarvingTablePacket;
import tconstruct.util.network.ToolStationPacket;

public class CarvingTableLogic extends InventoryLogic implements ISidedInventory
{
    boolean craftedTop;
    boolean craftedBottom;

    /**
     * The currently selected pattern to use for crafting parts.
     */
    public ItemStack currentPattern = null;



    public CarvingTableLogic ()
    {
        super(6);
        craftedTop = false;
        craftedBottom = false;
    }


    @Override
    public String getDefaultName ()
    {
        return "toolstation.knapping";
    }

    @Override
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        return new CarvingTableContainer(inventoryplayer, this);
    }

    //Called when emptying a slot, not when another item is placed in it
    @Override
    public ItemStack decrStackSize (int slotID, int quantity)
    {
        ItemStack returnStack = super.decrStackSize(slotID, quantity);
        tryBuildPart(slotID);
        return returnStack;
    }

    public void tryBuildPart (int slotID)
    {
        if (slotID == 2 || slotID == 3)
        {
            if (!craftedTop)
            {
                int value = PatternBuilder.instance.getPartValue(inventory[0]);
                int cost = currentPattern != null ? ((IPattern)currentPattern.getItem()).getPatternCost(currentPattern) : 0;
                if (value > 0 && cost > 0)
                {
                    int decrease = cost / value;
                    if (cost % value != 0)
                        decrease++;
                    super.decrStackSize(0, decrease); //Call super to avoid crafting again
                }
            }

            if (inventory[2] != null || inventory[3] != null)
                craftedTop = true;
            else
                craftedTop = false;
        }

        if (!craftedTop)
            buildTopPart();

        if (slotID == 4 || slotID == 5)
        {
            if (!craftedBottom)
            {
                int value = PatternBuilder.instance.getPartValue(inventory[1]);
                int cost = currentPattern != null ? ((IPattern)currentPattern.getItem()).getPatternCost(currentPattern) : 0;
                if (value > 0 && cost > 0)
                {
                    int decrease = cost / value;
                    if (cost % value != 0)
                        decrease++;
                    super.decrStackSize(1, decrease); //Call super to avoid crafting again
                }
            }

            if (inventory[4] != null || inventory[5] != null)
                craftedBottom = true;
            else
                craftedBottom = false;
        }

        if (!craftedBottom)
            buildBottomPart();
    }

    //Called when a slot has something placed into it.
    @Override
    public void setInventorySlotContents (int slot, ItemStack itemstack)
    {
        super.setInventorySlotContents(slot, itemstack);
        if ((slot == 0) && !craftedTop)
            buildTopPart();
        if ((slot == 1) && !craftedBottom)
            buildBottomPart();
    }

    public void buildTopPart ()
    {
        if(!craftedTop)
        {
            if (currentPattern != null)
            {
                ItemStack[] parts = PatternBuilder.instance.getToolPart(inventory[0], currentPattern, null);
                if (parts != null)
                {
                    inventory[2] = parts[0];
                    inventory[3] = parts[1];
                }
                else
                {
                    inventory[2] = inventory[3] = null;
                }
            }
            else
            {
                if (!craftedTop)
                {
                    inventory[2] = inventory[3] = null;
                }
            }
        }
    }

    public void buildBottomPart ()
    {
        if(!craftedBottom)
        {
            if (currentPattern != null)
            {
                ItemStack[] parts = PatternBuilder.instance.getToolPart(inventory[1], currentPattern, null);
                if (parts != null)
                {
                    inventory[4] = parts[0];
                    inventory[5] = parts[1];
                }
                else
                {
                    inventory[4] = inventory[5] = null;
                }
            }
            else
            {
                if (!craftedBottom)
                {
                    inventory[4] = inventory[5] = null;
                }
            }
        }
    }

    /* NBT */
    @Override
    public void readFromNBT (NBTTagCompound tags)
    {
        super.readFromNBT(tags);
        craftedTop = tags.getBoolean("CraftedTop");
        craftedBottom = tags.getBoolean("CraftedBottom");
    }

    @Override
    public void writeToNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);
        tags.setBoolean("CraftedTop", craftedTop);
        tags.setBoolean("CraftedBottom", craftedBottom);
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
        return getDefaultName();
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

    @Override
    public boolean canUpdate ()
    {
        return false;
    }



}
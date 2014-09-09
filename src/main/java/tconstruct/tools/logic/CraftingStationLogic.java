package tconstruct.tools.logic;

import java.lang.ref.WeakReference;
import mantle.blocks.abstracts.InventoryLogic;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.*;
import net.minecraft.world.World;
import tconstruct.tools.inventory.CraftingStationContainer;

public class CraftingStationLogic extends InventoryLogic implements ISidedInventory
{
    public WeakReference<IInventory> chest; // TODO: These are prototypes
    public WeakReference<IInventory> doubleChest;
    public WeakReference<IInventory> patternChest;
    public WeakReference<IInventory> furnace;
    public boolean tinkerTable;
    public boolean stencilTable;
    public boolean doubleFirst;

    public CraftingStationLogic()
    {
        super(17); // 9 for crafting, 1 for output, 6 for extensions, 1 for
                   // plans
    }

    @Override
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        chest = null;
        doubleChest = null;
        patternChest = null;
        furnace = null;
        tinkerTable = false;
        int[] ys = { y, y - 1, y + 1 };
        for (byte iy = 0; iy < 3; iy++)
        {
            int yPos = ys[iy];
            for (int xPos = x - 1; xPos <= x + 1; xPos++)
            {
                for (int zPos = z - 1; zPos <= z + 1; zPos++)
                {
                    TileEntity tile = world.getTileEntity(xPos, yPos, zPos);
                    if (chest == null && tile instanceof TileEntityChest)
                    {
                        chest = new WeakReference(tile);
                        checkForChest(world, xPos, yPos, zPos, 1, 0);
                        checkForChest(world, xPos, yPos, zPos, -1, 0);
                        checkForChest(world, xPos, yPos, zPos, 0, 1);
                        checkForChest(world, xPos, yPos, zPos, 0, -1);
                    }
                    else if (patternChest == null && tile instanceof PatternChestLogic)
                        patternChest = new WeakReference(tile);
                    else if (furnace == null && (tile instanceof TileEntityFurnace || tile instanceof FurnaceLogic))
                        furnace = new WeakReference(tile);
                    else if (tinkerTable == false && tile instanceof ToolStationLogic)
                        tinkerTable = true;
                }
            }
        }

        return new CraftingStationContainer(inventoryplayer, this, x, y, z);
    }

    void checkForChest (World world, int x, int y, int z, int dx, int dz)
    {
        TileEntity tile = world.getTileEntity(x + dx, y, z + dz);
        if (tile instanceof TileEntityChest)
        {
            doubleChest = new WeakReference(tile);
            doubleFirst = dx + dz < 0;
        }
    }

    @Override
    protected String getDefaultName ()
    {
        return "crafters.CraftingStation";
    }

    @Override
    public boolean canDropInventorySlot (int slot)
    {
        if (slot == 0)
            return false;
        return true;
    }

    @Override
    public int[] getAccessibleSlotsFromSide (int var1)
    {
        return new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
    }

    @Override
    public boolean canInsertItem (int i, ItemStack itemstack, int j)
    {
        return i != 0;
    }

    @Override
    public ItemStack decrStackSize (int slot, int quantity)
    {
        if (slot == 0)
        {
            for (int i = 1; i < getSizeInventory(); i++)
                decrStackSize(i, 1);
        }
        return super.decrStackSize(slot, quantity);
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
        return true;
    }

    @Override
    public void closeInventory ()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void openInventory ()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean canUpdate ()
    {
        return false;
    }
}

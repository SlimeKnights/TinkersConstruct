package tconstruct.blocks.logic;

import java.lang.ref.WeakReference;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.World;
import tconstruct.inventory.CraftingStationContainer;
import tconstruct.library.blocks.InventoryLogic;

public class CraftingStationLogic extends InventoryLogic implements ISidedInventory
{
    public WeakReference<IInventory> chest; //TODO: These are prototypes
    public WeakReference<IInventory> doubleChest;
    public WeakReference<IInventory> patternChest;
    public WeakReference<IInventory> furnace;
    public boolean tinkerTable;
    public boolean stencilTable;

    public CraftingStationLogic()
    {
        super(17); //9 for crafting, 1 for output, 6 for extensions, 1 for plans
    }

    @Override
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        chest = null;
        doubleChest = null;
        patternChest = null;
        furnace = null;
        tinkerTable = false;
        for (int yPos = y - 1; yPos <= y + 1; yPos++)
        {
            for (int xPos = x - 1; xPos <= x + 1; xPos++)
            {
                for (int zPos = z - 1; zPos <= z + 1; zPos++)
                {
                    TileEntity tile = world.getBlockTileEntity(xPos, yPos, zPos);
                    if (chest == null && tile instanceof TileEntityChest)
                    {
                        chest = new WeakReference(tile);
                        checkForChest(world, xPos + 1, yPos, zPos);
                        checkForChest(world, xPos - 1, yPos, zPos);
                        checkForChest(world, xPos, yPos, zPos + 1);
                        checkForChest(world, xPos, yPos, zPos - 1);
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

    void checkForChest (World world, int x, int y, int z)
    {
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (tile instanceof TileEntityChest)
            doubleChest = new WeakReference(tile);
    }

    @Override
    protected String getDefaultName ()
    {
        return "crafters.CraftingStation";
    }

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
    public boolean canExtractItem (int i, ItemStack itemstack, int j)
    {
        return true;
    }
}

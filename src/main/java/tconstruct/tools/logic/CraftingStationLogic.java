package tconstruct.tools.logic;

import mantle.blocks.abstracts.InventoryLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import tconstruct.tools.inventory.CraftingStationContainer;
import tconstruct.util.config.PHConstruct;

import java.lang.ref.WeakReference;

public class CraftingStationLogic extends InventoryLogic implements ISidedInventory {
    public ForgeDirection chestDirection = ForgeDirection.UNKNOWN;
    public int chestSize;
    public WeakReference<IInventory> chest;
    public WeakReference<IInventory> doubleChest;
    public WeakReference<IInventory> patternChest;
    public WeakReference<IInventory> furnace;
    public boolean tinkerTable;
    public boolean stencilTable;
    public boolean doubleFirst;

    public int invRows, invColumns, slotCount;

    public CraftingStationLogic() {
        super(10); // 9 for crafting, 1 for output
    }

    @Override
    public boolean canDropInventorySlot(int slot) {
        return slot != 0;
    }

    @Override
    public ItemStack decrStackSize(int slot, int quantity) {
        if (slot == 0) {
            for (int i = 1; i < getSizeInventory(); i++)
                decrStackSize(i, 1);
        }
        return super.decrStackSize(slot, quantity);
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return isUseableByPlayer(player, this.getInventories()) && super.isUseableByPlayer(player);
    }

    @Override
    public Container getGuiContainer(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        chest = null;
        chestSize = 0;
        slotCount = 0;
        chestDirection = ForgeDirection.UNKNOWN;
        doubleChest = null;
        patternChest = null;
        furnace = null;
        tinkerTable = false;

        for (final ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            final int xPos = x + dir.offsetX, yPos = y + dir.offsetY, zPos = z + dir.offsetZ;
            final TileEntity tile = world.getTileEntity(xPos, yPos, zPos);
            if (!(tile instanceof IInventory) || (tile instanceof CraftingStationLogic) || isBlacklisted(tile.getClass())) continue;

            final IInventory inv = (IInventory) tile;

            if (patternChest == null && tile instanceof PatternChestLogic) {
                patternChest = new WeakReference<>(inv);
                continue;
            } else if (furnace == null && (tile instanceof TileEntityFurnace || tile instanceof FurnaceLogic)) {
                furnace = new WeakReference<>(inv);
                continue;
            } else if (!tinkerTable && tile instanceof ToolStationLogic) {
                tinkerTable = true;
                continue;
            }

            if(tile instanceof ISidedInventory && ((ISidedInventory)tile).getAccessibleSlotsFromSide(dir.getOpposite().ordinal()).length == 0) continue;
            
            if (chest == null && inv.isUseableByPlayer(inventoryplayer.player)) {
                chest = new WeakReference<>(inv);
                chestDirection = dir;
                invColumns = 6;
                chestSize = tile instanceof ISidedInventory ? ((ISidedInventory)tile).getAccessibleSlotsFromSide(dir.getOpposite().ordinal()).length : inv.getSizeInventory();

                if (tile instanceof TileEntityChest) {
                    checkForChest(world, xPos, yPos, zPos, 1, 0);
                    checkForChest(world, xPos, yPos, zPos, -1, 0);
                    checkForChest(world, xPos, yPos, zPos, 0, 1);
                    checkForChest(world, xPos, yPos, zPos, 0, -1);
                }
                slotCount = chestSize * (doubleChest != null ? 2 : 1);
                invRows = (int) Math.ceil((double) slotCount / invColumns);
            } 

        }

        return new CraftingStationContainer(inventoryplayer, this, x, y, z);
    }

    private boolean isBlacklisted(Class<? extends TileEntity> clazz) {
        return PHConstruct.craftingStationBlacklist.contains(clazz.getName());
    }
    
    void checkForChest(World world, int x, int y, int z, int dx, int dz) {
        TileEntity tile = world.getTileEntity(x + dx, y, z + dz);
        if (tile instanceof TileEntityChest) {
            doubleChest = new WeakReference<>((IInventory) tile);
            doubleFirst = dx + dz < 0;
        }
    }
    
    public boolean isDoubleChest() {
        return this.doubleChest != null;
    }
    
    public IInventory getFirstInventory() {
        if (doubleFirst && doubleChest != null) {
            return doubleChest.get();
        } else {
            return chest.get();
        }
    }

    public IInventory getSecondInventory() {
        if(!isDoubleChest()) return null;

        if (doubleFirst) {
            return chest.get();
        } else {
            return doubleChest == null ? null : doubleChest.get();
        }
    }

    @Override
    protected String getDefaultName() {
        return "crafters.CraftingStation";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return true;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static boolean isUseableByPlayer(EntityPlayer player, WeakReference[] inventories) {
        for (WeakReference<IInventory> ref : inventories) {
            if (ref != null) {
                IInventory inv = ref.get();
                if (inv != null && !inv.isUseableByPlayer(player))
                    return false;
            }
        }

        return true;
    }

    @SuppressWarnings("rawtypes")
    public WeakReference[] getInventories() {
        return new WeakReference[]{this.chest, this.doubleChest, this.patternChest, this.furnace};
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int var1) {
        return new int[]{};
    }

    @Override
    public boolean canInsertItem(int i, ItemStack itemstack, int j) {
        return false;
    }

    @Override
    public boolean canExtractItem(int i, ItemStack itemstack, int j) {
        return false;
    }

    @Override
    public String getInventoryName() {
        return getDefaultName();
    }

    @Override
    public void openInventory() {
        // TODO Auto-generated method stub

    }

    @Override
    public void closeInventory() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean canUpdate() {
        return false;
    }
}

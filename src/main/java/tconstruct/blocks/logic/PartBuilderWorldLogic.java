package tconstruct.blocks.logic;

import tconstruct.inventory.PartCrafterChestContainer;
import tconstruct.inventory.PartCrafterContainer;
import tconstruct.library.blocks.InventoryLogic;
import tconstruct.library.crafting.PatternBuilder;
import tconstruct.library.modifier.ItemModifier;
import tconstruct.library.util.IPattern;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public class PartBuilderWorldLogic extends InventoryLogic implements ISidedInventory
{
    int buildAmount = 0;

    public PartBuilderWorldLogic()
    {
        super(4);
        /** Slots:
         *  0 - Pattern
         *  1 - Internal inventory
         *  2 - Output
         *  3 - Output leftovers
         */
    }

    @Override
    public void onInventoryChanged()
    {
        super.onInventoryChanged();
        if (inventory[2] == null)
            craftPart(inventory[1]);
    }

    public boolean buildItemPart (ItemStack equip)
    {
        if (equip == null || inventory[2] != null)
            return false;

        ItemStack check = equip.copy();
        check.stackSize = 1;
        if (inventory[1] == null)
        {
            PatternBuilder pb = PatternBuilder.instance;
            if (pb.validItemPart(check, inventory[0]))
            {
                if (!craftPart(check))
                    this.setInventorySlotContents(1, check);
                return true;
            }
        }
        else if (ItemModifier.areItemStacksEquivalent(check, inventory[1]))
        {
            inventory[1].stackSize++;
            craftPart(inventory[1]);
            return true;
        }
        return false;
    }

    boolean craftPart (ItemStack craft)
    {
        ItemStack[] result = PatternBuilder.instance.getToolPart(craft, inventory[0]);
        if (result[0] != null)
        {
            this.setInventorySlotContents(1, null);
            this.setInventorySlotContents(2, result[0]);
            this.setInventorySlotContents(3, result[1]);

            return true;
        }
        return false;
    }

    @Override
    public String getDefaultName ()
    {
        return "toolstation.parts";
    }

    @Override
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        return null; //Nope!
    }

    @Override
    public int[] getAccessibleSlotsFromSide (int side)
    {
        return new int[] { 1, 2, 3 };
    }

    @Override
    public boolean canInsertItem (int slot, ItemStack itemstack, int side)
    {
        if (inventory[2] != null || inventory[3] != null)
            return false;
        if (slot == 1 && inventory[1] == null)
            return PatternBuilder.instance.validItemPart(itemstack, inventory[0]);
        return false;
    }

    @Override
    public boolean canExtractItem (int slot, ItemStack itemstack, int side)
    {
        if (slot >= 2)
            return true;
        return false;
    }

    /* Packets */
    @Override
    public Packet getDescriptionPacket ()
    {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket (INetworkManager net, Packet132TileEntityData packet)
    {
        readFromNBT(packet.data);
        worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
    }
}

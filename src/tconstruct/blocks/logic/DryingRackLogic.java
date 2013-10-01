package tconstruct.blocks.logic;

import tconstruct.library.blocks.InventoryLogic;
import tconstruct.library.crafting.DryingRackRecipes;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DryingRackLogic extends InventoryLogic
{
    int currentTime;
    int maxTime;

    public DryingRackLogic()
    {
        super(1);
    }

    @Override
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        return null;
    }

    @Override
    protected String getDefaultName ()
    {
        return "";
    }

    @Override
    public void updateEntity ()
    {
        if (!worldObj.isRemote && maxTime > 0 && currentTime < maxTime)
        {
            currentTime++;
            if (currentTime >= maxTime)
            {
                inventory[0] = DryingRackRecipes.getDryingResult(inventory[0]);
                updateDryingTime();
            }
        }
    }

    @Override
    public void setInventorySlotContents (int slot, ItemStack itemstack)
    {
        super.setInventorySlotContents(slot, itemstack);
        updateDryingTime();
    }

    @Override
    public ItemStack decrStackSize (int slot, int quantity)
    {
        ItemStack stack = super.decrStackSize(slot, quantity);
        maxTime = 0;
        currentTime = 0;
        return stack;
    }

    public void updateDryingTime ()
    {
        currentTime = 0;
        if (inventory[0] != null)
            maxTime = DryingRackRecipes.getDryingTime(inventory[0]);
        else
            maxTime = 0;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void readFromNBT (NBTTagCompound tags)
    {
        currentTime = tags.getInteger("Time");
        maxTime = tags.getInteger("MaxTime");
        readCustomNBT(tags);
    }

    @Override
    public void writeToNBT (NBTTagCompound tags)
    {
        tags.setInteger("Time", currentTime);
        tags.setInteger("MaxTime", maxTime);
        writeCustomNBT(tags);
    }

    public void readCustomNBT (NBTTagCompound tags)
    {
        super.readFromNBT(tags);
    }

    public void writeCustomNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);
    }

    /* Packets */
    @Override
    public Packet getDescriptionPacket ()
    {
        NBTTagCompound tag = new NBTTagCompound();
        writeCustomNBT(tag);
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket (INetworkManager net, Packet132TileEntityData packet)
    {
        readCustomNBT(packet.data);
        worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox ()
    {
        AxisAlignedBB cbb = AxisAlignedBB.getAABBPool().getAABB(xCoord, yCoord - 1, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
        return cbb;
    }
}

package tconstruct.blocks.logic;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.TConstruct;
import tconstruct.blocks.component.TankAirComponent;
import mantle.blocks.abstracts.InventoryLogic;
import mantle.world.CoordTuple;
import mantle.blocks.iface.IMasterLogic;
import mantle.blocks.iface.IServantLogic;

public class TankAirLogic extends InventoryLogic implements IServantLogic, ISidedInventory
{
    TankAirComponent multitank = new TankAirComponent(TConstruct.ingotLiquidValue * 18);
    CoordTuple master;

    public TankAirLogic()
    {
        super(1);
    }

    public void overrideFluids (ArrayList<FluidStack> fluids)
    {
        /*multitank.overrideFluids(fluids);
        field_145850_b.markBlockForUpdate(xCoord, yCoord, zCoord);*/
    }

    public boolean hasItem ()
    {
        return inventory[0] != null;
    }

    public boolean hasFluids ()
    {
        return multitank.fluidlist.size() > 0;
    }

    public List<FluidStack> getFluids ()
    {
        return multitank.fluidlist;
    }

    @Override
    public void setInventorySlotContents (int slot, ItemStack itemstack)
    {
        /*inventory[slot] = itemstack;
        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit())
        {
            itemstack.stackSize = getInventoryStackLimit();
            field_145850_b.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, itemstack.getItemDamage(), 3);
            field_145850_b.markBlockForUpdate(xCoord, yCoord, zCoord);
        }*/
    }

    @Override
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        return null; //Not a gui block
    }

    @Override
    public String getDefaultName ()
    {
        return null; //Not a gui block
    }

    @Override
    public CoordTuple getMasterPosition ()
    {
        return master;
    }

    @Override
    public void notifyMasterOfChange ()
    {
        //Probably not useful here
    }

    @Override
    public boolean setPotentialMaster (IMasterLogic master, World world, int xMaster, int yMaster, int zMaster)
    {
        return false; //Master should be verified right after placement
    }

    @Override
    public boolean verifyMaster (IMasterLogic logic, World world, int xMaster, int yMaster, int zMaster)
    {
        /*if (master != null) //Is this even needed?
            return false;*/

        master = new CoordTuple(xMaster, yMaster, zMaster);
        return true;
    }

    @Override
    public void invalidateMaster (IMasterLogic master, World world, int xMaster, int yMaster, int zMaster)
    {
        world.setBlockToAir(xCoord, yCoord, zCoord);
    }

    @Override
    public int[] getAccessibleSlotsFromSide (int var1)
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

    /*@Override
    public boolean canUpdate()
    {
        return false;
    }*/

    //DELETE
    public void updateEntity ()
    {
        field_145850_b.setBlockToAir(xCoord, yCoord, zCoord);
    }

    //Keep TE regardless of metadata
    public boolean shouldRefresh (int oldID, int newID, int oldMeta, int newMeta, World world, int x, int y, int z)
    {
        return oldID != newID;
    }

    /* NBT */

    @Override
    public void readFromNBT (NBTTagCompound tags)
    {
        super.readFromNBT(tags);
        readNetworkNBT(tags);
        multitank.readFromNBT(tags);
    }

    public void readNetworkNBT (NBTTagCompound tags)
    {
        multitank.readNetworkNBT(tags);
        super.readInventoryFromNBT(tags);
    }

    @Override
    public void writeToNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);
        writeNetworkNBT(tags);
        multitank.writeToNBT(tags);
    }

    public void writeNetworkNBT (NBTTagCompound tags)
    {
        multitank.writeNetworkNBT(tags);
        super.writeInventoryToNBT(tags);
    }

    @Override
    public void onDataPacket (INetworkManager net, Packet132TileEntityData packet)
    {
        readNetworkNBT(packet.data);
        field_145850_b.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
        field_145850_b.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public Packet getDescriptionPacket ()
    {
        NBTTagCompound tag = new NBTTagCompound();
        writeNetworkNBT(tag);
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag);
    }
}

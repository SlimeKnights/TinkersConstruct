package tconstruct.smeltery.logic;

import java.util.*;
import mantle.blocks.abstracts.InventoryLogic;
import mantle.blocks.iface.*;
import mantle.world.CoordTuple;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.*;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.TConstruct;
import tconstruct.smeltery.component.TankAirComponent;

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
        /*
         * multitank.overrideFluids(fluids); worldObj.markBlockForUpdate(xCoord,
         * yCoord, zCoord);
         */
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
        /*
         * inventory[slot] = itemstack; if (itemstack != null &&
         * itemstack.stackSize > getInventoryStackLimit()) { itemstack.stackSize
         * = getInventoryStackLimit();
         * worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord,
         * itemstack.getItemDamage(), 3); worldObj.markBlockForUpdate(xCoord,
         * yCoord, zCoord); }
         */
    }

    @Override
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        return null; // Not a gui block
    }

    @Override
    protected String getDefaultName ()
    {
        return null; // Not a gui block
    }

    @Override
    public CoordTuple getMasterPosition ()
    {
        return master;
    }

    @Override
    public void notifyMasterOfChange ()
    {
        // Probably not useful here
    }

    @Override
    public boolean setPotentialMaster (IMasterLogic master, World world, int xMaster, int yMaster, int zMaster)
    {
        return false; // Master should be verified right after placement
    }

    @Override
    public boolean verifyMaster (IMasterLogic logic, World world, int xMaster, int yMaster, int zMaster)
    {
        /*
         * if (master != null) //Is this even needed? return false;
         */

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

    /*
     * @Override public boolean canUpdate() { return false; }
     */

    // DELETE
    @Override
    public void updateEntity ()
    {
        worldObj.setBlockToAir(xCoord, yCoord, zCoord);
    }

    // Keep TE regardless of metadata
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
    public void onDataPacket (NetworkManager net, S35PacketUpdateTileEntity packet)
    {
        worldObj.func_147479_m(xCoord, yCoord, zCoord);
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        readNetworkNBT(packet.func_148857_g());
    }

    @Override
    public Packet getDescriptionPacket ()
    {
        NBTTagCompound tag = new NBTTagCompound();
        writeNetworkNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public String getInventoryName ()
    {
        return this.getInventoryName();
    }

    @Override
    public boolean hasCustomInventoryName ()
    {
        return false;
    }

    @Override
    public void closeInventory ()
    {
    }

    @Override
    public void openInventory ()
    {
    }

}

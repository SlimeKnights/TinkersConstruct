package tconstruct.blocks.logic;

import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import tconstruct.TConstruct;
import tconstruct.library.util.IActiveLogic;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * @author BluSunrize
 */

public class CastingChannelLogic extends TileEntity implements IFluidTank, IFluidHandler, IActiveLogic
{
    public final static int fillMax = TConstruct.ingotLiquidValue*5;
    public final static int outputMax = TConstruct.ingotLiquidValue;
    public FluidStack liquid;
    public ForgeDirection lastProvider;
    private int ticks = 0;
    private int ticksLPReset = 0;
    public int recentlyFilledDelay;
    boolean pullingLiquids;

    public CastingChannelLogic()
    {
        recentlyFilledDelay = 0;
        lastProvider = ForgeDirection.UNKNOWN;
    }

    @Override
    public void updateEntity ()
    {
        if (this.worldObj.isRemote)
            return;
        ticks++;
        ticksLPReset++;
        if (!worldObj.isRemote)
        {
            if (this.pullingLiquids)
                pullLiquids();
        }
        //this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        if (ticks == 20)
        {
            if (recentlyFilledDelay != 0)
                recentlyFilledDelay--;
            if (recentlyFilledDelay == 0)
                moveLiquidToTanks();
            ticks = 0;
        }
        if (ticksLPReset == 60)
        {
            this.lastProvider = ForgeDirection.UNKNOWN;
            ticksLPReset = 0;
        }
    }

    void pullLiquids ()
    {
        if (pullLiquid(xCoord + 1, yCoord, zCoord, ForgeDirection.EAST) || pullLiquid(xCoord - 1, yCoord, zCoord, ForgeDirection.WEST) || pullLiquid(xCoord, yCoord, zCoord + 1, ForgeDirection.NORTH)
                || pullLiquid(xCoord, yCoord, zCoord - 1, ForgeDirection.SOUTH))
        {

        }
        else
        {
            pullingLiquids = false;
        }
    }

    boolean pullLiquid (int x, int y, int z, ForgeDirection direction)
    {
        TileEntity tank = worldObj.getBlockTileEntity(x, y, z);
        if (tank instanceof IFluidHandler && !(tank instanceof CastingChannelLogic))
        {
            FluidStack templiquid = ((IFluidHandler) tank).drain(direction, 3, false);
            if (templiquid != null)
            {
                if (this.liquid == null)
                {
                    if (templiquid.amount > fillMax)
                        templiquid.amount = fillMax;
                    this.liquid = ((IFluidHandler) tank).drain(direction, templiquid.amount, true);
                    return true;
                }
                else if (this.liquid.isFluidEqual(templiquid))
                {
                    if (templiquid.amount + this.liquid.amount > fillMax)
                        templiquid.amount = fillMax - this.liquid.amount;
                    liquid.amount += ((IFluidHandler) tank).drain(direction, templiquid.amount, true).amount;
                    return true;
                }
            }
        }
        return false;
    }

    private void moveLiquidToChannels ()
    {
        if (this.liquid == null || this.liquid.amount <= 0)
        {
            return;
        }
        TileEntity tankXplus = null;
        TileEntity tankXminus = null;
        TileEntity tankZplus = null;
        TileEntity tankZminus = null;
        TileEntity tankYminus = null;
        HashMap tankMap = this.getOutputs();
        if (tankMap.size() <= 0)
        {
            //TConstruct.logger.info("Nope, no connections to go to");
            return;
        }
        boolean yFilled = false;
        if (tankMap.containsKey("yMinus"))
        {
            tankYminus = (TileEntity) tankMap.get("yMinus");
            FluidStack liquidToPush = this.drain(24, false);
            if ((liquidToPush != null) && (liquidToPush.amount > 0))
            {
                int filled = ((IFluidHandler) tankYminus).fill(ForgeDirection.UP, liquidToPush, true);
                if (filled > 0)
                {
                    yFilled = true;
                    this.drain(filled, true);
                }
            }
        }

        if (!yFilled)
        {
            int divisionSurplus = (this.liquid.amount % tankMap.size());
            int amountXp = (this.outputMax / tankMap.size());
            int amountXm = (this.outputMax / tankMap.size());
            int amountZp = (this.outputMax / tankMap.size());
            int amountZm = (this.outputMax / tankMap.size());
            if (divisionSurplus > 0)
                amountXp++;
            if (divisionSurplus > 1)
                amountXm++;
            if (divisionSurplus > 2)
                amountZp++;

            if (tankMap.containsKey("xPlus"))
            {
                tankXplus = (TileEntity) tankMap.get("xPlus");
                FluidStack liquidToPush = this.drain(amountXp, false);
                if ((liquidToPush != null) && (liquidToPush.amount > 0))
                {
                    int filled = ((IFluidHandler) tankXplus).fill(ForgeDirection.WEST, liquidToPush, true);
                    this.drain(filled, true);
                }
            }
            if (tankMap.containsKey("xMinus"))
            {
                tankXminus = (TileEntity) tankMap.get("xMinus");
                FluidStack liquidToPush = this.drain(amountXm, false);
                if ((liquidToPush != null) && (liquidToPush.amount > 0))
                {
                    int filled = ((IFluidHandler) tankXminus).fill(ForgeDirection.EAST, liquidToPush, true);
                    this.drain(filled, true);
                }
            }
            if (tankMap.containsKey("zPlus"))
            {
                tankZplus = (TileEntity) tankMap.get("zPlus");
                FluidStack liquidToPush = this.drain(amountZp, false);
                if ((liquidToPush != null) && (liquidToPush.amount > 0))
                {
                    int filled = ((IFluidHandler) tankZplus).fill(ForgeDirection.NORTH, liquidToPush, true);
                    this.drain(filled, true);
                }
            }
            if (tankMap.containsKey("zMinus"))
            {
                tankZminus = (TileEntity) tankMap.get("zMinus");
                FluidStack liquidToPush = this.drain(amountZm, false);
                if ((liquidToPush != null) && (liquidToPush.amount > 0))
                {
                    int filled = ((IFluidHandler) tankZminus).fill(ForgeDirection.SOUTH, liquidToPush, true);
                    this.drain(filled, true);
                }
            }
        }
    }

    private void moveLiquidToTanks ()
    {
        if (this.liquid == null || this.liquid.amount <= 0)
        {
            return;
        }
        TileEntity tankXplus = null;
        TileEntity tankXminus = null;
        TileEntity tankZplus = null;
        TileEntity tankZminus = null;
        TileEntity tankYminus = null;
        HashMap tankMap = this.getOutputs();
        if (tankMap.size() <= 0)
        {
            //TConstruct.logger.info("Nope, no connections to go to");
            return;
        }
        boolean yFilled = false;
        if (tankMap.containsKey("yMinus"))
        {
            tankYminus = (TileEntity) tankMap.get("yMinus");
            FluidStack liquidToPush = this.drain(24, false);
            if ((liquidToPush != null) && (liquidToPush.amount > 0))
            {
                int filled = ((IFluidHandler) tankYminus).fill(ForgeDirection.UP, liquidToPush, true);
                if (filled > 0)
                {
                    yFilled = true;
                    this.drain(filled, true);
                }
            }
        }

        if (!yFilled)
        {
            int divisionSurplus = (this.liquid.amount % tankMap.size());
            int amountXp = (this.outputMax / tankMap.size());
            int amountXm = (this.outputMax / tankMap.size());
            int amountZp = (this.outputMax / tankMap.size());
            int amountZm = (this.outputMax / tankMap.size());
            if (divisionSurplus > 0)
                amountXp++;
            if (divisionSurplus > 1)
                amountXm++;
            if (divisionSurplus > 2)
                amountZp++;

            if (tankMap.containsKey("xPlus"))
            {
                tankXplus = (TileEntity) tankMap.get("xPlus");
                FluidStack liquidToPush = this.drain(amountXp, false);
                if ((liquidToPush != null) && (liquidToPush.amount > 0))
                {
                    int filled = ((IFluidHandler) tankXplus).fill(ForgeDirection.WEST, liquidToPush, true);
                    this.drain(filled, true);
                }
            }
            if (tankMap.containsKey("xMinus"))
            {
                tankXminus = (TileEntity) tankMap.get("xMinus");
                FluidStack liquidToPush = this.drain(amountXm, false);
                if ((liquidToPush != null) && (liquidToPush.amount > 0))
                {
                    int filled = ((IFluidHandler) tankXminus).fill(ForgeDirection.EAST, liquidToPush, true);
                    this.drain(filled, true);
                }
            }
            if (tankMap.containsKey("zPlus"))
            {
                tankZplus = (TileEntity) tankMap.get("zPlus");
                FluidStack liquidToPush = this.drain(amountZp, false);
                if ((liquidToPush != null) && (liquidToPush.amount > 0))
                {
                    int filled = ((IFluidHandler) tankZplus).fill(ForgeDirection.NORTH, liquidToPush, true);
                    this.drain(filled, true);
                }
            }
            if (tankMap.containsKey("zMinus"))
            {
                tankZminus = (TileEntity) tankMap.get("zMinus");
                FluidStack liquidToPush = this.drain(amountZm, false);
                if ((liquidToPush != null) && (liquidToPush.amount > 0))
                {
                    int filled = ((IFluidHandler) tankZminus).fill(ForgeDirection.SOUTH, liquidToPush, true);
                    this.drain(filled, true);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public float tankBelow ()
    {
        TileEntity te = this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord - 1, this.zCoord);
        if (te instanceof CastingChannelLogic)
            return -0.5f;
        if (te instanceof LavaTankLogic)
            return -1.0f;
        if (te instanceof CastingBasinLogic)
            return -0.75f;
        if (te instanceof CastingTableLogic)
            return -0.125f;
        if (te instanceof IFluidHandler)
            return 0f;

        return 0.5f;
    }

    public boolean hasChannelConnected (ForgeDirection dir)
    {
        switch (dir)
        {
        case DOWN:
            return (this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord - 1, this.zCoord) instanceof CastingChannelLogic);
        case NORTH:
            return (this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord - 1) instanceof CastingChannelLogic);
        case SOUTH:
            return (this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord + 1) instanceof CastingChannelLogic);
        case WEST:
            return (this.worldObj.getBlockTileEntity(this.xCoord - 1, this.yCoord, this.zCoord) instanceof CastingChannelLogic);
        case EAST:
            return (this.worldObj.getBlockTileEntity(this.xCoord + 1, this.yCoord, this.zCoord) instanceof CastingChannelLogic);
        default:
            return false;
        }
    }

    public boolean hasTankConnected (ForgeDirection dir)
    {
        switch (dir)
        {
        case DOWN:
            return (this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord - 1, this.zCoord) instanceof IFluidHandler);
        case NORTH:
            return (this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord - 1) instanceof IFluidHandler);
        case SOUTH:
            return (this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord + 1) instanceof IFluidHandler);
        case WEST:
            return (this.worldObj.getBlockTileEntity(this.xCoord - 1, this.yCoord, this.zCoord) instanceof IFluidHandler);
        case EAST:
            return (this.worldObj.getBlockTileEntity(this.xCoord + 1, this.yCoord, this.zCoord) instanceof IFluidHandler);
        default:
            return false;
        }
    }

    private HashMap getOutputs ()
    {
        HashMap map = new HashMap();
        TileEntity tankXplus = this.worldObj.getBlockTileEntity(this.xCoord + 1, this.yCoord, this.zCoord);
        TileEntity tankXminus = this.worldObj.getBlockTileEntity(this.xCoord - 1, this.yCoord, this.zCoord);
        TileEntity tankZplus = this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord + 1);
        TileEntity tankZminus = this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord - 1);
        TileEntity tankYminus = this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord - 1, this.zCoord);

        if (this.pullingLiquids)
        {
            if (this.hasChannelConnected(ForgeDirection.EAST) && (this.lastProvider != ForgeDirection.EAST))
            {
                map.put("xPlus", tankXplus);
            }
            if (this.hasChannelConnected(ForgeDirection.WEST) && (this.lastProvider != ForgeDirection.WEST))
            {
                map.put("xMinus", tankXminus);
            }
            if (this.hasChannelConnected(ForgeDirection.SOUTH) && (this.lastProvider != ForgeDirection.SOUTH))
            {
                map.put("zPlus", tankZplus);
            }
            if (this.hasChannelConnected(ForgeDirection.NORTH) && (this.lastProvider != ForgeDirection.NORTH))
            {
                map.put("zMinus", tankZminus);
            }
            if (this.hasChannelConnected(ForgeDirection.DOWN) && (this.lastProvider != ForgeDirection.DOWN))
            {
                map.put("yMinus", tankYminus);
            }
        }
        else
        {
            if (this.hasTankConnected(ForgeDirection.EAST) && (this.lastProvider != ForgeDirection.EAST))
            {
                map.put("xPlus", tankXplus);
            }
            if (this.hasTankConnected(ForgeDirection.WEST) && (this.lastProvider != ForgeDirection.WEST))
            {
                map.put("xMinus", tankXminus);
            }
            if (this.hasTankConnected(ForgeDirection.SOUTH) && (this.lastProvider != ForgeDirection.SOUTH))
            {
                map.put("zPlus", tankZplus);
            }
            if (this.hasTankConnected(ForgeDirection.NORTH) && (this.lastProvider != ForgeDirection.NORTH))
            {
                map.put("zMinus", tankZminus);
            }
            if (this.hasTankConnected(ForgeDirection.DOWN) && (this.lastProvider != ForgeDirection.DOWN))
            {
                map.put("yMinus", tankYminus);
            }
        }

        return map;
    }

    public int fill (FluidStack stack, boolean doFill)
    {
        if (stack == null)
        {
            return 0;
        }
        if (this.liquid == null)
        {
            FluidStack transfered = stack.copy();

            if (transfered.amount > this.fillMax)
            {
                transfered.amount = this.fillMax;
            }

            if (doFill)
            {
                this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                this.liquid = transfered;
            }
            recentlyFilledDelay = 2;
            if (this.lastProvider == ForgeDirection.UP)
                recentlyFilledDelay = 3;
            return transfered.amount;
        }

        if (stack.isFluidEqual(this.liquid))
        {
            if (stack.amount + this.liquid.amount >= this.fillMax)
            {
                int spaceInTank = this.fillMax - this.liquid.amount;
                if ((doFill) && (spaceInTank > 0))
                {
                    this.liquid.amount = this.fillMax;
                    this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                }
                return spaceInTank;
            }

            if (doFill)
            {
                this.liquid.amount += stack.amount;
                this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            }
            return stack.amount;
        }

        return 0;
    }

    @Override
    public int fill (ForgeDirection from, FluidStack resource, boolean doFill)
    {
        this.lastProvider = from;
        ticksLPReset = 0;
        int filled = fill(resource, doFill);
        if (from == ForgeDirection.UP)
            moveLiquidToTanks();
        return filled;
    }

    public FluidStack drain (int maxDrain, boolean doDrain)
    {
        if ((this.liquid == null))
        {
            return null;
        }
        if (this.liquid.amount <= 0)
        {
            return null;
        }
        int toDrain = maxDrain;
        if (this.liquid.amount < toDrain)
        {
            toDrain = this.liquid.amount;
        }
        if (doDrain)
        {
            this.liquid.amount -= toDrain;
        }

        FluidStack drained = new FluidStack(liquid.fluidID, toDrain);

        if (this.liquid.amount <= 0)
        {
            this.liquid = null;
        }
        if (doDrain)
        {
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            FluidEvent.fireEvent(new FluidEvent.FluidDrainingEvent(drained, this.worldObj, this.xCoord, this.yCoord, this.zCoord, this));
        }
        return drained;
    }

    @Override
    public FluidStack drain (ForgeDirection from, int maxDrain, boolean doDrain)
    {
        return this.drain(maxDrain, doDrain);
    }

    @Override
    public FluidStack drain (ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        return this.drain(resource.amount, doDrain);
    }

    @Override
    public int getCapacity ()
    {
        return this.fillMax;
    }

    public int getLiquidAmount ()
    {
        return this.liquid.amount;
    }

    @Override
    public void readFromNBT (NBTTagCompound tags)
    {
        super.readFromNBT(tags);
        readCustomNBT(tags);
        pullingLiquids = tags.getBoolean("PullingLiquids");
    }

    public void readCustomNBT (NBTTagCompound tags)
    {
        if (tags.getBoolean("hasLiquid"))
        {
            this.liquid = FluidStack.loadFluidStackFromNBT(tags.getCompoundTag("Fluid"));
        }
        else
            this.liquid = null;
        this.recentlyFilledDelay = tags.getInteger("recentlyFilledDelay");
        this.lastProvider = this.convertIntToFD(tags.getInteger("LastProvider"));
    }

    @Override
    public void writeToNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);
        writeCustomNBT(tags);
        tags.setBoolean("PullingLiquids", pullingLiquids);
    }

    public void writeCustomNBT (NBTTagCompound tags)
    {
        tags.setBoolean("hasLiquid", liquid != null);
        if (liquid != null)
        {
            NBTTagCompound nbt = new NBTTagCompound();
            liquid.writeToNBT(nbt);
            tags.setCompoundTag("Fluid", nbt);
        }
        tags.setInteger("LastProvider", this.convertFDToInt(this.lastProvider));
        tags.setInteger("recentlyFilledDelay", this.recentlyFilledDelay);
    }

    @Override
    public Packet getDescriptionPacket ()
    {
        NBTTagCompound tag = new NBTTagCompound();
        writeCustomNBT(tag);
        return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 1, tag);
    }

    @Override
    public void onDataPacket (INetworkManager net, Packet132TileEntityData packet)
    {
        readCustomNBT(packet.data);
        this.worldObj.markBlockForRenderUpdate(this.xCoord, this.yCoord, this.zCoord);
    }

    public int convertFDToInt (ForgeDirection dir)
    {
        switch (dir)
        {
        case DOWN:
            return 0;
        case UP:
            return 1;
        case NORTH:
            return 2;
        case SOUTH:
            return 3;
        case WEST:
            return 4;
        case EAST:
            return 5;
        default:
            return -1;
        }
    }

    public ForgeDirection convertIntToFD (int i)
    {
        switch (i)
        {
        case 0:
            return ForgeDirection.DOWN;
        case 1:
            return ForgeDirection.UP;
        case 2:
            return ForgeDirection.NORTH;
        case 3:
            return ForgeDirection.SOUTH;
        case 4:
            return ForgeDirection.WEST;
        case 5:
            return ForgeDirection.EAST;
        default:
            return ForgeDirection.UNKNOWN;
        }
    }

    @Override
    public boolean getActive ()
    {
        return pullingLiquids;
    }

    @Override
    public void setActive (boolean flag)
    {
        //pullingLiquids = flag;
        pullingLiquids = !pullingLiquids;
    }

    @Override
    public boolean canFill (ForgeDirection from, Fluid fluid)
    {
        return liquid == null || (liquid.amount < fillMax);
    }

    @Override
    public boolean canDrain (ForgeDirection from, Fluid fluid)
    {
        return true;
    }

    @Override
    public FluidTankInfo[] getTankInfo (ForgeDirection from)
    {
        return new FluidTankInfo[] { getInfo() };
    }

    @Override
    public FluidStack getFluid ()
    {
        return liquid == null ? null : liquid.copy();
    }

    @Override
    public int getFluidAmount ()
    {
        return liquid == null ? 0 : liquid.amount;
    }

    @Override
    public FluidTankInfo getInfo ()
    {
        FluidTankInfo info = new FluidTankInfo(this);
        return info;
    }
}

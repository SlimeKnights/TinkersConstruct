package mods.tinker.tconstruct.blocks.logic;

import java.util.HashMap;

import mods.tinker.tconstruct.library.util.IActiveLogic;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidEvent;
import net.minecraftforge.liquids.LiquidStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * @author BluSunrize
 */

public class CastingChannelLogic extends TileEntity implements ILiquidTank, ITankContainer, IActiveLogic
{
    public final static int fillMax = 288;
    public final static int outputMax = 48;
    public LiquidStack liquid;
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
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
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
        if (tank instanceof ITankContainer && !(tank instanceof CastingChannelLogic))
        {
            LiquidStack templiquid = ((ITankContainer) tank).drain(direction, 3, false);
            if (templiquid != null)
            {
                if (this.liquid == null)
                {
                    if (templiquid.amount > fillMax)
                        templiquid.amount = fillMax;
                    this.liquid = ((ITankContainer) tank).drain(direction, templiquid.amount, true);
                    return true;
                }
                else if (this.liquid.isLiquidEqual(templiquid))
                {
                    if (templiquid.amount + this.liquid.amount > fillMax)
                        templiquid.amount = fillMax - this.liquid.amount;
                    liquid.amount += ((ITankContainer) tank).drain(direction, templiquid.amount, true).amount;
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
            //System.out.println("Nope, no connections to go to");
            return;
        }
        boolean yFilled = false;
        if (tankMap.containsKey("yMinus"))
        {
            tankYminus = (TileEntity) tankMap.get("yMinus");
            LiquidStack liquidToPush = this.drain(24, false);
            if ((liquidToPush != null) && (liquidToPush.amount > 0))
            {
                int filled = ((ITankContainer) tankYminus).fill(ForgeDirection.UP, liquidToPush, true);
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
                LiquidStack liquidToPush = this.drain(amountXp, false);
                if ((liquidToPush != null) && (liquidToPush.amount > 0))
                {
                    int filled = ((ITankContainer) tankXplus).fill(ForgeDirection.WEST, liquidToPush, true);
                    this.drain(filled, true);
                }
            }
            if (tankMap.containsKey("xMinus"))
            {
                tankXminus = (TileEntity) tankMap.get("xMinus");
                LiquidStack liquidToPush = this.drain(amountXm, false);
                if ((liquidToPush != null) && (liquidToPush.amount > 0))
                {
                    int filled = ((ITankContainer) tankXminus).fill(ForgeDirection.EAST, liquidToPush, true);
                    this.drain(filled, true);
                }
            }
            if (tankMap.containsKey("zPlus"))
            {
                tankZplus = (TileEntity) tankMap.get("zPlus");
                LiquidStack liquidToPush = this.drain(amountZp, false);
                if ((liquidToPush != null) && (liquidToPush.amount > 0))
                {
                    int filled = ((ITankContainer) tankZplus).fill(ForgeDirection.NORTH, liquidToPush, true);
                    this.drain(filled, true);
                }
            }
            if (tankMap.containsKey("zMinus"))
            {
                tankZminus = (TileEntity) tankMap.get("zMinus");
                LiquidStack liquidToPush = this.drain(amountZm, false);
                if ((liquidToPush != null) && (liquidToPush.amount > 0))
                {
                    int filled = ((ITankContainer) tankZminus).fill(ForgeDirection.SOUTH, liquidToPush, true);
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
            //System.out.println("Nope, no connections to go to");
            return;
        }
        boolean yFilled = false;
        if (tankMap.containsKey("yMinus"))
        {
            tankYminus = (TileEntity) tankMap.get("yMinus");
            LiquidStack liquidToPush = this.drain(24, false);
            if ((liquidToPush != null) && (liquidToPush.amount > 0))
            {
                int filled = ((ITankContainer) tankYminus).fill(ForgeDirection.UP, liquidToPush, true);
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
                LiquidStack liquidToPush = this.drain(amountXp, false);
                if ((liquidToPush != null) && (liquidToPush.amount > 0))
                {
                    int filled = ((ITankContainer) tankXplus).fill(ForgeDirection.WEST, liquidToPush, true);
                    this.drain(filled, true);
                }
            }
            if (tankMap.containsKey("xMinus"))
            {
                tankXminus = (TileEntity) tankMap.get("xMinus");
                LiquidStack liquidToPush = this.drain(amountXm, false);
                if ((liquidToPush != null) && (liquidToPush.amount > 0))
                {
                    int filled = ((ITankContainer) tankXminus).fill(ForgeDirection.EAST, liquidToPush, true);
                    this.drain(filled, true);
                }
            }
            if (tankMap.containsKey("zPlus"))
            {
                tankZplus = (TileEntity) tankMap.get("zPlus");
                LiquidStack liquidToPush = this.drain(amountZp, false);
                if ((liquidToPush != null) && (liquidToPush.amount > 0))
                {
                    int filled = ((ITankContainer) tankZplus).fill(ForgeDirection.NORTH, liquidToPush, true);
                    this.drain(filled, true);
                }
            }
            if (tankMap.containsKey("zMinus"))
            {
                tankZminus = (TileEntity) tankMap.get("zMinus");
                LiquidStack liquidToPush = this.drain(amountZm, false);
                if ((liquidToPush != null) && (liquidToPush.amount > 0))
                {
                    int filled = ((ITankContainer) tankZminus).fill(ForgeDirection.SOUTH, liquidToPush, true);
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
        if (te instanceof ITankContainer)
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
            return (this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord - 1, this.zCoord) instanceof ITankContainer);
        case NORTH:
            return (this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord - 1) instanceof ITankContainer);
        case SOUTH:
            return (this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord + 1) instanceof ITankContainer);
        case WEST:
            return (this.worldObj.getBlockTileEntity(this.xCoord - 1, this.yCoord, this.zCoord) instanceof ITankContainer);
        case EAST:
            return (this.worldObj.getBlockTileEntity(this.xCoord + 1, this.yCoord, this.zCoord) instanceof ITankContainer);
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

    public int fill (LiquidStack stack, boolean doFill)
    {
        if (stack == null)
        {
            return 0;
        }
        if (this.liquid == null)
        {
            LiquidStack transfered = stack.copy();

            if (transfered.amount > this.fillMax)
            {
                transfered.amount = this.fillMax;
            }

            if (doFill)
            {
                this.worldObj.markBlockForRenderUpdate(this.xCoord, this.yCoord, this.zCoord);
                this.liquid = transfered;
            }
            recentlyFilledDelay = 2;
            if (this.lastProvider == ForgeDirection.UP)
                recentlyFilledDelay = 3;
            return transfered.amount;
        }

        if (stack.isLiquidEqual(this.liquid))
        {
            if (stack.amount + this.liquid.amount >= this.fillMax)
            {
                int spaceInTank = this.fillMax - this.liquid.amount;
                if ((doFill) && (spaceInTank > 0))
                {
                    this.liquid.amount = this.fillMax;
                    this.worldObj.markBlockForRenderUpdate(this.xCoord, this.yCoord, this.zCoord);
                }
                return spaceInTank;
            }

            if (doFill)
            {
                this.liquid.amount += stack.amount;
                this.worldObj.markBlockForRenderUpdate(this.xCoord, this.yCoord, this.zCoord);
            }
            return stack.amount;
        }

        return 0;
    }

    @Override
    public int fill (ForgeDirection from, LiquidStack resource, boolean doFill)
    {
        this.lastProvider = from;
        ticksLPReset = 0;
        int filled = fill(resource, doFill);
        if (from == ForgeDirection.UP)
            moveLiquidToTanks();
        return filled;
    }

    @Override
    public int fill (int tankIndex, LiquidStack resource, boolean doFill)
    {
        return fill(ForgeDirection.UNKNOWN, resource, doFill);
    }

    public LiquidStack drain (int maxDrain, boolean doDrain)
    {
        if ((this.liquid == null) || (this.liquid.itemID <= 0))
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

        LiquidStack drained = new LiquidStack(this.liquid.itemID, toDrain, this.liquid.itemMeta);

        if (this.liquid.amount <= 0)
        {
            this.liquid = null;
        }
        if (doDrain)
        {
            LiquidEvent.fireEvent(new LiquidEvent.LiquidDrainingEvent(drained, this.worldObj, this.xCoord, this.yCoord, this.zCoord, this));
        }
        return drained;
    }

    @Override
    public LiquidStack drain (ForgeDirection from, int maxDrain, boolean doDrain)
    {
        this.drain(0, maxDrain, doDrain);
        return null;
    }

    @Override
    public LiquidStack drain (int tankIndex, int maxDrain, boolean doDrain)
    {
        this.drain(maxDrain, doDrain);
        return null;
    }

    @Override
    public ILiquidTank[] getTanks (ForgeDirection direction)
    {
        ILiquidTank[] tanks = { this };
        return tanks;
    }

    @Override
    public ILiquidTank getTank (ForgeDirection direction, LiquidStack type)
    {
        return this;
    }

    @Override
    public LiquidStack getLiquid ()
    {
        return this.liquid;
    }

    @Override
    public int getCapacity ()
    {
        return this.fillMax;
    }

    @Override
    public int getTankPressure ()
    {
        return 0;
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
            this.liquid = new LiquidStack(tags.getInteger("itemID"), tags.getInteger("amount"), tags.getInteger("itemMeta"));
        else
        {
            this.liquid = null;
        }
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
        tags.setBoolean("hasLiquid", this.liquid != null);
        if (this.liquid != null)
        {
            tags.setInteger("itemID", this.liquid.itemID);
            tags.setInteger("amount", this.liquid.amount);
            tags.setInteger("itemMeta", this.liquid.itemMeta);
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
        readCustomNBT(packet.customParam1);
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
        pullingLiquids = flag;
    }
}

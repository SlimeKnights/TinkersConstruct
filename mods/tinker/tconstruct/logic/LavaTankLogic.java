package mods.tinker.tconstruct.logic;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;

public class LavaTankLogic extends MultiServantLogic
	implements ITankContainer
{
	public LiquidTank tank;
	
	public LavaTankLogic()
	{
		tank = new LiquidTank(LiquidContainerRegistry.BUCKET_VOLUME*4);
	}

	@Override
	public int fill (ForgeDirection from, LiquidStack resource, boolean doFill)
	{
		return fill(0, resource, doFill);
	}

	@Override
	public int fill (int tankIndex, LiquidStack resource, boolean doFill)
	{
		int amount = tank.fill(resource, doFill);
		if (amount > 0 && doFill)
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		return amount;
	}

	@Override
	public LiquidStack drain (ForgeDirection from, int maxDrain, boolean doDrain)
	{
		return drain(0, maxDrain, doDrain);
	}

	@Override
	public LiquidStack drain (int tankIndex, int maxDrain, boolean doDrain)
	{
		LiquidStack amount = tank.drain(maxDrain, doDrain);
		if (amount != null && doDrain)
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		return amount;
	}

	@Override
	public ILiquidTank[] getTanks (ForgeDirection direction)
	{
		return new ILiquidTank[] { tank };
	}

	@Override
	public ILiquidTank getTank (ForgeDirection direction, LiquidStack type)
	{
		return tank;
	}
	
	public float getLiquidAmountScaled()
	{
		return (float) (tank.getLiquid().amount) / (float) (tank.getCapacity() * 1.01F);
	}
	
	public boolean containsLiquid()
	{
		return tank.getLiquid() != null;
	}
	
	public void readFromNBT(NBTTagCompound tags)
    {
		if (tags.getBoolean("hasLiquid"))
			tank.setLiquid(new LiquidStack(tags.getInteger("itemID"), tags.getInteger("amount"), tags.getInteger("itemMeta")));
		super.readFromNBT(tags);
    }
	
	public void writeToNBT(NBTTagCompound tags)
    {
		LiquidStack liquid = tank.getLiquid();
		tags.setBoolean("hasLiquid", liquid != null);
		if (liquid != null)
		{
			tags.setInteger("itemID", liquid.itemID);
			tags.setInteger("amount", liquid.amount);
			tags.setInteger("itemMeta", liquid.itemMeta);
		}
		super.writeToNBT(tags);
    }
	
	public Packet getDescriptionPacket ()
	{
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag);
	}

	public void onDataPacket (INetworkManager net, Packet132TileEntityData packet)
	{
		worldObj.getBlockTileEntity(xCoord, yCoord, zCoord).readFromNBT(packet.customParam1);
		worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
	}
}

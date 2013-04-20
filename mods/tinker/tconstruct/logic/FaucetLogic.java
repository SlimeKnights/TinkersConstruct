package mods.tinker.tconstruct.logic;

import mods.tinker.common.IActiveLogic;
import mods.tinker.common.IFacingLogic;
import mods.tinker.tconstruct.TConstruct;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidStack;

public class FaucetLogic extends TileEntity implements IFacingLogic, IActiveLogic, ITankContainer
{
	byte direction;
	boolean active;
	public LiquidStack liquid;

	public boolean activateFaucet ()
	{
		if (liquid == null && active)
		{
			int x = xCoord, z = zCoord;
			switch (getRenderDirection())
			{
			case 2:
				z++;
				break;
			case 3:
				z--;
				break;
			case 4:
				x++;
				break;
			case 5:
				x--;
				break;
			}

			TileEntity drainte = worldObj.getBlockTileEntity(x, yCoord, z);
			TileEntity tankte = worldObj.getBlockTileEntity(xCoord, yCoord - 1, zCoord);

			if (drainte != null && drainte instanceof ITankContainer && tankte != null && tankte instanceof ITankContainer)
			{
				liquid = ((ITankContainer) drainte).drain(getForgeDirection(), TConstruct.ingotLiquidValue, true);
				if (liquid != null)
				{
					int drained = ((ITankContainer) tankte).fill(ForgeDirection.UP, liquid, true);
					if (drained != liquid.amount)
					{
						liquid.amount -= drained;
						((ITankContainer) drainte).fill(getForgeDirection(), liquid, true);
					}
					if (drained > 0)
					{
						worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
						return true;
					}
					else
					{
						liquid = null;
						worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
						return false;
					}
				}
				else
				{
					((ITankContainer) drainte).fill(getForgeDirection(), liquid, true);
				}
			}
		}
		return false;
	}

	@Override
	public void updateEntity ()
	{
		if (liquid != null)
		{
			liquid.amount -= TConstruct.liquidUpdateAmount;
			if (liquid.amount <= 0)
			{
				liquid = null;
				if (!activateFaucet())
				{
					active = false;
					worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				}
			}
		}
	}

	@Override
	public byte getRenderDirection ()
	{
		return direction;
	}

	@Override
	public ForgeDirection getForgeDirection ()
	{
		return ForgeDirection.VALID_DIRECTIONS[direction];
	}

	@Override
	public void setDirection (float yaw, float pitch, EntityLiving player)
	{
		int facing = MathHelper.floor_double((double) (yaw / 360) + 0.5D) & 3;
		switch (facing)
		{
		case 0:
			direction = 2;
			break;

		case 1:
			direction = 5;
			break;

		case 2:
			direction = 3;
			break;

		case 3:
			direction = 4;
			break;
		}
	}

	@Override
	public void readFromNBT (NBTTagCompound tags)
	{
		super.readFromNBT(tags);
		readCustomNBT(tags);
	}

	public void readCustomNBT (NBTTagCompound tags)
	{
		direction = tags.getByte("Direction");
		if (tags.getBoolean("hasLiquid"))
			this.liquid = new LiquidStack(tags.getInteger("itemID"), tags.getInteger("amount"), tags.getInteger("itemMeta"));
		else
			this.liquid = null;
	}

	@Override
	public void writeToNBT (NBTTagCompound tags)
	{
		super.writeToNBT(tags);
		writeCustomNBT(tags);
	}

	public void writeCustomNBT (NBTTagCompound tags)
	{
		tags.setByte("Direction", direction);
		tags.setBoolean("hasLiquid", liquid != null);
		if (liquid != null)
		{
			tags.setInteger("itemID", liquid.itemID);
			tags.setInteger("amount", liquid.amount);
			tags.setInteger("itemMeta", liquid.itemMeta);
		}
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
		readCustomNBT(packet.customParam1);
		worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public boolean getActive ()
	{
		return active;
	}

	@Override
	public void setActive (boolean flag)
	{
		if (!active)
		{
			active = true;
			activateFaucet();
		}
		else
		{
			active = false;
		}
	}

    @Override
    public int fill (ForgeDirection from, LiquidStack resource, boolean doFill)
    {
        return 0;
    }

    @Override
    public int fill (int tankIndex, LiquidStack resource, boolean doFill)
    {
        return 0;
    }

    @Override
    public LiquidStack drain (ForgeDirection from, int maxDrain, boolean doDrain)
    {
        return null;
    }

    @Override
    public LiquidStack drain (int tankIndex, int maxDrain, boolean doDrain)
    {
        return null;
    }

    @Override
    public ILiquidTank[] getTanks (ForgeDirection direction)
    {
        return null;
    }

    @Override
    public ILiquidTank getTank (ForgeDirection direction, LiquidStack type)
    {
        return null;
    }

}

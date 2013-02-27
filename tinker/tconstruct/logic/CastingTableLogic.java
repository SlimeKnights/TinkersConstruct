package tinker.tconstruct.logic;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.world.World;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.LiquidStack;
import tinker.common.IPattern;
import tinker.common.InventoryLogic;

public class CastingTableLogic extends InventoryLogic
	implements ILiquidTank
{
	public LiquidStack liquid;
	int baseMax = 125;
	
	public CastingTableLogic()
	{
		super(2);
	}

	@Override
	public String getInvName () //Not a gui block
	{
		return null;
	}

	@Override
	public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z) //Not a gui block
	{
		return null;
	}

	@Override
	public LiquidStack getLiquid ()
	{
		return null;
	}

	@Override
	public int getCapacity ()
	{
		if (inventory[0] != null && inventory[0].getItem() instanceof IPattern)
		{
			return baseMax * ((IPattern) inventory[0].getItem()).getPatternCost(inventory[0].getItemDamage());
		}
		return baseMax; //One ingot
	}

	@Override
	public int fill (LiquidStack resource, boolean doFill)
	{
		if (resource == null)
			return 0;
		
		if (liquid == null)
		{
			liquid = resource.copy();
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			return liquid.amount;
		}
		else if (resource.itemID != liquid.itemID || resource.itemMeta != liquid.itemMeta)
		{
			return 0;
		}
		else if (resource.amount + liquid.amount >= getCapacity()) //Start timer here
		{
			int total = getCapacity();
			int cap = total - liquid.amount;
			if (doFill && cap != total)
			{
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				liquid.amount = getCapacity();
			}
			return cap;
		}
		else
		{
			if (doFill)
			{
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				liquid.amount += resource.amount;
			}
			return resource.amount;
		}
	}

	@Override
	public LiquidStack drain (int maxDrain, boolean doDrain) //Doesn't actually drain
	{
		return null;
	}

	@Override
	public int getTankPressure ()
	{
		return 0;
	}
	
	/* NBT */
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
		this.liquid = new LiquidStack(par1NBTTagCompound.getInteger("itemID"), par1NBTTagCompound.getInteger("amount"), par1NBTTagCompound.getInteger("itemMeta"));
		super.readFromNBT(par1NBTTagCompound);
    }
	
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
		if (liquid != null)
		{
			par1NBTTagCompound.setInteger("itemID", liquid.itemID);
			par1NBTTagCompound.setInteger("amount", liquid.amount);
			par1NBTTagCompound.setInteger("itemMeta", liquid.itemMeta);
		}
		else
		{
			par1NBTTagCompound.setInteger("itemID", 0);
			par1NBTTagCompound.setInteger("amount", 0);
			par1NBTTagCompound.setInteger("itemMeta", 0);
		}
		super.writeToNBT(par1NBTTagCompound);
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
		readFromNBT(packet.customParam1);
		worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
	}
}

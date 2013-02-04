package tinker.tconstruct.logic;

import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.LiquidStack;
import tinker.common.IActiveLogic;
import tinker.common.IFacingLogic;
import tinker.common.InventoryLogic;
import tinker.tconstruct.TContent;
import tinker.tconstruct.container.SmelteryContainer;
import tinker.tconstruct.crafting.Smeltery;

/* Simple class for storing items in the block
 */

public class SmelteryLogic extends InventoryLogic
	implements IActiveLogic, IFacingLogic
{
	boolean validStructure;
	byte direction;
	int internalTemp;
	int maxTemp;
	int useTime;
	public int fuelGague;
	int[] tankCoords;
	int[] bottomTemps;
	int tick;
	
	public SmelteryLogic()
	{
		super(9);
		bottomTemps = new int[9];
		for (int i = 0; i < 9; i++)
			bottomTemps[i] = 20;
	}

	/* Misc */
	@Override
	public String getInvName ()
	{
		return "crafters.Smeltery";
	}

	@Override
	public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
	{
		return new SmelteryContainer(inventoryplayer, this);
	}

	@Override
	public byte getDirection() 
	{
		return direction;
	}

	@Override
	public void setDirection(byte dir) 
	{
		direction = dir;
	}

	@Override
	public boolean getActive() 
	{
		return validStructure;
	}

	@Override
	public void setActive(boolean flag) 
	{
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);		
	}

	@Override
	public boolean canFaceVertical() 
	{
		return false;
	}
	
	public int getScaledFuelGague (int scale)
	{
		return (fuelGague * scale) / 52;
	}
	
	public int getInternalTemperature ()
	{
		return internalTemp;
	}
	
	public int getTempForSlot (int slot)
	{
		return bottomTemps[slot];
	}
	
	/* Updating */
	public void updateEntity ()
	{
		tick++;
		if (tick % 4 == 0)
			updateTemperatures();
			
		if (tick == 20)
		{
			tick = 0;
			checkValidPlacement();
			updateFuelGague();
			
			if (useTime > 0)
				useTime--;
			if (!worldObj.isRemote)
				matchInventoryToWorld();
			
			if (validStructure && useTime == 0)
			{
				TileEntity tank = worldObj.getBlockTileEntity(tankCoords[0], tankCoords[1], tankCoords[2]);
				if (tank instanceof ILiquidTank)
				{
					LiquidStack liquid = ((ILiquidTank)tank).drain(10, true);
					if (liquid != null)
					{
						System.out.println("Drained "+liquid.amount);
						useTime += liquid.amount;
					}
				}
			}
		}
	}
	
	void updateTemperatures()
	{
		for (int i = 0; i < 9; i++)
		{
			if (this.isStackInSlot(i) && bottomTemps[i] < internalTemp)
				bottomTemps[i] += 1;
			else
				bottomTemps[i] = 20;
		}
	}
	
	void updateFuelGague()
	{
		if (tankCoords == null || tankCoords.length < 3)
			return;
		
		TileEntity tank = worldObj.getBlockTileEntity(tankCoords[0], tankCoords[1], tankCoords[2]);
		if (tank == null)
		{
			fuelGague = 0;
		}
		if (tank instanceof ILiquidTank)
		{
			LiquidStack liquid = ((ILiquidTank)tank).getLiquid();
			int capacity = ((ILiquidTank)tank).getCapacity();
			if (liquid != null)
				fuelGague = liquid.amount * 52 / capacity;
			else
				fuelGague = 0;
		}
	}
	
	public ItemStack getResultFor (ItemStack stack)
	{
		ItemStack result = Smeltery.instance.getSmeltingResult(stack);
		if (result != null)
			return result;

		return null;
	}
	
	/* Inventory */
	public int getInventoryStackLimit ()
	{
		return 1;
	}
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack)
    {
		super.setInventorySlotContents(slot, itemstack);
		setWorldToInventory();
    }
	
	@Override
	public ItemStack decrStackSize(int slot, int quantity)
    {
		ItemStack stack = super.decrStackSize(slot, quantity);
		setWorldToInventory();
		return stack;
    }
	
	/* World-inventory matching */
	void matchInventoryToWorld()
	{
		switch(getDirection())
		{
		case 2: // +z
			grabWorldBlocks(xCoord, yCoord, zCoord+2);
			break;
		case 3: // -z
			grabWorldBlocks(xCoord, yCoord, zCoord-2);
			break;
		case 4: // +x
			grabWorldBlocks(xCoord+2, yCoord, zCoord);
			break;
		case 5: // -x
			grabWorldBlocks(xCoord-2, yCoord, zCoord);
			break;
		}
	}
	
	void setWorldToInventory()
	{
		switch(getDirection())
		{
		case 2: // +z
			setWorldToInventory(xCoord, yCoord, zCoord+2);
			break;
		case 3: // -z
			setWorldToInventory(xCoord, yCoord, zCoord-2);
			break;
		case 4: // +x
			setWorldToInventory(xCoord+2, yCoord, zCoord);
			break;
		case 5: // -x
			setWorldToInventory(xCoord-2, yCoord, zCoord);
			break;
		}
	}
	
	public void grabWorldBlocks(int x, int y, int z)
	{
		for (int xPos = 0; xPos <= 2; xPos++)
		{
			for (int zPos = 0; zPos <= 2; zPos++)
			{
				int bID = worldObj.getBlockId(xPos+x-1, y, zPos+z-1);
				
				ItemStack stack = inventory[xPos+zPos*3];
				if (bID == 0)
					inventory[xPos+zPos*3] = null;
				else if (stack == null || stack.itemID != bID)
					inventory[xPos+zPos*3] = new ItemStack(bID, 1, worldObj.getBlockMetadata(xPos+x-1, y, zPos+z-1));
			}
		}
	}
	
	public void setWorldToInventory(int x, int y, int z)
	{
		for (int xPos = 0; xPos <= 2; xPos++)
		{
			for (int zPos = 0; zPos <= 2; zPos++)
			{
				ItemStack stack = inventory[xPos+zPos*3];
				if (stack == null)
					worldObj.setBlockWithNotify(xPos+x-1, y, zPos+z-1, 0);
				
				else if (stack.itemID < Block.blocksList.length && Block.blocksList[stack.itemID] != null)
					worldObj.setBlockAndMetadataWithNotify(xPos+x-1, y, zPos+z-1, stack.itemID, stack.getItemDamage());
			}
		}
	}
	
	public void checkValidPlacement()
	{
		switch(getDirection())
		{
		case 2: // +z
			checkValidStructure(xCoord, yCoord, zCoord+2);
			break;
		case 3: // -z
			checkValidStructure(xCoord, yCoord, zCoord-2);
			break;
		case 4: // +x
			checkValidStructure(xCoord+2, yCoord, zCoord);
			break;
		case 5: // -x
			checkValidStructure(xCoord-2, yCoord, zCoord);
			break;
		}
	}
	
	public void checkValidStructure(int x, int y, int z)
	{
		int numBricks = 0;
		boolean lavaTank = false;
		
		//Check outer layer
		for (int xPos = x-1; xPos <= x+1; xPos++)
		{
			int southID = worldObj.getBlockId(xPos, y, z-2);
			int northID = worldObj.getBlockId(xPos, y, z+2);
			if (southID == TContent.searedBrick.blockID)
				numBricks++;
			else if (southID == TContent.lavaTank.blockID)
			{
				tankCoords = new int[] {xPos, y, z-2};
				lavaTank = true;
			}
			
			if (northID == TContent.searedBrick.blockID)
				numBricks++;
			else if (northID == TContent.lavaTank.blockID)
			{
				tankCoords = new int[] {xPos, y, z+2};
				lavaTank = true;
			}
		}
		for (int zPos = z-1; zPos <= z+1; zPos++)
		{
			int westID = worldObj.getBlockId(x-2, y, zPos);
			int eastID = worldObj.getBlockId(x+2, y, zPos);
			if (westID == TContent.searedBrick.blockID)
				numBricks++;
			else if (westID == TContent.lavaTank.blockID)
			{
				tankCoords = new int[] {x-2, y, zPos};
				lavaTank = true;
			}
			
			if (eastID == TContent.searedBrick.blockID)
				numBricks++;
			else if (eastID == TContent.lavaTank.blockID)
			{
				tankCoords = new int[] {x+2, y, zPos};
				lavaTank = true;
			}
		}
		
		//Check floor
		for (int xPos = x-1; xPos <= x+1; xPos++)
		{
			for (int zPos = z-1; zPos <= z+1; zPos++)
			{
				if (worldObj.getBlockId(xPos, y-1, zPos) == TContent.searedBrick.blockID)
					numBricks++;
			}
		}
		
		if (numBricks == 19 && lavaTank)
		{
			validStructure = true;
			internalTemp = 550;
		}
		else
		{
			validStructure = false;
			internalTemp = 20;
		}
	}
	
	/* NBT */
	@Override
	public void readFromNBT(NBTTagCompound tags)
    {
        super.readFromNBT(tags);
        validStructure = tags.getBoolean("ValidStructure");
        direction = tags.getByte("Direction");
        useTime = tags.getInteger("UseTime");
        internalTemp = tags.getInteger("Temp");
        tankCoords = tags.getIntArray("TankCoords");
    }
	
	@Override
	public void writeToNBT(NBTTagCompound tags)
    {
        super.writeToNBT(tags);
        tags.setBoolean("ValidStructure", validStructure);
        tags.setByte("Direction", direction);
        tags.setInteger("UseTime", useTime);
        tags.setInteger("Temp", internalTemp);
        tags.setIntArray("TankCoords", tankCoords);
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
		worldObj.getBlockTileEntity(xCoord, yCoord, zCoord).readFromNBT(packet.customParam1);
		worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
	}
}

package tinker.tconstruct.logic;

import java.util.ArrayList;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.LiquidStack;
import tinker.common.CoordTuple;
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
	public boolean validStructure;
	byte direction;
	int internalTemp;
	int maxTemp;
	int useTime;
	public int fuelGague;
	
	CoordTuple lavaTank;
	CoordTuple drain;
	public CoordTuple centerPos;
	
	public int[] activeTemps;
	public int[] meltingTemps;
	int tick;
	
	public ArrayList<LiquidStack> moltenMetal = new ArrayList<LiquidStack>();
	int maxLiquid = 10000;
	int slag;

	public SmelteryLogic()
	{
		super(9);
		activeTemps = new int[9];
		meltingTemps = new int[9];
		for (int i = 0; i < 9; i++)
		{
			activeTemps[i] = 20;
			meltingTemps[i] = 0;
		}
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
	public byte getDirection ()
	{
		return direction;
	}

	@Override
	public void setDirection (float yaw, float pitch)
	{
		int facing = MathHelper.floor_double((double)(yaw / 360) + 0.5D) & 3;
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

		//direction = dir;
	}

	@Override
	public boolean getActive ()
	{
		return validStructure;
	}

	@Override
	public void setActive (boolean flag)
	{
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
		return activeTemps[slot];
	}

	/* Updating */
	public void updateEntity ()
	{
		//System.out.println("tick");
		tick++;
		if (tick % 4 == 0 && !worldObj.isRemote)
			heatItems();

		if (tick == 20)
		{
			tick = 0;
			checkValidPlacement();
			updateFuelGague();

			if (useTime > 0)
				useTime--;
			/*if (!worldObj.isRemote)
				matchInventoryToWorld();*/

			if (validStructure && useTime == 0)
			{
				TileEntity tank = worldObj.getBlockTileEntity(lavaTank.x, lavaTank.y, lavaTank.z);
				if (tank instanceof ILiquidTank)
				{
					LiquidStack liquid = ((ILiquidTank) tank).drain(10, true);
					if (liquid != null)
					{
						//System.out.println("Drained " + liquid.amount);
						useTime += liquid.amount;
					}
				}
			}
		}
	}

	void heatItems ()
	{
		for (int i = 0; i < 9; i++)
		{
			if (meltingTemps[i] > 20 && this.isStackInSlot(i))
			{
				if (activeTemps[i] < internalTemp && activeTemps[i] < meltingTemps[i])
					activeTemps[i] += 1;
				else if (meltingTemps[i] >= activeTemps[i])
				{
					LiquidStack result = getResultFor(inventory[i]);
					if (result != null)
					{
						inventory[i] = null;
						addMoltenMetal(result);
						ArrayList alloys = Smeltery.mixMetals(moltenMetal);
						for (int al = 0; al < alloys.size(); al++)
						{
							LiquidStack liquid = (LiquidStack) alloys.get(al);
							addMoltenMetal(liquid);
						}
						onInventoryChanged();
						//setWorldToInventory();
					}
				}

			}

			else
				activeTemps[i] = 20;
		}
	}
	
	void addMoltenMetal(LiquidStack liquid)
	{
		if (moltenMetal.size() == 0)
			moltenMetal.add(liquid);
		else
		{
			boolean added = false;
			for (LiquidStack l : moltenMetal)
			{
				if (l.itemID == liquid.itemID && l.itemMeta == liquid.itemMeta)
				{
					l.amount += liquid.amount;
					added = true;
				}
				/*if (l.amount <= 0)
				{
					moltenMetal.remove(l);
				}*/
			}
			if (!added)
				moltenMetal.add(liquid);
		}
	}
	
	void updateTemperatures()
	{
		for (int i = 0; i < 9; i++)
		{
			meltingTemps[i] = Smeltery.instance.getLiquifyTemperature(inventory[i]);
		}
	}

	void updateFuelGague ()
	{
		if (lavaTank == null)
			return;

		TileEntity tank = worldObj.getBlockTileEntity(lavaTank.x, lavaTank.y, lavaTank.z);
		if (tank == null)
		{
			fuelGague = 0;
		}
		if (tank instanceof ILiquidTank)
		{
			LiquidStack liquid = ((ILiquidTank) tank).getLiquid();
			int capacity = ((ILiquidTank) tank).getCapacity();
			if (liquid != null)
				fuelGague = liquid.amount * 52 / capacity;
			else
				fuelGague = 0;
		}
	}

	public LiquidStack getResultFor (ItemStack stack)
	{
		return Smeltery.instance.getSmelteryResult(stack);
	}

	/* Inventory */
	public int getInventoryStackLimit ()
	{
		return 1;
	}
	
	public void onInventoryChanged()
	{
		updateTemperatures();
		super.onInventoryChanged();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public void checkValidPlacement ()
	{
		switch (getDirection())
		{
		case 2: // +z
			checkValidStructure(xCoord, yCoord, zCoord + 2);
			break;
		case 3: // -z
			checkValidStructure(xCoord, yCoord, zCoord - 2);
			break;
		case 4: // +x
			checkValidStructure(xCoord + 2, yCoord, zCoord);
			break;
		case 5: // -x
			checkValidStructure(xCoord - 2, yCoord, zCoord);
			break;
		}
	}

	public void checkValidStructure (int x, int y, int z)
	{
		int numBricks = 0;
		boolean hasLavaTank = false;

		//Check outer layer
		for (int xPos = x - 1; xPos <= x + 1; xPos++)
		{
			int southID = worldObj.getBlockId(xPos, y, z - 2);
			int northID = worldObj.getBlockId(xPos, y, z + 2);
			if (southID == TContent.searedBrick.blockID)
				numBricks++;
			else if (southID == TContent.lavaTank.blockID)
			{
				lavaTank = new CoordTuple(xPos, y, z - 2);
				hasLavaTank = true;
			}

			if (northID == TContent.searedBrick.blockID)
				numBricks++;
			else if (northID == TContent.lavaTank.blockID)
			{
				lavaTank = new CoordTuple(xPos, y, z + 2 );
				hasLavaTank = true;
			}
		}
		for (int zPos = z - 1; zPos <= z + 1; zPos++)
		{
			int westID = worldObj.getBlockId(x - 2, y, zPos);
			int eastID = worldObj.getBlockId(x + 2, y, zPos);
			if (westID == TContent.searedBrick.blockID)
				numBricks++;
			else if (westID == TContent.lavaTank.blockID)
			{
				lavaTank = new CoordTuple(x - 2, y, zPos);
				hasLavaTank = true;
			}

			if (eastID == TContent.searedBrick.blockID)
				numBricks++;
			else if (eastID == TContent.lavaTank.blockID)
			{
				lavaTank = new CoordTuple( x + 2, y, zPos );
				hasLavaTank = true;
			}
		}

		//Check floor
		for (int xPos = x - 1; xPos <= x + 1; xPos++)
		{
			for (int zPos = z - 1; zPos <= z + 1; zPos++)
			{
				if (worldObj.getBlockId(xPos, y - 1, zPos) == TContent.searedBrick.blockID)
					numBricks++;
			}
		}

		if (numBricks == 19 && hasLavaTank)
		{
			if (!validStructure)
			{
				centerPos = new CoordTuple(x, y, z);
				validStructure = true;
			}
			internalTemp = 800;
		}
		else
		{
			validStructure = false;
			internalTemp = 20;
		}
	}

	/* NBT */
	@Override
	public void readFromNBT (NBTTagCompound tags)
	{
		super.readFromNBT(tags);
		direction = tags.getByte("Direction");
		useTime = tags.getInteger("UseTime");
		meltingTemps = tags.getIntArray("MeltingTemps");
		activeTemps = tags.getIntArray("ActiveTemps");
		
		NBTTagList liquidTag = tags.getTagList("Liquids");
		moltenMetal.clear();
		
		for (int iter = 0; iter < liquidTag.tagCount(); iter++)
        {
            NBTTagCompound tagList = (NBTTagCompound)liquidTag.tagAt(iter);
            int id = tagList.getInteger("id");
            int amount = tagList.getInteger("amount");
            int meta = tagList.getInteger("meta");
            moltenMetal.add(new LiquidStack(id, amount, meta));
        }
	}

	@Override
	public void writeToNBT (NBTTagCompound tags)
	{
		super.writeToNBT(tags);
		tags.setByte("Direction", direction);
		tags.setInteger("UseTime", useTime);
		tags.setIntArray("MeltingTemps", meltingTemps);
		tags.setIntArray("ActiveTemps", activeTemps);
		
		NBTTagList taglist = new NBTTagList();
		for (LiquidStack liquid : moltenMetal)
		{
			NBTTagCompound liquidTag = new NBTTagCompound();
			liquidTag.setInteger("id", liquid.itemID);
			liquidTag.setInteger("amount", liquid.amount);
			liquidTag.setInteger("meta", liquid.itemMeta);
			taglist.appendTag(liquidTag);
		}
		
		tags.setTag("Liquids", taglist);
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

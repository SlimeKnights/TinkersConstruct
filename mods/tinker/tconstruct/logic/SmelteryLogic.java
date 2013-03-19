package mods.tinker.tconstruct.logic;

import java.util.ArrayList;

import mods.tinker.common.CoordTuple;
import mods.tinker.common.IActiveLogic;
import mods.tinker.common.IFacingLogic;
import mods.tinker.common.IMasterLogic;
import mods.tinker.common.InventoryLogic;
import mods.tinker.tconstruct.TContent;
import mods.tinker.tconstruct.container.SmelteryContainer;
import mods.tinker.tconstruct.crafting.Smeltery;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
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
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidStack;

/* Simple class for storing items in the block
 */

public class SmelteryLogic extends InventoryLogic 
	implements IActiveLogic, IFacingLogic, ILiquidTank, IMasterLogic
{
	public boolean validStructure;
	byte direction;
	int internalTemp;
	int maxTemp;
	public int useTime;
	public int fuelGague;

	ArrayList<CoordTuple> lavaTanks;
	CoordTuple activeLavaTank;
	CoordTuple drain;
	public CoordTuple centerPos;

	public int[] activeTemps;
	public int[] meltingTemps;
	int tick;

	public ArrayList<LiquidStack> moltenMetal = new ArrayList<LiquidStack>();
	int maxLiquid;
	public int layers;
	int slag;

	int numBricks;

	public SmelteryLogic()
	{
		super(0);
		lavaTanks = new ArrayList<CoordTuple>();
		activeTemps = new int[0];
		meltingTemps = new int[0];
	}

	void adjustLayers (int lay, boolean forceAdjust)
	{
		if (lay != layers || forceAdjust)
		{
			layers = lay;
			maxLiquid = 20000 * lay;
			int[] tempActive = activeTemps;
			activeTemps = new int[9 * lay];
			int activeLength = tempActive.length > activeTemps.length ? activeTemps.length : tempActive.length;
			System.arraycopy(tempActive, 0, activeTemps, 0, activeLength);

			int[] tempMelting = meltingTemps;
			meltingTemps = new int[9 * lay];
			int meltingLength = tempMelting.length > meltingTemps.length ? meltingTemps.length : tempMelting.length;
			System.arraycopy(tempMelting, 0, meltingTemps, 0, meltingLength);

			ItemStack[] tempInv = inventory;
			inventory = new ItemStack[9 * lay];
			int invLength = tempInv.length > inventory.length ? inventory.length : tempInv.length;
			System.arraycopy(tempInv, 0, inventory, 0, invLength);

			if (activeTemps.length > 0 && activeTemps.length > tempActive.length)
			{
				for (int i = tempActive.length; i < activeTemps.length; i++)
				{
					activeTemps[i] = 20;
					meltingTemps[i] = 20;
				}
			}
		}
	}

	/* Misc */
	@Override
	public String getDefaultName ()
	{
		return "crafters.Smeltery";
	}

	@Override
	public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
	{
		return new SmelteryContainer(inventoryplayer, this);
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

	public int getMeltingPointForSlot (int slot)
	{
		return meltingTemps[slot];
	}

	/* Updating */
	public void updateEntity ()
	{
		tick++;
		if (tick % 4 == 0)
			heatItems();

		if (tick == 20)
		{
			tick = 0;
			if (!validStructure)
				checkValidPlacement();

			if (useTime > 0)
				useTime--;

			if (validStructure && useTime == 0)
			{
				updateFuelGague();
			}
		}
	}

	void heatItems ()
	{
		if (useTime > 0)
		{
			for (int i = 0; i < 9 * layers; i++)
			{
				if (meltingTemps[i] > 20 && this.isStackInSlot(i))
				{
					if (activeTemps[i] < internalTemp && activeTemps[i] < meltingTemps[i])
						activeTemps[i] += 5;
					else if (meltingTemps[i] >= activeTemps[i])
					{
						if (!worldObj.isRemote)
						{
							LiquidStack result = getResultFor(inventory[i]);
							if (result != null)
							{
								inventory[i] = null;
								addMoltenMetal(result, false);
								ArrayList alloys = Smeltery.mixMetals(moltenMetal);
								for (int al = 0; al < alloys.size(); al++)
								{
									LiquidStack liquid = (LiquidStack) alloys.get(al);
									addMoltenMetal(liquid, true);
								}
								onInventoryChanged();
								//setWorldToInventory();
							}
						}
					}

				}

				else
					activeTemps[i] = 20;
			}
		}
	}

	void addMoltenMetal (LiquidStack liquid, boolean first)
	{
		if (moltenMetal.size() == 0)
			moltenMetal.add(liquid);
		else
		{
			boolean added = false;
			//for (LiquidStack l : moltenMetal)
			for (int i = 0; i < moltenMetal.size(); i++)
			{
				LiquidStack l = moltenMetal.get(i);
				if (l.itemID == liquid.itemID && l.itemMeta == liquid.itemMeta)
				{
					l.amount += liquid.amount;
					added = true;
				}
				if (l.amount <= 0)
				{
					moltenMetal.remove(l);
					i--;
				}
			}
			if (!added)
			{
				if (first)
					moltenMetal.add(0, liquid);
				else
					moltenMetal.add(liquid);
			}
		}
	}

	void updateTemperatures ()
	{
		for (int i = 0; i < 9 * layers; i++)
		{
			meltingTemps[i] = Smeltery.instance.getLiquifyTemperature(inventory[i]);
		}
	}

	void updateFuelGague ()
	{
		if (activeLavaTank == null)
			return;

		TileEntity tankContainer = worldObj.getBlockTileEntity(activeLavaTank.x, activeLavaTank.y, activeLavaTank.z);
		if (tankContainer == null)
		{
			fuelGague = 0;
		}
		if (tankContainer instanceof ITankContainer)
		{
			LiquidStack liquid = ((ITankContainer) tankContainer).drain(ForgeDirection.DOWN, 50, false);
			if (liquid != null && liquid.itemID == Block.lavaStill.blockID)
			{
				liquid = ((ITankContainer) tankContainer).drain(ForgeDirection.DOWN, 50, true);
				useTime += liquid.amount;
				ILiquidTank tank = ((ITankContainer) tankContainer).getTank(ForgeDirection.DOWN, liquid);
				liquid = tank.getLiquid();
				int capacity = tank.getCapacity();
				fuelGague = liquid.amount * 52 / capacity;
			}
		}
	}

	public LiquidStack getResultFor (ItemStack stack)
	{
		return Smeltery.instance.getSmelteryResult(stack);
	}

	/* Inventory */
	public int getMaxStackStackSize (ItemStack stack)
	{
		LiquidStack liquid = getResultFor(stack);
		if (liquid == null)
			return 0;
		return liquid.amount;
	}

	public int getInventoryStackLimit ()
	{
		return 1;
	}

	public void onInventoryChanged ()
	{
		updateTemperatures();
		super.onInventoryChanged();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	/* Multiblock */
	public void checkValidPlacement ()
	{
		switch (getRenderDirection())
		{
		case 2: // +z
			alignInitialPlacement(xCoord, yCoord, zCoord + 2);
			break;
		case 3: // -z
			alignInitialPlacement(xCoord, yCoord, zCoord - 2);
			break;
		case 4: // +x
			alignInitialPlacement(xCoord + 2, yCoord, zCoord);
			break;
		case 5: // -x
			alignInitialPlacement(xCoord - 2, yCoord, zCoord);
			break;
		}
	}

	public void alignInitialPlacement (int x, int y, int z)
	{
		int northID = worldObj.getBlockId(x, y, z + 1);
		int southID = worldObj.getBlockId(x, y, z - 1);
		int eastID = worldObj.getBlockId(x + 1, y, z);
		int westID = worldObj.getBlockId(x - 1, y, z);

		if (northID == 0 && southID == 0 && eastID == 0 && westID == 0) //Center
		{
			checkValidStructure(x, y, z);
		}

		else if (northID != 0 && southID == 0 && eastID == 0 && westID == 0)
		{
			checkValidStructure(x, y, z - 1);
		}

		else if (northID == 0 && southID != 0 && eastID == 0 && westID == 0)
		{
			checkValidStructure(x, y, z + 1);
		}

		else if (northID == 0 && southID == 0 && eastID != 0 && westID == 0)
		{
			checkValidStructure(x - 1, y, z);
		}

		else if (northID == 0 && southID == 0 && eastID == 0 && westID != 0)
		{
			checkValidStructure(x + 1, y, z);
		}

		//Not valid, sorry
	}

	public void checkValidStructure (int x, int y, int z)
	{
		int capacity = 0;
		validStructure = false;
		if (checkSameLevel(x, y, z))
		{
			capacity++;
			capacity += recurseStructureUp(x, y + 1, z, 0);
			capacity += recurseStructureDown(x, y - 1, z, 0);
		}

		//maxLiquid = capacity * 20000;

		if (validStructure)
		{
			internalTemp = 800;
			activeLavaTank = lavaTanks.get(0);
			adjustLayers(capacity, false);
		}
		else
		{
			internalTemp = 20;
		}
	}

	public boolean checkSameLevel (int x, int y, int z)
	{
		numBricks = 0;
		lavaTanks.clear();
		boolean hasLavaTank = false;

		//Check inside
		for (int xPos = x - 1; xPos <= x + 1; xPos++)
		{
			for (int zPos = z - 1; zPos <= z + 1; zPos++)
			{
				if (worldObj.getBlockId(xPos, y, zPos) != 0)
					return false;
			}
		}

		//Check outer layer
		for (int xPos = x - 1; xPos <= x + 1; xPos++)
		{
			if (checkBricks(xPos, y, z - 2))
				hasLavaTank = true;
			if (checkBricks(xPos, y, z + 2))
				hasLavaTank = true;
		}

		for (int zPos = z - 1; zPos <= z + 1; zPos++)
		{
			if (checkBricks(x - 2, y, zPos))
				hasLavaTank = true;
			if (checkBricks(x + 2, y, zPos))
				hasLavaTank = true;
		}

		if (numBricks == 12 && hasLavaTank)
			return true;
		else
			return false;
	}

	public int recurseStructureUp (int x, int y, int z, int count)
	{
		numBricks = 0;
		//Check inside
		for (int xPos = x - 1; xPos <= x + 1; xPos++)
		{
			for (int zPos = z - 1; zPos <= z + 1; zPos++)
			{
				if (worldObj.getBlockId(xPos, y, zPos) != 0)
					return count;
			}
		}

		//Check outer layer
		for (int xPos = x - 1; xPos <= x + 1; xPos++)
		{
			checkBricks(xPos, y, z - 2);
			checkBricks(xPos, y, z + 2);
		}

		for (int zPos = z - 1; zPos <= z + 1; zPos++)
		{
			checkBricks(x - 2, y, zPos);
			checkBricks(x + 2, y, zPos);
		}

		if (numBricks != 12)
			return count;

		count++;
		return recurseStructureUp(x, y + 1, z, count);
	}

	public int recurseStructureDown (int x, int y, int z, int count)
	{
		numBricks = 0;
		//Check inside
		for (int xPos = x - 1; xPos <= x + 1; xPos++)
		{
			for (int zPos = z - 1; zPos <= z + 1; zPos++)
			{
				int blockID = worldObj.getBlockId(xPos, y, zPos);
				if (blockID != 0)
				{
					if (blockID == TContent.smeltery.blockID)
						return validateBottom(x, y, z, count);
					else
						return count;
				}
			}
		}

		//Check outer layer
		for (int xPos = x - 1; xPos <= x + 1; xPos++)
		{
			checkBricks(xPos, y, z - 2);
			checkBricks(xPos, y, z + 2);
		}

		for (int zPos = z - 1; zPos <= z + 1; zPos++)
		{
			checkBricks(x - 2, y, zPos);
			checkBricks(x + 2, y, zPos);
		}

		if (numBricks != 12)
			return count;

		count++;
		return recurseStructureDown(x, y - 1, z, count);
	}

	public int validateBottom (int x, int y, int z, int count)
	{
		int bottomBricks = 0;
		for (int xPos = x - 1; xPos <= x + 1; xPos++)
		{
			for (int zPos = z - 1; zPos <= z + 1; zPos++)
			{
				if (worldObj.getBlockId(xPos, y, zPos) == TContent.smeltery.blockID && (worldObj.getBlockMetadata(xPos, y, zPos) == 2))
					bottomBricks++;
			}
		}

		if (bottomBricks == 9)
		{
			validStructure = true;
			centerPos = new CoordTuple(x, y + 1, z);
		}
		return count;
	}

	boolean checkBricks (int x, int y, int z)
	{
		int blockID = worldObj.getBlockId(x, y, z);
		if (blockID == TContent.smeltery.blockID || blockID == TContent.lavaTank.blockID)
		{
			TileEntity te = worldObj.getBlockTileEntity(x, y, z);
			if (te == this)
			{
				numBricks++;
				return false;
			}
			else if (te instanceof MultiServantLogic)
			{
				MultiServantLogic servant = (MultiServantLogic) te;
				if (servant.hasValidMaster())
				{
					if (servant.verifyMaster(this.xCoord, this.yCoord, this.zCoord))
						numBricks++;
				}
				else if (servant.setMaster(this.xCoord, this.yCoord, this.zCoord))
				{
					numBricks++;
				}

				if (te instanceof LavaTankLogic)
				{
					lavaTanks.add(new CoordTuple(x, y, z));
					return true;
				}
			}
		}
		return false;
	}

	/* Not an ILiquidTank, but is still a liquid tank of sorts */
	public int getCapacity ()
	{
		return maxLiquid;
	}

	public LiquidStack drain (int maxDrain, boolean doDrain)
	{
		if (moltenMetal.size() == 0)
			return null;

		LiquidStack liquid = moltenMetal.get(0);
		if (liquid.amount - maxDrain <= 0)
		{
			LiquidStack liq = liquid.copy();
			if (doDrain)
			{
				//liquid = null;
				moltenMetal.remove(liquid);
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
			return liq;
		}
		else
		{
			if (doDrain)
			{
				liquid.amount -= maxDrain;
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
			return new LiquidStack(liquid.itemID, maxDrain, liquid.itemMeta);
		}
	}

	public int fill (LiquidStack resource, boolean doFill)
	{
		int amount = resource.amount;
		addMoltenMetal(resource, false);
		return amount;
	}

	@Override
	public LiquidStack getLiquid ()
	{
		if (moltenMetal.size() == 0)
			return null;
		return moltenMetal.get(0);
	}

	@Override
	public int getTankPressure ()
	{
		return 0;
	}

	/* NBT */
	@Override
	public void readFromNBT (NBTTagCompound tags)
	{
		direction = tags.getByte("Direction");
		useTime = tags.getInteger("UseTime");
		maxLiquid = tags.getInteger("MaxLiquid");
		layers = tags.getInteger("Layers");
		meltingTemps = tags.getIntArray("MeltingTemps");
		activeTemps = tags.getIntArray("ActiveTemps");

		NBTTagList liquidTag = tags.getTagList("Liquids");
		moltenMetal.clear();

		for (int iter = 0; iter < liquidTag.tagCount(); iter++)
		{
			NBTTagCompound tagList = (NBTTagCompound) liquidTag.tagAt(iter);
			int id = tagList.getInteger("id");
			int amount = tagList.getInteger("amount");
			int meta = tagList.getInteger("meta");
			moltenMetal.add(new LiquidStack(id, amount, meta));
		}

		adjustLayers(layers, true);
		super.readFromNBT(tags);
	}

	@Override
	public void writeToNBT (NBTTagCompound tags)
	{
		tags.setByte("Direction", direction);
		tags.setInteger("UseTime", useTime);
		tags.setInteger("MaxLiquid", maxLiquid);
		tags.setInteger("Layers", layers);
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
		super.writeToNBT(tags);
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

	@Override
	public void notifyChange (int x, int y, int z)
	{
		checkValidPlacement();
	}
}

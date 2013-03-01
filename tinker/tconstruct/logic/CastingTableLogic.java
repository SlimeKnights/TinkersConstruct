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
import tinker.tconstruct.crafting.CastingRecipe;
import tinker.tconstruct.crafting.LiquidCasting;

public class CastingTableLogic extends InventoryLogic implements ILiquidTank
{
	public LiquidStack liquid;
	int baseMax = 250;
	float materialRedux = 0;
	int castingDelay = 0;

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
		return liquid;
	}

	@Override
	public int getCapacity ()
	{
		int ret = baseMax;
		if (inventory[0] != null && inventory[0].getItem() instanceof IPattern)
			ret *= ((IPattern) inventory[0].getItem()).getPatternCost(inventory[0].getItemDamage());
		if (materialRedux > 0)
			ret *= materialRedux;
		return ret;
	}

	@Override
	public int fill (LiquidStack resource, boolean doFill)
	{
		if (resource == null)
			return 0;

		if (liquid == null)
		{
			//System.out.println("Woo");
			if (LiquidCasting.instance.getCastingRecipe(resource, inventory[0]) != null)
			{
				liquid = resource.copy();
				materialRedux = LiquidCasting.instance.getMaterialReduction(resource);
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				return liquid.amount;
			}
			else
				return 0;
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
				castingDelay = LiquidCasting.instance.getCastingDelay(liquid, inventory[0]);
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

	@Override
	public void updateEntity ()
	{
		if (castingDelay > 0)
		{
			castingDelay--;
			if (castingDelay == 0)
				castLiquid();
		}
	}

	public void castLiquid ()
	{
		CastingRecipe recipe = LiquidCasting.instance.getCastingRecipe(liquid, inventory[0]);
		if (recipe != null)
		{
			materialRedux = 0;
			inventory[1] = recipe.getResult();
			if (recipe.consumeCast)
				inventory[0] = null;
			liquid = null;
			//System.out.println("Casting: " + inventory[1].getItemName());
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

	/* NBT */
	public void readFromNBT (NBTTagCompound tags)
	{
		if (tags.getBoolean("hasLiquid"))
			this.liquid = new LiquidStack(tags.getInteger("itemID"), tags.getInteger("amount"), tags.getInteger("itemMeta"));
		else
			this.liquid = null;
		super.readFromNBT(tags);
	}

	public void writeToNBT (NBTTagCompound tags)
	{
		tags.setBoolean("hasLiquid", liquid != null);
		if (liquid != null)
		{
			tags.setInteger("itemID", liquid.itemID);
			tags.setInteger("amount", liquid.amount);
			tags.setInteger("itemMeta", liquid.itemMeta);
		}
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
}

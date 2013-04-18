package mods.tinker.tconstruct.logic;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.src.ModLoader;
import net.minecraft.tileentity.TileEntity;

public class GolemPedestalLogic extends TileEntity
    implements IInventory
{
    public ItemStack supply;
    public static int cost[] = null;
    public static int souls[] = null;

    public GolemPedestalLogic()
    {
        supply = null;
        if (cost == null)
        {
            cost = new int[512];
            souls = new int[512];
            cost[Block.planks.blockID] = 0;
            cost[Block.wood.blockID] = 32;
            cost[Block.cloth.blockID] = 8;
            cost[Block.cobblestoneMossy.blockID] = 16;
            cost[Block.obsidian.blockID] = 8;
            cost[Block.pumpkin.blockID] = 8;
            cost[Block.netherrack.blockID] = 64;
            cost[Block.slowSand.blockID] = 1;
            cost[Block.glowStone.blockID] = 1;
            cost[Block.pumpkinLantern.blockID] = 8;
            cost[Block.blockDiamond.blockID] = 1;
            cost[Block.blockGold.blockID] = 1;
            cost[Block.blockIron.blockID] = 1;
            cost[Block.blockLapis.blockID] = 1;
            cost[Block.jukebox.blockID] = 1;
            cost[Block.music.blockID] = 1;
            cost[Block.melon.blockID] = 8;
            cost[Block.torchRedstoneIdle.blockID] = 6;
            cost[Block.torchRedstoneActive.blockID] = cost[Block.torchRedstoneIdle.blockID];
            cost[Item.redstone.itemID] = 6;
            cost[Item.beefRaw.itemID] = 8;
            cost[Item.beefCooked.itemID] = cost[Item.beefRaw.itemID];
            cost[Item.porkRaw.itemID] = cost[Item.beefRaw.itemID];
            cost[Item.porkCooked.itemID] = cost[Item.beefRaw.itemID];
            cost[Item.enderPearl.itemID] = 1;
            cost[Item.eyeOfEnder.itemID] = 1;
            cost[Item.blazeRod.itemID] = 2;
            cost[Block.mushroomBrown.blockID] = 16;
            cost[Block.mushroomRed.blockID] = cost[Block.mushroomBrown.blockID];
            cost[Block.netherStalk.blockID] = 6;
            cost[Item.lightStoneDust.itemID] = 4;
            cost[Item.diamond.itemID] = 1;
            cost[Item.ingotIron.itemID] = 8;
            cost[Item.ingotGold.itemID] = 2;
            cost[Item.reed.itemID] = 8;
            cost[Item.sugar.itemID] = cost[Item.reed.itemID];
            cost[Item.bucketLava.itemID] = 1;
            cost[Item.cake.itemID] = 1;
            cost[Item.bucketMilk.itemID] = 1;
            cost[Item.wheat.itemID] = 32;
            cost[Item.bread.itemID] = 8;
            cost[Block.plantYellow.blockID] = 32;
            cost[Block.plantRed.blockID] = cost[Block.plantYellow.blockID];
            cost[Block.dragonEgg.blockID] = 1;
            cost[Block.tallGrass.blockID] = 64;
            cost[Block.sapling.blockID] = 64;
            cost[Item.ghastTear.itemID] = 8;
            cost[Item.goldNugget.itemID] = 18;
            cost[Item.spiderEye.itemID] = 16;
            cost[Item.fermentedSpiderEye.itemID] = 6;
            cost[Item.bowlSoup.itemID] = 0;
            cost[Item.fishRaw.itemID] = 1;
            cost[Item.fishCooked.itemID] = cost[Item.fishRaw.itemID];
            cost[Item.magmaCream.itemID] = 2;
            cost[Item.blazePowder.itemID] = 4;
            cost[Item.speckledMelon.itemID] = 1;
            cost[Item.paper.itemID] = 8;
            cost[Item.book.itemID] = 2;
            cost[Item.egg.itemID] = 4;
            cost[Item.slimeBall.itemID] = 2;
            cost[Item.saddle.itemID] = 1;
            cost[Item.feather.itemID] = 48;
            cost[Item.gunpowder.itemID] = 16;
            cost[Item.appleRed.itemID] = 8;
            cost[Item.appleGold.itemID] = 1;
            cost[Item.dyePowder.itemID] = 64;
            cost[Item.bone.itemID] = 2;
            cost[Item.rottenFlesh.itemID] = 48;
            cost[Item.cookie.itemID] = 16;
            cost[Item.melon.itemID] = 32;
            cost[Item.chickenCooked.itemID] = 8;
            cost[Item.chickenRaw.itemID] = cost[Item.chickenCooked.itemID];
            cost[Item.silk.itemID] = 64;
            cost[Block.cactus.blockID] = 32;
            souls[Block.planks.blockID] = 0;
            souls[Block.wood.blockID] = 1;
            souls[Block.cloth.blockID] = 1;
            souls[Block.cobblestoneMossy.blockID] = 1;
            souls[Block.obsidian.blockID] = 1;
            souls[Block.pumpkin.blockID] = 1;
            souls[Block.netherrack.blockID] = 1;
            souls[Block.slowSand.blockID] = 1;
            souls[Block.glowStone.blockID] = 2;
            souls[Block.pumpkinLantern.blockID] = 1;
            souls[Block.blockDiamond.blockID] = 27;
            souls[Block.blockGold.blockID] = 2;
            souls[Block.blockIron.blockID] = 2;
            souls[Block.blockLapis.blockID] = 3;
            souls[Block.jukebox.blockID] = 3;
            souls[Block.music.blockID] = 1;
            souls[Block.melon.blockID] = 1;
            souls[Block.torchRedstoneIdle.blockID] = 1;
            souls[Block.torchRedstoneActive.blockID] = souls[Block.torchRedstoneIdle.blockID];
            souls[Item.redstone.itemID] = 1;
            souls[Item.beefRaw.itemID] = 1;
            souls[Item.beefCooked.itemID] = souls[Item.beefRaw.itemID];
            souls[Item.porkRaw.itemID] = souls[Item.beefRaw.itemID];
            souls[Item.porkCooked.itemID] = souls[Item.beefRaw.itemID];
            souls[Item.enderPearl.itemID] = 1;
            souls[Item.eyeOfEnder.itemID] = 2;
            souls[Item.blazeRod.itemID] = 2;
            souls[Block.mushroomBrown.blockID] = 1;
            souls[Block.mushroomRed.blockID] = souls[Block.mushroomBrown.blockID];
            souls[Block.netherStalk.blockID] = 1;
            souls[Item.lightStoneDust.itemID] = 1;
            souls[Item.diamond.itemID] = 3;
            souls[Item.ingotIron.itemID] = 1;
            souls[Item.ingotGold.itemID] = 1;
            souls[Item.reed.itemID] = 1;
            souls[Item.sugar.itemID] = souls[Item.reed.itemID];
            souls[Item.bucketLava.itemID] = 1;
            souls[Item.cake.itemID] = 1;
            souls[Item.bucketMilk.itemID] = 1;
            souls[Item.wheat.itemID] = 1;
            souls[Item.bread.itemID] = 1;
            souls[Block.plantYellow.blockID] = 1;
            souls[Block.plantRed.blockID] = souls[Block.plantYellow.blockID];
            souls[Block.dragonEgg.blockID] = 1;
            souls[Block.tallGrass.blockID] = 1;
            souls[Block.sapling.blockID] = 1;
            souls[Item.ghastTear.itemID] = 1;
            souls[Item.goldNugget.itemID] = 1;
            souls[Item.spiderEye.itemID] = 1;
            souls[Item.fermentedSpiderEye.itemID] = 1;
            souls[Item.bowlSoup.itemID] = 1;
            souls[Item.fishRaw.itemID] = 1;
            souls[Item.fishCooked.itemID] = souls[Item.fishRaw.itemID];
            souls[Item.magmaCream.itemID] = 1;
            souls[Item.blazePowder.itemID] = 1;
            souls[Item.speckledMelon.itemID] = 1;
            souls[Item.paper.itemID] = 1;
            souls[Item.book.itemID] = 1;
            souls[Item.egg.itemID] = 1;
            souls[Item.slimeBall.itemID] = 1;
            souls[Item.saddle.itemID] = 1;
            souls[Item.feather.itemID] = 1;
            souls[Item.gunpowder.itemID] = 1;
            souls[Item.appleRed.itemID] = 1;
            souls[Item.appleGold.itemID] = 3;
            souls[Item.dyePowder.itemID] = 1;
            souls[Item.bone.itemID] = 1;
            souls[Item.rottenFlesh.itemID] = 1;
            souls[Item.cookie.itemID] = 1;
            souls[Item.melon.itemID] = 1;
            souls[Item.chickenCooked.itemID] = 1;
            souls[Item.chickenRaw.itemID] = souls[Item.chickenCooked.itemID];
            souls[Item.silk.itemID] = 1;
            souls[Block.cactus.blockID] = 1;
        }
    }

    public int getSizeInventory()
    {
        return 9;
    }

    public ItemStack getStackInSlot(int i)
    {
        if (supply != null && supply.getItem() == null)
        {
            supply = null;
        }
        return supply;
    }

    public boolean subtractSoul(int i)
    {
        System.out.println("SUBTRACT WHY");
        if (supply == null || supply.itemID >= 512 || cost[supply.itemID] == 0)
        {
            worldObj.playSoundAtEntity(ModLoader.getMinecraftInstance().thePlayer, "mob.blaze.death", 1.0F, 0.5F);
            ModLoader.getMinecraftInstance().thePlayer.addChatMessage("I require more souls...");
            return false;
        }
        int j = cost[supply.itemID];
        int k = souls[supply.itemID];
        int l = (supply.stackSize / j) * k;
        System.out.println((new StringBuilder()).append("Subtracting amt").append(i).append(" s").append(k).append(" c").append(j).append(" ss").append(supply.stackSize).append(" id").append(supply.itemID).append(" sa").append(l).append(" calc").append(Math.ceil((double)i / (double)k)).toString());
        if (l < i)
        {
            worldObj.playSoundAtEntity(ModLoader.getMinecraftInstance().thePlayer, "mob.blaze.death", 1.0F, 0.5F);
            ModLoader.getMinecraftInstance().thePlayer.addChatMessage("I need more souls...");
            return false;
        }
        supply.stackSize -= Math.ceil((double)i / (double)k) * (double)j;
        if (supply.stackSize == 0)
        {
            supply = null;
        }
        else if (supply.stackSize < 0)
        {
            throw new UnsupportedOperationException((new StringBuilder()).append("If you are getting this error then your golems mod has derped, \nplease contact billythegoat101 on the minecraftforums with the error code: LERN YO MATHS [i").append(supply.itemID).append(",ss").append(supply.stackSize).append(",a").append(i).append("]").toString());
        }
        return true;
    }

    public ItemStack decrStackSize(int i, int j)
    {
        if (supply != null && supply.getItem() == null)
        {
            supply = null;
        }
        if (supply != null)
        {
            if (supply.stackSize <= j)
            {
                ItemStack itemstack = supply;
                supply = null;
                onInventoryChanged();
                return itemstack;
            }
            ItemStack itemstack1 = supply.splitStack(j);
            if (supply.stackSize == 0)
            {
                supply = null;
            }
            onInventoryChanged();
            return itemstack1;
        }
        else
        {
            return null;
        }
    }

    public void setInventorySlotContents(int i, ItemStack itemstack)
    {
        supply = itemstack;
        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit())
        {
            itemstack.stackSize = getInventoryStackLimit();
        }
        if (supply != null && supply.getItem() == null)
        {
            supply = null;
        }
        onInventoryChanged();
    }

    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
        if (supply == null)
        {
            supply = new ItemStack(0, 0, 0);
        }
        NBTTagList nbttaglist = new NBTTagList();
        if (supply != null)
        {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            nbttagcompound1.setByte("Slot", (byte)0);
            supply.writeToNBT(nbttagcompound1);
            nbttaglist.appendTag(nbttagcompound1);
        }
        nbttagcompound.setTag("Items", nbttaglist);
    }

    public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
        supply = new ItemStack(0, 0, 0);
        NBTTagList nbttaglist = nbttagcompound.getTagList("Items");
        for (int i = 0; i < nbttaglist.tagCount(); i++)
        {
            NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
            int j = nbttagcompound1.getByte("Slot") & 0xff;
            if (j >= 0 && j < 1)
            {
                supply = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }

        if (supply != null && supply.getItem() == null)
        {
            supply = null;
        }
    }

    public String getInvName()
    {
        return "golempedestalinv";
    }

    public int getInventoryStackLimit()
    {
        return 64;
    }

    public boolean canInteractWith(EntityPlayer entityplayer)
    {
        return true;
    }

    public void openChest()
    {
    }

    public void closeChest()
    {
    }

    public void clear()
    {
        supply = null;
    }

    public boolean isUseableByPlayer(EntityPlayer entityplayer)
    {
        return true;
    }

	@Override
	public ItemStack getStackInSlotOnClosing (int i)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isInvNameLocalized ()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStackValidForSlot (int i, ItemStack itemstack)
	{
		// TODO Auto-generated method stub
		return false;
	}
}

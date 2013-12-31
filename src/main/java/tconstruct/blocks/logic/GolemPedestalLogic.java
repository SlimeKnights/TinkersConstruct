package tconstruct.blocks.logic;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import tconstruct.TConstruct;

public class GolemPedestalLogic extends TileEntity implements IInventory
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
            cost[Blocks.planks] = 0;
            cost[Blocks.wood] = 32;
            cost[Blocks.cloth] = 8;
            cost[Blocks.cobblestoneMossy] = 16;
            cost[Blocks.obsidian] = 8;
            cost[Blocks.pumpkin] = 8;
            cost[Blocks.netherrack] = 64;
            cost[Blocks.slowSand] = 1;
            cost[Blocks.glowStone] = 1;
            cost[Blocks.pumpkinLantern] = 8;
            cost[Blocks.blockDiamond] = 1;
            cost[Blocks.blockGold] = 1;
            cost[Blocks.blockIron] = 1;
            cost[Blocks.blockLapis] = 1;
            cost[Blocks.jukebox] = 1;
            cost[Blocks.music] = 1;
            cost[Blocks.melon] = 8;
            cost[Blocks.torchRedstoneIdle] = 6;
            cost[Blocks.torchRedstoneActive] = cost[Blocks.torchRedstoneIdle];
            cost[Items.redstone] = 6;
            cost[Items.beefRaw] = 8;
            cost[Items.beefCooked] = cost[Items.beefRaw];
            cost[Items.porkRaw] = cost[Items.beefRaw];
            cost[Items.porkCooked] = cost[Items.beefRaw];
            cost[Items.enderPearl] = 1;
            cost[Items.eyeOfEnder] = 1;
            cost[Items.blazeRod] = 2;
            cost[Blocks.mushroomBrown] = 16;
            cost[Blocks.mushroomRed] = cost[Blocks.mushroomBrown];
            cost[Blocks.netherStalk] = 6;
            cost[Items.glowstone] = 4;
            cost[Items.diamond] = 1;
            cost[Items.ingotIron] = 8;
            cost[Items.ingotGold] = 2;
            cost[Items.reed] = 8;
            cost[Items.sugar] = cost[Items.reed];
            cost[Items.bucketLava] = 1;
            cost[Items.cake] = 1;
            cost[Items.bucketMilk] = 1;
            cost[Items.wheat] = 32;
            cost[Items.bread] = 8;
            cost[Blocks.plantYellow] = 32;
            cost[Blocks.plantRed] = cost[Blocks.plantYellow];
            cost[Blocks.dragonEgg] = 1;
            cost[Blocks.tallGrass] = 64;
            cost[Blocks.sapling] = 64;
            cost[Items.ghastTear] = 8;
            cost[Items.goldNugget] = 18;
            cost[Items.spiderEye] = 16;
            cost[Items.fermentedSpiderEye] = 6;
            cost[Items.bowlSoup] = 0;
            cost[Items.fishRaw] = 1;
            cost[Items.fishCooked] = cost[Items.fishRaw];
            cost[Items.magmaCream] = 2;
            cost[Items.blazePowder] = 4;
            cost[Items.speckledMelon] = 1;
            cost[Items.paper] = 8;
            cost[Items.book] = 2;
            cost[Items.egg] = 4;
            cost[Items.slimeBall] = 2;
            cost[Items.saddle] = 1;
            cost[Items.feather] = 48;
            cost[Items.gunpowder] = 16;
            cost[Items.appleRed] = 8;
            cost[Items.appleGold] = 1;
            cost[Items.dyePowder] = 64;
            cost[Items.bone] = 2;
            cost[Items.rottenFlesh] = 48;
            cost[Items.cookie] = 16;
            cost[Items.melon] = 32;
            cost[Items.chickenCooked] = 8;
            cost[Items.chickenRaw] = cost[Items.chickenCooked];
            cost[Items.silk] = 64;
            cost[Blocks.cactus] = 32;
            souls[Blocks.planks] = 0;
            souls[Blocks.wood] = 1;
            souls[Blocks.cloth] = 1;
            souls[Blocks.cobblestoneMossy] = 1;
            souls[Blocks.obsidian] = 1;
            souls[Blocks.pumpkin] = 1;
            souls[Blocks.netherrack] = 1;
            souls[Blocks.slowSand] = 1;
            souls[Blocks.glowStone] = 2;
            souls[Blocks.pumpkinLantern] = 1;
            souls[Blocks.blockDiamond] = 27;
            souls[Blocks.blockGold] = 2;
            souls[Blocks.blockIron] = 2;
            souls[Blocks.blockLapis] = 3;
            souls[Blocks.jukebox] = 3;
            souls[Blocks.music] = 1;
            souls[Blocks.melon] = 1;
            souls[Blocks.torchRedstoneIdle] = 1;
            souls[Blocks.torchRedstoneActive] = souls[Blocks.torchRedstoneIdle];
            souls[Items.redstone] = 1;
            souls[Items.beefRaw] = 1;
            souls[Items.beefCooked] = souls[Items.beefRaw];
            souls[Items.porkRaw] = souls[Items.beefRaw];
            souls[Items.porkCooked] = souls[Items.beefRaw];
            souls[Items.enderPearl] = 1;
            souls[Items.eyeOfEnder] = 2;
            souls[Items.blazeRod] = 2;
            souls[Blocks.mushroomBrown] = 1;
            souls[Blocks.mushroomRed] = souls[Blocks.mushroomBrown];
            souls[Blocks.netherStalk] = 1;
            souls[Items.glowstone] = 1;
            souls[Items.diamond] = 3;
            souls[Items.ingotIron] = 1;
            souls[Items.ingotGold] = 1;
            souls[Items.reed] = 1;
            souls[Items.sugar] = souls[Items.reed];
            souls[Items.bucketLava] = 1;
            souls[Items.cake] = 1;
            souls[Items.bucketMilk] = 1;
            souls[Items.wheat] = 1;
            souls[Items.bread] = 1;
            souls[Blocks.plantYellow] = 1;
            souls[Blocks.plantRed] = souls[Blocks.plantYellow];
            souls[Blocks.dragonEgg] = 1;
            souls[Blocks.tallGrass] = 1;
            souls[Blocks.sapling] = 1;
            souls[Items.ghastTear] = 1;
            souls[Items.goldNugget] = 1;
            souls[Items.spiderEye] = 1;
            souls[Items.fermentedSpiderEye] = 1;
            souls[Items.bowlSoup] = 1;
            souls[Items.fishRaw] = 1;
            souls[Items.fishCooked] = souls[Items.fishRaw];
            souls[Items.magmaCream] = 1;
            souls[Items.blazePowder] = 1;
            souls[Items.speckledMelon] = 1;
            souls[Items.paper] = 1;
            souls[Items.book] = 1;
            souls[Items.egg] = 1;
            souls[Items.slimeBall] = 1;
            souls[Items.saddle] = 1;
            souls[Items.feather] = 1;
            souls[Items.gunpowder] = 1;
            souls[Items.appleRed] = 1;
            souls[Items.appleGold] = 3;
            souls[Items.dyePowder] = 1;
            souls[Items.bone] = 1;
            souls[Items.rottenFlesh] = 1;
            souls[Items.cookie] = 1;
            souls[Items.melon] = 1;
            souls[Items.chickenCooked] = 1;
            souls[Items.chickenRaw] = souls[Items.chickenCooked];
            souls[Items.silk] = 1;
            souls[Blocks.cactus] = 1;
        }
    }

    public int getSizeInventory ()
    {
        return 9;
    }

    public ItemStack getStackInSlot (int i)
    {
        if (supply != null && supply.getItem() == null)
        {
            supply = null;
        }
        return supply;
    }

    public boolean subtractSoul (int i)
    {
        TConstruct.logger.info("SUBTRACT WHY");
        if (supply == null || supply.itemID >= 512 || cost[supply] == 0)
        {
            worldObj.playSoundAtEntity(Minecraft.getMinecraft().thePlayer, "mob.blaze.death", 1.0F, 0.5F);
            Minecraft.getMinecraft().thePlayer.addChatMessage("I require more souls...");
            return false;
        }
        int j = cost[supply];
        int k = souls[supply];
        int l = (supply.stackSize / j) * k;
        TConstruct.logger.info((new StringBuilder()).append("Subtracting amt").append(i).append(" s").append(k).append(" c").append(j).append(" ss").append(supply.stackSize).append(" id")
                .append(supply.itemID).append(" sa").append(l).append(" calc").append(Math.ceil((double) i / (double) k)).toString());
        if (l < i)
        {
            worldObj.playSoundAtEntity(Minecraft.getMinecraft().thePlayer, "mob.blaze.death", 1.0F, 0.5F);
            Minecraft.getMinecraft().thePlayer.addChatMessage("I need more souls...");
            return false;
        }
        supply.stackSize -= Math.ceil((double) i / (double) k) * (double) j;
        if (supply.stackSize == 0)
        {
            supply = null;
        }
        else if (supply.stackSize < 0)
        {
            throw new UnsupportedOperationException((new StringBuilder())
                    .append("If you are getting this error then your golems mod has derped, \nplease contact billythegoat101 on the minecraftforums with the error code: LERN YO MATHS [i")
                    .append(supply.itemID).append(",ss").append(supply.stackSize).append(",a").append(i).append("]").toString());
        }
        return true;
    }

    public ItemStack decrStackSize (int i, int j)
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

    public void setInventorySlotContents (int i, ItemStack itemstack)
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

    public void writeToNBT (NBTTagCompound nbttagcompound)
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
            nbttagcompound1.setByte("Slot", (byte) 0);
            supply.writeToNBT(nbttagcompound1);
            nbttaglist.appendTag(nbttagcompound1);
        }
        nbttagcompound.setTag("Items", nbttaglist);
    }

    public void readFromNBT (NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
        supply = new ItemStack(0, 0, 0);
        NBTTagList nbttaglist = nbttagcompound.getTagList("Items");
        for (int i = 0; i < nbttaglist.tagCount(); i++)
        {
            NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.tagAt(i);
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

    public String getInvName ()
    {
        return "golempedestalinv";
    }

    public int getInventoryStackLimit ()
    {
        return 64;
    }

    public boolean canInteractWith (EntityPlayer entityplayer)
    {
        return true;
    }

    public void openChest ()
    {
    }

    public void closeChest ()
    {
    }

    public void clear ()
    {
        supply = null;
    }

    public boolean isUseableByPlayer (EntityPlayer entityplayer)
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
    public boolean isItemValidForSlot (int i, ItemStack itemstack)
    {
        // TODO Auto-generated method stub
        return false;
    }
}

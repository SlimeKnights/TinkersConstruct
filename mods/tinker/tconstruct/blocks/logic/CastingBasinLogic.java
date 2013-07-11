package mods.tinker.tconstruct.blocks.logic;

import mods.tinker.tconstruct.TConstruct;
import mods.tinker.tconstruct.library.blocks.InventoryLogic;
import mods.tinker.tconstruct.library.crafting.CastingRecipe;
import mods.tinker.tconstruct.library.util.IPattern;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidEvent;
import net.minecraftforge.liquids.LiquidStack;

public class CastingBasinLogic extends InventoryLogic implements ILiquidTank, ITankContainer, ISidedInventory
{
    public LiquidStack liquid;
    int castingDelay = 0;
    int renderOffset = 0;
    int capacity = 0;
    boolean needsUpdate;
    boolean init = true;
    int tick;

    public CastingBasinLogic()
    {
        super(2);
    }

    @Override
    public int getInventoryStackLimit ()
    {
        return 1;
    }

    @Override
    public String getInvName () //Not a gui block
    {
        return null;
    }

    @Override
    protected String getDefaultName () //Still not a gui block
    {
        return null;
    }

    @Override
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z) //Definitely not a gui block
    {
        return null;
    }

    /* Tank */
    @Override
    public LiquidStack getLiquid ()
    {
        return this.liquid;
    }

    @Override
    public int getCapacity ()
    {
        return this.capacity;
    }

    public int updateCapacity () //Only used to initialize
    {
        int ret = TConstruct.ingotLiquidValue;

        ItemStack inv = inventory[0];

        if (inv != null && inv.getItem() instanceof IPattern)
            ret *= ((IPattern) inv.getItem()).getPatternCost(inv) * 0.5;

        else
            ret = TConstruct.basinCasting.getCastingAmount(this.liquid, inv);

        return ret;
    }

    public int updateCapacity (int capacity)
    {
        int ret = TConstruct.ingotLiquidValue;

        ItemStack inv = inventory[0];

        if (inv != null && inv.getItem() instanceof IPattern)
            ret *= ((IPattern) inv.getItem()).getPatternCost(inv) * 0.5;

        else
            ret = capacity;

        return ret;
    }

    @Override
    public int fill (LiquidStack resource, boolean doFill)
    {
        if (resource == null)
            return 0;

        if (this.liquid == null)
        {
            CastingRecipe recipe = TConstruct.basinCasting.getCastingRecipe(resource, inventory[0]);
            if (recipe == null)
                return 0;
            this.capacity = updateCapacity(recipe.castingMetal.amount);

            if (inventory[1] == null)
            {
                LiquidStack copyLiquid = resource.copy();

                if (copyLiquid.amount > this.capacity)
                {
                    copyLiquid.amount = this.capacity;
                }

                if (doFill)
                {
                    if (copyLiquid.amount == this.capacity)
                    {
                        castingDelay = recipe.coolTime;
                    }
                    renderOffset = copyLiquid.amount;
                    worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
                    this.liquid = copyLiquid;
                    needsUpdate = true;
                }
                return copyLiquid.amount;
            }
            else
            {
                return 0;
            }
        }

        else if (resource.isLiquidEqual(this.liquid))
        {
            if (resource.amount + this.liquid.amount >= this.capacity) //Start timer here
            {
                int roomInTank = this.capacity - liquid.amount;
                if (doFill && roomInTank > 0)
                {
                    renderOffset = roomInTank;
                    castingDelay = TConstruct.basinCasting.getCastingDelay(this.liquid, inventory[0]);
                    this.liquid.amount = this.capacity;
                    worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
                    needsUpdate = true;
                }
                return roomInTank;
            }

            else
            {
                if (doFill)
                {
                    this.liquid.amount += resource.amount;
                    worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
                    needsUpdate = true;
                }
                return resource.amount;
            }
        }

        else
        {
            return 0;
        }
    }

    @Override
    public void onInventoryChanged () //Isn't actually called?
    {
        super.onInventoryChanged();
        worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
        needsUpdate = true;
    }

    public ItemStack decrStackSize (int slot, int quantity)
    {
        ItemStack stack = super.decrStackSize(slot, quantity);
        worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
        return stack;
    }

    @Override
    public LiquidStack drain (int maxDrain, boolean doDrain)
    {
        if (liquid == null || liquid.itemID <= 0 || castingDelay > 0)
            return null;
        if (liquid.amount <= 0)
            return null;

        int used = maxDrain;
        if (liquid.amount < used)
            used = liquid.amount;

        if (doDrain)
        {
            liquid.amount -= used;
        }

        LiquidStack drained = new LiquidStack(liquid.itemID, used, liquid.itemMeta);

        // Reset liquid if emptied
        if (liquid.amount <= 0)
            liquid = null;

        if (doDrain)
            LiquidEvent.fireEvent(new LiquidEvent.LiquidDrainingEvent(drained, this.worldObj, this.xCoord, this.yCoord, this.zCoord, this));

        return drained;
    }

    @Override
    public int getTankPressure ()
    {
        return 0;
    }

    /* Tank Container */

    @Override
    public int fill (ForgeDirection from, LiquidStack resource, boolean doFill)
    {
        //if (from == ForgeDirection.UP)
        return fill(0, resource, doFill);
        //return 0;
    }

    @Override
    public int fill (int tankIndex, LiquidStack resource, boolean doFill)
    {
        return fill(resource, doFill);
    }

    @Override
    public LiquidStack drain (ForgeDirection from, int maxDrain, boolean doDrain)
    {
        return drain(0, maxDrain, doDrain);
    }

    @Override
    public LiquidStack drain (int tankIndex, int maxDrain, boolean doDrain)
    {
        return drain(maxDrain, doDrain);
    }

    @Override
    public ILiquidTank[] getTanks (ForgeDirection direction)
    {
        return new ILiquidTank[] { this };
    }

    @Override
    public ILiquidTank getTank (ForgeDirection direction, LiquidStack type)
    {
        return this;
    }

    public int getLiquidAmount ()
    {
        return liquid.amount - renderOffset;
    }

    /* Updating */
    @Override
    public void updateEntity ()
    {
        if (castingDelay > 0)
        {
            //System.out.println("Casting");
            castingDelay--;
            if (castingDelay == 0)
                castLiquid();
        }
        if (renderOffset > 0)
        {
            renderOffset -= 6;
            worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
        }

        tick++;
        if (tick % 20 == 0)
        {
            tick = 0;
            if (needsUpdate)
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    public void castLiquid ()
    {
        CastingRecipe recipe = TConstruct.basinCasting.getCastingRecipe(liquid, inventory[0]);
        if (recipe != null)
        {
            inventory[1] = recipe.getResult();
            if (recipe.consumeCast)
                inventory[0] = null;
            liquid = null;
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    /* NBT */

    @Override
    public void readFromNBT (NBTTagCompound tags)
    {
        super.readFromNBT(tags);
        readCustomNBT(tags);
    }

    public void readCustomNBT (NBTTagCompound tags)
    {
        if (tags.getBoolean("hasLiquid"))
            this.liquid = new LiquidStack(tags.getInteger("itemID"), tags.getInteger("amount"), tags.getInteger("itemMeta"));
        else
            this.liquid = null;

        if (tags.getBoolean("Initialized"))
            this.capacity = tags.getInteger("Capacity");
        else
            this.capacity = updateCapacity();
    }

    @Override
    public void writeToNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);
        writeCustomNBT(tags);
    }

    public void writeCustomNBT (NBTTagCompound tags)
    {
        tags.setBoolean("hasLiquid", liquid != null);
        if (liquid != null)
        {
            tags.setInteger("itemID", liquid.itemID);
            tags.setInteger("amount", liquid.amount);
            tags.setInteger("itemMeta", liquid.itemMeta);
        }
        tags.setBoolean("Initialized", init);
        tags.setInteger("Capacity", capacity);
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
    public int[] getAccessibleSlotsFromSide (int side)
    {
        return new int[] { 0, 1 };
    }

    @Override
    public boolean canInsertItem (int slot, ItemStack itemstack, int side)
    {
        if (liquid != null)
            return false;

        if (slot == 0)
            return true;

        return false;
    }

    @Override
    public boolean canExtractItem (int slot, ItemStack itemstack, int side)
    {
        if (slot == 1)
            return true;

        return false;
    }
}

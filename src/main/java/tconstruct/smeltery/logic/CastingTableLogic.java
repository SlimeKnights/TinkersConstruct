package tconstruct.smeltery.logic;

import mantle.blocks.abstracts.InventoryLogic;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import tconstruct.TConstruct;
import tconstruct.library.crafting.CastingRecipe;
import tconstruct.library.util.IPattern;

public class CastingTableLogic extends InventoryLogic implements IFluidTank, IFluidHandler, ISidedInventory
{
    public FluidStack liquid;
    int castingDelay = 0;
    int renderOffset = 0;
    int capacity = 0;
    boolean needsUpdate;
    boolean init = true;
    int tick;

    public CastingTableLogic()
    {
        super(2);
    }

    @Override
    public int getInventoryStackLimit ()
    {
        return 1;
    }

    @Override
    public String getInvName () // Not a gui block
    {
        return null;
    }

    @Override
    protected String getDefaultName () // Still not a gui block
    {
        return null;
    }

    @Override
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z) // Definitely not a gui block
    {
        return null;
    }

    /* Tank */

    @Override
    public int getCapacity ()
    {
        return this.capacity;
    }

    public int updateCapacity () // Only used to initialize
    {
        ItemStack inv = inventory[0];
        int ret = TConstruct.ingotLiquidValue;
        int rec = TConstruct.tableCasting.getCastingAmount(this.liquid, inv);

        if (rec > 0)
            ret = rec;
        else {
            if (inv != null && inv.getItem() instanceof IPattern)
            {
                int cost = ((IPattern) inv.getItem()).getPatternCost(inv);
                if (cost > 0)
                    ret *= ((IPattern) inv.getItem()).getPatternCost(inv) * 0.5;
            }
        }

        TConstruct.logger.info("Ret: " + ret);
        return ret;
    }

    public int updateCapacity (int capacity)
    {
        int ret = TConstruct.ingotLiquidValue;

        if (capacity > 0)
            ret = capacity;
        else {
            ItemStack inv = inventory[0];

            if (inv != null && inv.getItem() instanceof IPattern)
            {
                int cost = ((IPattern) inv.getItem()).getPatternCost(inv);
                if (cost > 0)
                    ret *= cost * 0.5;
            }
        }

        return ret;
    }

    @Override
    public int fill (FluidStack resource, boolean doFill)
    {
        if (resource == null)
            return 0;

        if (this.liquid == null)
        {
            CastingRecipe recipe = TConstruct.tableCasting.getCastingRecipe(resource, inventory[0]);
            if (recipe == null)
                return 0;
            this.capacity = updateCapacity(recipe.castingMetal.amount);

            if (inventory[1] == null)
            {
                FluidStack copyLiquid = resource.copy();

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
                    worldObj.func_147479_m(xCoord, yCoord, zCoord);
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

        else if (resource.isFluidEqual(this.liquid))
        {
            if (resource.amount + this.liquid.amount >= this.capacity) // Start
                                                                       // timer
                                                                       // here
            {
                int roomInTank = this.capacity - liquid.amount;
                if (doFill && roomInTank > 0)
                {
                    renderOffset = roomInTank;
                    castingDelay = TConstruct.tableCasting.getCastingDelay(this.liquid, inventory[0]);
                    this.liquid.amount = this.capacity;
                    worldObj.func_147479_m(xCoord, yCoord, zCoord);
                    needsUpdate = true;
                }
                return roomInTank;
            }

            else
            {
                if (doFill)
                {
                    this.liquid.amount += resource.amount;
                    worldObj.func_147479_m(xCoord, yCoord, zCoord);
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
    public void markDirty () // Isn't actually called?
    {
        super.markDirty();
        worldObj.func_147479_m(xCoord, yCoord, zCoord);
        needsUpdate = true;
    }

    @Override
    public ItemStack decrStackSize (int slot, int quantity)
    {
        ItemStack stack = super.decrStackSize(slot, quantity);
        worldObj.func_147479_m(xCoord, yCoord, zCoord);
        return stack;
    }

    @Override
    public FluidStack drain (int maxDrain, boolean doDrain)
    {
        if (liquid == null || liquid.fluidID <= 0 || castingDelay > 0)
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

        FluidStack drained = liquid.copy();// new FluidStack(liquid.itemID,
                                           // used, liquid.itemMeta);
        drained.amount = used;

        // Reset liquid if emptied
        if (liquid.amount <= 0)
            liquid = null;

        if (doDrain)
            FluidEvent.fireEvent(new FluidEvent.FluidDrainingEvent(drained, this.worldObj, this.xCoord, this.yCoord, this.zCoord, this));

        return drained;
    }

    /* Tank Container */

    @Override
    public int fill (ForgeDirection from, FluidStack resource, boolean doFill)
    {
        // if (from == ForgeDirection.UP)
        return fill(resource, doFill);
        // return 0;
    }

    @Override
    public FluidStack drain (ForgeDirection from, int maxDrain, boolean doDrain)
    {
        return drain(maxDrain, doDrain);
    }

    @Override
    public FluidStack drain (ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        return null;
    }

    @Override
    public boolean canFill (ForgeDirection from, Fluid fluid)
    {
        return false;
    }

    @Override
    public boolean canDrain (ForgeDirection from, Fluid fluid)
    {
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo (ForgeDirection from)
    {
        return new FluidTankInfo[] { getInfo() };
    }

    @Override
    public FluidStack getFluid ()
    {
        return liquid == null ? null : liquid.copy();
    }

    @Override
    public int getFluidAmount ()
    {
        return liquid != null ? liquid.amount : 0;
    }

    @Override
    public FluidTankInfo getInfo ()
    {
        FluidTankInfo info = new FluidTankInfo(this);
        return info;
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
            // TConstruct.logger.info("Casting");
            castingDelay--;
            if (castingDelay == 0)
                castLiquid();
        }
        if (renderOffset > 0)
        {
            renderOffset -= 6;
            worldObj.func_147479_m(xCoord, yCoord, zCoord);
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
        CastingRecipe recipe = TConstruct.tableCasting.getCastingRecipe(liquid, inventory[0]);
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
        {
            this.liquid = FluidStack.loadFluidStackFromNBT(tags.getCompoundTag("Fluid"));
        }
        else
            this.liquid = null;

        if (tags.getBoolean("Initialized"))
            this.capacity = tags.getInteger("Capacity");
        else
            this.capacity = updateCapacity();
        this.castingDelay = tags.getInteger("castingDelay");
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
            NBTTagCompound nbt = new NBTTagCompound();
            liquid.writeToNBT(nbt);
            tags.setTag("Fluid", nbt);
        }
        tags.setBoolean("Initialized", init);
        tags.setInteger("Capacity", capacity);
        tags.setInteger("castingDelay", castingDelay);
    }

    /* Packets */
    @Override
    public Packet getDescriptionPacket ()
    {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket (NetworkManager net, S35PacketUpdateTileEntity packet)
    {
        readFromNBT(packet.func_148857_g());
        worldObj.func_147479_m(xCoord, yCoord, zCoord);
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

    @Override
    public String getInventoryName ()
    {
        return null;
    }

    @Override
    public boolean hasCustomInventoryName ()
    {
        return false;
    }

    @Override
    public void closeInventory ()
    {

    }

    @Override
    public void openInventory ()
    {

    }
}
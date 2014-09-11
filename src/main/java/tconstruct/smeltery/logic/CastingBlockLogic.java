package tconstruct.smeltery.logic;

import cpw.mods.fml.common.eventhandler.Event;
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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import tconstruct.TConstruct;
import tconstruct.library.crafting.CastingRecipe;
import tconstruct.library.crafting.LiquidCasting;
import tconstruct.library.event.SmelteryCastEvent;
import tconstruct.library.event.SmelteryCastedEvent;
import tconstruct.library.util.IPattern;

public abstract class CastingBlockLogic extends InventoryLogic implements IFluidTank, IFluidHandler, ISidedInventory {
    public FluidStack liquid;
    protected int castingDelay = 0;
    protected int renderOffset = 0;
    protected int capacity = 0;
    protected boolean needsUpdate;
    protected boolean init = true;
    protected int tick;
    protected final LiquidCasting liquidCasting;

    public CastingBlockLogic(LiquidCasting casting) {
        // input slot and output slot, 1 item in it max
        super(2, 1);
        this.liquidCasting = casting;
    }

    public int updateCapacity () // Only used to initialize
    {
        ItemStack inv = inventory[0];
        int ret = TConstruct.ingotLiquidValue;
        int rec = liquidCasting.getCastingAmount(this.liquid, inv);

        if (rec > 0)
            ret = rec;
        else
        {
            if (inv != null && inv.getItem() instanceof IPattern)
            {
                int cost = ((IPattern) inv.getItem()).getPatternCost(inv);
                if (cost > 0)
                    ret *= ((IPattern) inv.getItem()).getPatternCost(inv) * 0.5;
            }
        }

        return ret;
    }

    public int updateCapacity (int capacity)
    {
        int ret = TConstruct.ingotLiquidValue;

        if (capacity > 0)
            ret = capacity;
        else
        {
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

    /* FluidHandler stuff. Mostly delegated to Tank stuff */
    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return fill(resource, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        // only same liquid
        if (liquid.getFluid() != resource.getFluid())
            return null;

        return drain(resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return drain(maxDrain, doDrain);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return fill(from, new FluidStack(fluid, 1), false) > 0;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        FluidStack drained = drain(from, new FluidStack(fluid, 1), false);
        return drained != null && drained.amount > 0;
    }

    /* Tank stuff */
    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[] { getInfo() };
    }

    @Override
    public FluidStack getFluid() {
        return liquid == null ? null : liquid.copy();
    }

    @Override
    public int getFluidAmount() {
        return liquid != null ? liquid.amount : 0;
    }

    public int getLiquidAmount ()
    {
        return liquid.amount - renderOffset;
    }

    @Override
    public int getCapacity() {
        return this.capacity;
    }

    @Override
    public FluidTankInfo getInfo() {
        return new FluidTankInfo(this);
    }

    /**
     * Create and return the casting event here. It'll be fired automatically.
     */
    public abstract SmelteryCastEvent getCastingEvent(CastingRecipe recipe, FluidStack metal);

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (resource == null)
            return 0;

        if (this.liquid == null)
        {
            CastingRecipe recipe = liquidCasting.getCastingRecipe(resource, inventory[0]);
            if (recipe == null)
                return 0;

            SmelteryCastEvent event = getCastingEvent(recipe, resource);
            MinecraftForge.EVENT_BUS.post(event);

            if (event.getResult() == Event.Result.DENY)
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
            if (resource.amount + this.liquid.amount >= this.capacity) // Start timer here
            {
                int roomInTank = this.capacity - liquid.amount;
                if (doFill && roomInTank > 0)
                {
                    renderOffset = roomInTank;
                    castingDelay = liquidCasting.getCastingDelay(this.liquid, inventory[0]);
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
    public FluidStack drain(int maxDrain, boolean doDrain) {
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

        FluidStack drained = liquid.copy();
        drained.amount = used;

        // Reset liquid if emptied
        if (liquid.amount <= 0)
            liquid = null;

        if (doDrain)
            FluidEvent.fireEvent(new FluidEvent.FluidDrainingEvent(drained, this.worldObj, this.xCoord, this.yCoord, this.zCoord, this, used));

        return drained;
    }

    /* Inventory, inserting/extracting */

    @Override
    public ItemStack decrStackSize (int slot, int quantity)
    {
        ItemStack stack = super.decrStackSize(slot, quantity);
        worldObj.func_147479_m(xCoord, yCoord, zCoord);
        return stack;
    }

    @Override
    public int[] getAccessibleSlotsFromSide (int side)
    {
        return new int[] { 0, 1 };
    }

    @Override
    public boolean canInsertItem (int slot, ItemStack itemstack, int side) {
        // can't insert if there's liquid in it
        if (liquid != null)
            return false;

        // only into input slot
        return slot == 0;
    }

    @Override
    public boolean canExtractItem (int slot, ItemStack itemstack, int side) {
        // only output slot
        return slot == 1;
    }

    /* We don't have a gui or anything */
    @Override
    public Container getGuiContainer(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        return null;
    }

    @Override
    protected String getDefaultName() {
        return null;
    }

    @Override
    public String getInventoryName() {
        return null;
    }

    @Override
    public String getInvName() {
        return null;
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    /* NBT, Updating */
    @Override
    public void markDirty () // Isn't actually called?
    {
        super.markDirty();
        worldObj.func_147479_m(xCoord, yCoord, zCoord);
        needsUpdate = true;
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

    /**
     * Create and return the casting event here. It'll be fired automatically.
     */
    public abstract SmelteryCastedEvent getCastedEvent(CastingRecipe recipe, ItemStack result);

    public void castLiquid ()
    {
        CastingRecipe recipe = liquidCasting.getCastingRecipe(liquid, inventory[0]);
        if (recipe != null)
        {
            SmelteryCastedEvent event = getCastedEvent(recipe, recipe.getResult());
            MinecraftForge.EVENT_BUS.post(event);

            inventory[1] = event.output;
            if (event.consumeCast)
                inventory[0] = null;

            // if we just created a cast, move it to the first slot so we can use it directly afterwards
            if (event.output != null && event.output.getItem() instanceof IPattern)
            {
                inventory[1] = inventory[0];
                inventory[0] = event.output;
            }

            liquid = null;
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
}

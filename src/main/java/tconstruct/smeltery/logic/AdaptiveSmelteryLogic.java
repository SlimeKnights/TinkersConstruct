package tconstruct.smeltery.logic;

import java.util.*;
import mantle.blocks.abstracts.AdaptiveInventoryLogic;
import mantle.blocks.iface.*;
import mantle.world.CoordTuple;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.*;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import tconstruct.TConstruct;
import tconstruct.library.component.*;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.smeltery.component.*;
import tconstruct.smeltery.inventory.AdaptiveSmelteryContainer;

public class AdaptiveSmelteryLogic extends AdaptiveInventoryLogic implements IActiveLogic, IMasterLogic, IComponentHolder, IFluidHandler
{
    byte direction;
    boolean updateFluids = false;
    boolean recheckStructure = false;
    boolean updateAir = false;
    SmelteryScan structure = new SmelteryScan(this, TinkerSmeltery.smeltery, TinkerSmeltery.lavaTank);
    MultiFluidTank multitank = new MultiFluidTank();
    SmelteryComponent smeltery = new SmelteryComponent(this, structure, multitank, 800);
    HashMap<CoordTuple, LiquidDataInstance> airUpdates = new HashMap<CoordTuple, LiquidDataInstance>();
    int tick = 0;

    public MultiFluidTank getMultiTank ()
    {
        return multitank;
    }

    public SmelteryComponent getSmeltery ()
    {
        return smeltery;
    }

    @Override
    public void updateEntity ()
    {
        tick++;
        if (tick % 4 == 0)
            smeltery.heatItems();

        if (!worldObj.isRemote)
        {
            if (tick % 20 == 0)
            {
                if (structure.isComplete())
                    smeltery.update();

                if (updateFluids)
                {
                    distributeFluids();
                    updateFluids = false;
                }

                if (updateAir)
                {
                    updateAir();
                    updateAir = false;
                }

                if (recheckStructure)
                {
                    structure.recheckStructure();
                    recheckStructure = false;
                }
            }

            if (tick >= 60)
            {
                if (!structure.isComplete())
                {
                    structure.checkValidStructure();
                    if (structure.isComplete())
                    {
                        validateSmeltery();
                    }
                }
                tick = 0;
            }

            if (airUpdates.size() > 0)
                updateFluidBlocks();
        }
    }

    public void setUpdateFluids ()
    {
        updateFluids = true;
    }

    @Override
    public List<LogicComponent> getComponents ()
    {
        ArrayList<LogicComponent> ret = new ArrayList<LogicComponent>(3);
        ret.add(structure);
        ret.add(multitank);
        ret.add(smeltery);
        return ret;
    }

    /* Structure */

    @Override
    public void setWorldObj (World world)
    {
        super.setWorldObj(world);
        structure.setWorld(world);
        smeltery.setWorld(world);
    }

    @Override
    public void notifyChange (IServantLogic servant, int x, int y, int z)
    {
        if (!worldObj.isRemote)
        {
            // System.out.println("Notifying of change from "+new CoordTuple(x,
            // y, z));
            recheckStructure = true;
        }
    }

    @Override
    public void placeBlock (EntityLivingBase entity, ItemStack itemstack)
    {
        structure.checkValidStructure();
        if (structure.isComplete())
        {
            validateSmeltery();
        }
    }

    void validateSmeltery ()
    {
        adjustInventory(structure.getAirSize(), true);
        smeltery.adjustSize(structure.getAirSize(), true);
        multitank.setCapacity(structure.getAirSize() * (TConstruct.ingotLiquidValue * 18));
        smeltery.setActiveLavaTank(structure.lavaTanks.get(0));
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void removeBlock ()
    {
        structure.cleanup();
    }

    /* Direction */

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
    public void setDirection (int side)
    {

    }

    @Override
    public void setDirection (float yaw, float pitch, EntityLivingBase player)
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
        return structure.isComplete();
    }

    @Override
    public void setActive (boolean flag)
    {

    }

    /* Inventory */

    @Override
    public int getInventoryStackLimit ()
    {
        return 1;
    }

    @Override
    public void setInventorySlotContents (int slot, ItemStack itemstack)
    {
        inventory[slot] = itemstack;
        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit())
        {
            itemstack.stackSize = getInventoryStackLimit();
        }
        updateWorldBlock(slot, itemstack);
        updateAir = true;
    }

    @Override
    public ItemStack decrStackSize (int slot, int quantity)
    {
        if (inventory[slot] != null)
        {
            if (inventory[slot].stackSize <= quantity)
            {
                ItemStack stack = inventory[slot];
                inventory[slot] = null;
                updateWorldBlock(slot, inventory[slot]);
                return stack;
            }
            ItemStack split = inventory[slot].splitStack(quantity);
            if (inventory[slot].stackSize == 0)
            {
                inventory[slot] = null;
            }

            updateWorldBlock(slot, inventory[slot]);
            return split;
        }
        else
        {
            return null;
        }
    }

    @Override
    public void markDirty ()
    {
        smeltery.updateTemperatures();
        updateAir = true;
        super.markDirty();
    }

    void updateWorldBlock (int slot, ItemStack itemstack)
    {
        CoordTuple air = structure.getAirByIndex(slot);
        if (air != null)
        {
            TileEntity te = worldObj.getTileEntity(air.x, air.y, air.z);
            if (te != null && te instanceof TankAirLogic)
            {
                ((TankAirLogic) te).setInventorySlotContents(0, itemstack);
            }
        }
    }

    static final int pixelLayer = 162;

    // Do the Monster Mash~
    public void distributeFluids ()
    {
        // Calculate liquids in each block
        int size = structure.getAirLayerSize();
        // HashMap<CoordTuple, LiquidDataInstance> blocks = new
        // HashMap<CoordTuple, LiquidDataInstance>();

        for (FluidStack fluid : multitank.fluidlist)
        {
            // Base calculations per liquid
            LiquidData data = new LiquidData(fluid.amount, size);
            int baseY = structure.airCoords.get(0).y;
            int layerSize = structure.getAirLayerSize();

            // Calculate where to distribute liquids
            for (int i = 0; i < structure.airCoords.size(); i++)
            {
                LiquidDataInstance instance;
                CoordTuple coord = structure.airCoords.get(i);
                int height = 16 * (coord.y - baseY);
                int position = i % layerSize;

                if (!airUpdates.containsKey(coord))
                {
                    instance = new LiquidDataInstance();
                    airUpdates.put(coord, instance);
                }
                else
                {
                    instance = airUpdates.get(coord);
                }

                if (instance.openLayers() > 0) // This is capable of sending
                                               // negative liquid sizes to
                                               // blocks without the stopgap
                                               // check
                {
                    if (position > data.blocksWithExtra)
                    {
                        // Calculate open layers
                        int open = data.layers - height + 1;
                        if (open < 1) // Temporary stopgap check
                            continue;
                        if (open > instance.openLayers())
                            open = instance.openLayers();

                        // Copy fluid
                        FluidStack newFluid = fluid.copy();
                        newFluid.amount = pixelLayer * open;
                        instance.addFluid(open, newFluid);

                        // Subtract from total
                        data.totalAmount -= newFluid.amount;
                        if (data.totalAmount <= 0)
                            break;
                    }
                    else if (position == data.blocksWithExtra && data.leftovers > 0)
                    {
                        // Calculate open layers
                        int open = data.layers - height + 1;
                        if (open < 1) // Temporary stopgap check
                            continue;
                        boolean full = false;
                        if (open > instance.openLayers())
                        {
                            open = instance.openLayers();
                            full = true;
                        }

                        // Copy fluid
                        FluidStack newFluid = fluid.copy();
                        newFluid.amount = pixelLayer * open;
                        if (!full)
                            newFluid.amount += data.leftovers;
                        instance.addFluid(open, newFluid);

                        // Subtract from total
                        data.totalAmount -= newFluid.amount;
                        if (data.totalAmount <= 0)
                            break;
                    }
                    else
                    {
                        // Calculate open layers
                        int open = data.layers - height;
                        if (open < 1) // Temporary stopgap check
                            continue;
                        if (open > instance.openLayers())
                            open = instance.openLayers();

                        // Copy fluid
                        FluidStack newFluid = fluid.copy();
                        newFluid.amount = pixelLayer * open;
                        instance.addFluid(open, newFluid);

                        // Subtract from total
                        data.totalAmount -= newFluid.amount;
                        if (data.totalAmount <= 0)
                            break;
                    }
                }
            }
        }

        // Distribute liquids to each block
    }

    protected void updateFluidBlocks ()
    {
        Iterator iter = airUpdates.entrySet().iterator();
        byte count = 0;
        while (iter.hasNext() && count < 40)
        {
            Map.Entry pairs = (Map.Entry) iter.next();
            CoordTuple coord = (CoordTuple) pairs.getKey();
            TileEntity te = worldObj.getTileEntity(coord.x, coord.y, coord.z);
            if (te instanceof TankAirLogic)
            {
                ((TankAirLogic) te).overrideFluids(((LiquidDataInstance) pairs.getValue()).fluids);
            }
            iter.remove();
            count++;
        }
    }

    public void updateAir ()
    {
        for (CoordTuple loc : structure.airCoords)
            worldObj.markBlockForUpdate(loc.x, loc.y, loc.z);
    }

    class LiquidData
    {
        public int totalAmount;
        public int layers;
        public int leftovers;
        public int blocksWithExtra;

        LiquidData(int amount, int blocks)
        {
            totalAmount = amount;
            int layerAmount = pixelLayer * blocks;
            layers = amount / layerAmount;
            leftovers = amount % pixelLayer;
            blocksWithExtra = (amount % layerAmount) / pixelLayer;
        }
    }

    class LiquidDataInstance
    {
        public ArrayList<FluidStack> fluids = new ArrayList<FluidStack>();
        int layers = 0;

        public int openLayers ()
        {
            return 16 - layers;
        }

        public void addFluid (int l, FluidStack fluid)
        {
            layers += l;
            fluids.add(fluid);
        }
    }

    /* Gui */

    @Override
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        return new AdaptiveSmelteryContainer(inventoryplayer, this);
    }

    public int getTempForSlot (int slot)
    {
        return smeltery.activeTemps[slot];
    }

    public int getMeltingPointForSlot (int slot)
    {
        return smeltery.meltingTemps[slot];
    }

    @Override
    public String getDefaultName ()
    {
        return "crafters.Smeltery";
    }

    /* Fluids */

    @Override
    public int fill (ForgeDirection from, FluidStack resource, boolean doFill)
    {
        return multitank.fill(resource, doFill);
    }

    @Override
    public FluidStack drain (ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        return multitank.drain(resource.amount, doDrain);
    }

    @Override
    public FluidStack drain (ForgeDirection from, int maxDrain, boolean doDrain)
    {
        return multitank.drain(maxDrain, doDrain);
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
        return multitank.getMultiTankInfo();
    }

    public int getFillState ()
    {
        return 1;
    }

    /* NBT */

    @Override
    public void readFromNBT (NBTTagCompound tags)
    {
        super.readFromNBT(tags);
        readNetworkNBT(tags);

        structure.readFromNBT(tags);
        multitank.readFromNBT(tags);
        smeltery.readFromNBT(tags);
    }

    public void readNetworkNBT (NBTTagCompound tags)
    {
        direction = tags.getByte("Direction");
        adjustInventory(tags.getInteger("InvSize"), false);
        super.readInventoryFromNBT(tags);

        structure.readNetworkNBT(tags);
        multitank.readNetworkNBT(tags);
        smeltery.readNetworkNBT(tags);
    }

    @Override
    public void writeToNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);
        writeNetworkNBT(tags);

        structure.writeToNBT(tags);
        multitank.writeToNBT(tags);
        smeltery.writeToNBT(tags);
    }

    public void writeNetworkNBT (NBTTagCompound tags)
    {
        tags.setByte("Direction", direction);
        tags.setInteger("InvSize", inventory.length);
        super.writeInventoryToNBT(tags);

        structure.writeNetworkNBT(tags);
        multitank.writeNetworkNBT(tags);
        smeltery.writeNetworkNBT(tags);
    }

    @Override
    public void onDataPacket (NetworkManager net, S35PacketUpdateTileEntity packet)
    {
        readNetworkNBT(packet.func_148857_g());
        worldObj.func_147479_m(xCoord, yCoord, zCoord);
    }

    @Override
    public Packet getDescriptionPacket ()
    {
        NBTTagCompound tag = new NBTTagCompound();
        writeNetworkNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public String getInventoryName ()
    {
        return getDefaultName();
    }

    @Override
    public boolean hasCustomInventoryName ()
    {
        return true;
    }

    @Override
    public void openInventory ()
    {
    }

    @Override
    public void closeInventory ()
    {
    }
}
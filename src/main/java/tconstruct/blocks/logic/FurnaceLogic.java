package tconstruct.blocks.logic;

import cpw.mods.fml.common.registry.GameRegistry;
import mantle.blocks.BlockUtils;
import mantle.blocks.abstracts.InventoryLogic;
import mantle.blocks.iface.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.*;
import net.minecraft.inventory.Container;
import net.minecraft.item.*;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.*;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import tconstruct.inventory.FurnaceContainer;

/* Slots
 * 0: Input
 * 1: Fuel
 * 2: Output
 * 3-16: Chest
 */

public class FurnaceLogic extends InventoryLogic implements IActiveLogic, IFacingLogic
{
    boolean active;
    public int fuel;
    public int fuelGague;
    public int progress;
    public int fuelScale = 200;
    byte direction;

    public FurnaceLogic()
    {
        super(3);
        active = false;
    }

    @Override
    public String getDefaultName ()
    {
        return "container.furnace";
    }

    @Override
    public boolean getActive ()
    {
        return active;
    }

    @Override
    public void setActive (boolean flag)
    {
        active = flag;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    /* Fuel gauge */
    public int gaugeProgressScaled (int scale)
    {
        return (progress * scale) / fuelScale;
    }

    public int gaugeFuelScaled (int scale)
    {
        if (fuelGague == 0)
        {
            fuelGague = fuel;
            if (fuelGague == 0)
            {
                fuelGague = fuelScale;
            }
        }
        return (fuel * scale) / fuelGague;
    }

    /* Item cooking */
    public void updateEntity ()
    {
        boolean burning = isBurning();
        boolean updateInventory = false;
        if (fuel <= 0 && canSmelt())
        {
            fuel = fuelGague = (int) (getItemBurnTime(inventory[1]));
            if (fuel > 0)
            {
                if (inventory[1].getItem().hasContainerItem()) //Fuel slot
                {
                    inventory[1] = new ItemStack(inventory[1].getItem().getContainerItem());
                }
                else
                {
                    inventory[1].stackSize--;
                }
                if (inventory[1].stackSize <= 0)
                {
                    inventory[1] = null;
                }
                updateInventory = true;
            }
        }
        if (isBurning() && canSmelt())
        {
            progress++;
            if (progress >= fuelScale)
            {
                progress = 0;
                cookItems();
                updateInventory = true;
            }
        }
        else
        {
            progress = 0;
        }
        if (fuel > 0)
        {
            fuel--;
        }
        if (burning != isBurning())
        {
            setActive(isBurning());
            updateInventory = true;
        }
        if (updateInventory)
        {
            onInventoryChanged();
        }
    }

    public void cookItems ()
    {
        if (this.canSmelt())
        {
            ItemStack itemstack = FurnaceRecipes.smelting().getSmeltingResult(this.inventory[0]);

            if (this.inventory[2] == null)
            {
                this.inventory[2] = itemstack.copy();
            }
            else if (this.inventory[2].isItemEqual(itemstack))
            {
                inventory[2].stackSize += itemstack.stackSize;
            }

            --this.inventory[0].stackSize;

            if (this.inventory[0].stackSize <= 0)
            {
                this.inventory[0] = null;
            }
        }

    }

    public boolean canSmelt ()
    {
        if (inventory[0] == null) //Nothing here!
            return false;
        else
        {
            ItemStack itemstack = FurnaceRecipes.smelting().getSmeltingResult(this.inventory[0]);
            if (itemstack == null)
                return false;
            if (this.inventory[2] == null)
                return true;
            if (!this.inventory[2].isItemEqual(itemstack))
                return false;
            int result = inventory[2].stackSize + itemstack.stackSize;
            return (result <= getInventoryStackLimit() && result <= itemstack.getMaxStackSize());
        }
    }

    public boolean isBurning ()
    {
        return fuel > 0;
    }

    public ItemStack getResultFor (ItemStack stack)
    {
        ItemStack result = FurnaceRecipes.smelting().getSmeltingResult(stack);
        if (result != null) //Only valid for food
            return result.copy();

        return null;
    }

    public static int getItemBurnTime (ItemStack stack)
    {
        if (stack == null)
        {
            return 0;
        }
        else
        {
            Item item = stack.getItem();

            if (stack.getItem() instanceof ItemBlock && BlockUtils.getBlockFromItem(item) != null)
            {
                Block block = BlockUtils.getBlockFromItem(item);

                if (block == Blocks.wooden_slab)
                {
                    return 150;
                }

                if (block == Blocks.log)
                {
                    return 1200;
                }

                if (block.getMaterial() == Material.wood)
                {
                    return 300;
                }

                if (block == Blocks.coal_block)
                {
                    return 16000;
                }
            }

            if (item instanceof ItemTool && ((ItemTool) item).getToolMaterialName().equals("WOOD"))
                return 200;
            if (item instanceof ItemSword && ((ItemSword) item).getToolMaterialName().equals("WOOD"))
                return 200;
            if (item instanceof ItemHoe && ((ItemHoe) item).getToolMaterialName().equals("WOOD"))
                return 200;
            if (item == Items.stick)
                return 100;
            if (item == Items.coal)
                return 1600;
            if (item == Items.lava_bucket)
                return 20000;
            if (item == new ItemStack(Blocks.sapling).getItem())
                return 100;
            if (item == Items.blaze_rod)
                return 2400;
            return GameRegistry.getFuelValue(stack);
        }
    }

    /* NBT */
    public void readFromNBT (NBTTagCompound tags)
    {
        super.readFromNBT(tags);
        active = tags.getBoolean("Active");
        fuel = tags.getInteger("Fuel");
        fuelGague = tags.getInteger("FuelGague");
        readNetworkNBT(tags);
    }

    public void writeToNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);
        tags.setBoolean("Active", active);
        tags.setInteger("Fuel", fuel);
        tags.setInteger("FuelGague", fuelGague);
        writeNetworkNBT(tags);
    }

    public void readNetworkNBT (NBTTagCompound tags)
    {
        direction = tags.getByte("Direction");
    }

    public void writeNetworkNBT (NBTTagCompound tags)
    {
        tags.setByte("Direction", direction);
    }

    /* Packets */
    @Override
    public Packet getDescriptionPacket ()
    {
        NBTTagCompound tag = new NBTTagCompound();
        writeNetworkNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket (NetworkManager net, S35PacketUpdateTileEntity packet)
    {
        readNetworkNBT(packet.func_148857_g());
        worldObj.func_147479_m(xCoord, yCoord, zCoord);
    }

    @Override
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        return new FurnaceContainer(inventoryplayer, this);
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
    @Deprecated
    public void setDirection (int side)
    {
        //Nope!
    }

    @Override
    @Deprecated
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
    public String getInventoryName ()
    {
        return getDefaultName();
    }

    @Override
    public boolean hasCustomInventoryName ()
    {
        return false;
    }
}

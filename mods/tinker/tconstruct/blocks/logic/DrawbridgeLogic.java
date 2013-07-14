package mods.tinker.tconstruct.blocks.logic;

import mods.tinker.tconstruct.inventory.DrawbridgeContainer;
import mods.tinker.tconstruct.library.TConstructRegistry;
import mods.tinker.tconstruct.library.blocks.InventoryLogic;
import mods.tinker.tconstruct.library.util.IActiveLogic;
import mods.tinker.tconstruct.library.util.IFacingLogic;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class DrawbridgeLogic extends InventoryLogic implements IFacingLogic, IActiveLogic
{
    boolean active;
    boolean working;
    int ticks;
    byte extension;
    byte direction;

    public DrawbridgeLogic()
    {
        super(2);
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
        working = true;
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
    public void setDirection (int side)
    {
    }

    @Override
    public void setDirection (float yaw, float pitch, EntityLivingBase player)
    {
        if (pitch > 45)
        {
            direction = 1;
        }
        else if (pitch < -45)
        {
            direction = 0;
        }
        else
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
    }

    @Override
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        return new DrawbridgeContainer(inventoryplayer, this);
    }

    @Override
    protected String getDefaultName ()
    {
        return "tinker.drawbridge";
    }

    @Override
    public void setInventorySlotContents (int slot, ItemStack itemstack)
    {
        super.setInventorySlotContents(slot, itemstack);
        if (slot == 1)
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public ItemStack decrStackSize (int slot, int quantity)
    {
        ItemStack stack = super.decrStackSize(slot, quantity);
        if (slot == 1)
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        return stack;
    }

    public void updateEntity ()
    {
        if (working)
        {
            ticks++;
            if (ticks == 5)
            {
                ticks = 0;
                if (active) //Placement
                {
                    if (inventory[0] != null && inventory[0].stackSize > 1 && extension < 15)
                    {
                        extension++;
                        int xPos = xCoord;
                        int yPos = yCoord;
                        int zPos = zCoord;

                        switch (direction)
                        {
                        case 0:
                            yPos -= extension;
                            break;
                        case 1:
                            yPos += extension;
                            break;
                        case 2:
                            zPos -= extension;
                            break;
                        case 3:
                            zPos += extension;
                            break;
                        case 4:
                            xPos -= extension;
                            break;
                        case 5:
                            xPos += extension;
                            break;
                        }

                        Block block = Block.blocksList[worldObj.getBlockId(xPos, yPos, zPos)];
                        if (block == null || block.isAirBlock(worldObj, xPos, yPos, zPos) || block.isBlockReplaceable(worldObj, xPos, yPos, zPos))
                        {
                            //tryExtend(worldObj, xPos, yPos, zPos, direction);
                            int blockToItem = TConstructRegistry.blockToItemMapping[inventory[0].itemID];
                            if (blockToItem == 0)
                            {
                                if (inventory[0].itemID >= 4096 || Block.blocksList[inventory[0].itemID] == null)
                                    return;
                                
                                worldObj.setBlock(xPos, yPos, zPos, inventory[0].itemID, inventory[0].getItemDamage(), 3);
                            }
                            else
                            {
                                worldObj.setBlock(xPos, yPos, zPos, blockToItem, inventory[0].getItemDamage(), 3);
                            }
                            worldObj.playSoundEffect((double) xPos + 0.5D, (double) yPos + 0.5D, (double) zPos + 0.5D, "tile.piston.out", 0.25F, worldObj.rand.nextFloat() * 0.25F + 0.6F);
                            inventory[0].stackSize--;
                        }
                        else
                        {
                            extension--;
                            working = false;
                        }
                    }

                    else
                    {
                        working = false;
                    }
                }
                else
                //Retraction
                {
                    if ((inventory[0] == null || inventory[0].stackSize < inventory[0].getMaxStackSize()) && extension > 0)
                    {
                        int xPos = xCoord;
                        int yPos = yCoord;
                        int zPos = zCoord;

                        switch (direction)
                        {
                        case 0:
                            yPos -= extension;
                            break;
                        case 1:
                            yPos += extension;
                            break;
                        case 2:
                            zPos -= extension;
                            break;
                        case 3:
                            zPos += extension;
                            break;
                        case 4:
                            xPos -= extension;
                            break;
                        case 5:
                            xPos += extension;
                            break;
                        }

                        Block block = Block.blocksList[worldObj.getBlockId(xPos, yPos, zPos)];
                        if (block != null)
                        {
                            int meta = worldObj.getBlockMetadata(xPos, yPos, zPos);
                            if (inventory[0] != null && validBlock(block) && validMetadata(block.blockID, meta))
                            {
                                worldObj.playSoundEffect((double) xPos + 0.5D, (double) yPos + 0.5D, (double) zPos + 0.5D, "tile.piston.in", 0.25F, worldObj.rand.nextFloat() * 0.15F + 0.6F);
                                worldObj.setBlock(xPos, yPos, zPos, 0);
                                inventory[0].stackSize++;
                            }
                            else
                            {
                                working = false;
                            }
                        }
                        extension--;
                    }
                    else
                    {
                        working = false;
                    }
                }
            }
        }
    }

    boolean validBlock (Block block)
    {
        int type = TConstructRegistry.interchangableBlockMapping[block.blockID];
        if (type != 0)
        {
            if (type == inventory[0].itemID)
                return true;
        }
        int blockToItem = TConstructRegistry.blockToItemMapping[block.blockID];
        if (blockToItem != 0)
        {
            if (blockToItem == inventory[0].itemID)
                return true;
        }
        return block.blockID == inventory[0].itemID;
    }

    boolean validMetadata (int blockID, int metadata)
    {
        int type = TConstructRegistry.drawbridgeState[blockID];
        if (type == 0)
        {
            return metadata == inventory[0].getItemDamage();
        }
        if (type == 1)
        {
            return true;
        }
        if (type == 2)
        {
            return false;
        }
        if (type == 3)
        {
            return true; //TODO: rotational metadata
        }
        if (type == 4)
        {
            return true;
        }
        if (type == 5)
        {
            return metadata == inventory[0].getItemDamage();
        }
        return false;
    }
    
    @Override
    public void readFromNBT (NBTTagCompound tags)
    {
        super.readFromNBT(tags);
        active = tags.getBoolean("Active");
        working = tags.getBoolean("Working");
        extension = tags.getByte("Extension");
        readCustomNBT(tags);
    }

    @Override
    public void writeToNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);
        tags.setBoolean("Active", active);
        tags.setBoolean("Working", working);
        tags.setByte("Extension", extension);
        writeCustomNBT(tags);
    }

    public void readCustomNBT (NBTTagCompound tags)
    {
        direction = tags.getByte("Direction");
    }

    public void writeCustomNBT (NBTTagCompound tags)
    {
        tags.setByte("Direction", direction);
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

    public boolean hasExtended ()
    {
        return extension != 0;
    }
    
    @Override
	public void onInventoryChanged() {
		super.onInventoryChanged();
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
}

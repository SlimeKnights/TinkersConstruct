package tconstruct.blocks.logic;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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
import tconstruct.inventory.DrawbridgeContainer;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.blocks.InventoryLogic;
import tconstruct.library.util.IActiveLogic;
import tconstruct.library.util.IFacingLogic;
import tconstruct.util.player.FakePlayerLogic;

public class DrawbridgeLogic extends InventoryLogic implements IFacingLogic, IActiveLogic
{
    boolean active;
    boolean working;
    int ticks;
    byte extension;
    byte direction;
    byte placementDirection = 4;
    FakePlayerLogic fakePlayer;

    public DrawbridgeLogic()
    {
        super(2);
    }
    @Override
    public void setWorldObj (World par1World)
    {
        this.worldObj = par1World;
        fakePlayer = new FakePlayerLogic(worldObj, "Player.Drawbridge", this);
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

    public boolean canDropInventorySlot (int slot)
    {
        return false;
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
    
    /* 0 = Up
     * 1 = Right
     * 2 = Down
     * 3 = Left
     * 4 = Center, neutral
     */
    public void setPlacementDirection(byte keycode)
    {
        if (keycode == 4)
        {
            fakePlayer.rotationYaw = 0;
            fakePlayer.rotationPitch = 0;
        }
        else if (this.direction == 0 || this.direction == 1)
        {
            switch (keycode)
            {
            case 0: fakePlayer.rotationYaw = 0; break;
            case 1: fakePlayer.rotationYaw = 90; break;
            case 2: fakePlayer.rotationYaw = 180; break;
            case 3: fakePlayer.rotationYaw = 270; break;
            }
            
            
            if (this.direction == 0)
                fakePlayer.rotationPitch = -90;
            else
                fakePlayer.rotationPitch = 90;
        }
        else
        {
            if (keycode % 2 == 0)
            {
                fakePlayer.rotationYaw = mapDirection() * 90;
                
                if (keycode == 0)
                    fakePlayer.rotationPitch = 90;
                else
                    fakePlayer.rotationPitch = -90;
            }
            else
            {
                fakePlayer.rotationPitch = 0;
                
                int facing = mapDirection();
                if (keycode == 1)
                    facing += 1;
                else
                    facing -= 1;
                
                if (facing >= 4)
                    facing = 0;
                if (facing < 0)
                    facing = 3;
                
                fakePlayer.rotationYaw = facing * 90;
            }
        } 
        placementDirection = keycode;
    }
    
    int mapDirection()
    {
        if (this.direction == 2) //North
            return 0;
        if (this.direction == 5) //East
            return 1;
        if (this.direction == 3) //South
            return 2;
        
        return 3; //West
    }
    
    public byte getFacingDirection()
    {
        return placementDirection;
    }

    public byte getPlacementDirection ()
    {
        return placementDirection;
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
                                Block placeBlock = Block.blocksList[inventory[0].itemID];
                                placeBlockAt(inventory[0], fakePlayer, worldObj, xPos, yPos, zPos, direction, 0, 0, 0, inventory[0].getItemDamage(), placeBlock);
                            }
                            else
                            {
                                Block placeBlock = Block.blocksList[blockToItem];
                                placeBlockAt(inventory[0], fakePlayer, worldObj, xPos, yPos, zPos, direction, 0, 0, 0, inventory[0].getItemDamage(), placeBlock);
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
                                if (worldObj.setBlock(xPos, yPos, zPos, 0))
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

    /**
     * Called to actually place the block, after the location is determined
     * and all permission checks have been made.
     * Copied from ItemBlock
     *
     * @param stack The item stack that was used to place the block. This can be changed inside the method.
     * @param player The player who is placing the block. Can be null if the block is not being placed by a player.
     * @param side The side the player (or machine) right-clicked on.
     */
    public boolean placeBlockAt (ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata, Block block)
    {
        if (!world.setBlock(x, y, z, block.blockID, metadata, 3))
        {
            return false;
        }

        if (world.getBlockId(x, y, z) == block.blockID)
        {
            block.onBlockPlacedBy(world, x, y, z, player, stack);
            block.onPostBlockPlaced(world, x, y, z, metadata);
        }

        return true;
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
            return true; //TODO: rotational metadata, probably not needed anymore
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
        placementDirection = tags.getByte("Placement");
    }

    public void writeCustomNBT (NBTTagCompound tags)
    {
        tags.setByte("Direction", direction);
        tags.setByte("Placement", placementDirection);
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
    public void onInventoryChanged ()
    {
        super.onInventoryChanged();
        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
}

package tconstruct.blocks.logic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import tconstruct.library.blocks.IDrawbridgeLogicBase;

import net.minecraft.nbt.NBTTagCompound;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.*;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import tconstruct.inventory.DrawbridgeContainer;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.blocks.InventoryLogic;
import tconstruct.library.util.*;
import tconstruct.util.player.FakePlayerLogic;

public class DrawbridgeLogic extends InventoryLogic implements IFacingLogic, IActiveLogic, IDrawbridgeLogicBase
{
    boolean active;
    boolean working;
    int ticks;
    byte extension;
    byte direction;
    byte placementDirection = 4;
    FakePlayerLogic fakePlayer;
    
    ItemStack bufferStack = null;

    private List pushedObjects = new ArrayList();

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
    public void setPlacementDirection (byte keycode)
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
            case 0:
                fakePlayer.rotationYaw = 0;
                break;
            case 1:
                fakePlayer.rotationYaw = 90;
                break;
            case 2:
                fakePlayer.rotationYaw = 180;
                break;
            case 3:
                fakePlayer.rotationYaw = 270;
                break;
            }

            if (this.direction == 0)
                fakePlayer.rotationPitch = -90;
            else
                fakePlayer.rotationPitch = 90;
        }
        else
        {
            if (keycode == 0) //Forward
            {
                fakePlayer.rotationYaw = mapDirection() * 90;

                if (keycode == 0)
                    fakePlayer.rotationPitch = 90;
                else
                    fakePlayer.rotationPitch = -90;
            }
            else if (keycode == 2) //Backward
            {
                int face = mapDirection() + 2;
                if (face > 3)
                    face -= 4;
                fakePlayer.rotationYaw = face * 90;

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

    int mapDirection ()
    {
        if (this.direction == 2) //North
            return 0;
        if (this.direction == 5) //East
            return 1;
        if (this.direction == 3) //South
            return 2;

        return 3; //West
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
                    if (inventory[0] != null && inventory[0].stackSize > 0 && extension < 15)
                    {
                        extension++;
                        int xPos = xCoord;
                        int yPos = yCoord;
                        int zPos = zCoord;

                        bufferStack = inventory[0].copy();
                        bufferStack.stackSize = 1;
                        
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
                            int blockToItem = TConstructRegistry.blockToItemMapping[bufferStack.itemID];
                            if (blockToItem == 0)
                            {
                                if (inventory[0].itemID >= 4096 || Block.blocksList[inventory[0].itemID] == null)
                                    return;
                                Block placeBlock = Block.blocksList[bufferStack.itemID];
                                placeBlockAt(bufferStack, fakePlayer, worldObj, xPos, yPos, zPos, direction, 0, 0, 0, bufferStack.getItemDamage(), placeBlock);
                            }
                            else
                            {
                                Block placeBlock = Block.blocksList[blockToItem];
                                placeBlockAt(bufferStack, fakePlayer, worldObj, xPos, yPos, zPos, direction, 0, 0, 0, bufferStack.getItemDamage(), placeBlock);
                            }
                            worldObj.playSoundEffect((double) xPos + 0.5D, (double) yPos + 0.5D, (double) zPos + 0.5D, "tile.piston.out", 0.25F, worldObj.rand.nextFloat() * 0.25F + 0.6F);
                            decrStackSize(0, 1);
                            
                            AxisAlignedBB axisalignedbb = Block.blocksList[bufferStack.itemID].getCollisionBoundingBoxFromPool(worldObj, xPos, yPos, zPos);

                            if (axisalignedbb != null)
                            {
                            	List list = worldObj.getEntitiesWithinAABBExcludingEntity((Entity)null, axisalignedbb);
                            	if (!list.isEmpty())
	                            {
	                                this.pushedObjects.addAll(list);
	                                Iterator iterator = this.pushedObjects.iterator();
	
	                                while (iterator.hasNext())
	                                {
	                                    Entity entity = (Entity)iterator.next();
	                                    entity.moveEntity(Facing.offsetsXForSide[this.direction], Facing.offsetsYForSide[this.direction], Facing.offsetsZForSide[this.direction]);
	                                }
	
	                                this.pushedObjects.clear();
	                            }
	                        }
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
                            if (bufferStack != null && validBlock(block) && validMetadata(block.blockID, meta))
                            {
                                worldObj.playSoundEffect((double) xPos + 0.5D, (double) yPos + 0.5D, (double) zPos + 0.5D, "tile.piston.in", 0.25F, worldObj.rand.nextFloat() * 0.15F + 0.6F);
                                if (worldObj.setBlock(xPos, yPos, zPos, 0))
                                    if(inventory[0] == null){
                                    	inventory[0] = bufferStack.copy();
                                    }else{
                                    	inventory[0].stackSize++;
                                    }
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
            if (type == bufferStack.itemID)
                return true;
        }
        int blockToItem = TConstructRegistry.blockToItemMapping[block.blockID];
        if (blockToItem != 0)
        {
            if (blockToItem == bufferStack.itemID)
                return true;
        }
        return block.blockID == bufferStack.itemID;
    }

    boolean validMetadata (int blockID, int metadata)
    {
        int type = TConstructRegistry.drawbridgeState[blockID];
        if (type == 0)
        {
            return metadata == bufferStack.getItemDamage();
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
            return metadata == bufferStack.getItemDamage();
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
        
        NBTTagCompound bufferInv = (NBTTagCompound) tags.getTag("BufferInv");
        if(bufferInv != null){
        	bufferStack = ItemStack.loadItemStackFromNBT(bufferInv);
        }
        if(bufferStack == null && inventory[0] != null){
        	bufferStack = inventory[0];
        }
        
        readCustomNBT(tags);
    }

    @Override
    public void writeToNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);
        tags.setBoolean("Active", active);
        tags.setBoolean("Working", working);
        tags.setByte("Extension", extension);
        
        if(bufferStack != null){
	        NBTTagCompound bufferInv = new NBTTagCompound();
	        bufferStack.writeToNBT(bufferInv);
	        tags.setTag("BufferInv", bufferInv);
        }
        
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
        readFromNBT(packet.data);
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
        if(getStackInSlot(0) != null){
        	bufferStack = getStackInSlot(0).copy();
        	bufferStack.stackSize = 1;
        }
        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
}

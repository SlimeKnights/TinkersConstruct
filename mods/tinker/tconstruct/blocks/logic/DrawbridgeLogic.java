package mods.tinker.tconstruct.blocks.logic;

import mods.tinker.tconstruct.common.TContent;
import mods.tinker.tconstruct.inventory.DrawbridgeContainer;
import mods.tinker.tconstruct.library.TConstructRegistry;
import mods.tinker.tconstruct.library.blocks.InventoryLogic;
import mods.tinker.tconstruct.library.util.CoordTuple;
import mods.tinker.tconstruct.library.util.IActiveLogic;
import mods.tinker.tconstruct.library.util.IFacingLogic;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonMoving;
import net.minecraft.block.BlockSnow;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.util.Facing;
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
    public void setDirection (float yaw, float pitch, EntityLiving player)
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
    public void setInventorySlotContents(int slot, ItemStack itemstack)
    {
        super.setInventorySlotContents(slot, itemstack);
        if (slot == 1)
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    

    @Override
    public ItemStack decrStackSize(int slot, int quantity)
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
                    if (inventory[0] != null && inventory[0].stackSize > 1 && extension < 16)
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
                            worldObj.setBlock(xPos, yPos, zPos, inventory[0].itemID, inventory[0].getItemDamage(), 3);
                            worldObj.playSoundEffect((double)xPos + 0.5D, (double)yPos + 0.5D, (double)zPos + 0.5D, "tile.piston.out", 0.25F, worldObj.rand.nextFloat() * 0.25F + 0.6F);
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
                else //Retraction
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
                                worldObj.playSoundEffect((double)xPos + 0.5D, (double)yPos + 0.5D, (double)zPos + 0.5D, "tile.piston.in", 0.25F, worldObj.rand.nextFloat() * 0.15F + 0.6F);
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
    
    boolean validBlock(Block block)
    {
        int type = TConstructRegistry.interchangableBlockMapping[block.blockID];
        if (type != 0)
        {
            return block.blockID == inventory[0].itemID || type == inventory[0].itemID;
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
    
    private boolean tryExtend(World par1World, int x, int y, int z, int side)
    {
        int posX = x + Facing.offsetsXForSide[side];
        int posY = y + Facing.offsetsYForSide[side];
        int posZ = z + Facing.offsetsZForSide[side];
        int newX = 0;

        while (true)
        {
            int blockID;

            if (newX < 13)
            {
                if (posY <= 0 || posY >= par1World.getHeight() - 1)
                {
                    return false;
                }

                blockID = par1World.getBlockId(posX, posY, posZ);

                if (blockID != 0)
                {
                    if (!canPushBlock(blockID, par1World, posX, posY, posZ, true))
                    {
                        return false;
                    }

                    if (Block.blocksList[blockID].getMobilityFlag() != 1)
                    {
                        if (newX == 12)
                        {
                            return false;
                        }

                        posX += Facing.offsetsXForSide[side];
                        posY += Facing.offsetsYForSide[side];
                        posZ += Facing.offsetsZForSide[side];
                        ++newX;
                        continue;
                    }

                    //With our change to how snowballs are dropped this needs to dissallow to mimic vanilla behavior.
                    float chance = (Block.blocksList[blockID] instanceof BlockSnow ? -1.0f : 1.0f);
                    Block.blocksList[blockID].dropBlockAsItemWithChance(par1World, posX, posY, posZ, par1World.getBlockMetadata(posX, posY, posZ), chance, 0);
                    par1World.setBlockToAir(posX, posY, posZ);
                }
            }

            newX = posX;
            blockID = posY;
            int newZ = posZ;
            int k2 = 0;
            int[] aint;
            int l2;
            int i3;
            int j3;

            for (aint = new int[13]; posX != x || posY != y || posZ != z; posZ = j3)
            {
                l2 = posX - Facing.offsetsXForSide[side];
                i3 = posY - Facing.offsetsYForSide[side];
                j3 = posZ - Facing.offsetsZForSide[side];
                int k3 = par1World.getBlockId(l2, i3, j3);
                int l3 = par1World.getBlockMetadata(l2, i3, j3);

                if (k3 == TContent.redstoneMachine.blockID && l2 == x && i3 == y && j3 == z)
                {
                    par1World.setBlock(posX, posY, posZ, Block.pistonMoving.blockID, side | 0, 4);
                    par1World.setBlockTileEntity(posX, posY, posZ, BlockPistonMoving.getTileEntity(Block.pistonExtension.blockID, side | 0, side, true, false));
                }
                else
                {
                    par1World.setBlock(posX, posY, posZ, Block.pistonMoving.blockID, l3, 4);
                    par1World.setBlockTileEntity(posX, posY, posZ, BlockPistonMoving.getTileEntity(k3, l3, side, true, false));
                }

                aint[k2++] = k3;
                posX = l2;
                posY = i3;
            }

            posX = newX;
            posY = blockID;
            posZ = newZ;

            for (k2 = 0; posX != x || posY != y || posZ != z; posZ = j3)
            {
                l2 = posX - Facing.offsetsXForSide[side];
                i3 = posY - Facing.offsetsYForSide[side];
                j3 = posZ - Facing.offsetsZForSide[side];
                par1World.notifyBlocksOfNeighborChange(l2, i3, j3, aint[k2++]);
                posX = l2;
                posY = i3;
            }

            return true;
        }
    }
    
    private static boolean canPushBlock(int par0, World par1World, int par2, int par3, int par4, boolean par5)
    {
        if (par0 == Block.obsidian.blockID)
        {
            return false;
        }
        else
        {
            if (par0 != Block.pistonBase.blockID && par0 != Block.pistonStickyBase.blockID)
            {
                if (Block.blocksList[par0].getBlockHardness(par1World, par2, par3, par4) == -1.0F)
                {
                    return false;
                }

                if (Block.blocksList[par0].getMobilityFlag() == 2)
                {
                    return false;
                }

                if (Block.blocksList[par0].getMobilityFlag() == 1)
                {
                    if (!par5)
                    {
                        return false;
                    }

                    return true;
                }
            }
            /*else if (isExtended(par1World.getBlockMetadata(par2, par3, par4)))
            {
                return false;
            }*/

            return !par1World.blockHasTileEntity(par2, par3, par4);
        }
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
}

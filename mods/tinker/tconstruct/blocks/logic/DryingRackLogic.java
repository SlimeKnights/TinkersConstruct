package mods.tinker.tconstruct.blocks.logic;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.tinker.tconstruct.common.TContent;
import mods.tinker.tconstruct.library.blocks.InventoryLogic;
import mods.tinker.tconstruct.library.util.IActiveLogic;
import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class DryingRackLogic extends InventoryLogic implements IActiveLogic
{
    short currentTime;
    short maxTime = 20 * 60 * 5;
    public int currentLightLevel;
    public boolean active;
    public byte crystalValue;

    public DryingRackLogic()
    {
        super(1);
    }

    @Override
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        return null;
    }

    @Override
    protected String getDefaultName ()
    {
        return "";
    }

    @Override
    public void updateEntity ()
    {
        /*if (worldObj.isRemote)
            return;

        if (worldObj.canBlockSeeTheSky(xCoord, yCoord + 1, zCoord) && crystalValue < 64)
        {
            Block block = Block.blocksList[worldObj.getBlockId(xCoord, yCoord + 1, zCoord)];
            if (block == null || validBlock(block) || block.isAirBlock(worldObj, xCoord, yCoord + 1, zCoord))
            {
                currentLightLevel = worldObj.getSavedLightValue(EnumSkyBlock.Sky, xCoord, yCoord, zCoord) - worldObj.skylightSubtracted;
                if (currentLightLevel > 12)
                {
                    currentTime++;
                    if (currentTime >= 180)
                    {
                        currentTime = 0;
                        crystalValue++;
                        if (block == TContent.lightCrystalBase)
                        {
                            int meta = worldObj.getBlockMetadata(xCoord, yCoord + 1, zCoord);
                            if (crystalValue >= 64)
                            {
                                if (meta < 3)
                                {
                                    worldObj.setBlockMetadataWithNotify(xCoord, yCoord + 1, zCoord, 3, 3);
                                    TheftValueTracker.updateCrystallinity(worldObj.provider.dimensionId, xCoord, zCoord, 25); //Total 60
                                }
                            }
                            else if (crystalValue >= 28)
                            {
                                if (meta < 2)
                                {
                                    worldObj.setBlockMetadataWithNotify(xCoord, yCoord + 1, zCoord, 2, 3);
                                    TheftValueTracker.updateCrystallinity(worldObj.provider.dimensionId, xCoord, zCoord, 15); //Total 35
                                }
                            }
                            else if (crystalValue >= 8)
                            {
                                if (meta < 1)
                                {
                                    worldObj.setBlockMetadataWithNotify(xCoord, yCoord + 1, zCoord, 1, 3);
                                    TheftValueTracker.updateCrystallinity(worldObj.provider.dimensionId, xCoord, zCoord, 10); //Total 20
                                }
                            }
                        }
                        else
                        {
                            worldObj.setBlock(xCoord, yCoord + 1, zCoord, TContent.lightCrystalBase.blockID, 0, 3);
                            TheftValueTracker.updateCrystallinity(worldObj.provider.dimensionId, xCoord, zCoord, 10); //Total 10
                        }
                    }
                    if (active == false)
                    {
                        active = true;
                        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                    }
                }
                else
                {
                    if (active == true)
                    {
                        active = false;
                        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                    }
                }
            }
        }*/
    }

    boolean validBlock (Block block)
    {
        return block == TContent.lightCrystalBase;
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
    }

    @Override
    public void readFromNBT (NBTTagCompound tags)
    {
        super.readFromNBT(tags);
        tags.setShort("Time", currentTime);
        tags.setByte("Value", crystalValue);
        readCustomNBT(tags);
    }

    @Override
    public void writeToNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);
        currentTime = tags.getShort("Time");
        crystalValue = tags.getByte("Value");
        writeCustomNBT(tags);
    }

    public void readCustomNBT (NBTTagCompound tags)
    {
        active = tags.getBoolean("Active");
    }

    public void writeCustomNBT (NBTTagCompound tags)
    {
        tags.setBoolean("Active", active);
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

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        AxisAlignedBB cbb = AxisAlignedBB.getAABBPool().getAABB(xCoord, yCoord - 1, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
        return cbb;
    }
}

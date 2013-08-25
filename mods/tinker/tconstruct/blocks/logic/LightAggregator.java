package mods.tinker.tconstruct.blocks.logic;

import mods.tinker.tconstruct.common.TContent;
import mods.tinker.tconstruct.crystal.Crystallinity;
import mods.tinker.tconstruct.crystal.Crystallinity.CrystalType;
import mods.tinker.tconstruct.inventory.AggregatorContainer;
import mods.tinker.tconstruct.library.util.IActiveLogic;
import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class LightAggregator extends AggregatorLogic implements IActiveLogic
{
    short currentTime;
    short maxTime = 20 * 60 * 5;
    public int currentLightLevel;
    public boolean active;
    public int crystalValue;
    boolean blocked;

    public LightAggregator()
    {
        super(3);
    }

    @Override
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        return new AggregatorContainer(inventoryplayer, this);
    }

    @Override
    protected String getDefaultName ()
    {
        return "aggregator.glowstone";
    }

    @Override
    public void updateEntity ()
    {
        if (!blocked && crystalValue < 528)// && worldObj.canBlockSeeTheSky(xCoord, yCoord + 1, zCoord))
        {
            currentLightLevel = worldObj.getSavedLightValue(EnumSkyBlock.Sky, xCoord, yCoord + heightOfCrystal(), zCoord) - worldObj.skylightSubtracted;
            if (currentLightLevel > 3)
            {
                currentTime += currentLightLevel / 3 + 1;
                if (currentTime >= 1) //180 ticks * max light (15) 540
                {
                    currentTime = 0;
                    crystalValue++;
                    //System.out.println(crystalValue + " " + currentLightLevel);
                    if (!worldObj.isRemote)
                        growCrystal();
                }
                setActive(true);
            }
            else
            {
                setActive(false);
            }
        }
        else
        {
            setActive(false);
        }
    }

    int heightOfCrystal ()
    {
        if (crystalValue > 440)
            return 4;
        if (crystalValue > 224)
            return 3;
        if (crystalValue > 80)
            return 2;

        return 1;
    }

    void growCrystal ()
    {
        if (crystalValue == 1)
        {
            Block block = Block.blocksList[worldObj.getBlockId(xCoord, yCoord + 1, zCoord)];
            if (block == null || block.isAirBlock(worldObj, xCoord, yCoord + 1, zCoord))
            {
                worldObj.setBlock(xCoord, yCoord + 1, zCoord, TContent.lightCrystalBase.blockID, 0, 3);
            }
            else
            {
                crystalValue--;
                blocked = true;
            }
        }
        else if (crystalValue == 8)
        {
            Block block = Block.blocksList[worldObj.getBlockId(xCoord, yCoord + 1, zCoord)];
            if (validBlock(block))
            {
                worldObj.setBlockMetadataWithNotify(xCoord, yCoord + 1, zCoord, 1, 3);
                spawnNegativeAir(1, 1);
                
                int value = 8;
                Crystallinity.updateTheft(worldObj.provider.dimensionId, xCoord, zCoord, value, CrystalType.Light);
                Crystallinity.updateCharge(worldObj.provider.dimensionId, xCoord, zCoord, -value, CrystalType.Light);
                CrystalLogic crystal = (CrystalLogic) worldObj.getBlockTileEntity(xCoord, yCoord + 1, zCoord);
                crystal.setCrystalValue(8);
            }
        }
        else if (crystalValue == 24)
        {
            Block block = Block.blocksList[worldObj.getBlockId(xCoord, yCoord + 1, zCoord)];
            if (validBlock(block))
            {
                worldObj.setBlockMetadataWithNotify(xCoord, yCoord + 1, zCoord, 2, 3);
                spawnNegativeAir(2, 1);
                
                int value = 16;
                Crystallinity.updateTheft(worldObj.provider.dimensionId, xCoord, zCoord, value, CrystalType.Light);
                Crystallinity.updateCharge(worldObj.provider.dimensionId, xCoord, zCoord, -value, CrystalType.Light);
                CrystalLogic crystal = (CrystalLogic) worldObj.getBlockTileEntity(xCoord, yCoord + 1, zCoord);
                crystal.setCrystalValue(24);
            }
        }
        else if (crystalValue == 48)
        {
            Block block = Block.blocksList[worldObj.getBlockId(xCoord, yCoord + 1, zCoord)];
            if (validBlock(block))
            {
                worldObj.setBlockMetadataWithNotify(xCoord, yCoord + 1, zCoord, 3, 3);
                spawnNegativeAir(3, 1);
                
                int value = 24;
                Crystallinity.updateTheft(worldObj.provider.dimensionId, xCoord, zCoord, value, CrystalType.Light);
                Crystallinity.updateCharge(worldObj.provider.dimensionId, xCoord, zCoord, -value, CrystalType.Light);
                CrystalLogic crystal = (CrystalLogic) worldObj.getBlockTileEntity(xCoord, yCoord + 1, zCoord);
                crystal.setCrystalValue(48);
            }
        }
        else if (crystalValue == 80) //Transition
        {
            Block upperBlock = Block.blocksList[worldObj.getBlockId(xCoord, yCoord + 2, zCoord)];
            if (upperBlock == null || upperBlock.isAirBlock(worldObj, xCoord, yCoord + 2, zCoord))
            {
                Block block = Block.blocksList[worldObj.getBlockId(xCoord, yCoord + 1, zCoord)];
                if (validBlock(block))
                {
                    worldObj.setBlockMetadataWithNotify(xCoord, yCoord + 1, zCoord, 4, 3);
                    worldObj.setBlock(xCoord, yCoord + 2, zCoord, TContent.lightCrystalBase.blockID, 6, 3);
                    spawnNegativeAir(5, 1);
                    
                    int value = 32;
                    Crystallinity.updateTheft(worldObj.provider.dimensionId, xCoord, zCoord, value, CrystalType.Light);
                    Crystallinity.updateCharge(worldObj.provider.dimensionId, xCoord, zCoord, -value, CrystalType.Light);
                    CrystalLogic crystal = (CrystalLogic) worldObj.getBlockTileEntity(xCoord, yCoord + 1, zCoord);
                    crystal.setCrystalValue(80);
                }
            }
            else
            {
                crystalValue--;
                blocked = true;
            }
        }
        else if (crystalValue == 120)
        {
            Block block = Block.blocksList[worldObj.getBlockId(xCoord, yCoord + 1, zCoord)];
            Block upperBlock = Block.blocksList[worldObj.getBlockId(xCoord, yCoord + 2, zCoord)];
            if (validBlock(block) && validBlock(upperBlock))
            {
                worldObj.setBlockMetadataWithNotify(xCoord, yCoord + 1, zCoord, 5, 3);
                worldObj.setBlockMetadataWithNotify(xCoord, yCoord + 2, zCoord, 7, 3);
                spawnNegativeAir(6, 1);
                
                int value = 40;
                Crystallinity.updateTheft(worldObj.provider.dimensionId, xCoord, zCoord, value, CrystalType.Light);
                Crystallinity.updateCharge(worldObj.provider.dimensionId, xCoord, zCoord, -value, CrystalType.Light);
                CrystalLogic crystal = (CrystalLogic) worldObj.getBlockTileEntity(xCoord, yCoord + 1, zCoord);
                crystal.setCrystalValue(120);
            }
        }
        else if (crystalValue == 168)
        {
            Block block = Block.blocksList[worldObj.getBlockId(xCoord, yCoord + 2, zCoord)];
            if (validBlock(block))
            {
                worldObj.setBlockMetadataWithNotify(xCoord, yCoord + 2, zCoord, 8, 3);
                spawnNegativeAir(7, 1);
                
                int value = 48;
                Crystallinity.updateTheft(worldObj.provider.dimensionId, xCoord, zCoord, value, CrystalType.Light);
                Crystallinity.updateCharge(worldObj.provider.dimensionId, xCoord, zCoord, -value, CrystalType.Light);
                CrystalLogic crystal = (CrystalLogic) worldObj.getBlockTileEntity(xCoord, yCoord + 1, zCoord);
                crystal.setCrystalValue(168);
            }
        }
        else if (crystalValue == 224) //Transition
        {
            Block upperBlock = Block.blocksList[worldObj.getBlockId(xCoord, yCoord + 3, zCoord)];
            if (upperBlock == null || upperBlock.isAirBlock(worldObj, xCoord, yCoord + 3, zCoord))
            {
                Block block = Block.blocksList[worldObj.getBlockId(xCoord, yCoord + 2, zCoord)];
                if (validBlock(block))
                {
                    worldObj.setBlockMetadataWithNotify(xCoord, yCoord + 2, zCoord, 9, 3);
                    worldObj.setBlock(xCoord, yCoord + 3, zCoord, TContent.lightCrystalBase.blockID, 10, 3);
                    spawnNegativeAir(9, 1);
                    
                    int value = 56;
                    Crystallinity.updateTheft(worldObj.provider.dimensionId, xCoord, zCoord, value, CrystalType.Light);
                    Crystallinity.updateCharge(worldObj.provider.dimensionId, xCoord, zCoord, -value, CrystalType.Light);
                    CrystalLogic crystal = (CrystalLogic) worldObj.getBlockTileEntity(xCoord, yCoord + 1, zCoord);
                    crystal.setCrystalValue(224);
                }
            }
            else
            {
                crystalValue--;
                blocked = true;
            }
        }
        else if (crystalValue == 288)
        {
            Block block = Block.blocksList[worldObj.getBlockId(xCoord, yCoord + 3, zCoord)];
            if (validBlock(block))
            {
                worldObj.setBlockMetadataWithNotify(xCoord, yCoord + 3, zCoord, 11, 3);
                spawnNegativeAir(10, 1);
                
                int value = 64;
                Crystallinity.updateTheft(worldObj.provider.dimensionId, xCoord, zCoord, value, CrystalType.Light);
                Crystallinity.updateCharge(worldObj.provider.dimensionId, xCoord, zCoord, -value, CrystalType.Light);
                CrystalLogic crystal = (CrystalLogic) worldObj.getBlockTileEntity(xCoord, yCoord + 1, zCoord);
                crystal.setCrystalValue(288);
            }
        }
        else if (crystalValue == 360)
        {
            Block block = Block.blocksList[worldObj.getBlockId(xCoord, yCoord + 3, zCoord)];
            if (validBlock(block))
            {
                worldObj.setBlockMetadataWithNotify(xCoord, yCoord + 3, zCoord, 12, 3);
                spawnNegativeAir(11, 1);
                
                int value = 72;
                Crystallinity.updateTheft(worldObj.provider.dimensionId, xCoord, zCoord, value, CrystalType.Light);
                Crystallinity.updateCharge(worldObj.provider.dimensionId, xCoord, zCoord, -value, CrystalType.Light);
                CrystalLogic crystal = (CrystalLogic) worldObj.getBlockTileEntity(xCoord, yCoord + 1, zCoord);
                crystal.setCrystalValue(360);
            }
        }
        else if (crystalValue == 440) //Transition
        {
            Block upperBlock = Block.blocksList[worldObj.getBlockId(xCoord, yCoord + 4, zCoord)];
            if (upperBlock == null || upperBlock.isAirBlock(worldObj, xCoord, yCoord + 4, zCoord))
            {
                Block block = Block.blocksList[worldObj.getBlockId(xCoord, yCoord + 3, zCoord)];
                if (validBlock(block))
                {
                    worldObj.setBlockMetadataWithNotify(xCoord, yCoord + 3, zCoord, 13, 3);
                    worldObj.setBlock(xCoord, yCoord + 4, zCoord, TContent.lightCrystalBase.blockID, 14, 3);
                    spawnNegativeAir(13, 1);
                    
                    int value = 80;
                    Crystallinity.updateTheft(worldObj.provider.dimensionId, xCoord, zCoord, value, CrystalType.Light);
                    Crystallinity.updateCharge(worldObj.provider.dimensionId, xCoord, zCoord, -value, CrystalType.Light);
                    CrystalLogic crystal = (CrystalLogic) worldObj.getBlockTileEntity(xCoord, yCoord + 1, zCoord);
                    crystal.setCrystalValue(440);
                }
            }
            else
            {
                crystalValue--;
                blocked = true;
            }
        }
        else if (crystalValue == 528)
        {
            Block block = Block.blocksList[worldObj.getBlockId(xCoord, yCoord + 4, zCoord)];
            if (validBlock(block))
            {
                worldObj.setBlockMetadataWithNotify(xCoord, yCoord + 4, zCoord, 15, 3);
                spawnNegativeAir(15, 1);
                
                int value = 88;
                Crystallinity.updateTheft(worldObj.provider.dimensionId, xCoord, zCoord, value, CrystalType.Light);
                Crystallinity.updateCharge(worldObj.provider.dimensionId, xCoord, zCoord, -value, CrystalType.Light);
                CrystalLogic crystal = (CrystalLogic) worldObj.getBlockTileEntity(xCoord, yCoord + 1, zCoord);
                crystal.setCrystalValue(528);
            }
        }
    }

    public void spawnNegativeAir (int range, int darkness)
    {
        for (int xPos = -range; xPos <= range; xPos++)
        {
            for (int yPos = -range; yPos <= range; yPos++)
            {
                for (int zPos = -range; zPos <= range; zPos++)
                {
                    if (Math.abs(xPos) + Math.abs(yPos) + Math.abs(zPos) <= range)
                    {
                        Block block = Block.blocksList[worldObj.getBlockId(xCoord + xPos, yCoord + yPos, zCoord + zPos)];
                        if (block == null || block.isAirBlock(worldObj, xCoord + xPos, yCoord + yPos, zCoord + zPos))
                            worldObj.setBlock(xCoord + xPos, yCoord + yPos, zCoord + zPos, TContent.darkBlock.blockID, darkness, 3);
                    }
                }
            }
        }
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
        if (active != flag)
        {
            active = flag;
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public void readFromNBT (NBTTagCompound tags)
    {
        super.readFromNBT(tags);
        currentTime = tags.getShort("Time");
        readCustomNBT(tags);
    }

    @Override
    public void writeToNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);
        tags.setShort("Time", currentTime);
        writeCustomNBT(tags);
    }

    public void readCustomNBT (NBTTagCompound tags)
    {
        active = tags.getBoolean("Active");
        crystalValue = tags.getInteger("CrystalValue");
    }

    public void writeCustomNBT (NBTTagCompound tags)
    {
        tags.setBoolean("Active", active);
        tags.setInteger("CrystalValue", crystalValue);
    }

    /* Packets */
    @Override
    public Packet getDescriptionPacket ()
    {
        NBTTagCompound tag = new NBTTagCompound();
        writeCustomNBT(tag);
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket (INetworkManager net, Packet132TileEntityData packet)
    {
        readCustomNBT(packet.customParam1);
        worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
    }

    public int harvestCrystal ()
    {
        int tmp = crystalValue;
        //TheftTracker.updateValue(worldObj.provider.dimensionId, xCoord, zCoord, -tmp, CrystalType.Light);
        crystalValue = 0;
        return tmp;
    }
}

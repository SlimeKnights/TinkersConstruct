package common.darkknight.jewelrycraft.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;

public class TileEntityBlockShadow extends TileEntity
{
    public int metadata;

    public TileEntityBlockShadow()
    {
        this.metadata = -1;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("metadata", metadata);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.metadata = nbt.getInteger("metadata");
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        int blockLight, realLight;
        int lightValue = worldObj.getSavedLightValue(EnumSkyBlock.Sky, xCoord, yCoord, zCoord) - worldObj.skylightSubtracted;
        float sunPosAngle = worldObj.getCelestialAngleRadians(1.0F);

        if (sunPosAngle < (float)Math.PI) sunPosAngle += (0.0F - sunPosAngle) * 0.2F;
        else sunPosAngle += (((float)Math.PI * 2F) - sunPosAngle) * 0.2F;

        lightValue = Math.round((float)lightValue * MathHelper.cos(sunPosAngle));

        if (lightValue < 0) lightValue = 0;
        if (lightValue > 15) lightValue = 15;

        blockLight = worldObj.getChunkFromBlockCoords(xCoord, zCoord).getSavedLightValue(EnumSkyBlock.Block, xCoord & 15, yCoord, zCoord & 15);
        realLight = worldObj.getChunkFromBlockCoords(xCoord, zCoord).getBlockLightValue(xCoord & 15, yCoord, zCoord & 15, 0);

        if((blockLight == 0 && worldObj.canBlockSeeTheSky(xCoord, yCoord, zCoord)) || (lightValue >= blockLight)) metadata = 15 - lightValue;
        else if(!worldObj.canBlockSeeTheSky(xCoord, yCoord, zCoord)) metadata = 15 - realLight;
        else if(lightValue < blockLight) metadata = 15 - blockLight;

        worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, metadata, 2);
        worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlockId(xCoord, yCoord, zCoord));
    }

    @Override
    public Packet getDescriptionPacket() 
    {
        Packet132TileEntityData packet = (Packet132TileEntityData) super.getDescriptionPacket();
        NBTTagCompound dataTag = packet != null ? packet.data : new NBTTagCompound();
        writeToNBT(dataTag);
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, dataTag);
    }

    @Override
    public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) 
    {
        super.onDataPacket(net, pkt);
        NBTTagCompound tag = pkt != null ? pkt.data : new NBTTagCompound();
        readFromNBT(tag);
    }
}

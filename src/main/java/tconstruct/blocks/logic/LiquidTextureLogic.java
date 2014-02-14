package tconstruct.blocks.logic;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class LiquidTextureLogic extends TileEntity
{
    int texturePos;

    public boolean canUpdate ()
    {
        return false;
    }

    public void setLiquidType (int tex)
    {
        texturePos = tex;
        worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
    }

    public int getLiquidType ()
    {
        return texturePos;
    }

    public void readFromNBT (NBTTagCompound tags)
    {
        super.readFromNBT(tags);
        readCustomNBT(tags);
    }

    public void readCustomNBT (NBTTagCompound tags)
    {
        texturePos = tags.getInteger("Texture");
    }

    public void writeToNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);
        writeCustomNBT(tags);
    }

    public void writeCustomNBT (NBTTagCompound tags)
    {
        tags.setInteger("Texture", texturePos);
    }

    @Override
    public Packet getDescriptionPacket ()
    {
        NBTTagCompound tag = new NBTTagCompound();
        writeCustomNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket (NetworkManager net, S35PacketUpdateTileEntity packet)
    {
        worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
        readCustomNBT(packet.func_148857_g());
    }
}
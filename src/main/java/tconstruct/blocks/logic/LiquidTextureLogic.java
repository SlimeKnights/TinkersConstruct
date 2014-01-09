package tconstruct.blocks.logic;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
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
        field_145850_b.markBlockForRenderUpdate(field_145851_c, field_145848_d, field_145849_e);
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
        return new Packet132TileEntityData(field_145851_c, field_145848_d, field_145849_e, 1, tag);
    }

    @Override
    public void onDataPacket (INetworkManager net, Packet132TileEntityData packet)
    {
        readCustomNBT(packet.data);
        field_145850_b.markBlockForRenderUpdate(field_145851_c, field_145848_d, field_145849_e);
    }
}

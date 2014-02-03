package tconstruct.blocks.logic;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.*;
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
        field_145850_b.func_147479_m(field_145851_c, field_145848_d, field_145849_e);
    }

    public int getLiquidType ()
    {
        return texturePos;
    }

    public void func_145839_a (NBTTagCompound tags)
    {
        super.func_145839_a(tags);
        readCustomNBT(tags);
    }

    public void readCustomNBT (NBTTagCompound tags)
    {
        texturePos = tags.getInteger("Texture");
    }

    public void func_145841_b (NBTTagCompound tags)
    {
        super.func_145841_b(tags);
        writeCustomNBT(tags);
    }

    public void writeCustomNBT (NBTTagCompound tags)
    {
        tags.setInteger("Texture", texturePos);
    }

    @Override
    public Packet func_145844_m ()
    {
        NBTTagCompound tag = new NBTTagCompound();
        writeCustomNBT(tag);
        return new S35PacketUpdateTileEntity(field_145851_c, field_145848_d, field_145849_e, 1, tag);
    }

    @Override
    public void onDataPacket (NetworkManager net, S35PacketUpdateTileEntity packet)
    {
        readCustomNBT(packet.func_148857_g());
        field_145850_b.func_147479_m(field_145851_c, field_145848_d, field_145849_e);
    }
}

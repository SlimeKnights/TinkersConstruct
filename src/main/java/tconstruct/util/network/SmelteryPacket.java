package tconstruct.util.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import mantle.common.network.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.TConstruct;
import tconstruct.smeltery.logic.SmelteryLogic;

public class SmelteryPacket extends AbstractPacket
{

    int dimension, x, y, z, fluidID;
    boolean isShiftPressed;

    public SmelteryPacket()
    {

    }

    public SmelteryPacket(int dimension, int x, int y, int z, boolean isShiftPressed, int fluidID)
    {
        this.dimension = dimension;
        this.x = x;
        this.y = y;
        this.z = z;
        this.isShiftPressed = isShiftPressed;
        this.fluidID = fluidID;
    }

    @Override
    public void encodeInto (ChannelHandlerContext ctx, ByteBuf buffer)
    {
        buffer.writeInt(dimension);
        buffer.writeInt(x);
        buffer.writeInt(y);
        buffer.writeInt(z);
        buffer.writeBoolean(isShiftPressed);
        buffer.writeInt(fluidID);

    }

    @Override
    public void decodeInto (ChannelHandlerContext ctx, ByteBuf buffer)
    {
        dimension = buffer.readInt();
        x = buffer.readInt();
        y = buffer.readInt();
        z = buffer.readInt();
        isShiftPressed = buffer.readBoolean();
        fluidID = buffer.readInt();
    }

    @Override
    public void handleClientSide (EntityPlayer player)
    {

    }

    @Override
    public void handleServerSide (EntityPlayer player)
    {
        World world = player.worldObj;

        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof SmelteryLogic)
        {
            FluidStack temp = null;

            for (FluidStack liquid : ((SmelteryLogic) te).moltenMetal)
            {
                if (liquid.fluidID == fluidID)
                {
                    temp = liquid;
                }
            }

            if (temp != null)
            {
                ((SmelteryLogic) te).moltenMetal.remove(temp);
                if (isShiftPressed)
                    ((SmelteryLogic) te).moltenMetal.add(temp);
                else
                    ((SmelteryLogic) te).moltenMetal.add(0, temp);
            }

            NBTTagCompound data = new NBTTagCompound();
            te.writeToNBT(data);
            TConstruct.packetPipeline.sendToDimension(new PacketUpdateTE(x, y, z, data), dimension);
        }
    }

}

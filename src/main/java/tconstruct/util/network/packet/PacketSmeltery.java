package tconstruct.util.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.blocks.logic.SmelteryLogic;
import cpw.mods.fml.common.FMLCommonHandler;

public class PacketSmeltery extends AbstractPacket
{

    int dimension, x, y, z, fluidID;
    boolean isShiftPressed;

    public PacketSmeltery()
    {

    }

    public PacketSmeltery(int dimension, int x, int y, int z, boolean isShiftPressed, int fluidID)
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
            {// TODO
             // update
             // fluid
             // stuffs
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
            // TODO check if this works like it should
            // Old code:
            // PacketDispatcher.sendPacketToAllInDimension(te.getDescriptionPacket(),
            // dimension);
            FMLCommonHandler.instance().getClientToServerNetworkManager().scheduleOutboundPacket(te.getDescriptionPacket());
        }
    }

}

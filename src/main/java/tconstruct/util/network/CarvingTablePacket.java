package tconstruct.util.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import mantle.common.network.AbstractPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tconstruct.tools.VirtualPattern;
import tconstruct.tools.logic.CarvingTableLogic;
import tconstruct.tools.logic.ToolForgeLogic;
import tconstruct.tools.logic.ToolStationLogic;

public class CarvingTablePacket extends AbstractPacket
{

    private int x, y, z;
    private byte patternID;

    public CarvingTablePacket ()
    {
    }

    public CarvingTablePacket (int x, int y, int z, byte pID)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.patternID = pID;
    }

    @Override
    public void encodeInto (ChannelHandlerContext ctx, ByteBuf buffer)
    {
        buffer.writeInt(x);
        buffer.writeInt(y);
        buffer.writeInt(z);
        buffer.writeByte(patternID);
    }

    @Override
    public void decodeInto (ChannelHandlerContext ctx, ByteBuf buffer)
    {
        x = buffer.readInt();
        y = buffer.readInt();
        z = buffer.readInt();
        patternID = buffer.readByte();
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

        if (te instanceof CarvingTableLogic)
        {
            CarvingTableLogic ctl = (CarvingTableLogic) te;
            ctl.currentPattern = VirtualPattern.getAll()[patternID];
            ctl.buildTopPart();
            ctl.buildBottomPart();
        }
    }

}

package tconstruct.util.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import mantle.common.network.AbstractPacket;
import net.minecraft.entity.player.EntityPlayer;
import tconstruct.tools.inventory.ToolStationContainer;
import tconstruct.tools.logic.ToolStationLogic;

public class ToolStationPacket extends AbstractPacket
{

    private int x, y, z;
    private String toolName;

    public ToolStationPacket()
    {
    }

    public ToolStationPacket(int x, int y, int z, String toolName)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.toolName = toolName;
    }

    @Override
    public void encodeInto (ChannelHandlerContext ctx, ByteBuf buffer)
    {
        buffer.writeInt(x);
        buffer.writeInt(y);
        buffer.writeInt(z);
        ByteBufUtils.writeUTF8String(buffer, toolName);
    }

    @Override
    public void decodeInto (ChannelHandlerContext ctx, ByteBuf buffer)
    {
        x = buffer.readInt();
        y = buffer.readInt();
        z = buffer.readInt();
        toolName = ByteBufUtils.readUTF8String(buffer);
    }

    @Override
    public void handleClientSide (EntityPlayer player)
    {
    }

    @Override
    public void handleServerSide (EntityPlayer player)
    {
        if (player.openContainer instanceof ToolStationContainer)
        {
            ToolStationContainer container = (ToolStationContainer) player.openContainer;
            ToolStationLogic logic = container.logic;
            if (logic != null && logic.xCoord == x && logic.yCoord == y && logic.zCoord == z)
            {
                logic.setToolname(toolName);
            }
        }
    }

}

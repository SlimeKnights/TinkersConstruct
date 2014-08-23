package tconstruct.util.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import mantle.common.network.AbstractPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tconstruct.tools.logic.CarvingTableLogic;
import tconstruct.tools.logic.ToolForgeLogic;
import tconstruct.tools.logic.ToolStationLogic;

public class CarvingTablePacket extends AbstractPacket
{

    private int x, y, z;
    private int patternID;
    private int patternMeta;

    public CarvingTablePacket ()
    {
    }

    public CarvingTablePacket (int x, int y, int z, int pID, int pM)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.patternID = pID;
        this.patternMeta = pM;
    }

    @Override
    public void encodeInto (ChannelHandlerContext ctx, ByteBuf buffer)
    {
        buffer.writeInt(x);
        buffer.writeInt(y);
        buffer.writeInt(z);
        buffer.writeInt(patternID);
        buffer.writeInt(patternMeta);
    }

    @Override
    public void decodeInto (ChannelHandlerContext ctx, ByteBuf buffer)
    {
        x = buffer.readInt();
        y = buffer.readInt();
        z = buffer.readInt();
        patternID = buffer.readInt();
        patternMeta = buffer.readInt();
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
            if(patternID >= 0)
            {
                ctl.currentPattern = new ItemStack(Item.getItemById(patternID), 1, patternMeta);
            }
            else
            {
                ctl.currentPattern = null;
            }
            ctl.buildTopPart();
            ctl.buildBottomPart();
        }
    }
}

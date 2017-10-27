package slimeknights.tconstruct.smeltery.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import slimeknights.mantle.network.AbstractPacketThreadsafe;
import slimeknights.tconstruct.smeltery.tileentity.TileChannel;

public class ChannelConnectionPacket extends AbstractPacketThreadsafe {
  protected BlockPos pos;
  protected EnumFacing side;
  protected boolean connect;

  public ChannelConnectionPacket() {}

  public ChannelConnectionPacket(BlockPos pos, EnumFacing side, boolean connect) {
    this.pos = pos;
    this.side = side;
    this.connect = connect;
  }

  @Override
  public void handleClientSafe(NetHandlerPlayClient netHandler) {
    TileEntity te = Minecraft.getMinecraft().world.getTileEntity(pos);
    if(te instanceof TileChannel) {
      ((TileChannel) te).updateConnection(side, connect);
    }
  }

  @Override
  public void handleServerSafe(NetHandlerPlayServer netHandler) {
    // clientside only
    throw new UnsupportedOperationException("Serverside only");
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    pos = readPos(buf);
    side = EnumFacing.getFront(buf.readByte());
    connect = buf.readBoolean();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    writePos(pos, buf);
    buf.writeByte(side.getIndex());
    buf.writeBoolean(connect);
  }
}

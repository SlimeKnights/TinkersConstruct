package slimeknights.tconstruct.smeltery.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import slimeknights.mantle.network.AbstractPacketThreadsafe;
import slimeknights.tconstruct.smeltery.tileentity.TileSmeltery;

public class SmelteryFluidUpdatePacket extends AbstractPacketThreadsafe {

  public BlockPos pos;
  public List<FluidStack> liquids;

  public SmelteryFluidUpdatePacket() {
  }

  public SmelteryFluidUpdatePacket(BlockPos pos, List<FluidStack> liquids) {
    this.pos = pos;
    this.liquids = liquids;
  }

  @Override
  public void handleClientSafe(NetHandlerPlayClient netHandler) {
    TileEntity te = Minecraft.getMinecraft().theWorld.getTileEntity(pos);
    if(te instanceof TileSmeltery) {
      TileSmeltery smeltery = (TileSmeltery) te;
      smeltery.updateFluidsFromPacket(liquids);
    }
  }

  @Override
  public void handleServerSafe(NetHandlerPlayServer netHandler) {
    // Clientside only
    throw new UnsupportedOperationException("Clientside only");
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    pos = readPos(buf);
    int size = buf.readInt();
    liquids = new ArrayList<FluidStack>(size);
    for(int i = 0; i < size; i++) {
      NBTTagCompound fluidTag = ByteBufUtils.readTag(buf);
      FluidStack liquid = FluidStack.loadFluidStackFromNBT(fluidTag);
      liquids.add(liquid);
    }
  }

  @Override
  public void toBytes(ByteBuf buf) {
    writePos(pos, buf);
    buf.writeInt(liquids.size());
    for(FluidStack liquid : liquids) {
      NBTTagCompound fluidTag = new NBTTagCompound();
      liquid.writeToNBT(fluidTag);
      ByteBufUtils.writeTag(buf, fluidTag);
    }
  }
}

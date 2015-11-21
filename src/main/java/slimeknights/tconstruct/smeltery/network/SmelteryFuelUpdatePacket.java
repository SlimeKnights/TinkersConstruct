package slimeknights.tconstruct.smeltery.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import io.netty.buffer.ByteBuf;
import slimeknights.mantle.network.AbstractPacketThreadsafe;
import slimeknights.tconstruct.smeltery.tileentity.TileSmeltery;

// Sent to the client when the smeltery consumes fuel
public class SmelteryFuelUpdatePacket extends AbstractPacketThreadsafe {

  BlockPos pos;
  BlockPos tank;
  int temperature;
  FluidStack fuel;

  public SmelteryFuelUpdatePacket() {
  }

  public SmelteryFuelUpdatePacket(BlockPos pos, BlockPos tank, int temperature, FluidStack fuel) {
    this.pos = pos;
    this.tank = tank;
    this.temperature = temperature;
    this.fuel = fuel;
  }

  @Override
  public void handleClientSafe(NetHandlerPlayClient netHandler) {
    TileEntity te = Minecraft.getMinecraft().theWorld.getTileEntity(pos);
    if(te instanceof TileSmeltery) {
      TileSmeltery smeltery = (TileSmeltery) te;
      smeltery.currentFuel = fuel;
      smeltery.currentTank = tank;
      smeltery.updateTemperatureFromPacket(temperature);
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
    tank = readPos(buf);

    temperature = buf.readInt();

    NBTTagCompound fluidTag = ByteBufUtils.readTag(buf);
    fuel = FluidStack.loadFluidStackFromNBT(fluidTag);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    writePos(pos, buf);
    writePos(tank, buf);

    buf.writeInt(temperature);

    NBTTagCompound fluidTag = new NBTTagCompound();
    fuel.writeToNBT(fluidTag);
    ByteBufUtils.writeTag(buf, fluidTag);
  }
}

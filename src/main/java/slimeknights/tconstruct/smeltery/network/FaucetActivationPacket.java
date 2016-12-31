package slimeknights.tconstruct.smeltery.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;

import slimeknights.tconstruct.smeltery.tileentity.TileFaucet;

/** Sent to clients to activate the faucet animation clientside */
public class FaucetActivationPacket extends FluidUpdatePacket {

  public FaucetActivationPacket() {
  }

  public FaucetActivationPacket(BlockPos pos, FluidStack fluid) {
    super(pos, fluid);
  }

  @Override
  public void handleClientSafe(NetHandlerPlayClient netHandler) {
    TileEntity te = Minecraft.getMinecraft().world.getTileEntity(pos);
    if(te instanceof TileFaucet) {
      ((TileFaucet) te).onActivationPacket(fluid);
    }
  }
}

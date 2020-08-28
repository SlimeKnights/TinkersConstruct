package slimeknights.tconstruct.smeltery.network;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.network.NetworkEvent;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.mantle.util.TileEntityHelper;
import slimeknights.tconstruct.library.smeltery.ISmelteryTankHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SmelteryFluidUpdatePacket implements IThreadsafePacket {
  protected final BlockPos pos;
  public List<FluidStack> liquids;

  public SmelteryFluidUpdatePacket(BlockPos pos, List<FluidStack> liquids) {
    this.pos = pos;
    this.liquids = liquids;
  }

  public SmelteryFluidUpdatePacket(PacketBuffer buffer) {
    this.pos = buffer.readBlockPos();
    int count = buffer.readVarInt();
    List<FluidStack> fluids = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      fluids.add(FluidStack.readFromPacket(buffer));
    }
    this.liquids = fluids;
  }
  @Override
  public void handleThreadsafe(NetworkEvent.Context context) {
    HandleClient.handle(this);
  }

  @Override
  public void encode(PacketBuffer buffer) {
    buffer.writeBlockPos(pos);
    buffer.writeVarInt(liquids.size());
    for (FluidStack fluidStack : liquids) {
      fluidStack.writeToPacket(buffer);
    }
  }

  private static class HandleClient {
    private static void handle(SmelteryFluidUpdatePacket packet) {
      TileEntityHelper.getTile(ISmelteryTankHandler.class, Minecraft.getInstance().world, packet.pos).ifPresent(te -> te.updateFluidsFromPacket(packet.liquids));
    }
  }
}

package slimeknights.tconstruct.tools.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.fluids.FluidStack;
import slimeknights.mantle.compat.neoforged.neoforge.network.NetworkEvent.Context;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.tools.menu.ToolContainerMenu;

/** Packet used when a fluid is changed inside a tool container menu */
public record ToolContainerFluidUpdatePacket(FluidStack fluid) implements IThreadsafePacket {
  public ToolContainerFluidUpdatePacket(FriendlyByteBuf buffer) {
    this(FluidStack.OPTIONAL_STREAM_CODEC.decode((RegistryFriendlyByteBuf)buffer));
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    FluidStack.OPTIONAL_STREAM_CODEC.encode((RegistryFriendlyByteBuf)buffer, fluid);
  }

  @Override
  public void handleThreadsafe(Context context) {
    Player player = SafeClientAccess.getPlayer();
    if (player != null && player.containerMenu instanceof ToolContainerMenu toolMenu) {
      toolMenu.getTank().setFluid(fluid);
    }
  }
}

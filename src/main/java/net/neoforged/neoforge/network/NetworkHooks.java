package slimeknights.tconstruct.compat.neoforged.neoforge.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;

import java.util.function.Consumer;

public final class NetworkHooks {
  private NetworkHooks() {}

  public static Packet<ClientGamePacketListener> getEntitySpawningPacket(Entity entity) {
    return new ClientboundAddEntityPacket(entity, 0, entity.blockPosition());
  }

  public static void openScreen(ServerPlayer player, MenuProvider provider, BlockPos pos) {
    player.openMenu(provider, buffer -> buffer.writeBlockPos(pos));
  }

  public static void openScreen(ServerPlayer player, MenuProvider provider, Consumer<RegistryFriendlyByteBuf> extraDataWriter) {
    player.openMenu(provider, extraDataWriter);
  }
}

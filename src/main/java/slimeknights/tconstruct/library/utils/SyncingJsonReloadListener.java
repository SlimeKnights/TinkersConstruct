package slimeknights.tconstruct.library.utils;

import com.google.gson.Gson;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import slimeknights.mantle.network.NetworkWrapper;

/**
 * Extension of JsonReloadListener which syncs loaded data to the client on player connect
 */
public abstract class SyncingJsonReloadListener extends JsonDataLoader {
  private final NetworkWrapper network;
  public SyncingJsonReloadListener(NetworkWrapper network, Gson gson, String folder) {
    super(gson, folder);
    this.network = network;
  }

  /**
   * Gets the packet to send on player login
   * @return  Packet object
   */
  protected abstract Object getUpdatePacket();

  /**
   * Called when the player joins the server to send them a list of materials
   * @param event  Player logged in event
   */
  public void handleLogin(ServerPlayNetworkHandler networkHandler, PacketSender sender, MinecraftServer server) {
    ServerPlayerEntity player = networkHandler.player;
    if (player != null) {
      network.send(PacketDistributor.PLAYER.with(() -> player), getUpdatePacket());
    }
  }
}

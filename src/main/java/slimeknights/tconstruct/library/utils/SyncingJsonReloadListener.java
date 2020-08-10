package slimeknights.tconstruct.library.utils;

import com.google.gson.Gson;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import slimeknights.mantle.network.NetworkWrapper;

/**
 * Extension of JsonReloadListener which syncs loaded data to the client on player connect
 */
public abstract class SyncingJsonReloadListener extends JsonReloadListener {
  private final NetworkWrapper network;
  public SyncingJsonReloadListener(NetworkWrapper network, Gson gson, String folder) {
    super(gson, folder);
    this.network = network;
    MinecraftForge.EVENT_BUS.addListener(this::updatePlayerMaterials);
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
  private void updatePlayerMaterials(PlayerLoggedInEvent event) {
    PlayerEntity player = event.getPlayer();
    if (player instanceof ServerPlayerEntity) {
      ServerPlayerEntity serverPlayer = (ServerPlayerEntity)player;
      network.send(PacketDistributor.PLAYER.with(() -> serverPlayer), getUpdatePacket());
    }
  }
}

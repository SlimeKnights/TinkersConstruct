package slimeknights.tconstruct.library.capability.piggyback;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import slimeknights.tconstruct.library.network.TinkerNetwork;

import java.util.List;

public class TinkerPiggybackHandler implements ITinkerPiggyback {

  private PlayerEntity riddenPlayer;
  private List<Entity> lastPassengers;

  @Override
  public void setRiddenPlayer(PlayerEntity player) {
    this.riddenPlayer = player;
  }

  @Override
  public void updatePassengers() {
    if (this.riddenPlayer != null) {
      // tell the player itself if his riders changed serverside
      if (!this.riddenPlayer.getPassengerList().equals(this.lastPassengers)) {
        if (this.riddenPlayer instanceof ServerPlayerEntity) {
          TinkerNetwork.getInstance().sendVanillaPacket(this.riddenPlayer, new EntityPassengersSetS2CPacket(this.riddenPlayer));
        }
      }
      this.lastPassengers = this.riddenPlayer.getPassengerList();
    }
  }
}

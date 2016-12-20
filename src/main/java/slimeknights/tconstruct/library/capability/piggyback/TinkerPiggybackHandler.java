package slimeknights.tconstruct.library.capability.piggyback;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketSetPassengers;

import java.util.List;

import slimeknights.tconstruct.common.TinkerNetwork;

public class TinkerPiggybackHandler implements ITinkerPiggyback {

  private EntityPlayer riddenPlayer;
  private List<Entity> lastPassengers;

  @Override
  public void setRiddenPlayer(EntityPlayer entityPlayer) {
    riddenPlayer = entityPlayer;
  }

  @Override
  public void updatePassengers() {
    if(riddenPlayer != null) {
      // tell the player itself if his riders changed serverside
      if(!riddenPlayer.getPassengers().equals(lastPassengers)) {
        if(riddenPlayer instanceof EntityPlayerMP) {
          TinkerNetwork.sendPacket(riddenPlayer, new SPacketSetPassengers(riddenPlayer));
        }
      }
      lastPassengers = riddenPlayer.getPassengers();
    }
  }

}

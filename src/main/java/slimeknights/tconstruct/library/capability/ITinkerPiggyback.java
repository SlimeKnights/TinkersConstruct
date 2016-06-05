package slimeknights.tconstruct.library.capability;

import net.minecraft.entity.player.EntityPlayer;

public interface ITinkerPiggyback {
  void setRiddenPlayer(EntityPlayer player);
  void updatePassengers();
}

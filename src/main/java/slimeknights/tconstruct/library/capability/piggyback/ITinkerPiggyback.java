package slimeknights.tconstruct.library.capability.piggyback;

import net.minecraft.entity.player.EntityPlayer;

public interface ITinkerPiggyback {

  void setRiddenPlayer(EntityPlayer player);

  void updatePassengers();
}

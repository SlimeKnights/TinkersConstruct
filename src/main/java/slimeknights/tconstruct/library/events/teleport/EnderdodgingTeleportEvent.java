package slimeknights.tconstruct.library.events.teleport;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.entity.living.EntityTeleportEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/** Event fired when an entity teleports using the enderporting modifier */
@Cancelable
public class EnderdodgingTeleportEvent extends EntityTeleportEvent {
  public EnderdodgingTeleportEvent(LivingEntity entity, double targetX, double targetY, double targetZ) {
    super(entity, targetX, targetY, targetZ);
  }
}

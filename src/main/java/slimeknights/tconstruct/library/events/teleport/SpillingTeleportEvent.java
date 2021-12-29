package slimeknights.tconstruct.library.events.teleport;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/** Event fired when an entity teleports via the spilling effect */
@Cancelable
public class SpillingTeleportEvent extends EntityTeleportEvent {
  public SpillingTeleportEvent(Entity entity, double targetX, double targetY, double targetZ) {
    super(entity, targetX, targetY, targetZ);
  }
}

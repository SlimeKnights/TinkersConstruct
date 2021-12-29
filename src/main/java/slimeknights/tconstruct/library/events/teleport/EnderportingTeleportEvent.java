package slimeknights.tconstruct.library.events.teleport;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/** Event fired when an entity teleports using the enderporting modifier */
@Cancelable
public class EnderportingTeleportEvent extends EntityTeleportEvent {
  public EnderportingTeleportEvent(LivingEntity entity, double targetX, double targetY, double targetZ) {
    super(entity, targetX, targetY, targetZ);
  }
}

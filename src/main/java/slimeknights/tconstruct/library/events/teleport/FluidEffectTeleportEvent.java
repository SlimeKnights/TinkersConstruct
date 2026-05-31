package slimeknights.tconstruct.library.events.teleport;

import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import slimeknights.tconstruct.compat.neoforged.bus.api.Cancelable;
import slimeknights.tconstruct.library.utils.TeleportHelper.ITeleportEventFactory;

/** Event fired when an entity teleports via the fluid effect */
@Cancelable
public class FluidEffectTeleportEvent extends EntityTeleportEvent {
  public static final ITeleportEventFactory TELEPORT_FACTORY = FluidEffectTeleportEvent::new;

  public FluidEffectTeleportEvent(Entity entity, double targetX, double targetY, double targetZ) {
    super(entity, targetX, targetY, targetZ);
  }
}

package slimeknights.tconstruct.library.events.teleport;

import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.living.EntityTeleportEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import slimeknights.tconstruct.world.entity.EnderSlimeEntity;

/* Fired when an ender slime teleport or teleports another entity */
@Cancelable
public class EnderSlimeTeleportEvent extends EntityTeleportEvent {
  /** Gets the slime that caused this teleport. If this is the same as {@link #getEntity()} then the slime is teleporting itself */
  @Getter
  private final EnderSlimeEntity slime;

  public EnderSlimeTeleportEvent(Entity entity, double targetX, double targetY, double targetZ, EnderSlimeEntity slime) {
    super(entity, targetX, targetY, targetZ);
    this.slime = slime;
  }

  /** Checks if the enderslime is teleporting itself */
  public boolean isTeleportingSelf() {
    return getEntity() == slime;
  }
}

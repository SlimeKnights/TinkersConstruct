package slimeknights.tconstruct.library.events.teleport;

import lombok.Getter;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;

/** Event fired when an entity teleports via a tool modifier. Subclasses may have more context. */
@Cancelable
@Getter
public class ModifierTeleportEvent extends EntityTeleportEvent {
  private final ModifierEntry modifier;
  public ModifierTeleportEvent(Entity entity, double targetX, double targetY, double targetZ, ModifierEntry modifier) {
    super(entity, targetX, targetY, targetZ);
    this.modifier = modifier;
  }
}

package slimeknights.tconstruct.library.events.teleport;

import lombok.Getter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import slimeknights.tconstruct.compat.neoforged.bus.api.Cancelable;

/** @deprecated No longer used. See {@link SlingModifierTeleportEvent} */
@Deprecated(forRemoval = true)
@Cancelable
public class SlimeslingTeleportEvent extends EntityTeleportEvent {
  @Getter
  private final ItemStack sling;
  public SlimeslingTeleportEvent(Entity entity, double targetX, double targetY, double targetZ, ItemStack sling) {
    super(entity, targetX, targetY, targetZ);
    this.sling = sling;
  }
}

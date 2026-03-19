package slimeknights.tconstruct.library.events.teleport;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Cancelable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.tools.data.ModifierIds;

/**
 * Event fired when an entity teleports using {@link slimeknights.tconstruct.tools.modifiers.traits.general.EnderportingModifier}
 * @deprecated replacing with {@link ModifierTeleportEvent} in the future.
 */
@SuppressWarnings("DeprecatedIsStillUsed")
@Cancelable
@Deprecated
public class EnderportingTeleportEvent extends ModifierTeleportEvent {
  public EnderportingTeleportEvent(Entity entity, double targetX, double targetY, double targetZ, ModifierEntry modifier) {
    super(entity, targetX, targetY, targetZ, modifier);
  }

  /** @deprecated use {@link #EnderdodgingTeleportEvent(Entity, double, double, double, ModifierEntry)} */
  @Deprecated(forRemoval = true)
  public EnderportingTeleportEvent(Entity entity, double targetX, double targetY, double targetZ) {
    this(entity, targetX, targetY, targetZ, new ModifierEntry(ModifierIds.enderclearance, 1));
  }
}

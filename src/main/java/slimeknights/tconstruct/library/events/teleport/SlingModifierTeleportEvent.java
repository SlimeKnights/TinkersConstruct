package slimeknights.tconstruct.library.events.teleport;

import lombok.Getter;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Cancelable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/** Event fired when an entity teleports using the ender sling modifier */
@Cancelable
@Getter
public class SlingModifierTeleportEvent extends ModifierTeleportEvent {
  private final IToolStackView tool;
  public SlingModifierTeleportEvent(Entity entity, double targetX, double targetY, double targetZ, IToolStackView tool, ModifierEntry entry) {
    super(entity, targetX, targetY, targetZ, entry);
    this.tool = tool;
  }

  /** @deprecated use {@link #getModifier()} */
  @Deprecated
  public ModifierEntry getEntry() {
    return getModifier();
  }
}

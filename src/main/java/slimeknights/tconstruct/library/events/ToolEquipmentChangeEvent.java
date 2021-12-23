package slimeknights.tconstruct.library.events;

import lombok.Getter;
import net.minecraftforge.event.entity.living.LivingEvent;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;

/** Event fired at the end of {@link EquipmentChangeContext}, contains parsed Tinker Tools for all armor and also fires on the client */
public class ToolEquipmentChangeEvent extends LivingEvent {
  @Getter
  private final EquipmentChangeContext context;
  public ToolEquipmentChangeEvent(EquipmentChangeContext context) {
    super(context.getEntity());
    this.context = context;
  }
}

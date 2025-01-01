package slimeknights.tconstruct.plugin.jsonthings.item;

import dev.gigaherz.jsonthings.things.events.FlexEventHandler;
import dev.gigaherz.jsonthings.things.events.IEventRunner;
import lombok.Getter;
import slimeknights.tconstruct.tools.item.RepairKitItem;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/** Item for custom repair kits */
public class FlexRepairKitItem extends RepairKitItem implements IEventRunner {
  private final Map<String,FlexEventHandler> eventHandlers = new HashMap<>();
  @Getter
  private final float repairAmount;
  public FlexRepairKitItem(Properties properties, float repairAmount) {
    super(properties);
    this.repairAmount = repairAmount;
  }


  /* not honestly sure what events do, but trivial to support */

  @Override
  public void addEventHandler(String name, FlexEventHandler flexEventHandler) {
    this.eventHandlers.put(name, flexEventHandler);
  }

  @Nullable
  @Override
  public FlexEventHandler getEventHandler(String name) {
    return this.eventHandlers.get(name);
  }
}

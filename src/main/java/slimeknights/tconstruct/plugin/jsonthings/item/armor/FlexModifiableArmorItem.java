package slimeknights.tconstruct.plugin.jsonthings.item.armor;

import dev.gigaherz.jsonthings.things.events.FlexEventHandler;
import dev.gigaherz.jsonthings.things.events.IEventRunner;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.item.armor.ModifiableArmorItem;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/** Common code for a modifiable armor item in JSON Things */
public class FlexModifiableArmorItem extends ModifiableArmorItem implements IEventRunner {
  private final Map<String,FlexEventHandler> eventHandlers = new HashMap<>();
  public FlexModifiableArmorItem(ArmorMaterial materialIn, ArmorItem.Type slot, Properties builderIn, ToolDefinition toolDefinition) {
    super(materialIn, slot, builderIn, toolDefinition);
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

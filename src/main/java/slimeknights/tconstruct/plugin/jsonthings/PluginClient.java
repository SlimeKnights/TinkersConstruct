package slimeknights.tconstruct.plugin.jsonthings;

import dev.gigaherz.jsonthings.things.client.ItemColorHandler;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.model.tools.ToolModel;

/** Handles anything that requires clientside class loading */
public class PluginClient {
  public static void init() {
    ItemColorHandler.register(TConstruct.resourceString("tool"), block -> ToolModel.COLOR_HANDLER);
  }
}

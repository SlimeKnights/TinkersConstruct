package slimeknights.tconstruct.tools;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.tables.client.inventory.library.ToolBuildScreenInfo;

import java.util.Map;
import java.util.Set;

public class ToolRegistry {

  private static final Set<ToolCore> toolStationCrafting = Sets.newLinkedHashSet();
  private static final Set<ToolCore> toolForgeCrafting = Sets.newLinkedHashSet();

  /** Adds a tool to the Crafting UI of both the Tool Station as well as the Tool Forge */
  public static void registerToolCrafting(ToolCore tool) {
    registerToolStationCrafting(tool);
    registerToolForgeCrafting(tool);
  }

  /** Adds a tool to the Crafting UI of the Tool Station */
  public static void registerToolStationCrafting(ToolCore tool) {
    toolStationCrafting.add(tool);
  }

  public static Set<ToolCore> getToolStationCrafting() {
    return ImmutableSet.copyOf(toolStationCrafting);
  }

  /** Adds a tool to the Crafting UI of the Tool Forge */
  public static void registerToolForgeCrafting(ToolCore tool) {
    toolForgeCrafting.add(tool);
  }

  public static Set<ToolCore> getToolForgeCrafting() {
    return ImmutableSet.copyOf(toolForgeCrafting);
  }

  private static final Map<Item, ToolBuildScreenInfo> toolBuildInfo = Maps.newLinkedHashMap();

  @OnlyIn(Dist.CLIENT)
  public static void addToolBuilding(ToolBuildScreenInfo info) {
    toolBuildInfo.put(info.tool.getItem(), info);
  }

  @OnlyIn(Dist.CLIENT)
  public static ToolBuildScreenInfo getToolBuildInfoForTool(Item tool) {
    return toolBuildInfo.get(tool);
  }
}

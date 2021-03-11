package slimeknights.tconstruct.tools.harvest;

import com.google.common.collect.Sets;
import net.minecraft.block.material.Material;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.AOEToolHarvestLogic;

import java.util.Set;

public class PickaxeTool extends HarvestTool {
  protected static final Set<Material> EXTRA_MATERIALS = Sets.newHashSet(Material.ROCK, Material.IRON, Material.ANVIL);
  public static final AOEToolHarvestLogic HARVEST_LOGIC = new MaterialHarvestLogic(EXTRA_MATERIALS, 1, 1, 1);
  public PickaxeTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public AOEToolHarvestLogic getToolHarvestLogic() {
    return HARVEST_LOGIC;
  }
}

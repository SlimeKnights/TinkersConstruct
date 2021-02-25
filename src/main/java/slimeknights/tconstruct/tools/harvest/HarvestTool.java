package slimeknights.tconstruct.tools.harvest;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.AOEToolHarvestLogic;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.Set;

/**
 * Simple class that swaps the harvest logic for the AOE logic
 */
public class HarvestTool extends ToolCore {
  public HarvestTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public AOEToolHarvestLogic getToolHarvestLogic() {
    return AOEToolHarvestLogic.SMALL_TOOL;
  }

  /** Extension of AOE to sub in a material effective list */
  public static class MaterialHarvestLogic extends AOEToolHarvestLogic {
    private final Set<Material> materials;
    public MaterialHarvestLogic(Set<Material> materials, int width, int height, int depth) {
      super(width, height, depth);
      this.materials = materials;
    }

    @Override
    public boolean isEffectiveAgainst(ToolStack tool, ItemStack stack, BlockState state) {
      return materials.contains(state.getMaterial()) || super.isEffectiveAgainst(tool, stack, state);
    }
  }
}

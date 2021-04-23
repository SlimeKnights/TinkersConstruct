package slimeknights.tconstruct.tools.harvest;

import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.helper.aoe.RectangleAOEHarvestLogic;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.Set;

/**
 * Simple class that swaps the harvest logic for the AOE logic
 */
public class HarvestTool extends ToolCore {
  public HarvestTool(Settings properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public ToolHarvestLogic getToolHarvestLogic() {
    return RectangleAOEHarvestLogic.SMALL;
  }

  /** Extension of AOE to sub in a material effective list */
  public static class MaterialHarvestLogic extends RectangleAOEHarvestLogic {
    private final Set<Material> materials;
    public MaterialHarvestLogic(Set<Material> materials, int extraWidth, int extraHeight, int extraDepth) {
      super(extraWidth, extraHeight, extraDepth);
      this.materials = materials;
    }

    @Override
    public boolean isEffectiveAgainst(ToolStack tool, ItemStack stack, BlockState state) {
      return materials.contains(state.getMaterial()) || super.isEffectiveAgainst(tool, stack, state);
    }
  }
}

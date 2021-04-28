package slimeknights.tconstruct.tools.harvest.broad;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.helper.aoe.VeiningAOEHarvestLogic;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.harvest.HarvestTool;
import slimeknights.tconstruct.tools.harvest.PickaxeTool;

public class VeinHammerTool extends HarvestTool {
  private static final VeiningAOEHarvestLogic HARVEST_LOGIC = new VeiningAOEHarvestLogic(2) {
    @Override
    public boolean isEffectiveAgainst(IModifierToolStack tool, ItemStack stack, BlockState state) {
      return PickaxeTool.EXTRA_MATERIALS.contains(state.getMaterial()) || super.isEffectiveAgainst(tool, stack, state);
    }
  };

  public VeinHammerTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public ToolHarvestLogic getToolHarvestLogic() {
    return HARVEST_LOGIC;
  }
}

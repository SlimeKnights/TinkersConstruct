package slimeknights.tconstruct.tools.item.small;

import com.google.common.collect.Sets;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.helper.aoe.DepthAOEHarvestLogic;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import java.util.Set;

public class PickaxeTool extends HarvestTool {
  public static final Set<Material> EXTRA_MATERIALS = Sets.newHashSet(Material.ROCK, Material.IRON, Material.ANVIL);
  public static final DepthAOEHarvestLogic HARVEST_LOGIC = new DepthAOEHarvestLogic(0, 0) {
    @Override
    public boolean isEffectiveAgainst(IModifierToolStack tool, ItemStack stack, BlockState state) {
      return EXTRA_MATERIALS.contains(state.getMaterial()) || super.isEffectiveAgainst(tool, stack, state);
    }
  };

  public PickaxeTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public ToolHarvestLogic getToolHarvestLogic() {
    return HARVEST_LOGIC;
  }
}

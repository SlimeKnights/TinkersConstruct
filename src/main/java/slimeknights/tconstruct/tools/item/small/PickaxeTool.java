package slimeknights.tconstruct.tools.item.small;

import com.google.common.collect.Sets;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.helper.aoe.DepthAOEHarvestLogic;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import java.util.Set;

import net.minecraft.world.item.Item.Properties;

public class PickaxeTool extends HarvestTool {
  public static final Set<Material> EXTRA_MATERIALS = Sets.newHashSet(Material.STONE, Material.METAL, Material.HEAVY_METAL);
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

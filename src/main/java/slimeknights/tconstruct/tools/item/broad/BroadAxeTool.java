package slimeknights.tconstruct.tools.item.broad;

import com.google.common.collect.Sets;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.helper.aoe.TreeAOEHarvestLogic;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.item.small.HandAxeTool;

import java.util.Set;

public class BroadAxeTool extends HandAxeTool {
  private static final Set<Material> EXTRA_MATERIALS = Sets.newHashSet(Material.WOOD, Material.NETHER_WOOD, Material.BAMBOO, Material.GOURD);
  private static final TreeAOEHarvestLogic HARVEST_LOGIC = new TreeAOEHarvestLogic(0, 0, 5) {
    @Override
    public boolean isEffectiveAgainst(IModifierToolStack tool, ItemStack stack, BlockState state) {
      return EXTRA_MATERIALS.contains(state.getMaterial()) || super.isEffectiveAgainst(tool, stack, state);
    }
  };

  public BroadAxeTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public ToolHarvestLogic getToolHarvestLogic() {
    return HARVEST_LOGIC;
  }
}

package slimeknights.tconstruct.tools.item.small;

import com.google.common.collect.Sets;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ToolType;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import java.util.Set;

public class DaggerTool extends SwordTool {
  public static final ToolHarvestLogic HARVEST_LOGIC = new HarvestLogic();
  private static final Set<Material> EFFECTIVE_MATERIALS = Sets.newHashSet(Material.WOOL, Material.CACTUS, Material.PLANTS, Material.TALL_PLANTS, Material.NETHER_PLANTS, Material.OCEAN_PLANT);

  public DaggerTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public ToolHarvestLogic getToolHarvestLogic() {
    return HARVEST_LOGIC;
  }

  /** Adds more effective blocks */
  public static class HarvestLogic extends SwordTool.HarvestLogic {
    @Override
    public boolean isEffectiveAgainst(IModifierToolStack tool, ItemStack stack, BlockState state) {
      return state.isToolEffective(ToolType.HOE) || EFFECTIVE_MATERIALS.contains(state.getMaterial()) || super.isEffectiveAgainst(tool, stack, state);
    }
  }
}

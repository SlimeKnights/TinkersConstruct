package slimeknights.tconstruct.tools.item.small;

import com.google.common.collect.Sets;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ToolType;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import java.util.Set;

import net.minecraft.item.Item.Properties;

public class DaggerTool extends SwordTool {
  public static final ToolHarvestLogic HARVEST_LOGIC = new HarvestLogic();
  private static final Set<Material> EFFECTIVE_MATERIALS = Sets.newHashSet(Material.WOOL, Material.CACTUS, Material.PLANT, Material.REPLACEABLE_PLANT, Material.REPLACEABLE_FIREPROOF_PLANT, Material.WATER_PLANT);

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

package slimeknights.tconstruct.tools.item.small;

import com.google.common.collect.Sets;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.helper.aoe.VeiningAOEHarvestLogic;
import slimeknights.tconstruct.library.tools.item.ToolItem;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.Set;

public class MattockTool extends ToolItem {
  private static final Set<Material> EXTRA_MATERIALS = Sets.newHashSet(Material.GRASS, Material.WOOD, Material.NETHER_WOOD, Material.BAMBOO, Material.VEGETABLE);
  public static final VeiningAOEHarvestLogic HARVEST_LOGIC = new VeiningAOEHarvestLogic(0) {
    @Override
    public boolean isEffectiveAgainst(IModifierToolStack tool, ItemStack stack, BlockState state) {
      return EXTRA_MATERIALS.contains(state.getMaterial()) || super.isEffectiveAgainst(tool, stack, state);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState blockState) {
      if(!stack.hasTag()) {
        return 1f;
      }
      ToolStack tool = ToolStack.from(stack);
      if (tool.isBroken()) {
        return 0.3f;
      }
      if (!isEffective(tool, stack, blockState)) {
        return 1f;
      }
      // slower when a non-shovel block
      float speed = tool.getStats().getFloat(ToolStats.MINING_SPEED);
//      if (!blockState.isToolEffective(ToolType.SHOVEL)) { TODO
//        speed = Math.max(1f, (speed * tool.getStats().getFloat(ToolStats.SECONDARY_MINING)));
//      }
      return speed;
    }
  };

  public MattockTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public ToolHarvestLogic getToolHarvestLogic() {
    return HARVEST_LOGIC;
  }
}

package slimeknights.tconstruct.tools.item.small;

import com.google.common.collect.Sets;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ToolType;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.helper.aoe.VeiningAOEHarvestLogic;
import slimeknights.tconstruct.library.tools.item.ToolItem;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.Set;

public class MattockTool extends ToolItem {
  private static final Set<Material> EXTRA_MATERIALS = Sets.newHashSet(Material.ORGANIC, Material.WOOD, Material.NETHER_WOOD, Material.BAMBOO, Material.GOURD);
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
      // TODO: general modifiable
      ToolStack tool = ToolStack.from(stack);
      if (tool.isBroken()) {
        return 0.3f;
      }
      if (!isEffective(tool, stack, blockState)) {
        return 1f;
      }
      // slower when a non-shovel block
      float speed = tool.getStats().getFloat(ToolStats.MINING_SPEED);
      if (!blockState.isToolEffective(ToolType.SHOVEL)) {
        speed *= 0.75f;
      }
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

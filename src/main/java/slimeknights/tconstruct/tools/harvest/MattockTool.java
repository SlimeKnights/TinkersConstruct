package slimeknights.tconstruct.tools.harvest;

import com.google.common.collect.Sets;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.helper.aoe.VeiningAOEHarvestLogic;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.harvest.HarvestTool.MaterialHarvestLogic;

import java.util.Collections;
import java.util.Set;

public class MattockTool extends ToolCore {
  private static final Set<Material> EXTRA_MATERIALS = Sets.newHashSet(Material.WOOD, Material.NETHER_WOOD, Material.BAMBOO, Material.GOURD);
  public static final MaterialHarvestLogic HARVEST_LOGIC = new MaterialHarvestLogic(EXTRA_MATERIALS, 0, 0, 0) {
    @Override
    public Iterable<BlockPos> getAOEBlocks(ToolStack tool, ItemStack stack, PlayerEntity player, BlockState state, World world, BlockPos origin, Direction sideHit, AOEMatchType matchType) {
      if (!canAOE(tool, stack, state, matchType)) {
        return Collections.emptyList();
      }
      return VeiningAOEHarvestLogic.calculate(world, origin, tool.getModifierLevel(TinkerModifiers.expanded.get()));
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
      float speed = tool.getStats().getMiningSpeed();
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

  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    return getToolHarvestLogic().transformBlocks(context, ToolType.SHOVEL, SoundEvents.ITEM_SHOVEL_FLATTEN, true);
  }
}

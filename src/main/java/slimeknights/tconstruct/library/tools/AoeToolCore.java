package slimeknights.tconstruct.library.tools;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.tinkering.IAoeTool;
import slimeknights.tconstruct.library.tools.helper.AoeToolInteractionUtil;
import slimeknights.tconstruct.library.tools.helper.ToolMiningLogic;

import javax.annotation.Nonnull;

public abstract class AoeToolCore extends ToolCore implements IAoeTool {

  public AoeToolCore(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  protected AoeToolCore(Properties properties, ToolDefinition toolDefinition, ToolMiningLogic toolMiningLogic) {
    super(properties, toolDefinition, toolMiningLogic);
  }

  @Override
  public ImmutableList<BlockPos> getAOEBlocks(@Nonnull ItemStack stack, World world, PlayerEntity player, BlockPos origin) {
    return AoeToolInteractionUtil.calculateAOEBlocks(stack, world, player, origin, 1, 1, 1);
  }

  @Override
  public boolean isAoeHarvestTool() {
    return true;
  }
}

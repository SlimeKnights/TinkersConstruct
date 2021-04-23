package slimeknights.tconstruct.tools.harvest;

import com.google.common.collect.Sets;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.helper.aoe.VeiningAOEHarvestLogic;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.Collections;
import java.util.Set;

public class PickaxeTool extends HarvestTool {
  protected static final Set<Material> EXTRA_MATERIALS = Sets.newHashSet(Material.ROCK, Material.IRON, Material.ANVIL);
  public static final MaterialHarvestLogic HARVEST_LOGIC = new MaterialHarvestLogic(EXTRA_MATERIALS, 0, 0, 0) {
    @Override
    public Iterable<BlockPos> getAOEBlocks(ToolStack tool, ItemStack stack, PlayerEntity player, BlockState state, World world, BlockPos origin, Direction sideHit, AOEMatchType matchType) {
      if (!canAOE(tool, stack, state, matchType)) {
        return Collections.emptyList();
      }
      // veining block breaking
      return VeiningAOEHarvestLogic.calculate(world, origin, tool.getModifierLevel(TinkerModifiers.expanded.get()));
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

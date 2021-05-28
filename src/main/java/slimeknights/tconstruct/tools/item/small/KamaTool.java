package slimeknights.tconstruct.tools.item.small;

import com.google.common.collect.Sets;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.helper.aoe.CircleAOEHarvestLogic;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.Set;

public class KamaTool extends HarvestTool {
  /** Tool harvest logic to damage when breaking instant break blocks */
  public static final ToolHarvestLogic HARVEST_LOGIC = new HarvestLogic(1, true);

  public KamaTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public ToolHarvestLogic getToolHarvestLogic() {
    return HARVEST_LOGIC;
  }
  
  /** Harvest logic to match shears and hoes */
  public static class HarvestLogic extends CircleAOEHarvestLogic {
    private static final Set<Material> EFFECTIVE_MATERIALS = Sets.newHashSet(
      Material.LEAVES, Material.WEB, Material.WOOL, Material.CACTUS,
      Material.PLANTS, Material.TALL_PLANTS, Material.NETHER_PLANTS, Material.OCEAN_PLANT);

    public HarvestLogic(int diameter, boolean is3D) {
      super(diameter, is3D);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState blockState) {
      float speed = super.getDestroySpeed(stack, blockState);
      if (blockState.getMaterial() == Material.WOOL) {
        speed /= 3;
      }
      return speed;
    }

    @Override
    public boolean isEffectiveAgainst(IModifierToolStack tool, ItemStack stack, BlockState state) {
      return state.getBlock() == Blocks.TRIPWIRE || EFFECTIVE_MATERIALS.contains(state.getMaterial()) || super.isEffectiveAgainst(tool, stack, state);
    }

    @Override
    public int getDamage(ToolStack tool, ItemStack stack, World world, BlockPos pos, BlockState state) {
      return state.isIn(BlockTags.FIRE) ? 0 : 1;
    }
  }
}

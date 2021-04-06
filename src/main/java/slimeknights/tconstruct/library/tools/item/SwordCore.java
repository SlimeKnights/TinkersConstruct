package slimeknights.tconstruct.library.tools.item;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

/**
 * Shared logic for all sword types
 */
public abstract class SwordCore extends ToolCore {
  public static final ToolType TOOL_TYPE = ToolType.get("sword");
  public static final ImmutableSet<Material> EFFECTIVE_MATERIALS = ImmutableSet.of(Material.WEB, Material.TALL_PLANTS, Material.CORAL, Material.GOURD, Material.LEAVES);
  public static final ToolHarvestLogic HARVEST_LOGIC = new HarvestLogic();

  public SwordCore(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public ToolHarvestLogic getToolHarvestLogic() {
    return HARVEST_LOGIC;
  }

  @Override
  public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
    return !player.isCreative();
  }

  /** Harvest logic for swords */
  public static class HarvestLogic extends ToolHarvestLogic {
    @Override
    public boolean isEffectiveAgainst(ToolStack tool, ItemStack stack, BlockState state) {
      // no sword tool type by default, so augment with vanilla list
      return EFFECTIVE_MATERIALS.contains(state.getMaterial()) || super.isEffectiveAgainst(tool, stack, state);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
      // webs are slow
      float speed = super.getDestroySpeed(stack, state);
      if (state.getMaterial() == Material.WEB) {
        speed *= 7.5f;
      }
      return speed;
    }
  }
}

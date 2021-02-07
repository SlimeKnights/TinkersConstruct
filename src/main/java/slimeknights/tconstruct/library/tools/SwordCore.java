package slimeknights.tconstruct.library.tools;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class SwordCore extends ToolCore {

  public static final ImmutableSet<Material> EFFECTIVE_MATERIALS =
    ImmutableSet.of(Material.WEB,
      Material.TALL_PLANTS,
      Material.CORAL,
      Material.GOURD,
      Material.LEAVES);

  public SwordCore(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public boolean canHarvestBlock(BlockState state) {
    return EFFECTIVE_MATERIALS.contains(state.getMaterial());
  }

  @Override
  public float getDestroySpeed(ItemStack stack, BlockState state) {
    if (state.getBlock() == Blocks.COBWEB) {
      return super.getDestroySpeed(stack, state) * 7.5f;
    }

    return super.getDestroySpeed(stack, state);
  }

  @Override
  public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
    return !player.isCreative();
  }
}

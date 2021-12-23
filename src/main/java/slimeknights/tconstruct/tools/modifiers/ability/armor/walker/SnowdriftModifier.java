package slimeknights.tconstruct.tools.modifiers.ability.armor.walker;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class SnowdriftModifier extends AbstractWalkerModifier {
  public SnowdriftModifier() {
    super(-1);
  }

  @Override
  public int getPriority() {
    return 90; // after frostwalker
  }

  @Override
  protected float getRadius(IModifierToolStack tool, int level) {
    return 1.5f + tool.getModifierLevel(TinkerModifiers.expanded.get());
  }

  @Override
  protected void walkOn(IModifierToolStack tool, int level, LivingEntity living, World world, BlockPos target, Mutable mutable) {
    BlockState snow = Blocks.SNOW.getDefaultState();
    if (world.isAirBlock(target) && world.getBiome(target).getTemperature(target) < 0.8F && snow.isValidPosition(world, target)) {
      world.setBlockState(target, snow);
    }
  }
}

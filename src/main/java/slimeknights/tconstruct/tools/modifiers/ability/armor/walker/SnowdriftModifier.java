package slimeknights.tconstruct.tools.modifiers.ability.armor.walker;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;

/** @deprecated {@link slimeknights.tconstruct.library.modifiers.modules.armor.CoverGroundWalkerModule} */
@Deprecated
public class SnowdriftModifier extends AbstractWalkerModifier {
  @Override
  public int getPriority() {
    return 90; // after frostwalker
  }

  @Override
  protected float getRadius(IToolStackView tool, int level) {
    return 1.5f + tool.getModifierLevel(TinkerModifiers.expanded.getId());
  }

  @Override
  protected void walkOn(IToolStackView tool, int level, LivingEntity living, Level world, BlockPos target, MutableBlockPos mutable) {
    BlockState snow = Blocks.SNOW.defaultBlockState();
    if (world.isEmptyBlock(target) && snow.canSurvive(world, target)) {
      world.setBlockAndUpdate(target, snow);
    }
  }
}

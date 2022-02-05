package slimeknights.tconstruct.tools.modifiers.ability.interaction;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolActions;

public class PathingModifier extends BlockTransformModifier {
  public PathingModifier(int priority) {
    super(priority, ToolActions.SHOVEL_FLATTEN, SoundEvents.SHOVEL_FLATTEN, true);
  }

  @Override
  protected boolean transform(UseOnContext context, BlockState original, boolean playSound) {
    if (super.transform(context, original, playSound)) {
      return true;
    }

    // shovel special case: campfires
    if (original.getBlock() instanceof CampfireBlock && original.getValue(CampfireBlock.LIT)) {
      Level level = context.getLevel();
      BlockPos pos = context.getClickedPos();
      if (!level.isClientSide) {
        if (playSound) {
          level.playSound(null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        CampfireBlock.dowse(context.getPlayer(), level, pos, original);
      }
      level.setBlock(pos, original.setValue(CampfireBlock.LIT, false), Block.UPDATE_ALL_IMMEDIATE);
      return true;
    }
    return false;
  }
}

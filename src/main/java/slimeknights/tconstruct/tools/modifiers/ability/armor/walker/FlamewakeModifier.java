package slimeknights.tconstruct.tools.modifiers.ability.armor.walker;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class FlamewakeModifier extends AbstractWalkerModifier {
  @Override
  protected float getRadius(IToolStackView tool, int level) {
    return 1.5f + tool.getModifierLevel(TinkerModifiers.expanded.get());
  }

  @Override
  protected void walkOn(IToolStackView tool, int level, LivingEntity living, Level world, BlockPos target, MutableBlockPos mutable) {
    // fire starting
    if (BaseFireBlock.canBePlacedAt(world, target, living.getDirection())) {
      world.playSound(null, target, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, RANDOM.nextFloat() * 0.4F + 0.8F);
      world.setBlock(target, BaseFireBlock.getState(world, target), Block.UPDATE_ALL_IMMEDIATE);
      ToolDamageUtil.damageAnimated(tool, 1, living, EquipmentSlot.FEET);
    }
  }
}

package slimeknights.tconstruct.tools.modifiers.ability.armor.walker;

import lombok.RequiredArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.ToolAction;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;

@RequiredArgsConstructor
public class BlockTransformWalkerModifier extends AbstractWalkerModifier {
  private final ToolAction action;
  private final SoundEvent sound;

  @Override
  protected float getRadius(IToolStackView tool, int level) {
    return 1.5f + tool.getModifierLevel(TinkerModifiers.expanded.get());
  }

  @Override
  public boolean canPerformAction(IToolStackView tool, int level, ToolAction toolAction) {
    return toolAction == this.action;
  }

  @Override
  public void onWalk(IToolStackView tool, int level, LivingEntity living, BlockPos prevPos, BlockPos newPos) {
    if (living instanceof Player) {
      super.onWalk(tool, level, living, prevPos, newPos);
    }
  }

  @Override
  protected void walkOn(IToolStackView tool, int level, LivingEntity living, Level world, BlockPos target, MutableBlockPos mutable) {
    Material material = world.getBlockState(target).getMaterial();
    if (material.isReplaceable() || material == Material.PLANT) {
      mutable.set(target.getX(), target.getY() - 1, target.getZ());
      BlockState original = world.getBlockState(mutable);
      BlockState transformed = original.getToolModifiedState(world, mutable, (Player)living, living.getItemBySlot(EquipmentSlot.FEET), action);
      if (transformed != null) {
        world.setBlock(mutable, transformed, Block.UPDATE_ALL_IMMEDIATE);
        world.destroyBlock(target, true);
        world.playSound(null, mutable, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
        ToolDamageUtil.damageAnimated(tool, 1, living, EquipmentSlot.FEET);
      }
    }
  }
}

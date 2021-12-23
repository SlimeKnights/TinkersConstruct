package slimeknights.tconstruct.tools.modifiers.ability.armor.walker;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.Constants;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class BlockTransformWalkerModifier extends AbstractWalkerModifier {
  private final ToolType toolType;
  private final SoundEvent sound;
  public BlockTransformWalkerModifier(int color, ToolType toolType, SoundEvent sound) {
    super(color);
    this.toolType = toolType;
    this.sound = sound;
  }

  @Override
  protected float getRadius(IModifierToolStack tool, int level) {
    return 1.5f + tool.getModifierLevel(TinkerModifiers.expanded.get());
  }

  @Override
  public void onWalk(IModifierToolStack tool, int level, LivingEntity living, BlockPos prevPos, BlockPos newPos) {
    if (living instanceof PlayerEntity) {
      super.onWalk(tool, level, living, prevPos, newPos);
    }
  }

  @Override
  protected void walkOn(IModifierToolStack tool, int level, LivingEntity living, World world, BlockPos target, Mutable mutable) {
    Material material = world.getBlockState(target).getMaterial();
    if (material.isReplaceable() || material == Material.PLANTS) {
      mutable.setPos(target.getX(), target.getY() - 1, target.getZ());
      BlockState original = world.getBlockState(mutable);
      BlockState transformed = original.getToolModifiedState(world, mutable, (PlayerEntity)living, living.getItemStackFromSlot(EquipmentSlotType.FEET), toolType);
      if (transformed != null) {
        world.setBlockState(mutable, transformed, Constants.BlockFlags.DEFAULT_AND_RERENDER);
        world.destroyBlock(target, true);
        world.playSound(null, mutable, sound, SoundCategory.BLOCKS, 1.0F, 1.0F);
        ToolDamageUtil.damageAnimated(tool, 1, living, EquipmentSlotType.FEET);
      }
    }
  }
}

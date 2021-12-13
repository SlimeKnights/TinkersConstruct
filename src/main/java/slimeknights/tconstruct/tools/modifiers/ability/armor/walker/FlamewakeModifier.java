package slimeknights.tconstruct.tools.modifiers.ability.armor.walker;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class FlamewakeModifier extends AbstractWalkerModifier {
  public FlamewakeModifier() {
    super(0xC35F01);
  }

  @Override
  protected float getRadius(IModifierToolStack tool, int level) {
    return 1.5f + tool.getModifierLevel(TinkerModifiers.expanded.get());
  }

  @Override
  protected void walkOn(IModifierToolStack tool, int level, LivingEntity living, World world, BlockPos target, Mutable mutable) {
    // fire starting
    if (AbstractFireBlock.canLightBlock(world, target, living.getHorizontalFacing())) {
      world.playSound(null, target, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, RANDOM.nextFloat() * 0.4F + 0.8F);
      world.setBlockState(target, AbstractFireBlock.getFireForPlacement(world, target), Constants.BlockFlags.DEFAULT_AND_RERENDER);
      ToolDamageUtil.damageAnimated(tool, 1, living, EquipmentSlotType.FEET);
    }
  }
}

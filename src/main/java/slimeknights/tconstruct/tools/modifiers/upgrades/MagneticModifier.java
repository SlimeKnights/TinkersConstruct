package slimeknights.tconstruct.tools.modifiers.upgrades;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class MagneticModifier extends Modifier {
  public MagneticModifier() {
    super(0x720000);
  }

  @Override
  public void afterBlockBreak(IModifierToolStack tool, int level, World world, BlockState state, BlockPos pos, LivingEntity living, boolean wasEffective) {
    TinkerModifiers.magneticEffect.get().apply(living, 30, level - 1);
  }

  @Override
  public int afterLivingHit(IModifierToolStack tool, int level, LivingEntity attacker, LivingEntity target, float damageDealt, boolean isCritical, boolean fullyCharged) {
    TinkerModifiers.magneticEffect.get().apply(attacker, 30, level - 1);
    return 0;
  }
}

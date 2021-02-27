package slimeknights.tconstruct.tools.modifiers.traits;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class MomentumModifier extends Modifier {
  public MomentumModifier() {
    super(0x60496b);
  }

  @Override
  public int getPriority() {
    // run this last as we boost original speed, adds to existing boosts
    return 90;
  }

  @Override
  public void onBreakSpeed(IModifierToolStack tool, int level, BreakSpeed event) {
    // 50% boost per level at max
    int effectLevel = TinkerModifiers.momentumEffect.get().getLevel(event.getEntityLiving()) + 1;
    event.setNewSpeed(event.getNewSpeed() + event.getOriginalSpeed() * level * effectLevel / 64f);
  }

  @Override
  public void afterBlockBreak(IModifierToolStack tool, int level, World world, BlockState state, BlockPos pos, LivingEntity living, boolean wasEffective) {
    // 16 blocks gets you to max, levels faster at higher levels
    int effectLevel = Math.min(31, TinkerModifiers.momentumEffect.get().getLevel(living) + 1);
    // funny formula from 1.12, guess it makes faster tools have a slightly shorter effect
    int duration = (int) ((10f / tool.getStats().getMiningSpeed()) * 1.5f * 20f);
    TinkerModifiers.momentumEffect.get().apply(living, duration, effectLevel);
  }
}

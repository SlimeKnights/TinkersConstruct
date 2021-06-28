package slimeknights.tconstruct.tools.modifiers.traits;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class MomentumModifier extends Modifier {
  public MomentumModifier() {
    super(0x60496b);
  }

  @Override
  public int getPriority() {
    // run this last as we boost original speed, adds to existing boosts
    return 75;
  }

  @Override
  public void onBreakSpeed(IModifierToolStack tool, int level, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
    if (isEffective) {
      // 50% boost per level at max
      int effectLevel = TinkerModifiers.momentumEffect.get().getLevel(event.getEntityLiving()) + 1;
      event.setNewSpeed(event.getNewSpeed() * (1 + level * effectLevel / 64f));
    }
  }

  @Override
  public void afterBlockBreak(IModifierToolStack tool, int level, ToolHarvestContext context) {
    if (context.canHarvest() && context.isEffective() && !context.isAOE()) {
      // 16 blocks gets you to max, levels faster at higher levels
      LivingEntity living = context.getLiving();
      int effectLevel = Math.min(31, TinkerModifiers.momentumEffect.get().getLevel(living) + 1);
      // funny formula from 1.12, guess it makes faster tools have a slightly shorter effect
      int duration = (int) ((10f / tool.getStats().getFloat(ToolStats.MINING_SPEED)) * 1.5f * 20f);
      TinkerModifiers.momentumEffect.get().apply(living, duration, effectLevel);
    }
  }
}

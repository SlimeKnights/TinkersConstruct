package slimeknights.tconstruct.library.modifiers.hook.mining;

import net.minecraft.core.Direction;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

/**
 * Hook used to increase mining speed of a block conditioned on the environment or the player.
 * <br>
 * Alternatives:
 * <ul>
 *   <li>{@link slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook}: Limited context, but effect shows in the tooltip automatically.</li>
 *   <li>{@link TooltipModifierHook}: Allows adding bonus effects to the tooltip.</li>
 * </ul>
 */
public interface BreakSpeedModifierHook {
  /**
   * Called when break speed is being calculated to affect mining speed conditionally.
   * @param tool                 Current tool instance
   * @param modifier             Modifier level
   * @param event                Event instance
   * @param sideHit              Side of the block that was hit
   * @param isEffective          If true, the tool is effective against this block type
   * @param miningSpeedModifier  Calculated modifier from potion effects such as haste and environment such as water, use for additive bonuses to ensure consistency with the mining speed stat
   * @deprecated use {@link #modifyBreakSpeed(IToolStackView, ModifierEntry, BreakSpeedContext, float)}. Overriding is okay.
   */
  @Deprecated
  void onBreakSpeed(IToolStackView tool, ModifierEntry modifier, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier);

  /**
   * Called when break speed is being calculated to affect mining speed conditionally.
   * @param tool                 Current tool instance
   * @param modifier             Modifier level
   * @param context   Context about the block being broken
   * @param speed     Speed as modified by other modifiers.
   * @return new speed as modified by this hook.
   */
  default float modifyBreakSpeed(IToolStackView tool, ModifierEntry modifier, BreakSpeedContext context, float speed) {
    BreakSpeed event = context.event();
    event.setNewSpeed(speed);
    onBreakSpeed(tool, modifier, event, context.sideHit(), context.isEffective(), context.miningSpeedMultiplier());
    return event.isCanceled() ? -1 : event.getNewSpeed();
  }


  /** Merger that runs each hook in succession */
  record AllMerger(Collection<BreakSpeedModifierHook> modules) implements BreakSpeedModifierHook {
    @Override
    public void onBreakSpeed(IToolStackView tool, ModifierEntry modifier, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
      for (BreakSpeedModifierHook module : modules) {
        module.onBreakSpeed(tool, modifier, event, sideHit, isEffective, miningSpeedModifier);
      }
    }

    @Override
    public float modifyBreakSpeed(IToolStackView tool, ModifierEntry modifier, BreakSpeedContext context, float speed) {
      for (BreakSpeedModifierHook module : modules) {
        speed = module.modifyBreakSpeed(tool, modifier, context, speed);
      }
      return speed;
    }
  }
}

package slimeknights.tconstruct.library.modifiers.hook.build;

import net.minecraft.core.Direction;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;

import java.util.Collection;

/**
 * Hook for adding direct stats to a tool. Stats show in the tooltip and need not be tied to attributes, plus are easier to query and have nicer builders.
 * Overall, its what Mojang really should have done for many attributes.
 */
public interface ToolStatsModifierHook {
  /**
   * Adds raw stats to the tool. Called whenever tool stats are rebuilt.
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link AttributesModifierHook}: Allows dynamic stats based on any tool stat, but does not support mining speed, mining level, or durability.</li>
   *   <li>{@link slimeknights.tconstruct.library.modifiers.Modifier#onBreakSpeed(IToolStackView, int, BreakSpeed, Direction, boolean, float)}: Allows dynamic mining speed based on the block mined and the entity mining. Will not show in tooltips.</li>
   * </ul>
   * @param context         Context about the tool beilt. Partial view of {@link IToolStackView} as the tool is not fully built. Note this hook runs after volatile data builds
   * @param modifier        Modifier level
   * @param builder         Tool stat builder
   */
  void addToolStats(ToolRebuildContext context, ModifierEntry modifier, ModifierStatsBuilder builder);

  /** Merger that runs all hooks */
  record AllMerger(Collection<ToolStatsModifierHook> modules) implements ToolStatsModifierHook {
    @Override
    public void addToolStats(ToolRebuildContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
      for (ToolStatsModifierHook module : modules) {
        module.addToolStats(context, modifier, builder);
      }
    }
  }
}

package slimeknights.tconstruct.library.modifiers.hook.build;

import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import java.util.Collection;

/**
 * Hook to add NBT to a tool that is reset every time modifiers or stats need a refresh. Ideal for storing data to communicate between modifiers or cache stat calculations.
 */
public interface VolatileDataModifierHook {
  /**
   * Adds any relevant volatile data to the tool data. This data is rebuilt every time modifiers rebuild.
   * <br>
   * Alternatives:
   * <ul>
   *   <li>Persistent mod data (accessed via {@link IToolStackView}): Can be written to freely, but will not automatically remove if the modifier is removed.</li>
   *   <li>{@link RawDataModifierHook}: Allows modifying a restricted view of the tools main data, might help with other mod compat, but not modifier compat</li>
   * </ul>
   * @param context         Context about the tool beilt. Partial view of {@link IToolStackView} as the tool is not fully built
   * @param modifier        Modifier level
   * @param volatileData    Mutable mod NBT data, result of this method
   */
  void addVolatileData(ToolRebuildContext context, ModifierEntry modifier, ModDataNBT volatileData);

  /** Merger that runs all hooks */
  record AllMerger(Collection<VolatileDataModifierHook> modules) implements VolatileDataModifierHook {
    @Override
    public void addVolatileData(ToolRebuildContext context, ModifierEntry modifier, ModDataNBT volatileData) {
      for (VolatileDataModifierHook module : modules) {
        module.addVolatileData(context, modifier, volatileData);
      }
    }
  }
}

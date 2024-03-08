package slimeknights.tconstruct.library.modifiers.hook.build;

import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.RestrictedCompoundTag;

import java.util.Collection;

/**
 * Hook for adding and removing raw NBT on a tool. Used when mods rely on NBT for their API rather than something nice like tool actions.
 * It is generally better to use volatile data when possible, or persistent data if you need it preserved between rebuilds.
 */
public interface RawDataModifierHook {
  /**
   * Allows editing a restricted view of the tools raw NBT. You are responsible for cleaning up that data on removal via {@link #removeRawData(IToolStackView, Modifier, RestrictedCompoundTag)}.
   * This may be called when your data is already on the tool, ensure running multiple times in a row will give the same result.
   * In most cases volatile data via {@link VolatileDataModifierHook} is a much better choice, only use this hook if you have no other choice.
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link VolatileDataModifierHook}: Modifier data that automatically cleans up when the modifier is removed.</li>
   * </ul>
   * @param tool      Tool stack instance
   * @param modifier  Modifier entry of the modifier
   * @param tag       Mutable tag, will not allow modifiying any important tool stat
   */
  void addRawData(IToolStackView tool, ModifierEntry modifier, RestrictedCompoundTag tag);

  /**
   * Called when this modifier is about to be removed to remove any raw data added. At this time stats are not yet rebuild and the modifier is still on the tool.
   * Mainly exists to work with the raw tool NBT, as it's a lot more difficult for multiple modifiers to collaborate on that.
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link ModifierRemovalHook}: Called after the modifier is removed and stat are rebuilt without it. Typically a better choice for working with persistent NBT</li>
   *   <li>{@link VolatileDataModifierHook}: Adds NBT that is automatically removed</li>
   *   <li>{@link ValidateModifierHook}: Allows marking a new state invalid</li>
   * </ul>
   * @param tool      Tool instance
   * @param modifier  Modifier being removed
   * @param tag       Modifiable data that can be edited now that this modifier will no longer be here
   */
  void removeRawData(IToolStackView tool, Modifier modifier, RestrictedCompoundTag tag);

  /** Merger that runs all hooks */
  record AllMerger(Collection<RawDataModifierHook> modules) implements RawDataModifierHook {
    @Override
    public void addRawData(IToolStackView tool, ModifierEntry modifier, RestrictedCompoundTag tag) {
      for (RawDataModifierHook module : modules) {
        module.addRawData(tool, modifier, tag);
      }
    }

    @Override
    public void removeRawData(IToolStackView tool, Modifier modifier, RestrictedCompoundTag tag) {
      for (RawDataModifierHook module : modules) {
        module.removeRawData(tool, modifier, tag);
      }
    }
  }
}

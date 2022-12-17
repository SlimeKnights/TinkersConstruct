package slimeknights.tconstruct.library.modifiers.hook;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.NamespacedNBT;

import java.util.Collection;
import java.util.function.Function;

/**
 * Hook for modifiers which wish to modify an arrow before its fired
 */
public interface ArrowLaunchModifierHook {
  /** Default instance */
  ArrowLaunchModifierHook EMPTY = (tool, modifier, shooter, arrow, persistentData) -> {};
  /** Merger instance */
  Function<Collection<ArrowLaunchModifierHook>,ArrowLaunchModifierHook> ALL_MERGER = AllMerger::new;

  /**
   * Hook to modify arrow properties after an arrow is fired. Called serverside only, so randomness is safe.
   * @param tool            Bow instance
   * @param modifier        Modifier being used
   * @param shooter         Entity firing the arrow
   * @param arrow           Created arrow to modify
   * @param persistentData  Persistent data instance stored on the arrow to write arbitrary data. Note the modifier list was already written
   */
  void onArrowLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, AbstractArrow arrow, NamespacedNBT persistentData);

  /** Logic to merge multiple hooks into one */
  record AllMerger(Collection<ArrowLaunchModifierHook> modules) implements ArrowLaunchModifierHook {
    @Override
    public void onArrowLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, AbstractArrow arrow, NamespacedNBT persistentData) {
      for (ArrowLaunchModifierHook module : modules) {
        module.onArrowLaunch(tool, modifier, shooter, arrow, persistentData);
      }
    }
  }
}

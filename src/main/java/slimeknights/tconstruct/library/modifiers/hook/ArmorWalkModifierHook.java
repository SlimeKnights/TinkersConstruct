package slimeknights.tconstruct.library.modifiers.hook;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;
import java.util.function.Function;

/**
 * Modifier hook for boots when the player walks.
 * TODO 1.19: move to {@link slimeknights.tconstruct.library.modifiers.hook.armor}
 */
public interface ArmorWalkModifierHook {
  /** Empty implementation for the sake of default behavior */
  ArmorWalkModifierHook EMPTY = (tool, modifier, living, prevPos, newPos) -> {};

  /** Constructor for a merger that runs all hooks from the children */
  Function<Collection<ArmorWalkModifierHook>, ArmorWalkModifierHook> ALL_MERGER = AllMerger::new;


  /**
   * Called when an entity's block position changes
   * @param tool     Tool in boots slot
   * @param modifier Entry calling this hook
   * @param living   Living entity instance
   * @param prevPos  Previous block position
   * @param newPos   New block position, will match the entity's position
   */
  void onWalk(IToolStackView tool, ModifierEntry modifier, LivingEntity living, BlockPos prevPos, BlockPos newPos);


  /** Walk modifier hook merger: runs hooks of all children */
  record AllMerger(Collection<ArmorWalkModifierHook> modules) implements ArmorWalkModifierHook {
    @Override
    public void onWalk(IToolStackView tool, ModifierEntry modifier, LivingEntity living, BlockPos prevPos, BlockPos newPos) {
      for (ArmorWalkModifierHook module : modules) {
        module.onWalk(tool, modifier, living, prevPos, newPos);
      }
    }
  }
}

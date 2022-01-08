package slimeknights.tconstruct.library.modifiers.hooks;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

/**
 * Modifier hook for boots when the player walks
 */
public interface IArmorWalkModifier {
  /**
   * Called when an entity's block position changes
   * @param tool     Tool in boots slot
   * @param level    Level of the modifier
   * @param living   Living entity instance
   * @param prevPos  Previous block position
   * @param newPos   New block position, will match the entity's position
   */
  void onWalk(IModifierToolStack tool, int level, LivingEntity living, BlockPos prevPos, BlockPos newPos);
}

package slimeknights.tconstruct.library.modifiers.hooks;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.BlockPos;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

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
  void onWalk(IToolStackView tool, int level, LivingEntity living, BlockPos prevPos, BlockPos newPos);
}

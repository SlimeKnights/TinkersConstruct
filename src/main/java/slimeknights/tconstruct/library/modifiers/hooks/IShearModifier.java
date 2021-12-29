package slimeknights.tconstruct.library.modifiers.hooks;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

/**
 * Interface that allows another modifier to hook into the shears modifier. Use with {@link slimeknights.tconstruct.library.modifiers.Modifier#getModule(Class)}
 */
public interface IShearModifier {

  /**
   * Called after a block is successfully harvested
   * @param tool     Tool used in harvesting
   * @param level    Modifier level
   * @param player   Player shearing
   * @param entity   Entity sheared
   * @param isTarget If true, the sheared entity was targeted. If false, this is AOE shearing
   */
  void afterShearEntity(IModifierToolStack tool, int level, Player player, Entity entity, boolean isTarget);
}

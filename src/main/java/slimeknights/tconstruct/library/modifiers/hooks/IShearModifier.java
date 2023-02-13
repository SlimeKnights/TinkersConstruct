package slimeknights.tconstruct.library.modifiers.hooks;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/**
 * Interface that allows another modifier to hook into the shears modifier. Use with {@link slimeknights.tconstruct.library.modifiers.Modifier#getModule(Class)}
 * @deprecated use {@link slimeknights.tconstruct.library.modifiers.hook.ShearsModifierHook}
 */
@SuppressWarnings("DeprecatedIsStillUsed")
@Deprecated
public interface IShearModifier {
  /**
   * Called after a block is successfully harvested
   * @param tool     Tool used in harvesting
   * @param level    Modifier level
   * @param player   Player shearing
   * @param entity   Entity sheared
   * @param isTarget If true, the sheared entity was targeted. If false, this is AOE shearing
   */
  void afterShearEntity(IToolStackView tool, int level, Player player, Entity entity, boolean isTarget);
}

package slimeknights.tconstruct.library.modifiers.hooks;

import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/**
 * Module for chestplate modifiers to control flight behavior
 * @deprecated use {@link slimeknights.tconstruct.library.modifiers.hook.ElytraFlightModifierHook}
 */
@SuppressWarnings("DeprecatedIsStillUsed")
@Deprecated
public interface IElytraFlightModifier {
  /**
   * Call on elytra flight tick to run any update effects
   * @param tool         Elytra instance
   * @param level        Modifier level
   * @param entity       Entity flying
   * @param flightTicks  Number of ticks the elytra has been in the air
   * @return  True if the elytra should keep flying
   */
  boolean elytraFlightTick(IToolStackView tool, int level, LivingEntity entity, int flightTicks);
}

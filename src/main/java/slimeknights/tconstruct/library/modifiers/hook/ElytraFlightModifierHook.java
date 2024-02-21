package slimeknights.tconstruct.library.modifiers.hook;

import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;
import java.util.function.Function;

/**
 * Hook for chestplate modifiers to control eltyra flight behavior.
 * TODO 1.19: move to {@link slimeknights.tconstruct.library.modifiers.hook.armor}
 */
public interface ElytraFlightModifierHook {
  /** Default implementation */
  ElytraFlightModifierHook DEFAULT = (tool, modifier, entity, flightTicks) -> true;

  /** Constructor for a merger that stops after the first hook returns */
  Function<Collection<ElytraFlightModifierHook>, ElytraFlightModifierHook> FIRST_MERGER = FirstMerger::new;


  /**
   * Call on elytra flight tick to run any update effects
   * @param tool         Elytra instance
   * @param modifier     Entry calling this hook
   * @param entity       Entity flying
   * @param flightTicks  Number of ticks the elytra has been in the air
   * @return  True if the elytra should stop flying
   */
  boolean elytraFlightTick(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int flightTicks);


  /** Flight merger: stops once the first hook says to stop flying */
  record FirstMerger(Collection<ElytraFlightModifierHook> modules) implements ElytraFlightModifierHook {
    @Override
    public boolean elytraFlightTick(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int flightTicks) {
      for (ElytraFlightModifierHook module : modules) {
        if (module.elytraFlightTick(tool, modifier, entity, flightTicks)) {
          return true;
        }
      }
      return false;
    }
  }
}

package slimeknights.tconstruct.library.modifiers.hook.armor;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

/**
 * Modifier hook for entirely preventing damage from a given source
 */
public interface DamageBlockModifierHook {
  /**
   * Checks if this modifier blocks damage from the given source.
   * <br/>
   * Alternatives:
   * <ul>
   *   <li>{@link ProtectionModifierHook}: Allows reducing damage from a source rather than completely blocking it. Reduced damage will still play the attack animation.</li>
   *   <li>{@link OnAttackedModifierHook}: Allows running logic that should take place on attack, such as counterattacks.</li>
   * </ul>
   * @param tool       Tool being used
   * @param modifier   Level of the modifier
   * @param context    Context of entity and other equipment
   * @param slotType   Slot containing the tool
   * @param source     Damage source causing the attack
   * @param amount     Amount of damage caused
   * @return True if this attack should be blocked entirely
   */
  boolean isDamageBlocked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount);

  /** Merger that returns true if any is successful */
  record AnyMerger(Collection<DamageBlockModifierHook> modules) implements DamageBlockModifierHook {
    @Override
    public boolean isDamageBlocked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount) {
      for (DamageBlockModifierHook module : modules) {
        if (module.isDamageBlocked(tool, modifier, context, slotType, source, amount)) {
          return true;
        }
      }
      return false;
    }
  }
}

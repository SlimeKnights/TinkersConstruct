package slimeknights.tconstruct.library.modifiers.hook.combat;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

/**
 * Hook called when attacked while wearing armor with this modifier, ideal for counterattacks or buffing the attack target.
 */
public interface DamageTakenModifierHook {
  /**
   * Runs after an entity is attacked (and we know the attack will land). Note you can attack the entity here, but you are responsible for preventing infinite recursion if you do so (by detecting your own attack source for instance)
   * <br/>
   * Alternatives:
   * <ul>
   *   <li>{@link DamageBlockModifierHook}: Allows canceling the attack entirely, including the hurt animation.</li>
   *   <li>{@link ProtectionModifierHook}: Allows reducing the attack damage.</li>
   * </ul>
   * @param tool             Tool being used
   * @param modifier         Level of the modifier
   * @param context          Context of entity and other equipment
   * @param slotType         Slot containing the tool
   * @param source           Damage source causing the attack
   * @param amount           Amount of damage caused
   * @param isDirectDamage   If true, this attack is direct damage from an entity
   */
  void onDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage);

  /** Merger that runs all submodules */
  record AllMerger(Collection<DamageTakenModifierHook> modules) implements DamageTakenModifierHook {
    @Override
    public void onDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
      for (DamageTakenModifierHook module : modules) {
        module.onDamageTaken(tool, modifier, context, slotType, source, amount, isDirectDamage);
      }
    }
  }
}

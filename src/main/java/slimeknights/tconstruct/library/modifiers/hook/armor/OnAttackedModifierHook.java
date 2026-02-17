package slimeknights.tconstruct.library.modifiers.hook.armor;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.EquipmentIterator.EquipmentEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

/** Hook called when attacked while wearing armor with this modifier, ideal for counterattacks or buffing the attack target. */
public interface OnAttackedModifierHook {
  /**
   * Runs after an entity is attacked (and we know the attack will land). Note you can attack the entity here, but you are responsible for preventing infinite recursion if you do so (by detecting your own attack source for instance)
   * <br/>
   * Alternatives:
   * <ul>
   *   <li>{@link DamageBlockModifierHook}: Allows canceling the attack entirely, including the hurt animation.</li>
   *   <li>{@link ProtectionModifierHook}: Allows reducing the attack damage.</li>
   *   <li>{@link ModifyDamageModifierHook}: Allows directly setting the attack damage, or responding after you are certain the attack lands.</li>
   * </ul>
   * @param tool             Tool being used
   * @param modifier         Level of the modifier
   * @param context          Context of entity and other equipment
   * @param slotType         Slot containing the tool
   * @param source           Damage source causing the attack
   * @param amount           Amount of damage caused
   * @param isDirectDamage   If true, this attack is direct damage from an entity
   */
  void onAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage);

  /** Merger that runs all submodules */
  record AllMerger(Collection<OnAttackedModifierHook> modules) implements OnAttackedModifierHook {
    @Override
    public void onAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
      for (OnAttackedModifierHook module : modules) {
        module.onAttacked(tool, modifier, context, slotType, source, amount, isDirectDamage);
      }
    }
  }

  /** Checks if the damage source is caused directly by another entity, as opposed to indirectly by a projectile */
  static boolean isDirectDamage(DamageSource source) {
    return source.getEntity() != null && !source.isIndirect() && !source.is(DamageTypeTags.AVOIDS_GUARDIAN_THORNS);
  }

  /**
   * Allows modifiers to respond to the entity being attacked
   * @param hook            Hook to use
   * @param context         Equipment context
   * @param source          Source of the damage
   * @param amount          Damage amount
   * @param isDirectDamage  If true, the damage source is applying directly
   */
  static void handleAttack(ModuleHook<OnAttackedModifierHook> hook, EquipmentContext context, DamageSource source, float amount, boolean isDirectDamage) {
    for (EquipmentEntry entry : context.iterateTools()) {
      ModifierEntry modifier = entry.modifier();
      modifier.getHook(hook).onAttacked(entry.tool(), modifier, context, entry.slot(), source, amount, isDirectDamage);
    }
  }
}

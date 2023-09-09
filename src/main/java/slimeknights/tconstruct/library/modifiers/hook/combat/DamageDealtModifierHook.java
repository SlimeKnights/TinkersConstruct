package slimeknights.tconstruct.library.modifiers.hook.combat;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

/**
 * Hook used when dealing damage while wearing armor with this modifier
 */
public interface DamageDealtModifierHook {
  /**
   * Called when an entity is attacked and this entity is the attacker and is wearing this equipment.
   * @param tool             Tool being used
   * @param modifier         Level of the modifier
   * @param context          Context of entity and other equipment
   * @param slotType         Slot containing the tool
   * @param target           Entity that was attacked
   * @param source           Damage source used in the attack
   * @param amount           Amount of damage caused
   * @param isDirectDamage   If true, this attack is direct damage from an entity
   */
  void onDamageDealt(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, LivingEntity target, DamageSource source, float amount, boolean isDirectDamage);

  /** Merger that runs all nested modules */
  record AllMerger(Collection<DamageDealtModifierHook> modules) implements DamageDealtModifierHook {
    @Override
    public void onDamageDealt(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, LivingEntity target, DamageSource source, float amount, boolean isDirectDamage) {
      for (DamageDealtModifierHook module : modules) {
        module.onDamageDealt(tool, modifier, context, slotType, target, source, amount, isDirectDamage);
      }
    }
  }
}

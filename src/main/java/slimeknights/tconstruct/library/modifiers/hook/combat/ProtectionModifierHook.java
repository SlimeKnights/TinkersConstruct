package slimeknights.tconstruct.library.modifiers.hook.combat;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.common.util.LazyOptional;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

/**
 * Hook for increasing or decreasing protection from a given damage source.
 */
public interface ProtectionModifierHook {
  /**
   * Gets the protection value of the armor from this modifier. A value of 1 blocks about 4% of damage, equivalent to 1 level of the protection enchantment.
   * Maximum effect is 80% reduction from a modifier value of 20. Can also go negative, up to 180% increase from a modifier value of -20
   * <br/>
   * Alternatives:
   * <ul>
   *   <li>{@link DamageBlockModifierHook}: Allows canceling the attack entirely, including the hurt animation.</li>
   *   <li>{@link DamageTakenModifierHook}: Allows running logic that should take place on attack, such as counterattacks.</li>
   * </ul>
   * @param tool            Worn armor
   * @param modifier        Modifier level
   * @param context         Equipment context of the entity wearing the armor
   * @param slotType        Slot containing the armor
   * @param source          Damage source
   * @param modifierValue   Modifier value from previous modifiers to add
   * @return  New modifier value
   */
  float getProtectionModifier(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float modifierValue);

  /** Gets the maximum protection amount on the given entity */
  static float getProtectionCap(LazyOptional<TinkerDataCapability.Holder> capability) {
    return Math.min(20 + capability.resolve().map(data -> data.get(TinkerDataKeys.PROTECTION_CAP)).orElse(0f), 25 * 0.95f);
  }

  /** Merger that combines all values */
  record AllMerger(Collection<ProtectionModifierHook> modules) implements ProtectionModifierHook {
    @Override
    public float getProtectionModifier(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float modifierValue) {
      for (ProtectionModifierHook module : modules) {
        modifierValue = module.getProtectionModifier(tool, modifier, context, slotType, source, modifierValue);
      }
      return modifierValue;
    }
  }
}

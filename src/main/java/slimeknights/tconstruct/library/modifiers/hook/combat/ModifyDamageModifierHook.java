package slimeknights.tconstruct.library.modifiers.hook.combat;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.Util;

import java.util.Collection;

/**
 * Hook called when attacked while wearing armor with this modifier, ideal for counterattacks or buffing the attack target.
 */
public interface ModifyDamageModifierHook {
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
   * @param amount           Amount of damage to be taken, as modified by previous hooks
   * @param isDirectDamage   If true, this attack is direct damage from an entity
   * @return  Replacement amount of damage, if 0 will stop further hooks
   */
  float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage);

  /** Merger that runs all submodules */
  record AllMerger(Collection<ModifyDamageModifierHook> modules) implements ModifyDamageModifierHook {
    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
      for (ModifyDamageModifierHook module : modules) {
        amount = module.modifyDamageTaken(tool, modifier, context, slotType, source, amount, isDirectDamage);
        if (amount <= 0) {
          break;
        }
      }
      return amount;
    }
  }

  /** Internal logic for {@link #modifyDamageTaken(ModifierHook, EquipmentContext, DamageSource, float, boolean)} */
  private static float modifyDamageTaken(ModifierHook<ModifyDamageModifierHook> hook, EquipmentContext context, DamageSource source, float amount, boolean isDirectDamage, EquipmentSlot slotType) {
    IToolStackView toolStack = context.getToolInSlot(slotType);
    if (toolStack != null && !toolStack.isBroken()) {
      for (ModifierEntry entry : toolStack.getModifierList()) {
        amount = entry.getHook(hook).modifyDamageTaken(toolStack, entry, context, slotType, source, amount, isDirectDamage);
        if (amount < 0) {
          return 0;
        }
      }
    }
    return amount;
  }

  /**
	 * Allows modifiers to respond to the entity being attacked
   * @param hook            Hook to use
   * @param context         Equipment context
   * @param source          Source of the damage
   * @param amount          Damage amount
   * @param isDirectDamage  If true, the damage source is applying directly
   */
  static float modifyDamageTaken(ModifierHook<ModifyDamageModifierHook> hook, EquipmentContext context, DamageSource source, float amount, boolean isDirectDamage) {
    // first we need to determine if any of the four slots want to cancel the event, then we need to determine if any want to respond assuming its not canceled
    for (EquipmentSlot slotType : ModifiableArmorMaterial.ARMOR_SLOTS) {
      amount = modifyDamageTaken(hook, context, source, amount, isDirectDamage, slotType);
      if (amount <= 0) {
        return 0;
      }
    }
    // shields only run this hook when blocking
    LivingEntity entity = context.getEntity();
    if (entity.isBlocking()) {
      amount = modifyDamageTaken(hook, context, source, amount, isDirectDamage, Util.getSlotType(entity.getUsedItemHand()));
    }
    return amount;
  }
}

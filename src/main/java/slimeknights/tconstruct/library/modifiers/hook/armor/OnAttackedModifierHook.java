package slimeknights.tconstruct.library.modifiers.hook.armor;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.Util;

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

  /** Internal logic for {@link #handleAttack(ModuleHook, EquipmentContext, DamageSource, float, boolean)} */
  private static void handleAttack(ModuleHook<OnAttackedModifierHook> hook, EquipmentContext context, DamageSource source, float amount, boolean isDirectDamage, EquipmentSlot slotType) {
    IToolStackView toolStack = context.getToolInSlot(slotType);
    if (toolStack != null && !toolStack.isBroken()) {
      for (ModifierEntry entry : toolStack.getModifierList()) {
        entry.getHook(hook).onAttacked(toolStack, entry, context, slotType, source, amount, isDirectDamage);
      }
    }
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
    // first we need to determine if any of the four slots want to cancel the event, then we need to determine if any want to respond assuming its not canceled
    for (EquipmentSlot slotType : ModifiableArmorMaterial.ARMOR_SLOTS) {
      handleAttack(hook, context, source, amount, isDirectDamage, slotType);
    }
    // run on both hands for shields, provided its a held tool (i.e. not armor)
    LivingEntity holder = context.getEntity();
    for (InteractionHand hand : InteractionHand.values()) {
      if (holder.getItemInHand(hand).is(TinkerTags.Items.HELD)) {
        handleAttack(hook, context, source, amount, isDirectDamage, Util.getSlotType(hand));
      }
    }
  }
}

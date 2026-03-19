package slimeknights.tconstruct.tools.modules.armor;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.CombatHelper;
import slimeknights.tconstruct.common.TinkerDamageTypes;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.modules.DamageOnUnequipModule;

import java.util.List;
import java.util.Set;

/**
 * Module to refresh help when changing equipment
 * @param bonus      Amount of extra health to add when equipping this. Removed when unequipping.
 * @param slots      Slots that apply this module
 * @param condition  Common modifier conditions
 */
public record UpdateHealthModule(LevelingValue bonus, Set<EquipmentSlot> slots, ModifierCondition<IToolStackView> condition) implements ModifierModule, EquipmentChangeModifierHook, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<DamageOnUnequipModule>defaultHooks(ModifierHooks.EQUIPMENT_CHANGE);
  public static final RecordLoadable<UpdateHealthModule> LOADER = RecordLoadable.create(
    LevelingValue.LOADABLE.defaultField("bonus", LevelingValue.ZERO, false, UpdateHealthModule::bonus),
    TinkerLoadables.EQUIPMENT_SLOT_SET.requiredField("slots", UpdateHealthModule::slots),
    ModifierCondition.TOOL_FIELD,
    UpdateHealthModule::new);

  public UpdateHealthModule(LevelingValue bonus, EquipmentSlot... slots) {
    this(bonus, ImmutableSet.copyOf(slots), ModifierCondition.ANY_TOOL);
  }

  @Override
  public RecordLoadable<UpdateHealthModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  /** Updates health on the target on change */
  private void updateHealth(LivingEntity entity, float bonus) {
    float maxHealth = entity.getMaxHealth();
    float newHealth = entity.getHealth() + bonus;
    // ensure new health is not greater than max
    if (newHealth > maxHealth) {
      entity.setHealth(maxHealth);
    } else if (bonus > 0) {
      // if healing, grant immediately
      entity.setHealth(newHealth);
    } else if (bonus < 0) {
      // if harming, apply using a hurt to trigger a death message if it kills you
      entity.hurt(CombatHelper.damageSource(entity.level(), TinkerDamageTypes.UPDATE_HEALTH), -bonus);
    }
  }

  @Override
  public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (condition.matches(tool, modifier) && slots.contains(context.getChangedSlot())) {
      Level level = context.getLevel();
      if (!level.isClientSide && EquipmentChangeModifierHook.didEquip(tool, context)) {
        updateHealth(context.getEntity(), bonus.compute(modifier));
      }
    }
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (condition.matches(tool, modifier) && slots.contains(context.getChangedSlot())) {
      Level level = context.getLevel();
      if (!level.isClientSide && EquipmentChangeModifierHook.didUnequip(tool, context)) {
        updateHealth(context.getEntity(), -bonus.compute(modifier));
      }
    }
  }
}

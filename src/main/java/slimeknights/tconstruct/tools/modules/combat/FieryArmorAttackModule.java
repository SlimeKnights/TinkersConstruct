package slimeknights.tconstruct.tools.modules.combat;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.damage.DamageSourcePredicate;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.DamageDealtModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/** Module for lighting the target on fire after an attack wearing this as armor */
public record FieryArmorAttackModule(LevelingInt time, IJsonPredicate<DamageSource> damageSource) implements ModifierModule, DamageDealtModifierHook {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<FieryArmorAttackModule>defaultHooks(ModifierHooks.DAMAGE_DEALT);
  public static final RecordLoadable<FieryArmorAttackModule> LOADER = RecordLoadable.create(
    LevelingInt.LOADABLE.requiredField("seconds", FieryArmorAttackModule::time),
    DamageSourcePredicate.LOADER.defaultField("damage_source", FieryArmorAttackModule::damageSource),
    FieryArmorAttackModule::new);

  @Override
  public RecordLoadable<FieryArmorAttackModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void onDamageDealt(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, LivingEntity target, DamageSource source, float amount, boolean isDirectDamage) {
    if (this.damageSource.matches(source)) {
      target.setSecondsOnFire(time.compute(modifier));
    }
  }
}

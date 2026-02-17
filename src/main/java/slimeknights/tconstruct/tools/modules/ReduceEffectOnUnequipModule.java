package slimeknights.tconstruct.tools.modules;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.loadable.primitive.EnumLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.LevelingValue;
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

import java.util.List;

/** Module to reduce duration of effects on unequip. Used to prevent an exploit with {@link slimeknights.tconstruct.shared.TinkerAttributes#GOOD_EFFECT_DURATION} */
public record ReduceEffectOnUnequipModule(MobEffectCategory category, LevelingValue percent, ModifierCondition<IToolStackView> condition) implements ModifierModule, EquipmentChangeModifierHook, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ReduceEffectOnUnequipModule>defaultHooks(ModifierHooks.EQUIPMENT_CHANGE);
  public static final RecordLoadable<ReduceEffectOnUnequipModule> LOADER = RecordLoadable.create(
    new EnumLoadable<>(MobEffectCategory.class).requiredField("category", ReduceEffectOnUnequipModule::category),
    LevelingValue.LOADABLE.requiredField("percent", ReduceEffectOnUnequipModule::percent),
    ModifierCondition.TOOL_FIELD,
    ReduceEffectOnUnequipModule::new);

  @Override
  public RecordLoadable<ReduceEffectOnUnequipModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    // must actually be removing, and run on both sides so we don't need to sync
    if (condition.matches(tool, modifier) && EquipmentChangeModifierHook.didUnequip(tool, context)) {
      LivingEntity entity = context.getEntity();
      float percent = this.percent.compute(modifier);
      if (percent != 0) {
        // iterate all matching effects, updating the duration
        for (MobEffectInstance instance : entity.getActiveEffects()) {
          if (!instance.isInfiniteDuration() && instance.getEffect().getCategory() == this.category && !instance.getCurativeItems().isEmpty()) {
            instance.duration = Math.max(1, (int) (instance.duration * (1 - percent)));
          }
        }
      }
    }
  }
}

package slimeknights.tconstruct.tools.modules;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
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
public record ClearEffectOnUnequipModule(MobEffect effect, ModifierCondition<IToolStackView> condition) implements ModifierModule, EquipmentChangeModifierHook, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ClearEffectOnUnequipModule>defaultHooks(ModifierHooks.EQUIPMENT_CHANGE);
  public static final RecordLoadable<ClearEffectOnUnequipModule> LOADER = RecordLoadable.create(
    Loadables.MOB_EFFECT.requiredField("effect", ClearEffectOnUnequipModule::effect),
    ModifierCondition.TOOL_FIELD,
    ClearEffectOnUnequipModule::new);

  @Override
  public RecordLoadable<ClearEffectOnUnequipModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    LivingEntity entity = context.getEntity();
    if (!entity.level().isClientSide && condition.matches(tool, modifier) && EquipmentChangeModifierHook.didUnequip(tool, context)) {
      entity.removeEffect(effect);
    }
  }
}

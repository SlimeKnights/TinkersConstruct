package slimeknights.tconstruct.tools.modules;

import net.minecraft.world.level.Level;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.common.TinkerDamageTypes;
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

/** Module to damage the player when taking off the equipment */
public record DamageOnUnequipModule(float damage, ModifierCondition<IToolStackView> condition) implements ModifierModule, EquipmentChangeModifierHook, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<DamageOnUnequipModule>defaultHooks(ModifierHooks.EQUIPMENT_CHANGE);
  public static final RecordLoadable<DamageOnUnequipModule> LOADER = RecordLoadable.create(
    FloatLoadable.FROM_ZERO.requiredField("damage", DamageOnUnequipModule::damage),
    ModifierCondition.TOOL_FIELD,
    DamageOnUnequipModule::new);

  @Override
  public RecordLoadable<DamageOnUnequipModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (condition.matches(tool, modifier)) {
      IToolStackView replacement = context.getReplacementTool();
      // modifier list changing is a good heuristic for tool changing, avoids tool just taking damage
      Level level = context.getLevel();
      if (!level.isClientSide && (replacement == null || replacement.getItem() != tool.getItem() || !replacement.getModifiers().equals(tool.getModifiers()))) {
        context.getEntity().hurt(TinkerDamageTypes.source(level.registryAccess(), TinkerDamageTypes.ENTANGLED), damage * modifier.getEffectiveLevel());
      }
    }
  }
}

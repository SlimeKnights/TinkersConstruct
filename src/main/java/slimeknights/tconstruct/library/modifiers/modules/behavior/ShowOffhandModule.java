package slimeknights.tconstruct.library.modifiers.modules.behavior;

import net.minecraft.world.entity.EquipmentSlot;
import slimeknights.mantle.data.loadable.primitive.EnumLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.unserializable.ArmorLevelModule;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;
import java.util.function.Function;

/** Module to show the offhand for a tool that can interact using the offhand */
public enum ShowOffhandModule implements ModifierModule, EquipmentChangeModifierHook {
  /** Mode which will always show the offhand */
  ALLOW_BROKEN,
  /** Mode which will only show the offhand when the tool is not broken */
  DISALLOW_BROKEN;

  private static final List<ModifierHook<?>> DEFAULT_HOOKS = ModifierModule.<ShowOffhandModule>defaultHooks(TinkerHooks.EQUIPMENT_CHANGE);
  public static final RecordLoadable<ShowOffhandModule> LOADER = RecordLoadable.create(new EnumLoadable<>(ShowOffhandModule.class).requiredField("mode", Function.identity()), Function.identity());

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (context.getChangedSlot() == EquipmentSlot.CHEST && (!tool.isBroken() || this == ALLOW_BROKEN)) {
      ArmorLevelModule.addLevels(context, TinkerDataKeys.SHOW_EMPTY_OFFHAND, 1);
    }
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (context.getChangedSlot() == EquipmentSlot.CHEST && (!tool.isBroken() || this == ALLOW_BROKEN)) {
      ArmorLevelModule.addLevels(context, TinkerDataKeys.SHOW_EMPTY_OFFHAND, -1);
    }
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }
}

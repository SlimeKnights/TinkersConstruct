package slimeknights.tconstruct.library.modifiers.modules.behavior;

import net.minecraft.world.entity.EquipmentSlot;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.GenericLoaderRegistry.SingletonLoader;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/** Module to show the offhand for a tool that can interact using the offhand */
public enum ShowOffhandModule implements ModifierModule, EquipmentChangeModifierHook {
  INSTANCE;

  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.EQUIPMENT_CHANGE);
  public static final IGenericLoader<ShowOffhandModule> LOADER = new SingletonLoader<>(INSTANCE);

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (context.getChangedSlot() == EquipmentSlot.CHEST) {
      ModifierUtil.addTotalArmorModifierLevel(tool, context, TinkerDataKeys.SHOW_EMPTY_OFFHAND, 1);
    }
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (context.getChangedSlot() == EquipmentSlot.CHEST) {
      ModifierUtil.addTotalArmorModifierLevel(tool, context, TinkerDataKeys.SHOW_EMPTY_OFFHAND, -1);
    }
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }
}

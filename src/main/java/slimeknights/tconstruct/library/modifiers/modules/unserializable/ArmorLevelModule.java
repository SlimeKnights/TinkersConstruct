package slimeknights.tconstruct.library.modifiers.modules.unserializable;

import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierHookProvider;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/**
 * Module for keeping track of the total level of a modifier across all pieces of equipment. Does not support incremental, use {@link ArmorStatModule} for that.
 * @see ArmorStatModule
 * @see TinkerDataKey
 * @see slimeknights.tconstruct.library.modifiers.modules.behavior.ShowOffhandModule
 */
public record ArmorLevelModule(TinkerDataKey<Integer> key, boolean allowBroken) implements ModifierHookProvider, EquipmentChangeModifierHook {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = ModifierModule.<ArmorLevelModule>defaultHooks(TinkerHooks.EQUIPMENT_CHANGE);

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    addLevelsIfArmor(tool, context, key, modifier.getLevel(), allowBroken);
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    addLevelsIfArmor(tool, context, key, -modifier.getLevel(), allowBroken);
  }


  /* Helpers */

  /**
   * Adds levels to the given key in entity modifier data for an armor modifier
   * @param context  Equipment change context
   * @param key      Key to modify
   * @param amount   Amount to add
   */
  public static void addLevels(EquipmentChangeContext context, TinkerDataKey<Integer> key, int amount) {
    context.getTinkerData().ifPresent(data -> {
      int totalLevels = data.get(key, 0) + amount;
      if (totalLevels <= 0) {
        data.remove(key);
      } else {
        data.put(key, totalLevels);
      }
    });
  }

  /**
   * Adds levels to the given key in entity modifier data for an armor modifier
   * @param tool     Tool instance
   * @param context  Equipment change context
   * @param key      Key to modify
   * @param amount   Amount to add
   */
  public static void addLevelsIfArmor(IToolStackView tool, EquipmentChangeContext context, TinkerDataKey<Integer> key, int amount, boolean allowBroken) {
    if (ModifierUtil.validArmorSlot(tool, context.getChangedSlot()) && (allowBroken || !tool.isBroken())) {
      addLevels(context, key, amount);
    }
  }

  /**
   * Gets the total level from the key in the entity modifier data
   * @param living  Living entity
   * @param key     Key to get
   * @return  Level from the key
   */
  public static int getLevel(LivingEntity living, TinkerDataKey<Integer> key) {
    return living.getCapability(TinkerDataCapability.CAPABILITY).resolve().map(data -> data.get(key)).orElse(0);
  }
}

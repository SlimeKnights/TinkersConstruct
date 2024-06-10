package slimeknights.tconstruct.library.modifiers.modules.technical;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.item.Item;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Module for keeping track of the max level of a modifier across all pieces of equipment.
 * @see TinkerDataKey
 * @see slimeknights.tconstruct.library.modifiers.modules.behavior.ShowOffhandModule
 */
public record MaxArmorLevelModule(TinkerDataKey<Float> key, boolean allowBroken, @Nullable TagKey<Item> heldTag) implements HookProvider, EquipmentChangeModifierHook, ModifierModule {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<MaxArmorLevelModule>defaultHooks(ModifierHooks.EQUIPMENT_CHANGE);
  public static final RecordLoadable<MaxArmorLevelModule> LOADER = RecordLoadable.create(
    TinkerDataKeys.FLOAT_REGISTRY.requiredField("data_key", MaxArmorLevelModule::key),
    BooleanLoadable.INSTANCE.defaultField("allow_broken", false, MaxArmorLevelModule::allowBroken),
    Loadables.ITEM_TAG.nullableField("held_tag", MaxArmorLevelModule::heldTag),
    MaxArmorLevelModule::new);

  @Override
  public RecordLoadable<MaxArmorLevelModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    updateMaxLevelIfArmor(tool, context, key, modifier.intEffectiveLevel(), allowBroken, heldTag);
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    updateMaxLevelIfArmor(tool, context, key, -modifier.intEffectiveLevel(), allowBroken, heldTag);
  }


  /* Helpers */

  /**
   * Adds levels to the given key in entity modifier data for an armor modifier
   * @param context  Equipment change context
   * @param key      Key to modify
   * @param amount   Amount to add
   */
  public static void updateMaxLevel(EquipmentChangeContext context, TinkerDataKey<Float> key, int amount) {
    context.getTinkerData().ifPresent(data -> {
      float maxLevel = Math.max(data.get(key, 0f), amount);
      if (maxLevel <= 0) {
        data.remove(key);
      } else {
        data.put(key, maxLevel);
      }
    });
  }

  /** Checks if the given slot is valid */
  public static boolean validSlot(IToolStackView tool, EquipmentSlot slot, @Nullable TagKey<Item> heldTag) {
    return slot.getType() == Type.ARMOR || heldTag != null && tool.hasTag(heldTag);
  }

  /**
   * Adds levels to the given key in entity modifier data for an armor modifier
   * @param tool     Tool instance
   * @param context  Equipment change context
   * @param key      Key to modify
   * @param amount   Amount to add
   * @param heldTag  Tag to check to validate held items, if null held items are considered to never be valid
   */
  public static void updateMaxLevelIfArmor(IToolStackView tool, EquipmentChangeContext context, TinkerDataKey<Float> key, int amount, boolean allowBroken, @Nullable TagKey<Item> heldTag) {
    if (validSlot(tool, context.getChangedSlot(), heldTag) && (allowBroken || !tool.isBroken())) {
      updateMaxLevel(context, key, amount);
    }
  }
}

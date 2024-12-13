package slimeknights.tconstruct.library.modifiers.modules.technical;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.item.Item;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.data.ModifierMaxLevel;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.ComputableDataKey;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * Module for keeping track of the max level of a modifier across all pieces of equipment.
 * @see TinkerDataKey
 */
public record MaxArmorLevelModule(ComputableDataKey<ModifierMaxLevel> key, boolean allowBroken, @Nullable TagKey<Item> heldTag) implements HookProvider, EquipmentChangeModifierHook {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<MaxArmorLevelModule>defaultHooks(ModifierHooks.EQUIPMENT_CHANGE);

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    updateMaxLevelIfArmor(tool, context, key, modifier, allowBroken, heldTag);
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    updateMaxLevelIfArmor(tool, context, key, modifier, allowBroken, heldTag);
  }


  /* Helpers */

  /**
   * Calculate the current max value of the given maxLevel in entity modifier data for an armor modifier
   *
   * @param context Equipment change context
   * @param key     Key to modify
   * @param entry   Entry for the given armor modifier
   * @return The current max level for key
   */
  public static float updateMaxLevel(EquipmentChangeContext context, ComputableDataKey<ModifierMaxLevel> key, ModifierEntry entry) {
    Optional<TinkerDataCapability.Holder> holder = context.getTinkerData().resolve();

    if(holder.isPresent()) {
      ModifierMaxLevel maxLevel = holder.get().computeIfAbsent(key);
      maxLevel.set(context.getChangedSlot(), entry.getEffectiveLevel());
      return maxLevel.getMax();
    } else {
      return 0f;
    }
  }

  /** Checks if the given slot is valid */
  public static boolean validSlot(IToolStackView tool, EquipmentSlot slot, @Nullable TagKey<Item> heldTag) {
    return slot.getType() == Type.ARMOR || heldTag != null && tool.hasTag(heldTag);
  }

  /**
   * Adds levels to the given maxLevel in entity modifier data for an armor modifier
   * @param tool     Tool instance
   * @param context  Equipment change context
   * @param key      Key to modify
   * @param entry   Entry for the given armor modifier
   * @param heldTag  Tag to check to validate held items, if null held items are considered to never be valid
   */
  public static void updateMaxLevelIfArmor(IToolStackView tool, EquipmentChangeContext context, ComputableDataKey<ModifierMaxLevel> key, ModifierEntry entry, boolean allowBroken, @Nullable TagKey<Item> heldTag) {
    if (validSlot(tool, context.getChangedSlot(), heldTag) && (allowBroken || !tool.isBroken())) {
      updateMaxLevel(context, key, entry);
    }
  }
}

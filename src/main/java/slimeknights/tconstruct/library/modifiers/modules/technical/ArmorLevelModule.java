package slimeknights.tconstruct.library.modifiers.modules.technical;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.util.LazyOptional;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Module for keeping track of the total level of a modifier across all pieces of equipment. Does not support incremental, use {@link slimeknights.tconstruct.library.modifiers.modules.behavior.AttributeModule} for that.
 * @see slimeknights.tconstruct.library.modifiers.modules.behavior.AttributeModule
 * @see slimeknights.tconstruct.library.modifiers.modules.behavior.ShowOffhandModule
 * @see TinkerDataKeys#INTEGER_REGISTRY
 */
public record ArmorLevelModule(TinkerDataKey<Integer> key, boolean allowBroken, @Nullable TagKey<Item> heldTag) implements HookProvider, EquipmentChangeModifierHook, ModifierModule {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ArmorLevelModule>defaultHooks(ModifierHooks.EQUIPMENT_CHANGE);
  public static final RecordLoadable<ArmorLevelModule> LOADER = RecordLoadable.create(
    TinkerDataKeys.INTEGER_REGISTRY.requiredField("key", ArmorLevelModule::key),
    BooleanLoadable.INSTANCE.defaultField("allow_broken", false, ArmorLevelModule::allowBroken),
    Loadables.ITEM_TAG.nullableField("held_tag", ArmorLevelModule::heldTag),
    ArmorLevelModule::new);

  @Override
  public RecordLoadable<ArmorLevelModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    addLevelsIfArmor(tool, context, key, modifier.intEffectiveLevel(), allowBroken, heldTag);
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    addLevelsIfArmor(tool, context, key, -modifier.intEffectiveLevel(), allowBroken, heldTag);
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

  /** Checks if the given slot is valid */
  public static boolean validSlot(IToolStackView tool, EquipmentSlot slot, @Nullable TagKey<Item> heldTag) {
    return slot.isArmor() || heldTag != null && tool.hasTag(heldTag);
  }

  /**
   * Adds levels to the given key in entity modifier data for an armor modifier
   * @param tool     Tool instance
   * @param context  Equipment change context
   * @param key      Key to modify
   * @param amount   Amount to add
   * @param heldTag  Tag to check to validate held items, if null held items are considered to never be valid
   */
  public static void addLevelsIfArmor(IToolStackView tool, EquipmentChangeContext context, TinkerDataKey<Integer> key, int amount, boolean allowBroken, @Nullable TagKey<Item> heldTag) {
    if (validSlot(tool, context.getChangedSlot(), heldTag) && (allowBroken || !tool.isBroken())) {
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
    return getLevel(living.getCapability(TinkerDataCapability.CAPABILITY), key);
  }

  /**
   * Gets the total level from the key in the entity modifier data
   * @param cap    Capability instance
   * @param key    Key to get
   * @return  Level from the key
   */
  public static int getLevel(LazyOptional<TinkerDataCapability.Holder> cap, TinkerDataKey<Integer> key) {
    return cap.resolve().map(data -> data.get(key)).orElse(0);
  }
}

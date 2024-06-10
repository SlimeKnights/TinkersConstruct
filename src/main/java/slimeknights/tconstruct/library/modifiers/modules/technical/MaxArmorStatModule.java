package slimeknights.tconstruct.library.modifiers.modules.technical;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.primitive.EnumLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModuleBuilder;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.List;

public record MaxArmorStatModule(TinkerDataCapability.TinkerDataKey<Float> key, LevelingValue amount, TinkerDataCapability.TinkerDataKey<Integer> key2, boolean allowBroken, @Nullable TagKey<Item> heldTag, ArmorStatModule.TooltipStyle tooltipStyle, ModifierCondition<IToolStackView> condition) implements HookProvider, EquipmentChangeModifierHook, ModifierModule, TooltipModifierHook, ModifierCondition.ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> TOOLTIP_HOOKS = HookProvider.<ArmorStatModule>defaultHooks(ModifierHooks.EQUIPMENT_CHANGE, ModifierHooks.TOOLTIP);
  private static final List<ModuleHook<?>> NO_TOOLTIP_HOOKS = HookProvider.<ArmorStatModule>defaultHooks(ModifierHooks.EQUIPMENT_CHANGE);
  public static final RecordLoadable<MaxArmorStatModule> LOADER = RecordLoadable.create(
    TinkerDataKeys.FLOAT_REGISTRY.requiredField("key", MaxArmorStatModule::key),
    LevelingValue.LOADABLE.directField(MaxArmorStatModule::amount),
    TinkerDataKeys.INTEGER_REGISTRY.requiredField("key2", MaxArmorStatModule::key2),
    BooleanLoadable.INSTANCE.defaultField("allow_broken", false, MaxArmorStatModule::allowBroken),
    Loadables.ITEM_TAG.nullableField("held_tag", MaxArmorStatModule::heldTag),
    new EnumLoadable<>(ArmorStatModule.TooltipStyle.class).defaultField("tooltip_style", ArmorStatModule.TooltipStyle.NONE, MaxArmorStatModule::tooltipStyle),
    ModifierCondition.TOOL_FIELD,
    MaxArmorStatModule::new);

  @Override
  public RecordLoadable<MaxArmorStatModule> getLoader() {
    return LOADER;
  }

  @Override
  public void addModules(ModuleHookMap.Builder builder) {
    builder.addModule(new MaxArmorLevelModule(key2, allowBroken, heldTag));
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return tooltipStyle == ArmorStatModule.TooltipStyle.NONE ? NO_TOOLTIP_HOOKS : TOOLTIP_HOOKS;
  }

  @Override
  public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (condition.matches(tool, modifier)) {
      context.getTinkerData().ifPresent(data -> {
        addStatIfArmor(tool, context, key, amount.compute(data.get(key2, 0)), allowBroken, heldTag);
      });

    }
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (condition.matches(tool, modifier)) {
      context.getTinkerData().ifPresent(data -> {
        addStatIfArmor(tool, context, key, amount.compute(data.get(key2, 0)), allowBroken, heldTag);
      });    }
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    if (condition.matches(tool, modifier) && (tool.hasTag(TinkerTags.Items.WORN_ARMOR) || heldTag != null && tool.hasTag(heldTag)) && (!tool.isBroken() || allowBroken)) {
      float value = amount.compute(modifier.getEffectiveLevel());
      if (value != 0) {
        Component name = Component.translatable(Util.makeTranslationKey("armor_stat", key.getId()));
        switch (tooltipStyle) {
          case BOOST -> TooltipModifierHook.addFlatBoost(modifier.getModifier(), name, value, tooltip);
          case PERCENT -> TooltipModifierHook.addPercentBoost(modifier.getModifier(), name, value, tooltip);
        }
      }
    }
  }

  /**
   * Adds to the armor stat for the given key. Make sure to subtract on unequip if you add on equip, it will not automatically be removed.
   * @param context  Equipment change context
   * @param key      Key to modify
   * @param amount   Amount to add
   */
  public static void addStat(EquipmentChangeContext context, TinkerDataCapability.TinkerDataKey<Float> key, float amount) {
    context.getTinkerData().ifPresent(data -> {
      float totalLevels = data.get(key, 0f) + amount;
      if (totalLevels <= 0.005f) {
        data.remove(key);
      } else {
        data.put(key, totalLevels);
      }
    });
  }

  /**
   * Adds to the armor stat for the given key if the tool is in a valid armor slot
   * @param tool     Tool instance
   * @param context  Equipment change context
   * @param key      Key to modify
   * @param amount   Amount to add
   * @param heldTag  Tag to check to validate held items, null means held disallowed
   */
  public static void addStatIfArmor(IToolStackView tool, EquipmentChangeContext context, TinkerDataCapability.TinkerDataKey<Float> key, float amount, boolean allowBroken, @Nullable TagKey<Item> heldTag) {
    if (ArmorLevelModule.validSlot(tool, context.getChangedSlot(), heldTag) && (!tool.isBroken() || allowBroken)) {
      addStat(context, key, amount);
    }
  }

  /**
   * Gets the total level from the key in the entity modifier data
   * @param living  Living entity
   * @param key     Key to get
   * @return  Level from the key
   */
  public static float getStat(LivingEntity living, TinkerDataCapability.TinkerDataKey<Float> key) {
    return living.getCapability(TinkerDataCapability.CAPABILITY).resolve().map(data -> data.get(key)).orElse(0f);
  }


  /* Builder */

  public static MaxArmorStatModule.Builder builder(TinkerDataCapability.TinkerDataKey<Float> key, TinkerDataCapability.TinkerDataKey<Integer> key2) {
    return new MaxArmorStatModule.Builder(key, key2);
  }

  @Setter
  @Accessors(fluent = true)
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder extends ModuleBuilder.Stack<MaxArmorStatModule.Builder> implements LevelingValue.Builder<MaxArmorStatModule> {
    private final TinkerDataCapability.TinkerDataKey<Float> key;
    private final TinkerDataCapability.TinkerDataKey<Integer> key2;
    private boolean allowBroken = false;
    @Nullable
    private TagKey<Item> heldTag;
    private ArmorStatModule.TooltipStyle tooltipStyle = ArmorStatModule.TooltipStyle.NONE;

    public MaxArmorStatModule.Builder allowBroken() {
      this.allowBroken = true;
      return this;
    }

    @Override
    public MaxArmorStatModule amount(float flat, float eachLevel) {
      return new MaxArmorStatModule(key, new LevelingValue(flat, eachLevel), key2, allowBroken, heldTag, tooltipStyle, condition);
    }
  }
}

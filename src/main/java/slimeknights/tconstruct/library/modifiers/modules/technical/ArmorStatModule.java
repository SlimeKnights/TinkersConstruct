package slimeknights.tconstruct.library.modifiers.modules.technical;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
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
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModuleBuilder;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Modifier that to keep track of a stat that is contributed to by all armor pieces. Can scale the stat on different modifiers or for incremental and can use float values unlike {@link ArmorLevelModule}.
 * @see ArmorLevelModule
 * @see TinkerDataKey
 */
public record ArmorStatModule(TinkerDataKey<Float> key, LevelingValue amount, boolean allowBroken, @Nullable TagKey<Item> heldTag, TooltipStyle tooltipStyle, ModifierCondition<IToolStackView> condition) implements HookProvider, EquipmentChangeModifierHook, ModifierModule, TooltipModifierHook, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> TOOLTIP_HOOKS = HookProvider.<ArmorStatModule>defaultHooks(ModifierHooks.EQUIPMENT_CHANGE, ModifierHooks.TOOLTIP);
  private static final List<ModuleHook<?>> NO_TOOLTIP_HOOKS = HookProvider.<ArmorStatModule>defaultHooks(ModifierHooks.EQUIPMENT_CHANGE);
  public static final RecordLoadable<ArmorStatModule> LOADER = RecordLoadable.create(
    TinkerDataKeys.FLOAT_REGISTRY.requiredField("key", ArmorStatModule::key),
    LevelingValue.LOADABLE.directField(ArmorStatModule::amount),
    BooleanLoadable.INSTANCE.defaultField("allow_broken", false, ArmorStatModule::allowBroken),
    Loadables.ITEM_TAG.nullableField("held_tag", ArmorStatModule::heldTag),
    new EnumLoadable<>(TooltipStyle.class).defaultField("tooltip_style", TooltipStyle.NONE, ArmorStatModule::tooltipStyle),
    ModifierCondition.TOOL_FIELD,
    ArmorStatModule::new);

  @Override
  public RecordLoadable<ArmorStatModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return tooltipStyle == TooltipStyle.NONE ? NO_TOOLTIP_HOOKS : TOOLTIP_HOOKS;
  }

  @Override
  public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (condition.matches(tool, modifier)) {
      ArmorStatModule.addStatIfArmor(tool, context, key, amount.compute(modifier.getEffectiveLevel()), allowBroken, heldTag);
    }
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (condition.matches(tool, modifier)) {
      ArmorStatModule.addStatIfArmor(tool, context, key, -amount.compute(modifier.getEffectiveLevel()), allowBroken, heldTag);
    }
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    if (condition.matches(tool, modifier) && (tool.hasTag(TinkerTags.Items.WORN_ARMOR) || heldTag != null && tool.hasTag(heldTag)) && (!tool.isBroken() || allowBroken)) {
      addStatTooltip(modifier, key.getId(), amount.computeForLevel(modifier.getEffectiveLevel()), tooltipStyle, tooltip);
    }
  }

  /** Adds the given stat tooltip */
  public static void addStatTooltip(ModifierEntry modifier, ResourceLocation key, float value, TooltipStyle tooltipStyle, List<Component> tooltip) {
    if (value != 0) {
      Component name = Component.translatable(Util.makeTranslationKey("armor_stat", key));
      switch (tooltipStyle) {
        case BOOST -> TooltipModifierHook.addFlatBoost(modifier.getModifier(), name, value, tooltip);
        case PERCENT -> TooltipModifierHook.addPercentBoost(modifier.getModifier(), name, value, tooltip);
      }
    }
  }


  /* Helpers */

  public enum TooltipStyle { NONE, BOOST, PERCENT }

  /**
   * Adds to the armor stat for the given key. Make sure to subtract on unequip if you add on equip, it will not automatically be removed.
   * @param context  Equipment change context
   * @param key      Key to modify
   * @param amount   Amount to add
   */
  public static void addStat(EquipmentChangeContext context, TinkerDataKey<Float> key, float amount) {
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
  public static void addStatIfArmor(IToolStackView tool, EquipmentChangeContext context, TinkerDataKey<Float> key, float amount, boolean allowBroken, @Nullable TagKey<Item> heldTag) {
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
  public static float getStat(Entity living, TinkerDataKey<Float> key) {
    return living.getCapability(TinkerDataCapability.CAPABILITY).resolve().map(data -> data.get(key)).orElse(0f);
  }


  /* Builder */

  public static Builder builder(TinkerDataKey<Float> key) {
    return new Builder(key);
  }

  @Setter
  @Accessors(fluent = true)
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder extends ModuleBuilder.Stack<Builder> implements LevelingValue.Builder<ArmorStatModule> {
    private final TinkerDataKey<Float> key;
    private boolean allowBroken = false;
    @Nullable
    private TagKey<Item> heldTag;
    private TooltipStyle tooltipStyle = TooltipStyle.NONE;

    public Builder allowBroken() {
      this.allowBroken = true;
      return this;
    }

    @Override
    public ArmorStatModule amount(float flat, float eachLevel) {
      return new ArmorStatModule(key, new LevelingValue(flat, eachLevel), allowBroken, heldTag, tooltipStyle, condition);
    }
  }
}

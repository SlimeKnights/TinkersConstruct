package slimeknights.tconstruct.library.modifiers.modules.technical;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.primitive.EnumLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.IdExtender;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.data.ModifierMaxLevel;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModuleBuilder;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.ComputableDataKey;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.IdParser;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.List;

public record MaxArmorStatModule(TinkerDataKey<Float> statKey, LevelingValue amount, ComputableDataKey<ModifierMaxLevel> maxLevel, boolean allowBroken, @Nullable TagKey<Item> heldTag, ArmorStatModule.TooltipStyle tooltipStyle, ModifierCondition<IToolStackView> condition) implements HookProvider, EquipmentChangeModifierHook, ModifierModule, TooltipModifierHook, ModifierCondition.ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> TOOLTIP_HOOKS = HookProvider.<ArmorStatModule>defaultHooks(ModifierHooks.EQUIPMENT_CHANGE, ModifierHooks.TOOLTIP);
  private static final List<ModuleHook<?>> NO_TOOLTIP_HOOKS = HookProvider.<ArmorStatModule>defaultHooks(ModifierHooks.EQUIPMENT_CHANGE);
  public static final RecordLoadable<MaxArmorStatModule> LOADER = RecordLoadable.create(
    new IdParser<>(ResourceLocation::new, "stat").xmap((location, factory) -> TinkerDataKey.<Float>of(location), (tinkerDataKey, errorFactory) -> tinkerDataKey.getId()).requiredField("key", MaxArmorStatModule::statKey),
    LevelingValue.LOADABLE.directField(MaxArmorStatModule::amount),
    BooleanLoadable.INSTANCE.defaultField("allow_broken", false, MaxArmorStatModule::allowBroken),
    Loadables.ITEM_TAG.nullableField("held_tag", MaxArmorStatModule::heldTag),
    new EnumLoadable<>(ArmorStatModule.TooltipStyle.class).defaultField("tooltip_style", ArmorStatModule.TooltipStyle.NONE, MaxArmorStatModule::tooltipStyle),
    ModifierCondition.TOOL_FIELD,
    MaxArmorStatModule::new);

  public MaxArmorStatModule(TinkerDataKey<Float> statKey, LevelingValue amount, boolean allowBroken, @Nullable TagKey<Item> heldTag, ArmorStatModule.TooltipStyle tooltipStyle, ModifierCondition<IToolStackView> condition) {
    this(statKey, amount, ComputableDataKey.of(IdExtender.LocationExtender.INSTANCE.prefix(statKey.getId(), "_data"), ModifierMaxLevel::new), allowBroken, heldTag, tooltipStyle, condition);
  }

  @Override
  public RecordLoadable<MaxArmorStatModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return tooltipStyle == ArmorStatModule.TooltipStyle.NONE ? NO_TOOLTIP_HOOKS : TOOLTIP_HOOKS;
  }

  @Override
  public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (condition.matches(tool, modifier)) {
      addStatIfArmor(tool, context, statKey, maxLevel, amount, modifier, allowBroken, heldTag);
    }
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (condition.matches(tool, modifier)) {
      addStatIfArmor(tool, context, statKey, maxLevel, amount, modifier, allowBroken, heldTag);
    }
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    if (condition.matches(tool, modifier) && (tool.hasTag(TinkerTags.Items.WORN_ARMOR) || heldTag != null && tool.hasTag(heldTag)) && (!tool.isBroken() || allowBroken)) {
      float value = player != null ? ModifierMaxLevel.getStat(player, maxLevel) : modifier.getEffectiveLevel();
      if (value > 0) {
        Component name = Component.translatable(Util.makeTranslationKey("armor_stat", statKey.getId()));
        switch (tooltipStyle) {
          case BOOST -> TooltipModifierHook.addFlatBoost(modifier.getModifier(), name, value, tooltip);
          case PERCENT -> TooltipModifierHook.addPercentBoost(modifier.getModifier(), name, value, tooltip);
        }
      }
    }
  }

  /**
   * Adds to the armor stat for the given maxLevel if the tool is in a valid armor slot
   * @param tool          Tool instance
   * @param context       Equipment change context
   * @param statKey       Key to modify
   * @param maxLevelKey   Key for max level of armor modifier
   * @param amount        Amount by which stat is modified per level
   * @param modifierEntry Modifier entry for stat
   * @param allowBroken   Whether the tool can work while broken
   * @param heldTag       Tag to check to validate held items, null means held disallowed
   */
  public static void addStatIfArmor(IToolStackView tool, EquipmentChangeContext context, TinkerDataKey<Float> statKey, ComputableDataKey<ModifierMaxLevel> maxLevelKey, LevelingValue amount, ModifierEntry modifierEntry, boolean allowBroken, @Nullable TagKey<Item> heldTag) {
    if (ArmorLevelModule.validSlot(tool, context.getChangedSlot(), heldTag) && (!tool.isBroken() || allowBroken)) {
      context.getTinkerData().ifPresent(data -> {
        ModifierMaxLevel maxLevel = data.computeIfAbsent(maxLevelKey);
        float oldLevel = maxLevel.getMax();
        maxLevel.set(context.getChangedSlot(), modifierEntry.getEffectiveLevel());
        data.add(statKey, amount.computeForLevel(maxLevel.getMax()) - amount.computeForLevel(oldLevel));
      });
    }
  }


  /* Builder */
  public static MaxArmorStatModule.Builder builder(TinkerDataKey<Float> statKey) {
    return new MaxArmorStatModule.Builder(statKey);
  }

  @Setter
  @Accessors(fluent = true)
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder extends ModuleBuilder.Stack<MaxArmorStatModule.Builder> implements LevelingValue.Builder<MaxArmorStatModule> {
    private final TinkerDataKey<Float> statKey;
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
      return new MaxArmorStatModule(statKey, new LevelingValue(flat, eachLevel), allowBroken, heldTag, tooltipStyle, condition);
    }
  }
}

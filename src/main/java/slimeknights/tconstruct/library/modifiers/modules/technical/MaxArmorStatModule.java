package slimeknights.tconstruct.library.modifiers.modules.technical;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.primitive.EnumLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.data.ModifierMaxLevel;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModuleBuilder;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.ComputableDataKey;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.Holder;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;

/** Module that applies an armor stat to the user of this tool based on the largest level of a modifier equipped. */
public record MaxArmorStatModule(TinkerDataKey<Float> statKey, LevelingValue amount, ComputableDataKey<ModifierMaxLevel> maxLevel, boolean allowBroken, @Nullable TagKey<Item> heldTag, ArmorStatModule.TooltipStyle tooltipStyle, ModifierCondition<IToolStackView> condition) implements ModifierModule, TooltipModifierHook, MaxArmorLevelModule {
  public static final RecordLoadable<MaxArmorStatModule> LOADER = RecordLoadable.create(
    TinkerDataKeys.FLOAT_REGISTRY.requiredField("key", MaxArmorStatModule::statKey),
    LevelingValue.LOADABLE.directField(MaxArmorStatModule::amount),
    BooleanLoadable.INSTANCE.defaultField("allow_broken", false, MaxArmorStatModule::allowBroken),
    Loadables.ITEM_TAG.nullableField("held_tag", MaxArmorStatModule::heldTag),
    new EnumLoadable<>(ArmorStatModule.TooltipStyle.class).defaultField("tooltip_style", ArmorStatModule.TooltipStyle.NONE, MaxArmorStatModule::tooltipStyle),
    ModifierCondition.TOOL_FIELD,
    MaxArmorStatModule::new);

  public MaxArmorStatModule(TinkerDataKey<Float> statKey, LevelingValue amount, boolean allowBroken, @Nullable TagKey<Item> heldTag, ArmorStatModule.TooltipStyle tooltipStyle, ModifierCondition<IToolStackView> condition) {
    this(statKey, amount, MaxArmorLevelModule.createKey(statKey.getId()), allowBroken, heldTag, tooltipStyle, condition);
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
  public void updateValue(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context, Holder data, float newLevel, float oldLevel) {
    data.add(statKey, amount.computeForLevel(newLevel) - amount.computeForLevel(oldLevel));
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    if (MaxArmorLevelModule.shouldAddTooltip(this, tool, modifier, player)) {
      ArmorStatModule.addStatTooltip(modifier, statKey.getId(), amount.computeForLevel(modifier.getEffectiveLevel()), tooltipStyle, tooltip);
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

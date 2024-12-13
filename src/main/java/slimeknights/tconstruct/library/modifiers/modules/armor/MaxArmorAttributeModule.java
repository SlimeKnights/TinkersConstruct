package slimeknights.tconstruct.library.modifiers.modules.armor;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.primitive.StringLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.data.ModifierMaxLevel;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.technical.MaxArmorLevelModule;
import slimeknights.tconstruct.library.modifiers.modules.technical.MaxArmorStatModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModuleBuilder;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.ComputableDataKey;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.Holder;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.helper.TooltipUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/** Module that sets an attribute value on the entity based on the largest level equipped. TODO: tooltip value on max piece. */
public record MaxArmorAttributeModule(String unique, Attribute attribute, Operation operation, LevelingValue amount, UUID uuid, ComputableDataKey<ModifierMaxLevel> maxLevel, boolean allowBroken, @Nullable TagKey<Item> heldTag, ModifierCondition<IToolStackView> condition) implements EquipmentChangeModifierHook, ModifierModule, MaxArmorLevelModule, TooltipModifierHook {
  public static final RecordLoadable<MaxArmorAttributeModule> LOADER = RecordLoadable.create(
    StringLoadable.DEFAULT.requiredField("unique", MaxArmorAttributeModule::unique),
    Loadables.ATTRIBUTE.requiredField("attribute", MaxArmorAttributeModule::attribute),
    TinkerLoadables.OPERATION.requiredField("operation", MaxArmorAttributeModule::operation),
    LevelingValue.LOADABLE.directField(MaxArmorAttributeModule::amount),
    BooleanLoadable.INSTANCE.defaultField("allow_broken", false, MaxArmorAttributeModule::allowBroken),
    Loadables.ITEM_TAG.nullableField("held_tag", MaxArmorAttributeModule::heldTag),
    ModifierCondition.TOOL_FIELD,
    MaxArmorAttributeModule::new);

  public MaxArmorAttributeModule(String unique, Attribute attribute, Operation operation, LevelingValue amount, boolean allowBroken, @Nullable TagKey<Item> heldTag, ModifierCondition<IToolStackView> condition) {
    this(unique, attribute, operation, amount, UUID.nameUUIDFromBytes(unique.getBytes()), MaxArmorLevelModule.createKey(Registry.ATTRIBUTE.getKey(attribute)), allowBroken, heldTag, condition);
  }

  @Override
  public RecordLoadable<MaxArmorAttributeModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return TOOLTIP_HOOKS;
  }

  @Override
  public void updateValue(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context, Holder data, float newLevel, float oldLevel) {
    AttributeInstance instance = context.getEntity().getAttribute(attribute);
    if (instance != null) {
      instance.removeModifier(uuid);
      float attributeValue = amount.computeForLevel(newLevel);
      if (attributeValue != 0) {
        instance.addTransientModifier(new AttributeModifier(uuid, unique, attributeValue, operation));
      }
    }
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    if (MaxArmorLevelModule.shouldAddTooltip(this, tool, modifier, player)) {
      float value = amount.computeForLevel(modifier.getEffectiveLevel());
      if (value != 0) {
        TooltipUtil.addAttribute(attribute, operation, value, uuid, player, tooltip);
      }
    }
  }


  /* Builder */
  public static Builder builder(Attribute attribute, Operation operation) {
    return new Builder(attribute, operation);
  }


  @Setter
  @Accessors(fluent = true)
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder extends ModuleBuilder.Stack<MaxArmorStatModule.Builder> implements LevelingValue.Builder<MaxArmorAttributeModule> {
    private final Attribute attribute;
    private final Operation operation;
    protected String unique;
    private boolean allowBroken = false;
    @Nullable
    private TagKey<Item> heldTag;

    public Builder allowBroken() {
      this.allowBroken = true;
      return this;
    }

    /**
     * Sets the unique string using a resource location
     */
    public Builder uniqueFrom(ResourceLocation id) {
      return unique(id.getNamespace() + ".modifier." + id.getPath());
    }

    @Override
    public MaxArmorAttributeModule amount(float flat, float eachLevel) {
      if (unique == null) {
        throw new IllegalStateException("Must set unique for attributes");
      }
      return new MaxArmorAttributeModule(unique, attribute, operation, new LevelingValue(flat, eachLevel), allowBroken, heldTag, condition);
    }
  }
}

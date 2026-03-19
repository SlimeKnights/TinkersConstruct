package slimeknights.tconstruct.library.modifiers.modules.behavior;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.primitive.EnumLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.json.math.ModifierFormula;
import slimeknights.tconstruct.library.json.math.ModifierFormula.FallbackFormula;
import slimeknights.tconstruct.library.json.variable.VariableFormula;
import slimeknights.tconstruct.library.json.variable.VariableFormulaLoadable;
import slimeknights.tconstruct.library.json.variable.tool.ToolFormula;
import slimeknights.tconstruct.library.json.variable.tool.ToolVariable;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.helper.TooltipUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Module to add an attribute to a tool.
 */
public record AttributeModule(String unique, Attribute attribute, Operation operation, ToolFormula formula, UUID[] slotUUIDs, TooltipStyle tooltipStyle, ModifierCondition<IToolStackView> condition) implements AttributesModifierHook, ModifierModule, EquipmentChangeModifierHook, TooltipModifierHook, ConditionalModule<IToolStackView> {
  /** Default variables */
  private static final String[] VARIABLES = { "level" };
  /** Loader for the variables */
  private static final RecordLoadable<ToolFormula> VARIABLE_LOADER = new VariableFormulaLoadable<>(ToolVariable.LOADER, VARIABLES, FallbackFormula.IDENTITY, (formula, variables, percent) -> new ToolFormula(formula, variables, VariableFormula.EMPTY_STRINGS));
  /* Default hook options */
  private static final List<ModuleHook<?>> ATTRIBUTE_HOOKS = HookProvider.<AttributeModule>defaultHooks(ModifierHooks.ATTRIBUTES);
  private static final List<ModuleHook<?>> TOOLTIP_HOOKS = HookProvider.<AttributeModule>defaultHooks(ModifierHooks.EQUIPMENT_CHANGE, ModifierHooks.TOOLTIP);
  private static final List<ModuleHook<?>> NO_TOOLTIP_HOOKS = HookProvider.<AttributeModule>defaultHooks(ModifierHooks.EQUIPMENT_CHANGE);
  /** Loader for the module */
  public static final RecordLoadable<AttributeModule> LOADER = RecordLoadable.create(
    new AttributeUniqueField<>(AttributeModule::unique),
    Loadables.ATTRIBUTE.requiredField("attribute", AttributeModule::attribute),
    TinkerLoadables.OPERATION.requiredField("operation", AttributeModule::operation),
    VARIABLE_LOADER.directField(AttributeModule::formula),
    TinkerLoadables.EQUIPMENT_SLOT_SET.requiredField("slots", m -> uuidsToSlots(m.slotUUIDs)),
    TooltipStyle.LOADABLE.defaultField("tooltip_style", TooltipStyle.ATTRIBUTE, AttributeModule::tooltipStyle),
    ModifierCondition.TOOL_FIELD,
    (unique, attribute, operation, amount, slots, tooltipStyle, condition) -> new AttributeModule(unique, attribute, operation, amount, slotsToUUIDs(unique, slots), tooltipStyle, condition));

  /** Gets the UUID from a name */
  public static UUID getUUID(String name, EquipmentSlot slot) {
    return UUID.nameUUIDFromBytes((name + "." + slot.getName()).getBytes());
  }

  /** Converts a list of slots to an array of UUIDs at each index */
  public static UUID[] slotsToUUIDs(String name, Collection<EquipmentSlot> slots) {
    UUID[] slotUUIDs = new UUID[6];
    for (EquipmentSlot slot : slots) {
      slotUUIDs[slot.getFilterFlag()] = getUUID(name, slot);
    }
    return slotUUIDs;
  }

  /** Maps the UUID array to a set for serializing */
  public static Set<EquipmentSlot> uuidsToSlots(UUID[] uuids) {
    Set<EquipmentSlot> set = EnumSet.noneOf(EquipmentSlot.class);
    for (EquipmentSlot slot : EquipmentSlot.values()) {
      if (uuids[slot.getFilterFlag()] != null) {
        set.add(slot);
      }
    }
    return set;
  }

  /** @apiNote Internal constructor, use {@link #builder(Attribute, Operation)} */
  @Internal
  public AttributeModule {}

  /** Gets the UUID for this slot */
  @Nullable
  private UUID getUUID(EquipmentSlot slot) {
    return slotUUIDs[slot.getFilterFlag()];
  }

  /** Creates an attribute for the given slot */
  @Nullable
  private AttributeModifier createModifier(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot) {
    UUID uuid = getUUID(slot);
    if (uuid != null) {
      return new AttributeModifier(uuid, unique + "." + slot.getName(), formula.apply(tool, modifier), operation);
    }
    return null;
  }

  @Override
  public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute,AttributeModifier> consumer) {
    if (condition.matches(tool, modifier)) {
      AttributeModifier attributeModifier = createModifier(tool, modifier, slot);
      if (attributeModifier != null) {
        consumer.accept(attribute, attributeModifier);
      }
    }
  }


  /* Equipment change approach, for when you wish to not show the attribute in the tooltip */
  @Override
  public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (condition.matches(tool, modifier)) {
      AttributeInstance instance = context.getEntity().getAttribute(attribute);
      if (instance != null) {
        AttributeModifier attributeModifier = createModifier(tool, modifier, context.getChangedSlot());
        if (attributeModifier != null) {
          // for safety, remove it already there
          instance.removeModifier(attributeModifier.getId());
          instance.addTransientModifier(attributeModifier);
        }
      }
    }
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (condition.matches(tool, modifier)) {
      UUID uuid = getUUID(context.getChangedSlot());
      if (uuid != null) {
        AttributeInstance instance = context.getEntity().getAttribute(attribute);
        if (instance != null) {
          instance.removeModifier(uuid);
        }
      }
    }
  }

  /** Adds the tooltip for the given attribute */
  public static void addTooltip(Modifier modifier, Attribute attribute, Operation operation, TooltipStyle tooltipStyle, float amount, @Nullable UUID uuid, @Nullable Player player, List<Component> tooltip) {
    switch (tooltipStyle) {
      case ATTRIBUTE -> TooltipUtil.addAttribute(attribute, operation, amount, uuid, player, tooltip);
      case BOOST -> TooltipModifierHook.addFlatBoost(modifier, Component.translatable(attribute.getDescriptionId()), amount, tooltip);
      case PERCENT -> TooltipModifierHook.addPercentBoost(modifier, Component.translatable(attribute.getDescriptionId()), amount, tooltip);
    }
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    if (condition.matches(tool, modifier)) {
      float value = formula.apply(tool, modifier);
      if (value != 0) {
        addTooltip(modifier.getModifier(), attribute, operation, tooltipStyle, value, null, player, tooltip);
      }
    }
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    if (tooltipStyle == TooltipStyle.ATTRIBUTE) {
      return ATTRIBUTE_HOOKS;
    }
    if (tooltipStyle == TooltipStyle.NONE) {
      return NO_TOOLTIP_HOOKS;
    }
    return TOOLTIP_HOOKS;
  }

  @Override
  public RecordLoadable<AttributeModule> getLoader() {
    return LOADER;
  }

  /** Module tooltip styles */
  public enum TooltipStyle {
    ATTRIBUTE, NONE, BOOST, PERCENT;

    public static final EnumLoadable<TooltipStyle> LOADABLE = new EnumLoadable<>(TooltipStyle.class);
  }


  /** Creates a new builder instance */
  public static Builder builder(Attribute attribute, Operation operation) {
    return new Builder(attribute, operation);
  }

  public static Builder builder(Supplier<Attribute> attribute, Operation operation) {
    return new Builder(attribute.get(), operation);
  }

  @Accessors(fluent = true)
  public static class Builder extends VariableFormula.Builder<Builder,AttributeModule,ToolVariable> {
    protected final Attribute attribute;
    protected final Operation operation;
    @Setter
    protected String unique = "";
    private EquipmentSlot[] slots = EquipmentSlot.values();
    /** Tooltip style override. If set, switches from item stack attributes to equipment change attributes and shows modifier style tooltips. */
    @Setter
    private TooltipStyle tooltipStyle = TooltipStyle.ATTRIBUTE;

    protected Builder(Attribute attribute, Operation operation) {
      super(VARIABLES);
      this.attribute = attribute;
      this.operation = operation;
    }

    /** Adds the given slots to this builder */
    public Builder slots(EquipmentSlot... slots) {
      this.slots = slots;
      return this;
    }

    /**
     * Sets the unique string using a resource location
     */
    public Builder uniqueFrom(ResourceLocation id) {
      return unique(id.getNamespace() + ".modifier." + id.getPath());
    }

    /** @deprecated use {@link #tooltipStyle(TooltipStyle)} */
    @Deprecated
    @Override
    public Builder percent() {
      tooltipStyle = TooltipStyle.PERCENT;
      return this;
    }

    @Override
    protected AttributeModule build(ModifierFormula formula) {
      return new AttributeModule(unique, attribute, operation, new ToolFormula(formula, variables), slotsToUUIDs(unique, List.of(slots)), tooltipStyle, condition);
    }
  }
}

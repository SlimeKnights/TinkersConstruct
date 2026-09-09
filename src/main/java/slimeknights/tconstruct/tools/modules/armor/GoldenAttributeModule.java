package slimeknights.tconstruct.tools.modules.armor;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.behavior.AttributeUniqueField;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModuleBuilder;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.item.armor.ModifiableArmorItem;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Module that applies an attribute conditioned on the amount of gold the player is wearing.
 * Used for {@link slimeknights.tconstruct.tools.data.ModifierIds#goldGuard} and {@link slimeknights.tconstruct.tools.data.ModifierIds#chrysophilite} */
public record GoldenAttributeModule(String unique, TinkerDataKey<TotalGold> dataKey, Attribute attribute, UUID uuid, Operation operation, LevelingValue amount, ModifierCondition<IToolStackView> condition) implements ModifierModule, EquipmentChangeModifierHook, TooltipModifierHook, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<GoldenAttributeModule>defaultHooks(ModifierHooks.EQUIPMENT_CHANGE, ModifierHooks.TOOLTIP);
  public static final RecordLoadable<GoldenAttributeModule> LOADER = RecordLoadable.create(
    new AttributeUniqueField<>(GoldenAttributeModule::unique),
    ContextKey.ID.mappedField((id, error) -> TinkerDataKey.<TotalGold>of(id.withSuffix("_key"))),
    Loadables.ATTRIBUTE.requiredField("attribute", GoldenAttributeModule::attribute),
    TinkerLoadables.OPERATION.requiredField("operation", GoldenAttributeModule::operation),
    LevelingValue.LOADABLE.directField(GoldenAttributeModule::amount),
    ModifierCondition.TOOL_FIELD,
    GoldenAttributeModule::new);

  /** @apiNote Internal constructor, use {@link #builder(Attribute, Operation)} */
  @Internal
  public GoldenAttributeModule {}

  private GoldenAttributeModule(String unique, TinkerDataKey<TotalGold> dataKey, Attribute attribute, Operation operation, LevelingValue amount, ModifierCondition<IToolStackView> condition) {
    this(unique, dataKey, attribute, UUID.nameUUIDFromBytes(unique.getBytes()), operation, amount, condition);
  }

  @Override
  public RecordLoadable<GoldenAttributeModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  /** Checks that health is not over max */
  private void checkHealth(LivingEntity living, AttributeInstance instance) {
    if (attribute == Attributes.MAX_HEALTH) {
      float newMax = (float) instance.getValue();
      if (living.getHealth() > newMax) {
        living.setHealth(newMax);
      }
    }
  }

  /** Updates the attribute on the given entity */
  private void updateAttribute(LivingEntity living, int totalGold) {
    // update attribute
    AttributeInstance instance = living.getAttribute(attribute);
    if (instance != null) {
      if (instance.getModifier(uuid) != null) {
        instance.removeModifier(uuid);
      }
      instance.addTransientModifier(new AttributeModifier(uuid, unique, amount.compute(totalGold), operation));
      checkHealth(living, instance);
    }
  }

  @Override
  public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    // adding armor? activate bonus
    EquipmentSlot slot = context.getChangedSlot();
    if (context.getChangedSlot().isArmor()) {
      TinkerDataCapability.Holder data = context.getDataHolder();
      if (data != null) {
        TotalGold gold = data.get(dataKey);
        if (gold == null) {
          gold = new TotalGold();
          gold.initialize(context);
          data.put(dataKey, gold);
          updateAttribute(context.getEntity(), gold.getTotalGold());
        } else if (gold.setGold(slot, tool.getVolatileData().getBoolean(ModifiableArmorItem.PIGLIN_NEUTRAL))) {
          updateAttribute(context.getEntity(), gold.getTotalGold());
        }
      }
    }
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (context.getChangedSlot().isArmor()) {
      IToolStackView newTool = context.getReplacementTool();
      // when replacing with a helmet that lacks this modifier, remove bonus
      if (newTool == null || newTool.getModifierLevel(modifier.getId()) == 0) {
        TinkerDataCapability.Holder data = context.getDataHolder();
        if (data != null) {
          data.remove(dataKey);
        }
        LivingEntity living = context.getEntity();
        AttributeInstance instance = living.getAttribute(attribute);
        if (instance != null) {
          instance.removeModifier(uuid);
          checkHealth(living, instance);
        }
      }
    }
  }

  @Override
  public void onEquipmentChange(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context, EquipmentSlot slotType) {
    // adding a helmet? activate bonus
    EquipmentSlot changed = context.getChangedSlot();
    if (slotType.isArmor() && changed.isArmor()) {
      TinkerDataCapability.Holder data = context.getDataHolder();
      if (data != null) {
        TotalGold gold = data.get(dataKey);
        if (gold != null && gold.setGold(changed, hasGold(context, changed))) {
          updateAttribute(context.getEntity(), gold.getTotalGold());
        }
      }
    }
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry entry, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    double amount = this.amount.compute(tool.getVolatileData().getBoolean(ModifiableArmorItem.PIGLIN_NEUTRAL) ? 1 : 0);
    if (player != null && tooltipKey == TooltipKey.SHIFT) {
      AttributeInstance instance = player.getAttribute(attribute);
      if (instance != null) {
        AttributeModifier modifier = instance.getModifier(uuid);
        if (modifier != null) {
          amount = (float) modifier.getAmount();
        }
      }
    }
    if (amount != 0) {
      TooltipModifierHook.addFlatBoost(entry.getModifier(), Component.translatable(attribute.getDescriptionId()), amount, tooltip);
    }
  }


  /* Utilities */

  /** Checks if the entity has gold in the given slot */
  public static boolean hasGold(EquipmentChangeContext context, EquipmentSlot slotType) {
    IToolStackView tool = context.getToolInSlot(slotType);
    if (tool != null) {
      return tool.getVolatileData().getBoolean(ModifiableArmorItem.PIGLIN_NEUTRAL);
    } else {
      LivingEntity living = context.getEntity();
      return living.getItemBySlot(slotType).makesPiglinsNeutral(living);
    }
  }

  /** Tracker to count how many slots contain gold */
  public static class TotalGold {
    private final boolean[] hasGold = new boolean[4];
    /** Gold value of the modifier, will be 1 for the modifier, and +1 for each golden armor piece */
    @Getter
    private int totalGold = 0;

    /**
     * Updates the status of gold in a slot on the entity
     * @param slotType  Slot to update
     * @param value     New value
     * @return true if the amount of gold changed.
     */
    public boolean setGold(EquipmentSlot slotType, boolean value) {
      if (slotType.isArmor()) {
        int index = slotType.getIndex();
        if (hasGold[index] != value) {
          hasGold[index] = value;
          if (value) {
            totalGold++;
          } else {
            totalGold--;
          }
          return true;
        }
      }
      return false;
    }

    /** Initializes the gold data */
    public void initialize(EquipmentChangeContext context) {
      totalGold = 0;
      for (EquipmentSlot slotType : ModifiableArmorMaterial.ARMOR_SLOTS) {
        boolean gold = hasGold(context, slotType);
        hasGold[slotType.getIndex()] = gold;
        if (gold) {
          totalGold++;
        }
      }
    }
  }


  /* Builder */

  /** Creates a new builder instance */
  public static Builder builder(Attribute attribute, Operation operation) {
    return new Builder(attribute, operation);
  }

  public static Builder builder(Supplier<Attribute> attribute, Operation operation) {
    return new Builder(attribute.get(), operation);
  }

  @Setter
  @Accessors(fluent = true)
  @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
  public static class Builder extends ModuleBuilder.Stack<Builder>  implements LevelingValue.Builder<GoldenAttributeModule> {
    protected final Attribute attribute;
    protected final Operation operation;
    protected String unique = "";

    @Override
    public GoldenAttributeModule amount(float flat, float eachLevel) {
      return new GoldenAttributeModule(unique, TConstruct.createKey("datagen"), attribute, operation, new LevelingValue(flat, eachLevel), condition);
    }
  }
}

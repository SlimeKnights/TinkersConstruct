package slimeknights.tconstruct.library.modifiers.modules.behavior;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.AttributeModuleBuilder;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModuleCondition;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * Module to add an attribute to a tool
 */
public record AttributeModule(String unique, Attribute attribute, Operation operation, LevelingValue amount, UUID[] slotUUIDs, ModifierModuleCondition condition) implements AttributesModifierHook, ModifierModule {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.ATTRIBUTES);

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

  @Override
  public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute,AttributeModifier> consumer) {
    if (condition.matches(tool, modifier)) {
      UUID uuid = slotUUIDs[slot.getFilterFlag()];
      if (uuid != null) {
        consumer.accept(attribute, new AttributeModifier(uuid, unique + "." + slot.getName(), amount.compute(tool, modifier), operation));
      }
    }
  }

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }

  public static final IGenericLoader<AttributeModule> LOADER = new IGenericLoader<>() {
    private static final BiFunction<JsonElement, String, EquipmentSlot> SLOT_PARSER = (element, string) -> EquipmentSlot.byName(GsonHelper.convertToString(element, string));

    @Override
    public AttributeModule deserialize(JsonObject json) {
      String unique = GsonHelper.getAsString(json, "unique");
      return new AttributeModule(unique,
        JsonHelper.getAsEntry(ForgeRegistries.ATTRIBUTES, json, "attribute"),
        JsonHelper.getAsEnum(json, "operation", Operation.class),
        LevelingValue.deserialize(json),
        slotsToUUIDs(unique, JsonHelper.parseList(json, "slots", SLOT_PARSER)),
        ModifierModuleCondition.deserializeFrom(json)
      );
    }

    @Override
    public void serialize(AttributeModule object, JsonObject json) {
      object.condition.serializeInto(json);
      json.addProperty("unique", object.unique);
      json.addProperty("attribute", Objects.requireNonNull(object.attribute.getRegistryName()).toString());
      json.addProperty("operation", object.operation.name().toLowerCase(Locale.ROOT));
      object.amount.serialize(json);
      JsonArray array = new JsonArray();
      for (EquipmentSlot slot : EquipmentSlot.values()) {
        if (object.slotUUIDs[slot.getFilterFlag()] != null) {
          array.add(slot.getName());
        }
      }
      json.add("slots", array);
    }

    @Override
    public AttributeModule fromNetwork(FriendlyByteBuf buffer) {
      String name = buffer.readUtf(Short.MAX_VALUE);
      int packed = buffer.readByte();
      UUID[] slotUUIDs = new UUID[6];
      for (EquipmentSlot slot : EquipmentSlot.values()) {
        if ((packed & (1 << slot.getFilterFlag())) > 0) {
          slotUUIDs[slot.getFilterFlag()] = getUUID(name, slot);
        }
      }
      return new AttributeModule(name,
        buffer.readRegistryIdUnsafe(ForgeRegistries.ATTRIBUTES),
        buffer.readEnum(Operation.class),
        LevelingValue.fromNetwork(buffer),
        slotUUIDs,
        ModifierModuleCondition.fromNetwork(buffer)
      );
    }

    @Override
    public void toNetwork(AttributeModule object, FriendlyByteBuf buffer) {
      buffer.writeUtf(object.unique);
      int packed = 0;
      for (EquipmentSlot slot : EquipmentSlot.values()) {
        if (object.slotUUIDs[slot.getFilterFlag()] != null) {
          packed |= (1 << slot.getFilterFlag());
        }
      }
      buffer.writeByte(packed);
      buffer.writeRegistryIdUnsafe(ForgeRegistries.ATTRIBUTES, object.attribute);
      buffer.writeEnum(object.operation);
      object.amount.toNetwork(buffer);
      object.condition.toNetwork(buffer);
    }
  };


  /** Creates a new builder instance */
  public static Builder builder(Attribute attribute, Operation operation) {
    return new Builder(attribute, operation);
  }

  public static class Builder extends AttributeModuleBuilder<Builder,AttributeModule> {
    private EquipmentSlot[] slots = EquipmentSlot.values();

    protected Builder(Attribute attribute, Operation operation) {
      super(attribute, operation);
    }

    /** Adds the given slots to this builder */
    public Builder slots(EquipmentSlot... slots) {
      this.slots = slots;
      return this;
    }

    @Override
    public AttributeModule amount(float flat, float eachLevel) {
      if (unique == null) {
        throw new IllegalStateException("Must set unique for attributes");
      }
      return new AttributeModule(unique, attribute, operation, new LevelingValue(flat, eachLevel), slotsToUUIDs(unique, List.of(slots)), condition);
    }
  }
}

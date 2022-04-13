package slimeknights.tconstruct.library.modifiers.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.JsonUtils;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;

/** Represents an attribute in a modifier */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ModifierAttribute {
  private final String name;
  private final Attribute attribute;
  private final Operation operation;
  private final float amount;
  private final UUID[] slotUUIDs;

  public ModifierAttribute(String name, Attribute attribute, Operation operation, float amount, List<EquipmentSlot> slots) {
    this.name = name;
    this.attribute = attribute;
    this.operation = operation;
    this.amount = amount;
    this.slotUUIDs = new UUID[6];
    for (EquipmentSlot slot : slots) {
      slotUUIDs[slot.getFilterFlag()] = getUUID(name, slot);
    }
  }

  public ModifierAttribute(String name, Attribute attribute, Operation operation, float amount, EquipmentSlot... slots) {
    this.name = name;
    this.attribute = attribute;
    this.operation = operation;
    this.amount = amount;
    this.slotUUIDs = new UUID[6];
    for (EquipmentSlot slot : slots) {
      slotUUIDs[slot.getFilterFlag()] = getUUID(name, slot);
    }
  }

  /**
   * Applies this attribute boost
   * @param tool       Tool receiving the boost
   * @param level      Modifier level
   * @param slot       Slot receiving the boost
   * @param consumer   Consumer accepting attributes
   */
  public void apply(IToolStackView tool, float level, EquipmentSlot slot, BiConsumer<Attribute,AttributeModifier> consumer) {
    // TODO: tag condition?
    UUID uuid = slotUUIDs[slot.getFilterFlag()];
    if (uuid != null) {
      consumer.accept(attribute, new AttributeModifier(uuid, name + "." + slot.getName(), amount * level, operation));
    }
  }

  /** Converts this to JSON */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.addProperty("unique", name);
    json.addProperty("attribute", Objects.requireNonNull(attribute.getRegistryName()).toString());
    json.addProperty("operation", operation.name().toLowerCase(Locale.ROOT));
    json.addProperty("amount", amount);
    JsonArray array = new JsonArray();
    for (EquipmentSlot slot : EquipmentSlot.values()) {
      if (slotUUIDs[slot.getFilterFlag()] != null) {
        array.add(slot.getName());
      }
    }
    json.add("slots", array);
    return json;
  }

  /** Parses the modifier attribute from JSON */
  public static ModifierAttribute fromJson(JsonObject json) {
    String unique = GsonHelper.getAsString(json, "unique");
    Attribute attribute = JsonUtils.getAsEntry(ForgeRegistries.ATTRIBUTES, json, "attribute");
    Operation op = JsonUtils.getAsEnum(json, "operation", Operation.class);
    float amount = GsonHelper.getAsFloat(json, "amount");
    List<EquipmentSlot> slots = JsonHelper.parseList(json, "slots", (element, string) -> EquipmentSlot.byName(GsonHelper.convertToString(element, string)));
    return new ModifierAttribute(unique, attribute, op, amount, slots);
  }

  /** Writes this to the network */
  public void toNetwork(FriendlyByteBuf buffer) {
    buffer.writeUtf(name);
    buffer.writeRegistryIdUnsafe(ForgeRegistries.ATTRIBUTES, attribute);
    buffer.writeEnum(operation);
    buffer.writeFloat(amount);
    int packed = 0;
    for (EquipmentSlot slot : EquipmentSlot.values()) {
      if (slotUUIDs[slot.getFilterFlag()] != null) {
        packed |= (1 << slot.getFilterFlag());
      }
    }
    buffer.writeInt(packed);
  }

  /** Reads this from the network */
  public static ModifierAttribute fromNetwork(FriendlyByteBuf buffer) {
    String name = buffer.readUtf(Short.MAX_VALUE);
    Attribute attribute = buffer.readRegistryIdUnsafe(ForgeRegistries.ATTRIBUTES);
    Operation operation = buffer.readEnum(Operation.class);
    float amount = buffer.readFloat();
    int packed = buffer.readInt();
    UUID[] slotUUIDs = new UUID[6];
    for (EquipmentSlot slot : EquipmentSlot.values()) {
      if ((packed & (1 << slot.getFilterFlag())) > 0) {
        slotUUIDs[slot.getFilterFlag()] = getUUID(name, slot);
      }
    }
    return new ModifierAttribute(name, attribute, operation, amount, slotUUIDs);
  }

  /** Gets the UUID from a name */
  private static UUID getUUID(String name, EquipmentSlot slot) {
    return UUID.nameUUIDFromBytes((name + "." + slot.getName()).getBytes());
  }
}

package slimeknights.tconstruct.library.json.variable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import io.netty.handler.codec.DecoderException;
import net.minecraft.Util;
import net.minecraft.core.IdMap;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.util.typed.TypedMap;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.json.TinkerLoadables;

/** Loadable fetching stat instances. Format is either a resource location (if a generic custom stat) or an array with two resource locations (if using another type) */
public enum StatLoadable implements Loadable<Stat<?>> {
  INSTANCE;

  /* JSON */

  @Override
  public Stat<?> convert(JsonElement element, String key, TypedMap context) {
    // primitives are interpreted as custom stat
    if (element.isJsonPrimitive()) {
      return Stats.CUSTOM.get(TinkerLoadables.CUSTOM_STAT.convert(element, key, context));
    }
    // arrays are interpreted as a pair of stat type and registry key
    if (element.isJsonArray()) {
      JsonArray array = element.getAsJsonArray();
      if (array.size() != 2) {
        throw new JsonSyntaxException(key + " must have exactly 2 elements");
      }
      StatType<?> statType = TinkerLoadables.STAT_TYPE.convert(array.get(0), key + "[0]", context);
      return parseStat(statType, array.get(1), key + "[1]", context);
    }
    // anything else throws
    throw new JsonSyntaxException("Expected " + key + " to be a string or array, was " + GsonHelper.getType(element));
  }

  /** Parses the stat as the given type */
  private static <T> Stat<T> parseStat(StatType<T> statType, JsonElement element, String key, TypedMap context) {
    Registry<T> registry = statType.getRegistry();
    ResourceLocation name = Loadables.RESOURCE_LOCATION.convert(element, key, context);
    if (registry.containsKey(name)) {
      T value = registry.get(name);
      if (value != null) {
        return statType.get(value);
      }
    }
    throw new JsonSyntaxException("Unable to parse " + key + " as registry " + registry.key().location() + " does not contain ID " + name);
  }

  @Override
  public JsonElement serialize(Stat<?> stat) {
    // serialize custom stats to just a single key
    if (stat.getType() == Stats.CUSTOM) {
      return new JsonPrimitive(stat.getValue().toString());
    }
    return serializeGeneric(stat);
  }

  /** Serializes the given stat to a JSON array containing the two IDs */
  private static <T> JsonElement serializeGeneric(Stat<T> stat) {
    // ensure the value is registered
    StatType<T> type = stat.getType();
    Registry<T> registry = type.getRegistry();
    T value = stat.getValue();
    ResourceLocation location = registry.getKey(value);
    if (location == null) {
      throw new RuntimeException("Registry " + registry.key().location() + " does not contain object " + value);
    }
    JsonArray array = new JsonArray();
    array.add(TinkerLoadables.STAT_TYPE.serialize(type));
    array.add(location.toString());
    return array;
  }


  /* Buffer */

  /** Reads a value, throwing if missing instead of returning null like {@link FriendlyByteBuf#readById(IdMap)} */
  private static <T> T decodeRegistry(FriendlyByteBuf buffer, Registry<T> registry) {
    int id = buffer.readVarInt();
    T value = registry.byId(id);
    if (value != null) {
      return value;
    }
    throw new DecoderException("Unknown " + registry.key().location() + " id " + id);
  }

  @Override
  public Stat<?> decode(FriendlyByteBuf buffer, TypedMap context) {
    return decodeValue(buffer, decodeRegistry(buffer, BuiltInRegistries.STAT_TYPE));
  }

  /** Helper to decode the value using the type generics */
  private static <T> Stat<T> decodeValue(FriendlyByteBuf buffer, StatType<T> statType) {
    return statType.get(decodeRegistry(buffer, statType.getRegistry()));
  }

  @Override
  public void encode(FriendlyByteBuf buffer, Stat<?> value) {
    encodeGeneric(buffer, value);
  }

  /** Encodes the value to the registry using the type generics */
  private <T> void encodeGeneric(FriendlyByteBuf buffer, Stat<T> value) {
    StatType<T> type = value.getType();
    buffer.writeId(BuiltInRegistries.STAT_TYPE, type);
    buffer.writeId(type.getRegistry(), value.getValue());
  }


  /* Tooltip */

  /** Key for pairing a stat name and value, e.g. "Dropped Stick" */
  private static final String KEY_STAT_VALUE = TConstruct.makeTranslationKey("stat", "stat_value");
  // vanilla stat types that contain formatting we have to recreate
  private static final String KEY_KILLED = TConstruct.makeTranslationKey("stat_type", "killed");
  private static final String KEY_KILLED_BY = TConstruct.makeTranslationKey("stat_type", "killed_by");

  /** Gets the name for the given stat */
  public static Component statName(Stat<?> stat) {
    StatType<?> type = stat.getType();
    Object value = stat.getValue();
    // custom stats format using the ID as the translation key
    if (type == Stats.CUSTOM) {
      return Component.translatable(Util.makeDescriptionId("stat", (ResourceLocation) value));
    }
    // killed and killed by have weird translations at their "stat_type." key
    if (type == Stats.ENTITY_KILLED) {
      return Component.translatable(KEY_KILLED, ((EntityType<?>) value).getDescription());
    }
    if (type == Stats.ENTITY_KILLED_BY) {
      return Component.translatable(KEY_KILLED_BY, ((EntityType<?>) value).getDescription());
    }

    // no special name, start forming generic name
    Registry<?> registry = type.getRegistry();
    Component name;
    // vanilla registries
    if (registry == BuiltInRegistries.BLOCK) {
      name = ((Block) value).getName();
    } else if (registry == BuiltInRegistries.ITEM) {
      name = ((Item) value).getDescription();
    } else if (registry == BuiltInRegistries.ENTITY_TYPE) {
      name = ((EntityType<?>) value).getDescription();
    // other useful registries - some mod might be using them
    } else if (registry == BuiltInRegistries.FLUID) {
      name = ((Fluid) value).getFluidType().getDescription();
    } else if (registry == BuiltInRegistries.MOB_EFFECT) {
      name = ((MobEffect) value).getDisplayName();
    } else if (registry == BuiltInRegistries.ENCHANTMENT) {
      name = Component.translatable(((Enchantment) value).getDescriptionId());
    } else {
      // if it's not one of the above types we do not know how to translate it, so use the raw key
      name = Component.literal(getKey(stat));
    }
    return Component.translatable(KEY_STAT_VALUE, type.getDisplayName(), name);
  }

  /** Gets the registry key for the given stat's value */
  private static <T> String getKey(Stat<T> stat) {
    Registry<T> registry = stat.getType().getRegistry();
    ResourceLocation key = registry.getKey(stat.getValue());
    if (key != null) {
      return key.toString();
    }
    return "(unregistered)";
  }
}

package slimeknights.tconstruct.common.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;
import slimeknights.mantle.recipe.ICondition;
import slimeknights.tconstruct.common.config.TConfig;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.TinkerCommons;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BooleanSupplier;

public class ConfigEnabledCondition implements ICondition, LootCondition {
  public static final Identifier ID = Util.getResource("config");
  public static final Serializer SERIALIZER = new Serializer();
  /* Map of config names to condition cache */
  private static final Map<String,ConfigEnabledCondition> PROPS = new HashMap<>();

  private final String configName;
  private final BooleanSupplier supplier;

  public ConfigEnabledCondition(String cn, BooleanSupplier sup) {
    this.configName = cn;
    this.supplier = sup;
  }

  @Override
  public Identifier getID() {
    return ID;
  }

  @Override
  public boolean test() {
    return supplier.getAsBoolean();
  }

  @Override
  public boolean test(LootContext lootContext) {
    return supplier.getAsBoolean();
  }

  @Override
  public LootConditionType getType() {
    return TinkerCommons.lootConfig;
  }

  private static class Serializer implements JsonSerializer<ConfigEnabledCondition> {
    public Identifier getID() {
      return ID;
    }

    public void write(JsonObject json, ConfigEnabledCondition value) {
      json.addProperty("prop", value.configName);
    }

    public ConfigEnabledCondition read(JsonObject json) {
      String prop = JsonHelper.getString(json, "prop");
      ConfigEnabledCondition config = PROPS.get(prop.toLowerCase(Locale.ROOT));
      if (config == null) {
        throw new JsonSyntaxException("Invalid property name '" + prop + "'");
      }
      return config;
    }

    @Override
    public void toJson(JsonObject json, ConfigEnabledCondition condition, JsonSerializationContext context) {
      write(json, condition);
    }

    @Override
    public ConfigEnabledCondition fromJson(JsonObject json, JsonDeserializationContext context) {
      return read(json);
    }
  }

  /**
   * Adds a condition
   * @param prop     Property name
   * @param supplier Boolean supplier
   * @return Added condition
   */
  private static ConfigEnabledCondition add(String prop, BooleanSupplier supplier) {
    ConfigEnabledCondition conf = new ConfigEnabledCondition(prop, supplier);
    PROPS.put(prop.toLowerCase(Locale.ROOT), conf);
    return conf;
  }

  /**
   * Adds a condition
   * @param prop     Property name
   * @param supplier Config value
   * @return Added condition
   */
  private static ConfigEnabledCondition add(String prop, boolean supplier) {
    return add(prop, () -> supplier);
  }

  @Override
  public String toString() {
    return "config_setting_enabled(\"" + this.configName + "\")";
  }

  /* Properties */
  public static final ConfigEnabledCondition GRAVEL_TO_FLINT = add("gravel_to_flint", TConfig.common.addGravelToFlintRecipe);
  public static final ConfigEnabledCondition CHEAPER_NETHERITE_ALLOY = add("cheaper_netherite_alloy", TConfig.common.cheaperNetheriteAlloy);
  public static final ConfigEnabledCondition WITHER_BONE_DROP = add("wither_bone_drop", TConfig.common.witherBoneDrop);
  public static final ConfigEnabledCondition WITHER_BONE_CONVERSION = add("wither_bone_conversion", TConfig.common.witherBoneConversion);
}

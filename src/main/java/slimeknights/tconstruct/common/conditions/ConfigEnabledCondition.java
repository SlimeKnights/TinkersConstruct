package slimeknights.tconstruct.common.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.TinkerCommons;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BooleanSupplier;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigEnabledCondition implements ICondition, ILootCondition {
  public static final ResourceLocation ID = Util.getResource("config");
  public static final Serializer SERIALIZER = new Serializer();
  /* Map of config names to condition cache */
  private static final Map<String,ConfigEnabledCondition> PROPS = new HashMap<>();

  private final String configName;
  private final BooleanSupplier supplier;

  @Override
  public ResourceLocation getID() {
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
  public LootConditionType func_230419_b_() {
    return TinkerCommons.lootConfig;
  }

  private static class Serializer implements ILootSerializer<ConfigEnabledCondition>, IConditionSerializer<ConfigEnabledCondition> {
    @Override
    public ResourceLocation getID() {
      return ID;
    }

    @Override
    public void write(JsonObject json, ConfigEnabledCondition value) {
      json.addProperty("prop", value.configName);
    }

    @Override
    public ConfigEnabledCondition read(JsonObject json) {
      String prop = JSONUtils.getString(json, "prop");
      ConfigEnabledCondition config = PROPS.get(prop.toLowerCase(Locale.ROOT));
      if (config == null) {
        throw new JsonSyntaxException("Invalid property name '" + prop + "'");
      }
      return config;
    }

    @Override
    public void serialize(JsonObject json, ConfigEnabledCondition condition, JsonSerializationContext context) {
      write(json, condition);
    }

    @Override
    public ConfigEnabledCondition deserialize(JsonObject json, JsonDeserializationContext context) {
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
  private static ConfigEnabledCondition add(String prop, BooleanValue supplier) {
    return add(prop, supplier::get);
  }

  @Override
  public String toString() {
    return "config_setting_enabled(\"" + this.configName + "\")";
  }

  /* Properties */
  public static final ConfigEnabledCondition GRAVEL_TO_FLINT = add("gravel_to_flint", Config.COMMON.addGravelToFlintRecipe);
  public static final ConfigEnabledCondition CHEAPER_NETHERITE_ALLOY = add("cheaper_netherite_alloy", Config.COMMON.cheaperNetheriteAlloy);
  public static final ConfigEnabledCondition WITHER_BONE_DROP = add("wither_bone_drop", Config.COMMON.witherBoneDrop);
  public static final ConfigEnabledCondition WITHER_BONE_CONVERSION = add("wither_bone_conversion", Config.COMMON.witherBoneConversion);
}

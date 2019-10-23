package slimeknights.tconstruct.common.conditions;

import com.google.gson.JsonObject;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import slimeknights.tconstruct.common.config.Config;

public class ConfigOptionEnabledCondition implements ICondition {

  private static final ResourceLocation NAME = new ResourceLocation("tconstruct", "config_option_enabled");
  private final String configSetting;

  public ConfigOptionEnabledCondition(String configSetting) {
    this.configSetting = configSetting;
  }

  @Override
  public ResourceLocation getID() {
    return NAME;
  }

  @Override
  public boolean test() {
    switch (this.configSetting) {
      case "registerAllRecipes":
        return Config.SERVER.registerAllRecipes.get();
      case "addGravelToFlintRecipe":
        return Config.SERVER.addGravelToFlintRecipe.get();
      case "matchVanillaSlimeblock":
        return Config.SERVER.requireSlimeballsToMatchInVanillaRecipe.get();
      default:
        throw new RuntimeException(String.format("Invalid config setting: %s", this.configSetting));
    }
  }

  @Override
  public String toString() {
    return "config_setting_enabled(\"" + this.configSetting + "\")";
  }

  public static class Serializer implements IConditionSerializer<ConfigOptionEnabledCondition> {

    public static final Serializer INSTANCE = new Serializer();

    @Override
    public void write(JsonObject json, ConfigOptionEnabledCondition value) {
      json.addProperty("config_setting", value.configSetting);
    }

    @Override
    public ConfigOptionEnabledCondition read(JsonObject json) {
      return new ConfigOptionEnabledCondition(JSONUtils.getString(json, "config_setting"));
    }

    @Override
    public ResourceLocation getID() {
      return ConfigOptionEnabledCondition.NAME;
    }
  }
}